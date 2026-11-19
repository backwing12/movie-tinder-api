package dev.backwing

import java.sql.DriverManager

class MovieCache(
    private val url: String,
    private val user: String,
    private val password: String
) {
    fun get(id: Int): String? {
        DriverManager.getConnection(url, user, password).use { conn ->
            conn.prepareStatement("SELECT response_json FROM movie_cache WHERE tmdb_id = ?").use { stmt ->
                stmt.setInt(1, id)
                stmt.executeQuery().use { result ->
                    return if (result.next()) result.getString("response_json") else null
                }
            }
        }
    }

    fun put(id: Int, json: String) {
        DriverManager.getConnection(url, user, password).use { conn ->
            conn.prepareStatement("INSERT INTO movie_cache (tmdb_id, response_json, fetched_at) VALUES (?, ?, ?)").use { stmt ->
                stmt.setInt(1, id)                                   // første ?  → tmdb_id
                stmt.setString(2, json)                              // andre ?   → response_json
                stmt.setObject(3, java.time.LocalDateTime.now())     // tredje ?  → fetched_at
                stmt.executeUpdate()
            }
        }
    }
}