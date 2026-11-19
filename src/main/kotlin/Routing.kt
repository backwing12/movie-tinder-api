package dev.backwing

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlin.coroutines.cancellation.CancellationException

fun Application.configureRouting(
    client: HttpClient = HttpClient(CIO),
    cache: MovieCache? = MovieCache(
        environment.config.property("database.url").getString(),
        environment.config.property("database.user").getString(),
        environment.config.property("database.password").getString()
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
                call.respondText(response.bodyAsText(), ContentType.Application.Json, response.status)
                
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadGateway, mapOf("error" to "Could not get movie from TMDB"))
            }
            
        }
    }
}