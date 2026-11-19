package dev.backwing

import java.sql.DriverManager

class MovieCache(
    private val url: String,
    private val user: String,
    private val password: String,
    private val ttlHours: Long
) {
    fun get(id: Int): String? {
        DriverManager.getConnection(url, user, password).use { conn ->
            conn.prepareStatement("SELECT response_json FROM movie_cache WHERE tmdb_id = ? AND fetched_at > ?")
                .use { stmt ->
                    stmt.setInt(1, id)
                    stmt.setObject(2, java.time.LocalDateTime.now().minusHours(ttlHours))
                    stmt.executeQuery().use { result ->
                        return if (result.next()) result.getString("response_json") else null
                    }
                }
        }
    }

    fun put(id: Int, json: String) {
        DriverManager.getConnection(url, user, password).use { conn ->
            conn.prepareStatement("UPDATE movie_cache SET response_json = ?, fetched_at = ? WHERE tmdb_id = ? ")
                .use { stmt ->
                    stmt.setString(1, json)
                    stmt.setObject(2, java.time.LocalDateTime.now())
                    stmt.setInt(3, id)
                    val updated = stmt.executeUpdate()
                    if (updated == 0) {
                        conn.prepareStatement("INSERT INTO movie_cache (tmdb_id, response_json, fetched_at) VALUES (?, ?, ?)")
                            .use { insert ->
                                insert.setInt(1, id)
                                insert.setString(2, json)
                                insert.setObject(3, java.time.LocalDateTime.now())
                                insert.executeUpdate()
                            }
                    }

                }


        }
    }
}