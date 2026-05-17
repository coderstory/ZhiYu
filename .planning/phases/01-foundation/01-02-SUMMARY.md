---
phase: 01-foundation
plan: 02
subsystem: "data-layer, di-wiring"
tags: ["room", "fts4", "datastore", "koin", "entities", "dao", "database"]
requires: ["01-01"]
provides: ["ArticleEntity", "CategoryEntity", "TagEntity", "ArticleTagCrossRef", "QuickNoteEntity", "ArticleFts", "ZhiYuDatabase", "AppPreferences", "Koin modules", "ZhiYuApplication"]
affects: ["03-theme", "04-navigation"]
tech-stack:
  added: ["Room 2.8.4", "DataStore Preferences 1.2.1", "Koin"]
  patterns: ["MVVM + UDF structural stubs"]
key-files:
  created:
    - app/src/main/java/com/zhiyu/app/data/local/entity/ArticleEntity.kt
    - app/src/main/java/com/zhiyu/app/data/local/entity/CategoryEntity.kt
    - app/src/main/java/com/zhiyu/app/data/local/entity/TagEntity.kt
    - app/src/main/java/com/zhiyu/app/data/local/entity/ArticleTagCrossRef.kt
    - app/src/main/java/com/zhiyu/app/data/local/entity/QuickNoteEntity.kt
    - app/src/main/java/com/zhiyu/app/data/local/entity/ArticleFts.kt
    - app/src/main/java/com/zhiyu/app/data/local/converter/Converters.kt
    - app/src/main/java/com/zhiyu/app/data/local/dao/ArticleDao.kt
    - app/src/main/java/com/zhiyu/app/data/local/dao/CategoryDao.kt
    - app/src/main/java/com/zhiyu/app/data/local/dao/TagDao.kt
    - app/src/main/java/com/zhiyu/app/data/local/dao/QuickNoteDao.kt
    - app/src/main/java/com/zhiyu/app/data/local/ZhiYuDatabase.kt
    - app/src/main/java/com/zhiyu/app/data/preferences/AppPreferences.kt
    - app/src/main/java/com/zhiyu/app/di/AppModule.kt
    - app/src/main/java/com/zhiyu/app/di/RepositoryModule.kt
    - app/src/main/java/com/zhiyu/app/di/ViewModelModule.kt
    - app/src/main/java/com/zhiyu/app/ZhiYuApplication.kt
    - app/src/main/java/com/zhiyu/app/model/ThemeMode.kt
    - app/schemas/.gitkeep
decisions:
  - "All 5 entities created upfront with schema version 1 (D-04)"
  - "FTS4 uses TOKENIZER_UNICODE61, not ICU (D-05)"
  - "DataStore keys: theme_mode, last_active_tab, is_first_launch (D-06)"
  - "No auto-migration; manual migration only (D-07)"
  - "RepositoryModule and ViewModelModule created as empty stubs for future population"
  - "Converters uses manual join/split for List<String> (no kotlinx.serialization dependency)"
metrics:
  duration: "~3 minutes"
  files_created: 19
  tasks: 3
  commits: 3
---

# Phase 1 Plan 2: Data Layer + DI Wiring Summary

Complete local data persistence layer (Room database with 5 entities + FTS4 virtual table, DataStore Preferences) and Koin dependency injection wiring for the ZhiYu Android application.

**One-liner:** Room database with 6 entities (5 data + 1 FTS4), DataStore Preferences wrapper with 3 keys, and 3 Koin DI modules wired through ZhiYuApplication.

---

## Tasks Executed

### Task 1: Room Entities (7 files)
**Commit:** `93abf90`

Created 5 data entities (ArticleEntity, CategoryEntity, TagEntity, ArticleTagCrossRef, QuickNoteEntity), 1 FTS4 virtual table entity (ArticleFts), and 1 TypeConverters class.

Key details:
- **ArticleEntity** references CategoryEntity via FK with ON DELETE SET NULL
- **CategoryEntity** has self-referential parentId FK with ON DELETE CASCADE
- **ArticleTagCrossRef** manages many-to-many with cascading deletes both directions
- **QuickNoteEntity** is lightweight with no foreign key dependencies
- **ArticleFts** uses `FtsOptions.TOKENIZER_UNICODE61` (CRITICAL: ICU is unavailable on Android SQLite and would crash at runtime)
- **No @Relation annotations** -- Phase 4 will use JOIN queries to avoid N+1
- **Converters** uses manual join/split for `List<String>` <-> `String` conversion

### Task 2: Room DAOs, Database, and AppPreferences (7 files)
**Commit:** `959cb84`

Created 4 DAO interfaces, ZhiYuDatabase abstract class, AppPreferences DataStore wrapper, and ThemeMode enum.

Key details:
- **ArticleDao** includes FTS4 search: `SELECT * FROM articles WHERE rowid IN (SELECT rowid FROM articles_fts WHERE content MATCH :query)`
- **TagDao** includes JOIN query for tags by article
- **QuickNoteDao** orders by createdAt DESC for reverse chronological display
- **ZhiYuDatabase** registers all 6 entities in `@Database(entities = [...], version = 1, exportSchema = true)`
- **AppPreferences** exposes 3 Flow properties (`themeMode`, `lastActiveTab`, `isFirstLaunch`) each with `.flowOn(Dispatchers.IO)` and `.distinctUntilChanged()`
- **ThemeMode** enum (SYSTEM, LIGHT, DARK) in shared `model/` package for cross-layer use

### Task 3: Koin DI and ZhiYuApplication (5 files)
**Commit:** `94d96fc`

Created 3 Koin modules and Application class.

Key details:
- **AppModule** provides: Room database singleton, 4 DAOs (from database), AppPreferences
- **RepositoryModule** is empty stub (populated in Phases 3-5)
- **ViewModelModule** is empty stub (populated in Phases 3-5)
- **ZhiYuApplication** calls `startKoin { androidContext(...); modules(appModule, repositoryModule, viewModelModule) }` in `onCreate()`
- Existing AndroidManifest.xml (from Plan 01) already references `android:name=".ZhiYuApplication"`
- `app/schemas/.gitkeep` ensures the schema export directory is tracked

---

## Deviations from Plan

None -- plan executed exactly as written. All 19 files created as specified.

---

## Threat Flags

None. All created files stay within the threat model boundaries defined in the plan. No new network endpoints, no file access patterns, no auth paths introduced.

---

## Verification Results

| Check | Result |
|-------|--------|
| All 18 Kotlin files + 1 .gitkeep exist | PASS (19 files) |
| TOKENIZER_ICU in ArticleFts | PASS (0 occurrences) |
| TOKENIZER_UNICODE61 in ArticleFts | PASS (1 occurrence) |
| @Relation annotations in entity package | PASS (0 occurrences) |
| .flowOn(Dispatchers.IO) in AppPreferences | PASS (3 occurrences) |
| .distinctUntilChanged() in AppPreferences | PASS (3 occurrences) |
| startKoin call in ZhiYuApplication | PASS (1 occurrence) |
| Koin module val declarations | PASS (3 declarations across 3 files) |
| ZhiYuApplication referenced in manifest | PASS (already set from Plan 01) |

---

## Self-Check: PASSED

All commits verified:
- `93abf90` feat(01-foundation-02): create Room entities with FTS4 and TypeConverters
- `959cb84` feat(01-foundation-02): create Room DAOs, database, and DataStore preferences
- `94d96fc` feat(01-foundation-02): create Koin DI modules and ZhiYuApplication

All 19 files confirmed present on disk.
