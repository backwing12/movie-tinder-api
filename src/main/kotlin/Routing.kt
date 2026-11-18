package dev.backwing

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

fun Application.configureRouting() {
    val apiKey = environment.config.property("tmdb.apiKey").getString()
    val client = HttpClient(CIO)
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
            val id = call.parameters["id"]
            val response = client.get("https://api.themoviedb.org/3/movie/$id") {
                parameter("api_key", apiKey)
            }
            call.respondText(response.bodyAsText(), ContentType.Application.Json, response.status)
        }
    }
}