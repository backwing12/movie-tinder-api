package dev.backwing

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import kotlin.coroutines.cancellation.CancellationException

fun Application.configureRouting(
    client: HttpClient = HttpClient(CIO),
    cache: MovieCache? = MovieCache(
        environment.config.property("database.url").getString(),
        environment.config.property("database.user").getString(),
        environment.config.property("database.password").getString(),
        environment.config.property("cache.ttlHours").getString().toLong()
    )
) {
    val apiKey = environment.config.property("tmdb.apiKey").getString()
    routing {
        get("/") {
            call.respondText("Hello, World!")
        }
        get("/json/kotlinx-serialization") {
            call.respond(mapOf("hello" to "world"))
        }
        get("/health") {
            call.respond(mapOf("status" to "ok"))
        }
        get("/movies/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid movie ID"))
                return@get
            }
            val cached = try {
                withContext(Dispatchers.IO) { cache?.get(id) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                null
            }
            if (cached != null) {
                call.respondText(cached, ContentType.Application.Json)
                return@get
            }

            try {
                val response = client.get("https://api.themoviedb.org/3/movie/$id") {
                    parameter("api_key", apiKey)
                }
                if (response.status == HttpStatusCode.NotFound) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Movie not found"))
                    return@get
                }
                if (!response.status.isSuccess()) {
                    call.respond(HttpStatusCode.BadGateway, mapOf("error" to "Could not get movie from TMDB"))
                    return@get
                }
                val body = response.bodyAsText()
                try {
                    withContext(Dispatchers.IO) { cache?.put(id, body) }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    // lagring feilet, men vi har filmen, så vi gjør ingenting
                }
                call.respondText(body, ContentType.Application.Json, response.status)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadGateway, mapOf("error" to "Could not get movie from TMDB"))
            }
        }
        get("/movies/search") {
            val pageText = call.request.queryParameters["page"]
            val page = if (pageText == null) 1 else pageText.toIntOrNull() 
            val genre = call.request.queryParameters["genre"]
            val decadeText = call.request.queryParameters["decade"]
            val decade = decadeText?.toIntOrNull()
            val startDate = if (decade == null) null else "$decade-01-01"
            val endDate = if (decade == null) null else "${decade+9}-12-31"
            val language = call.request.queryParameters["language"]
            if (page == null || page < 1 || page > 500) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Page must be between 1 and 500"))
                return@get
            }
            if (decadeText != null && (decade == null || decade % 10 != 0 || decade < 1900 || decade > LocalDate.now().year + 10)) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Decade must be a year ending in 0 from 1900 up to the next decade"))
                return@get
            }
            try {
                val response = client.get("https://api.themoviedb.org/3/discover/movie") {
                    parameter("api_key", apiKey)
                    parameter("page", page)
                    parameter("with_genres", genre)
                    parameter("primary_release_date.gte", startDate)
                    parameter("primary_release_date.lte", endDate)
                    parameter("with_original_language", language)
                }
                if (!response.status.isSuccess()) {
                    call.respond(HttpStatusCode.BadGateway, mapOf("error" to "Could not get movies from TMDB"))
                    return@get
                }
                val body = response.bodyAsText()
                call.respondText(body, ContentType.Application.Json, response.status)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadGateway, mapOf("error" to "Could not get movies from TMDB"))
            }
        }
    }
}