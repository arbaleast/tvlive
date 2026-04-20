# AGENTS.md — modules/data

Generated: 2026-04-20

## Overview

Data layer providing content models, repository pattern, and catalog caching for TV content.

## Where To Look

| File | Purpose |
|------|---------|
| `Content.kt` | Data class for TV content (title, description, thumbnail, stream URL) |
| `Category.kt` | Category model for organizing content |
| `ContentRepository.kt` | Interface defining content access operations |
| `ContentRepositoryImpl.kt` | Implementation loading from JSON assets |
| `CatalogCache.kt` | In-memory cache with TTL (30 min) |
| `AppConstants.kt` | Constants: CONTENT_DATA_FILE, CACHE_TTL_MS, seek durations |
| `AppError.kt` | Error handling (ContentNotFound, LoadError) |

## Conventions

- Repository pattern: interface in `ContentRepository.kt`, implementation in `ContentRepositoryImpl.kt`
- Cache TTL defined in `AppConstants.CACHE_TTL_MS` (currently 30 minutes)
- Content loaded from `assets/content_data.json` via `CONTENT_DATA_FILE` constant

## Anti-Patterns

- Do not bypass `ContentRepository` interface; always depend on abstraction
- Do not hardcode seek durations; use `AppConstants.SEEK_BACK_MS` / `SEEK_FORWARD_MS`
