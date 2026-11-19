CREATE DATABASE movietinder;
GO

USE movietinder;
GO

CREATE TABLE movie_cache (
    tmdb_id INT PRIMARY KEY,
    response_json NVARCHAR(MAX) NOT NULL,
    fetched_at DATETIME2 NOT NULL
);
GO