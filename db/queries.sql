SELECT * FROM movie_cache;

UPDATE movie_cache SET fetched_at = DATEADD(hour, -25, fetched_at) WHERE tmdb_id = 550;

SELECT tmdb_id, fetched_at FROM movie_cache;