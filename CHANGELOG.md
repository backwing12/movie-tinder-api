# Changelog

All notable changes to this project are documented here.
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
The project has no versioned releases yet, so entries are grouped by date.

## [Unreleased]

## 2026-11-20
### Added
- `docker-compose.yml` that starts the API and SQL Server together, with a named volume so cached data survives restarts.
- `.env.example` documenting the required environment variables.
- Test that `/movies/{id}` still returns the movie from TMDB when the cache database is down.

### Changed
- Database URL is configurable through `DB_URL`, so the same image runs locally and in Docker Compose.

### Fixed
- `/movies/{id}` no longer fails when the cache database is unavailable. Read and write errors fall back to fetching directly from TMDB.

## 2026-11-19
### Added
- SQL Server schema and `MovieCache` class for storing TMDB responses.
- Caching in `/movies/{id}`: responses are reused for 24 hours and refreshed when stale.
- Cache TTL configurable in `application.yaml`.

## 2026-11-18
### Added
- `/movies/{id}` endpoint that proxies TMDB, with the API key read from an environment variable.
- Error handling with proper status codes: 400 for an invalid id, 404 when the movie does not exist, 502 when TMDB fails.
- Tests for these cases using a mocked TMDB client.

## 2026-11-17
### Added
- Dockerfile and `.dockerignore` for running the API in a container.

## 2026-11-16
### Added
- Ktor project skeleton with a `/health` endpoint and test.
- GitHub Actions workflow that builds and runs the tests on every push.
- Project plan.
