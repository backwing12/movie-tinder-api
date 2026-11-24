package dev.backwing

import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import kotlin.test.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.*
import io.ktor.client.statement.bodyAsText
import java.io.IOException

class ServerTest {

    @Test
    fun `test root endpoint`() = testApplication {
        // loads default configuration
        configure()
        // verify server root returns 200
        assertEquals(HttpStatusCode.OK, client.get("/").status)
    }

    @Test
    fun `test health endpoint`() = testApplication {
        configure()
        assertEquals(HttpStatusCode.OK, client.get("/health").status)
    }
    
    @Test
    fun `invalid movie id returns 400`() = testApplication {
        configure()
        assertEquals(HttpStatusCode.BadRequest, client.get("/movies/abc").status)
    }
    
    @Test
    fun `movie not found at TMDB returns 404`() = testApplication {
        environment {
            config = MapApplicationConfig("tmdb.apiKey" to "test-key")
        }
        application {
            configureSerialization()       
            configureRouting(HttpClient(MockEngine { respond("", HttpStatusCode.NotFound) }), cache = null)
        }
        assertEquals(HttpStatusCode.NotFound, client.get("/movies/123").status)
    }
    
    @Test
    fun `TMDB unreachable returns 502`() = testApplication {
        environment {
            config = MapApplicationConfig("tmdb.apiKey" to "test-key")
        }
        application {
            configureSerialization()       
            configureRouting(HttpClient(MockEngine { throw IOException("TMDB is down") }), cache = null)
        }
        assertEquals(HttpStatusCode.BadGateway, client.get("/movies/500").status)
    }
    
    @Test
    fun `cache database down still returns movie from TMDB`() = testApplication {
        environment {
            config = MapApplicationConfig("tmdb.apiKey" to "test-key")
        }
        application {
            configureSerialization()
            configureRouting(
                HttpClient(MockEngine { respond("""{"id":550,"title":"Fight Club"}""", HttpStatusCode.OK) }),
                cache = MovieCache("jdbc:finnes-ikke", "sa", "x", 24)
            )
        }
        val response = client.get("/movies/550")
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Fight Club"))
    }
    
    @Test
    fun `invalid page returns 400`() = testApplication { 
        configure()
        assertEquals(HttpStatusCode.BadRequest, client.get("/movies/search?page=abc").status)
        assertEquals(HttpStatusCode.BadRequest, client.get("/movies/search?page=0").status)
        assertEquals(HttpStatusCode.BadRequest, client.get("/movies/search?page=600").status)
    }
    
    @Test
    fun `valid page returns movies from TMDB`() = testApplication {
        environment {
            config = MapApplicationConfig("tmdb.apiKey" to "test-key")
        }
        application {
            configureSerialization()
            configureRouting (
                HttpClient(MockEngine { respond("""{"page":3,"results":[]}""", HttpStatusCode.OK) }),
                cache = null
            )
        }
        val response = client.get("/movies/search?page=3")
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains(""""page":3"""))
    }
    
}
