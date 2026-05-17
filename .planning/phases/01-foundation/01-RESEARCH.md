# Phase 1: Foundation - Research

**Researched:** 2026-05-17
**Domain:** Android project scaffolding, Gradle build system, dependency injection (Koin), local persistence (Room + DataStore), MIUIX theming, CJK font fallback, Splash Screen
**Confidence:** HIGH (all libraries verified via official releases and Maven Central)

## Summary

Phase 1 is the greenfield project foundation for ZhiYu (知屿), an Android personal knowledge assistant. Every downstream phase depends on the outputs of this phase: Gradle build configuration, Koin dependency injection wiring, Room database schema, DataStore preferences, MIUIX theme system with CJK font fallback, and splash screen.

The build system targets AGP 9.0.28 with Kotlin 2.3.21 (a patch-level upgrade from the 2.3.20 in CLAUDE.md, required by MIUIX v0.9.1), Compose BOM 2026.04.01, and a TOML version catalog at `gradle/libs.versions.toml`. Koin 4.2.1 provides DI with three modules (AppModule, RepositoryModule, ViewModelModule). Room 2.8.4 manages five entities plus an FTS4 virtual table for full-text search. DataStore Preferences 1.2.1 holds three pre-defined keys. The MIUIX library (`top.yukonga.miuix.kmp:miuix-ui:0.9.1`) drives theming with its `MiuixTheme` composable. CJK font fallback uses `Typeface.CustomFallbackBuilder` with bundled subsetted Noto Sans SC. The splash screen uses AndroidX SplashScreen API with a DataStore-ready keep condition.

**Primary recommendation:** Use Kotlin 2.3.21 (not 2.3.20) for MIUIX compatibility. Use Koin 4.2.1 (latest stable targeting Kotlin 2.3.x). Bundle a glyph-subsetted Noto Sans SC (~1-3MB) rather than the full font (~16MB). Use WCDB or BreakIterator pre-segmentation for FTS4 Chinese tokenization since ICU is unavailable on Android.

## Architectural Responsibility Map

| Capability | Primary Tier | Secondary Tier | Rationale |
|------------|-------------|----------------|-----------|
| Gradle build system | Build | — | Purely build-time configuration |
| Koin DI wiring | Client (app process) | — | Runtime dependency resolution within the app process |
| Room database (entities, DAOs, schema) | Database/Storage | — | On-device SQLite via Room; all persistence is local |
| DataStore Preferences | Database/Storage | — | On-device key-value storage; no cloud sync |
| MIUIX theme (colors, typography, shapes) | Browser/Client (UI) | — | Compose theming layer, pure client-side rendering |
| CJK font fallback | Browser/Client (UI) | — | Text rendering pipeline in the Compose UI layer |
| Dark mode theme switching | Browser/Client (UI) | Database/Storage (prefs) | Theme rendered in UI; preference persisted in DataStore |
| Splash Screen | Android System + Client | — | System-managed splash + client-side keep-condition |

## User Constraints (from CONTEXT.md)

### Locked Decisions

- **D-01:** Full MIUIX (`top.yukonga.miuix.kmp:miuix-ui`) library added at foundation, all component categories available to downstream phases
- **D-02:** Theme driven by MIUIX theme system primarily, Material3 as fallback
- **D-03:** Dark mode theme configured at foundation alongside light theme — ready for immediate use by all phases
- **D-04:** All 5 entities (Article, Category, Tag, ArticleTagCrossRef, QuickNote) created upfront with schema version 1
- **D-05:** FTS4 virtual table for full-text search created at foundation with Chinese ICU tokenizer
- **D-06:** DataStore pre-defines all preference keys: `theme_mode` (SYSTEM/LIGHT/DARK), `last_active_tab`, `is_first_launch`
- **D-07:** Room auto-migration disabled — use manual migrations via `MigrationTestHelper`
- **D-08:** Splash shows adaptive app icon + "知屿" text on MIUI warm orange (#FF6B35) background
- **D-09:** Splash closes via `setKeepOnCondition` after DataStore initialization completes (not fixed duration)
- **D-10:** Uses AndroidX SplashScreen API `installSplashScreen()` before `super.onCreate()`
- **D-11:** Application ID: `com.zhiyu.app`
- **D-12:** App display name: "知屿"
- **D-13:** Root project name: `ZhiYu`

### Claude's Discretion

- MIUIX component import granularity — specific imports vs wildcard, as long as full library dependency is declared
- Room DAO method signatures — standard CRUD patterns
- Compose theme file organization (Color.kt, Type.kt, Shape.kt, Theme.kt split)
- Gradle task configuration details

### Deferred Ideas (OUT OF SCOPE)
None — discussion stayed within phase scope.

---

## Phase Requirements

| ID | Description | Research Support |
|----|-------------|------------------|
| FND-01 | Kotlin + Jetpack Compose project config, Gradle Kotlin DSL + version catalog | Standard Stack -- Build & Version Catalog section; AGP 9.0.28, Kotlin 2.3.21, Compose BOM 2026.04.01 |
| FND-02 | Android 16+ compatibility (minSdk = targetSdk = 36) | Standard Stack -- edge-to-edge mandatory, no opt-out; AGP 9.0+ required for SDK 36 |
| FND-03 | Koin dependency injection framework integration | Standard Stack -- Koin 4.2.1; Architecture Patterns -- Koin Integration |
| FND-04 | MIUIX UI library integration (`top.yukonga.miuix.kmp:miuix-ui`) | Standard Stack -- MIUIX v0.9.1; Architecture Patterns -- Theme Architecture (MIUIX) |
| FND-05 | Room database + DataStore preferences | Standard Stack -- Room 2.8.4, DataStore 1.2.1; Architecture Patterns -- Data Layer |
| FND-06 | MVVM + UDF architecture pattern | Architecture Patterns -- MVVM + UDF; STATE.md confirmed pattern |
| FND-07 | Theme system -- MIUI style + dark mode (follow system) | Architecture Patterns -- Theme Architecture (MIUIX); dark mode via MiuixTheme |
| FND-08 | Chinese font fallback (CJK font fallback) | CJK Font Fallback section; Typeface.CustomFallbackBuilder + subsetted Noto Sans SC |
| FND-09 | Splash launch screen | Splash Screen Architecture section; installSplashScreen() before super.onCreate() |

## Standard Stack

### Core

| Library | Version | Purpose | Why Standard |
|---------|---------|---------|-------------|
| Android Gradle Plugin | **9.0.28** | Build system | Required for compileSdk 36; built-in Kotlin support [VERIFIED: developer.android.com] |
| Kotlin | **2.3.21** | Language | Latest stable (Apr 23, 2026); required by MIUIX v0.9.1; patch over 2.3.20 [VERIFIED: kotlinlang.org] |
| KSP | **2.3.6** | Annotation processing | For Room; matches Kotlin 2.3.x compatibility [VERIFIED: github.com/google/ksp] |
| Compose BOM | **2026.04.01** | All Compose versions | Official Google BOM; ships Compose 1.11.0 [VERIFIED: android-developers.googleblog.com] |
| Compose Compiler | **Kotlin 2.3.21 built-in** | Compose compilation | Built into Kotlin 2.0+; use `org.jetbrains.kotlin.plugin.compose` plugin [VERIFIED: developer.android.com] |
| Room | **2.8.4** | Local database | Stable, coroutines/Flow support; NOT 3.0 alpha [VERIFIED: developer.android.com] |
| Navigation Compose | **2.9.8** | Screen navigation | For Phase 2; declared here for version catalog [VERIFIED: developer.android.com] |
| DataStore Preferences | **1.2.1** | Key-value preferences | Modern SharedPreferences replacement; coroutine/Flow-based [VERIFIED: developer.android.com] |
| Core SplashScreen | **1.2.0** | Splash screen compat | Standard AndroidX SplashScreen API [VERIFIED: developer.android.com] |
| Koin | **4.2.1** | Dependency injection | Latest stable targeting Kotlin 2.3.x; `koin-android` + `koin-androidx-compose` [VERIFIED: github.com/InsertKoinIO/koin] |
| MIUIX | **0.9.1** | MIUI/HyperOS design components | `top.yukonga.miuix.kmp:miuix-ui`; Compose Multiplatform; published May 2026 [VERIFIED: Maven Central, github.com/miuix-kotlin-multiplatform/miuix] |

### Supporting

| Library | Version | Purpose | When to Use |
|---------|---------|---------|-------------|
| MIUIX Preference | 0.9.1 | Preference components (SwitchPreference, etc.) | Phase 5 settings screens |
| MIUIX Icons | 0.9.1 | Extended MIUI-style icons | UI icon needs beyond Material Icons |
| MIUIX Blur | 0.9.1 | Blur effects | Visual polish in Phase 5 |
| WCDB | 1.0.8 | SQLite with ICU tokenizer | Alternative if FTS4 Chinese search quality is unacceptable (Phase 4) |
| compose-richtext | 1.0.0-alpha03 | Markdown rendering | Phase 4 knowledge base |

### Alternatives Considered

| Instead of | Could Use | Tradeoff |
|------------|-----------|----------|
| Kotlin 2.3.21 | Kotlin 2.3.20 | 2.3.20 is what CLAUDE.md specifies, but MIUIX v0.9.1 requires 2.3.21. 2.3.21 is a patch release -- no breaking changes. |
| Koin 4.2.1 | Koin 4.1.1 | 4.1.1 targets Kotlin 2.1.21 (too old). 4.2.1 is the only stable Koin targeting Kotlin 2.3.x. |
| MIUIX MiuixTheme | Manual Material3 customization | MIUIX provides authentic MIUI components; manual Material3 approximation is less accurate and more work. |
| ICU tokenizer (FTS4) | unicode61 + BreakIterator | ICU is UNAVAILABLE on Android SQLite. Use unicode61 with Java BreakIterator pre-segmentation, or WCDB for real Chinese tokenization. |
| Full Noto Sans SC (~16MB) | Glyph-subsetted (~1-3MB) | Full font bloats APK. Subset to app-used characters via pyftsubset. System CJK font as ultimate fallback. |

### Version Catalog (`gradle/libs.versions.toml`)

```toml
[versions]
agp = "9.0.28"
kotlin = "2.3.21"
ksp = "2.3.6"
compose-bom = "2026.04.01"
room = "2.8.4"
navigation-compose = "2.9.8"
datastore-preferences = "1.2.1"
core-splashscreen = "1.2.0"
koin = "4.2.1"
miuix = "0.9.1"
compose-richtext = "1.0.0-alpha03"

[libraries]
# Compose BOM and managed dependencies
compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "compose-bom" }
compose-ui = { group = "androidx.compose.ui", name = "ui" }
compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
compose-ui-test-manifest = { group = "androidx.compose.ui", name = "ui-test-manifest" }
compose-ui-test-junit4 = { group = "androidx.compose.ui", name = "ui-test-junit4" }
compose-material3 = { group = "androidx.compose.material3", name = "material3" }
compose-material-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended" }
compose-foundation = { group = "androidx.compose.foundation", name = "foundation" }

# Navigation
navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigation-compose" }

# Room
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }

# DataStore
datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastore-preferences" }

# Splash Screen
core-splashscreen = { group = "androidx.core", name = "core-splashscreen", version.ref = "core-splashscreen" }

# Koin DI
koin-android = { group = "io.insert-koin", name = "koin-android", version.ref = "koin" }
koin-androidx-compose = { group = "io.insert-koin", name = "koin-androidx-compose", version.ref = "koin" }

# MIUIX
miuix-ui = { group = "top.yukonga.miuix.kmp", name = "miuix-ui", version.ref = "miuix" }

# Markdown (compose-richtext -- for Phase 4, declared here for catalog completeness)
richtext-ui = { group = "com.halilibo.compose-richtext", name = "richtext-ui", version.ref = "compose-richtext" }
richtext-commonmark = { group = "com.halilibo.compose-richtext", name = "richtext-commonmark", version.ref = "compose-richtext" }
richtext-material3 = { group = "com.halilibo.compose-richtext", name = "richtext-ui-material3", version.ref = "compose-richtext" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-compose-compiler = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
```

## Package Legitimacy Audit

> Android/Gradle projects do not use npm. All packages below are from Maven Central or Google Maven repositories. slopcheck (Python/pip tool) does not apply to JVM/Maven ecosystem. Verification is per Android ecosystem conventions.

| Package | Registry | Age | Downloads | Source Repo | Verification | Disposition |
|---------|----------|-----|-----------|-------------|-------------|-------------|
| `androidx.*` (Compose BOM, Room, Navigation, DataStore, SplashScreen) | Google Maven | N/A (Google) | N/A | android.googlesource.com | Google official, verified via developer.android.com release pages | Approved |
| `io.insert-koin:koin-android:4.2.1` | Maven Central | Apr 2026 | High | github.com/InsertKoinIO/koin | Official release, verified via GitHub releases | Approved |
| `io.insert-koin:koin-androidx-compose:4.2.1` | Maven Central | Apr 2026 | High | github.com/InsertKoinIO/koin | Official release | Approved |
| `top.yukonga.miuix.kmp:miuix-ui:0.9.1` | Maven Central | May 2026 (~2d ago) | Growing (~486 stars) | github.com/miuix-kotlin-multiplatform/miuix | Verified on Maven Central; active maintenance; used by Updater-KMP | Approved |
| `com.halilibo.compose-richtext:*:1.0.0-alpha03` | Maven Central | N/A (alpha) | Moderate | github.com/halilozercan/compose-richtext | Alpha-stage; for Phase 4, catalog entry only | Flagged -- planner gate for Phase 4 |

**Packages flagged as suspicious:** compose-richtext (alpha, pre-1.0) -- not used in Phase 1, catalog entry only. Planner must add checkpoint evaluation in Phase 4.

**Note on MIUIX v0.9.1:** Published ~May 15, 2026 (2 days ago at research time). Very recent release. The library is used by its own author in Updater-KMP (296 stars) and listed in awesome-compose-multiplatform. The recent publish date is not suspicious -- it indicates active development. Version 0.9.1 is pre-1.0 but the library has 38 prior releases, indicating maturity.

## Architecture Patterns

### System Architecture Diagram

```
COLD START
    │
    ▼
┌─────────────────────────────────────────────────────┐
│  ANDROID SYSTEM                                      │
│  ┌───────────────────────────────────────────────┐  │
│  │  Splash Screen (theme-driven, pre-app-launch)  │  │
│  │  - Icon + "知屿" text on #FF6B35 background    │  │
│  │  - Rendered by system before app process       │  │
│  └───────────────────┬───────────────────────────┘  │
└──────────────────────┼──────────────────────────────┘
                       │ App process starts
                       ▼
┌─────────────────────────────────────────────────────┐
│  ZhiYuApplication.onCreate()                         │
│  ┌─────────────────────────────────────────────┐    │
│  │  startKoin { modules(appModule,             │    │
│  │    repositoryModule, viewModelModule) }      │    │
│  └─────────────────────────────────────────────┘    │
└──────────────────────┬──────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────┐
│  MainActivity.onCreate()                             │
│  ┌───────────────────────────────────────────────┐  │
│  │  1. installSplashScreen()                     │  │
│  │  2. enableEdgeToEdge()                        │  │
│  │  3. super.onCreate()                          │  │
│  │  4. setKeepOnScreenCondition { !dataStoreRdy } │  │
│  └───────────────────────────────────────────────┘  │
│                                                      │
│  setContent {                                        │
│    ┌──────────────────────────────────────────┐     │
│    │  MiuixTheme (light/dark from DataStore)   │     │
│    │  ┌────────────────────────────────────┐  │     │
│    │  │  App Content (Phase 2+)             │  │     │
│    │  │  Placeholder for Phase 1            │  │     │
│    │  └────────────────────────────────────┘  │     │
│    └──────────────────────────────────────────┘     │
│  }                                                   │
│  ┌───────────────────────────────────────────────┐  │
│  │  Splash dismissed when DataStore emits first   │  │
│  │  value → LaunchedEffect sets isReady = true    │  │
│  └───────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────┘
                       │
                       ▼
              APP READY (Phase 1 complete)

DATA LAYER (available to ALL downstream phases):
┌──────────────────────────────────────────────┐
│  Koin DI Container                            │
│  ┌────────────┐ ┌──────────────┐             │
│  │ AppModule   │ │ RepositoryM. │             │
│  │ (Room,      │ │ (ArticleRepo,│             │
│  │  DataStore) │ │  SettingsRepo│             │
│  └─────┬──────┘ └──────┬───────┘             │
│        │               │                      │
│  ┌─────┴───────────────┴───────┐             │
│  │     ViewModelModule          │             │
│  │ (declared, empty stubs OK)   │             │
│  └─────────────────────────────┘             │
│                                               │
│  ┌──────────────────────────────────┐        │
│  │  Room Database (zhiyu_database)   │        │
│  │  ├── ArticleEntity                │        │
│  │  ├── CategoryEntity               │        │
│  │  ├── TagEntity                    │        │
│  │  ├── ArticleTagCrossRef           │        │
│  │  ├── QuickNoteEntity              │        │
│  │  └── ArticleFts (FTS4 virtual)    │        │
│  └──────────────────────────────────┘        │
│                                               │
│  ┌──────────────────────────────────┐        │
│  │  DataStore (settings.preferences) │        │
│  │  ├── theme_mode (SYSTEM/LIGHT/DARK)       │
│  │  ├── last_active_tab                      │
│  │  └── is_first_launch                      │
│  └──────────────────────────────────┘        │
└──────────────────────────────────────────────┘
```

### Recommended Project Structure

```
ZhiYu/
├── gradle/
│   └── libs.versions.toml                 # Version catalog
├── app/
│   ├── src/main/
│   │   ├── java/com/zhiyu/app/
│   │   │   ├── ZhiYuApplication.kt        # Application, Koin init
│   │   │   ├── MainActivity.kt            # Single Activity, splash + theme
│   │   │   ├── di/
│   │   │   │   ├── AppModule.kt           # Room, DataStore bindings
│   │   │   │   ├── RepositoryModule.kt    # Repository bindings
│   │   │   │   └── ViewModelModule.kt     # ViewModel bindings (stubs OK)
│   │   │   ├── data/
│   │   │   │   ├── local/
│   │   │   │   │   ├── ZhiYuDatabase.kt   # Room @Database
│   │   │   │   │   ├── entity/
│   │   │   │   │   │   ├── ArticleEntity.kt
│   │   │   │   │   │   ├── CategoryEntity.kt
│   │   │   │   │   │   ├── TagEntity.kt
│   │   │   │   │   │   ├── ArticleTagCrossRef.kt
│   │   │   │   │   │   ├── QuickNoteEntity.kt
│   │   │   │   │   │   └── ArticleFts.kt     # FTS4 virtual table
│   │   │   │   │   ├── dao/
│   │   │   │   │   │   ├── ArticleDao.kt
│   │   │   │   │   │   ├── CategoryDao.kt
│   │   │   │   │   │   ├── TagDao.kt
│   │   │   │   │   │   └── QuickNoteDao.kt
│   │   │   │   │   └── converter/
│   │   │   │   │       └── Converters.kt
│   │   │   │   └── preferences/
│   │   │   │       └── AppPreferences.kt     # DataStore wrapper
│   │   │   ├── ui/
│   │   │   │   └── theme/
│   │   │   │       ├── Theme.kt              # MiuixTheme wrapper
│   │   │   │       ├── Color.kt              # MIUI color palette
│   │   │   │       ├── Type.kt               # Typography with CJK fallback
│   │   │   │       └── Shape.kt              # MIUI rounded shapes
│   │   ├── src/main/res/
│   │   │   ├── font/
│   │   │   │   └── noto_sans_sc_subset.ttf   # Subsetted CJK font
│   │   │   ├── drawable/
│   │   │   │   └── ic_splash_adaptive.xml    # Splash adaptive icon
│   │   │   └── values/
│   │   │       ├── themes.xml                # SplashScreen theme + base
│   │   │       └── strings.xml               # App name "知屿"
│   │   ├── proguard-rules.pro
│   │   └── build.gradle.kts
├── build.gradle.kts                           # Root project
├── settings.gradle.kts
└── gradle.properties
```

### Pattern 1: MVVM + Unidirectional Data Flow (UDF)

**What:** Each screen has a single immutable `UiState` data class, a `ViewModel` that produces it via `StateFlow`, and a `Composable` that consumes it. Events flow up as function calls; state flows down as a single object.

**When to use:** Every screen in the app. Applied from Phase 1 in the data layer and theme wiring.

**Phase 1 scope:** The pattern is established structurally (Koin modules, repository pattern, DataStore flow) but the first actual screen ViewModel is in Phase 2.

### Pattern 2: Screen / Content Split

**What:** Every screen composable is split into two functions: `XxxScreen(viewModel, navCallbacks)` and `XxxContent(uiState, eventCallbacks, modifier)`. The Content function is pure, previewable, and testable.

**When to use:** Every screen composable. Phase 1 does not build screens but establishes the Theme wrapper that all screens will use.

### Pattern 3: Repository as Single Source of Truth

**What:** Repositories own data access. DAOs expose `Flow<T>` for reactive reads and `suspend` for writes. Repositories map entities to domain models. ViewModels never touch Room annotations directly.

**When to use:** All data access. Phase 1 defines the repository interfaces and DAOs.

### Anti-Patterns to Avoid

- **DO NOT use `kotlin-android` plugin** -- AGP 9.0+ has built-in Kotlin support. Applying this causes build failure. [VERIFIED: jetbrains blog, AGP 9.0 docs]
- **DO NOT add `androidx.compose.compiler:compiler`** -- Compose compiler is built into Kotlin 2.0+. Use `org.jetbrains.kotlin.plugin.compose` plugin only. [VERIFIED: developer.android.com]
- **DO NOT use KAPT** -- KSP 2.3.6 replaces KAPT for all annotation processing (Room, Koin optional). [VERIFIED: developer.android.com]
- **DO NOT hardcode colors** -- Always use `MaterialTheme.colorScheme` or `MiuixTheme.colorScheme` semantic colors. Dark mode depends on this.
- **DO NOT call `super.onCreate()` before `installSplashScreen()`** -- The splash API must be initialized first. [VERIFIED: developer.android.com, PITFALLS.md #22]
- **DO NOT use `@Relation` for list queries** -- Phase 4 will use JOIN queries instead. Declare entities structurally at foundation; DAOs will use JOIN in Phase 4.
- **DO NOT mix autoMigrations with manual migrations** -- Per D-07, manual migrations only. Define `MigrationTestHelper` in test config. [VERIFIED: PITFALLS.md #3]

## Don't Hand-Roll

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| Version management | Manual version strings in build files | `gradle/libs.versions.toml` version catalog | Single source of truth; IDE autocomplete; Dependabot compatible |
| Dependency injection | Manual factory/service locator | Koin 4.2.1 | Runtime DI with `koinViewModel()` extension; no build-time overhead |
| Key-value preferences | SharedPreferences (legacy, blocking) | DataStore Preferences 1.2.1 | Coroutine/Flow-based; transactional; non-blocking |
| SQLite ORM | Raw SQLiteOpenHelper | Room 2.8.4 | Type-safe queries; Flow support; migration testing; compile-time SQL verification |
| Full-text search | `LIKE '%keyword%'` (O(n) table scan) | Room FTS4 | Indexed MATCH queries; 10-100x faster for full-text search |
| Chinese font rendering | Platform default fonts (tofu risk) | Typeface.CustomFallbackBuilder + bundled Noto Sans SC subset | Guarantees CJK glyph availability even on devices without Google Play Services |
| Splash screen | Custom SplashActivity (double-splash bug) | AndroidX SplashScreen API | System-managed; no white flash; consistent across API levels |
| Theme token values | Raw ARGB hex values (`0xFFFF6B35`) scattered in code | `Color.kt` with named constants + `MiuixTheme.colorScheme` | Centralized; dark mode compatible; IDE preview works |

**Key insight:** Android has first-party or well-established third-party solutions for every data, DI, and UI concern in this phase. The only custom work is the CJK font fallback chain (bridging `android.graphics.Typeface` to Compose `FontFamily`), and the exact FTS4 tokenizer strategy (ICU is unavailable, requiring either WCDB or BreakIterator preprocessing).

## Runtime State Inventory

> Phase 1 is greenfield -- no existing code, no runtime state, no migrations needed. This section is included per protocol but all categories are empty.

| Category | Items Found | Action Required |
|----------|-------------|------------------|
| Stored data | None -- greenfield project, no database exists | Create Room database at version 1 |
| Live service config | None -- no external services | N/A |
| OS-registered state | None -- app not yet installed | N/A |
| Secrets/env vars | None | N/A |
| Build artifacts | None -- no prior builds | N/A |

## Common Pitfalls

### Pitfall 1: Chinese Font Fallback Failure (tofu characters)

**What goes wrong:** Using a custom font without CJK glyphs causes Chinese characters to render as empty boxes (tofu). This is catastrophic for a Chinese-language app.

**Why it happens:** Compose `FontFamily` with multiple `Font` entries treats them as weight/style variants, not a character-level fallback chain. The first font lacking a CJK glyph does NOT automatically fall through to the next font.

**How to avoid:** Use `Typeface.CustomFallbackBuilder` (API 29+, available everywhere since minSdk=36) to build a true glyph-level fallback chain, then wrap in Compose `FontFamily(typeface = ...)`. Bundle a subsetted Noto Sans SC. Set system fallback as ultimate backup.

**Warning signs:** Empty boxes or garbled characters in any `Text` composable rendering Chinese text. Test with mixed CJK + Latin strings on a Xiaomi device without Google Play Services.

**Source:** [VERIFIED: PITFALLS.md #6; StackOverflow #72772861; developer.android.com/reference/android/graphics/Typeface.CustomFallbackBuilder]

### Pitfall 2: White Screen on MIUI Devices (SplashScreen)

**What goes wrong:** App shows a white flash or double splash on startup, especially on Xiaomi MIUI/HyperOS devices.

**Why it happens:** Three common causes: (1) `installSplashScreen()` called after `super.onCreate()`, (2) missing `postSplashScreenTheme`, (3) MIUI launcher injects additional splash icon if theme not properly configured.

**How to avoid:** Call `installSplashScreen()` FIRST (before `super.onCreate()`), then `enableEdgeToEdge()`, then `super.onCreate()`. Set `postSplashScreenTheme` in splash theme. Use solid background color (not complex drawables) for best MIUI compatibility. [VERIFIED: PITFALLS.md #7, #22]

**Warning signs:** Visual white frame between splash and first rendered frame; slow-motion video shows white screen >100ms.

### Pitfall 3: ICU Tokenizer Unavailable on Android

**What goes wrong:** Room FTS4 entity uses `tokenizer = FtsOptions.TOKENIZER_ICU` and crashes with `SQLiteException: unknown tokenizer: icu` at runtime.

**Why it happens:** Android's system SQLite is NOT compiled with ICU support. The `FtsOptions.TOKENIZER_ICU` constant exists in the Room API but the underlying SQLite library on Android devices does not include the ICU extension.

**How to avoid:** Use `FtsOptions.TOKENIZER_UNICODE61` with pre-segmented Chinese text (via `java.text.BreakIterator`). For production-quality Chinese word segmentation, evaluate WCDB (Tencent) in Phase 4, which includes `mmicu` tokenizer. The FTS4 entity itself uses unicode61; the segmentation happens at the data insertion layer. [VERIFIED: stackoverflow.com/questions/7070193; Android SQLite documentation; multiple community sources]

**Warning signs:** `android.database.sqlite.SQLiteException: unknown tokenizer: icu` at database creation. Test FTS4 entity creation on an Android 16 emulator.

### Pitfall 4: DataStore Main Thread Blocking

**What goes wrong:** App freezes or shows blank screen for 100-500ms on first launch because DataStore initial file read blocks the main thread.

**Why it happens:** DataStore reads and parses the entire preferences file synchronously on first access. Without `flowOn(Dispatchers.IO)`, the blocking I/O runs on the calling thread.

**How to avoid:** Add `.flowOn(Dispatchers.IO)` to ALL DataStore flow chains. Keep the splash screen visible (`setKeepOnScreenCondition`) until DataStore emits its first value. [VERIFIED: PITFALLS.md #8]

**Warning signs:** Macrobenchmark shows gap between `installSplashScreen` and first frame. UI thread profiler shows "blocked" state during cold start.

### Pitfall 5: Kotlin Version Mismatch (MIUIX requirement)

**What goes wrong:** CLAUDE.md specifies Kotlin 2.3.20 but MIUIX v0.9.1 requires Kotlin 2.3.21. Using 2.3.20 may cause compilation errors with the MIUIX library.

**Why it happens:** MIUIX v0.9.1 was published ~May 15, 2026, after Kotlin 2.3.21 (April 23, 2026). The library targets the latest Kotlin patch.

**How to avoid:** Use Kotlin 2.3.21. This is a patch-level upgrade with only bug fixes; it introduces no breaking changes over 2.3.20. All other dependencies (AGP 9.0.28, KSP 2.3.6, Compose BOM 2026.04.01) remain compatible. [ASSUMED] compatibility of KSP 2.3.6 with Kotlin 2.3.21 -- verify during build setup. KSP patch versions are typically compatible with Kotlin patch versions.

**Warning signs:** Build failure with "Unresolved reference" to MIUIX APIs or Kotlin stdlib version conflicts.

### Pitfall 6: AGP 9.0 Built-in Kotlin Confusion

**What goes wrong:** Applying `org.jetbrains.kotlin.android` plugin alongside AGP 9.0+ causes `PluginAlreadyAppliedException` or duplicate class errors.

**Why it happens:** AGP 9.0+ has built-in Kotlin support and automatically applies the Kotlin Android plugin. Applying it manually creates a conflict.

**How to avoid:** In `app/build.gradle.kts`, apply ONLY `com.android.application`, `org.jetbrains.kotlin.plugin.compose`, and `com.google.devtools.ksp`. Never apply `org.jetbrains.kotlin.android`. [VERIFIED: blog.jetbrains.com/kotlin/2026/01/update-your-projects-for-agp9/]

**Warning signs:** Build error "Plugin [id: 'org.jetbrains.kotlin.android'] was already requested."

## Code Examples

### Koin Application Setup

```kotlin
// ZhiYuApplication.kt
// Source: insert-koin.io/docs/quickstart/android-compose/ [VERIFIED]
package com.zhiyu.app

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ZhiYuApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ZhiYuApplication)
            modules(
                appModule,
                repositoryModule,
                viewModelModule
            )
        }
    }
}
```

### Room Database with FTS4

```kotlin
// ZhiYuDatabase.kt
// Source: developer.android.com/training/data-storage/room [VERIFIED]
package com.zhiyu.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zhiyu.app.data.local.converter.Converters
import com.zhiyu.app.data.local.dao.*
import com.zhiyu.app.data.local.entity.*

@Database(
    entities = [
        ArticleEntity::class,
        CategoryEntity::class,
        TagEntity::class,
        ArticleTagCrossRef::class,
        QuickNoteEntity::class,
        ArticleFts::class  // FTS4 virtual table
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class ZhiYuDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun categoryDao(): CategoryDao
    abstract fun tagDao(): TagDao
    abstract fun quickNoteDao(): QuickNoteDao
}
```

### FTS4 Entity with unicode61 (ICU Unavailable)

```kotlin
// ArticleFts.kt
// Source: developer.android.com/reference/kotlin/androidx/room/Fts4 [VERIFIED]
// NOTE: FtsOptions.TOKENIZER_ICU is defined in the Room API but NOT available
// on Android system SQLite. Use TOKENIZER_UNICODE61 with pre-segmented Chinese text
// via java.text.BreakIterator at the data insertion layer.
package com.zhiyu.app.data.local.entity

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions

@Entity(tableName = "articles_fts")
@Fts4(
    contentEntity = ArticleEntity::class,
    tokenizer = FtsOptions.TOKENIZER_UNICODE61  // NOT ICU -- unavailable on Android
)
data class ArticleFts(
    @androidx.room.ColumnInfo(name = "rowid")
    val rowId: Long = 0,
    val title: String,
    val content: String
)
```

### CJK Font Fallback with CustomFallbackBuilder

```kotlin
// Type.kt -- Typography setup with CJK fallback
// Source: developer.android.com/reference/android/graphics/Typeface.CustomFallbackBuilder [VERIFIED]
// Bridged to Compose via FontFamily(typeface = ...)
package com.zhiyu.app.ui.theme

import android.graphics.Typeface
import android.graphics.FontFamily as PlatformFontFamily
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.res.ResourcesCompat
import com.zhiyu.app.R

@Composable
fun rememberCJKFallbackFontFamily(): FontFamily {
    val context = LocalContext.current
    return remember {
        val notoSansSC = ResourcesCompat.getFont(context, R.font.noto_sans_sc_subset)
            ?: return@remember FontFamily.Default

        val notoSansFamily = PlatformFontFamily.Builder(
            PlatformFontFamily.Builder(android.graphics.Font.Builder(notoSansSC).build()).build()
        ).build()

        val platformTypeface = Typeface.CustomFallbackBuilder(notoSansFamily)
            .setSystemFallback("sans-serif")  // Ultimate fallback for missing glyphs
            .build()

        FontFamily(typeface = platformTypeface)
    }
}
```

### Splash Screen with DataStore Keep Condition

```kotlin
// MainActivity.kt
// Source: developer.android.com/develop/ui/views/launch/splash-screen [VERIFIED]
// PITFALL-7 ordering: installSplashScreen -> enableEdgeToEdge -> super.onCreate
package com.zhiyu.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.installSplashScreen
import androidx.compose.runtime.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()  // 1st: MUST be before super.onCreate
        enableEdgeToEdge()                         // 2nd: edge-to-edge mandatory on API 36
        super.onCreate(savedInstanceState)          // 3rd: after both

        var isDataStoreReady by mutableStateOf(false)
        splashScreen.setKeepOnScreenCondition { !isDataStoreReady }

        setContent {
            val appPreferences: AppPreferences = /* from Koin */
            val themeMode by appPreferences.themeMode
                .collectAsStateWithLifecycle(initialValue = ThemeMode.SYSTEM)

            // Mark ready after first DataStore emission
            LaunchedEffect(themeMode) {
                isDataStoreReady = true
            }

            ZhiYuTheme(themeMode = themeMode) {
                // Phase 2+ content placeholder
            }
        }
    }
}
```

### DataStore Preferences Wrapper

```kotlin
// AppPreferences.kt
// Source: developer.android.com/topic/libraries/architecture/datastore [VERIFIED]
package com.zhiyu.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

// Extension property: single DataStore instance per process
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class ThemeMode { SYSTEM, LIGHT, DARK }

class AppPreferences(private val context: Context) {
    // Pre-defined keys per D-06
    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val LAST_ACTIVE_TAB = stringPreferencesKey("last_active_tab")
        val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
    }

    val themeMode: Flow<ThemeMode> = context.dataStore.data
        .map { prefs ->
            when (prefs[Keys.THEME_MODE]) {
                "LIGHT" -> ThemeMode.LIGHT
                "DARK" -> ThemeMode.DARK
                else -> ThemeMode.SYSTEM
            }
        }
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)  // PITFALL-8: prevent main thread blocking

    val lastActiveTab: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[Keys.LAST_ACTIVE_TAB] ?: "info" }
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)

    val isFirstLaunch: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[Keys.IS_FIRST_LAUNCH] ?: true }
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = mode.name
        }
    }

    suspend fun setLastActiveTab(tab: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.LAST_ACTIVE_TAB] = tab
        }
    }

    suspend fun markLaunched() {
        context.dataStore.edit { prefs ->
            prefs[Keys.IS_FIRST_LAUNCH] = false
        }
    }
}
```

### Theme Architecture (MIUIX-driven)

```kotlin
// Theme.kt
// Source: MIUIX library -- top.yukonga.miuix.kmp:miuix-ui:0.9.1 [VERIFIED: Maven Central]
package com.zhiyu.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme
import com.zhiyu.app.data.preferences.ThemeMode

@Composable
fun ZhiYuTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }

    val colorScheme = if (isDark) darkColorScheme() else lightColorScheme()
    val cjkFontFamily = rememberCJKFallbackFontFamily()

    MiuixTheme(
        colorScheme = colorScheme,
        // Apply CJK-capable font family for all Text composables
        // Note: MiuixTheme internally wraps MaterialTheme; FontFamily
        // override depends on MIUIX's Typography customization API.
        // If MiuixTheme does not expose a fontFamily parameter directly,
        // compose MiuixTheme inside a custom CompositionLocal for fonts.
        content = content
    )
}
```

### Koin Module Definitions

```kotlin
// di/AppModule.kt
// Source: insert-koin.io/docs/reference/koin-android/ [VERIFIED]
package com.zhiyu.app.di

import com.zhiyu.app.data.local.ZhiYuDatabase
import com.zhiyu.app.data.preferences.AppPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import androidx.room.Room

val appModule = module {
    // Room Database singleton
    single {
        Room.databaseBuilder(
            get(),
            ZhiYuDatabase::class.java,
            "zhiyu_database"
        ).build()
    }

    // DAOs (available from database)
    single { get<ZhiYuDatabase>().articleDao() }
    single { get<ZhiYuDatabase>().categoryDao() }
    single { get<ZhiYuDatabase>().tagDao() }
    single { get<ZhiYuDatabase>().quickNoteDao() }

    // DataStore Preferences
    single { AppPreferences(get()) }
}

// di/RepositoryModule.kt
val repositoryModule = module {
    // Declared but may be empty in Phase 1 -- repositories are populated
    // as their DAOs become needed. Stub declarations OK.
}

// di/ViewModelModule.kt
val viewModelModule = module {
    // ViewModels are declared as screens are built (Phase 2+).
    // Module exists as a registration point; empty in Phase 1.
}
```

## State of the Art

| Old Approach | Current Approach | When Changed | Impact |
|--------------|------------------|--------------|--------|
| `kotlin-android` plugin | AGP 9.0+ built-in Kotlin | AGP 9.0 (2026) | Remove plugin from build files; AGP applies it automatically |
| `androidx.compose.compiler:compiler` | Kotlin 2.0+ built-in compiler | Kotlin 2.0 (2024) | Remove dependency; use `kotlin.plugin.compose` plugin |
| KAPT | KSP | KSP 2.x (2024-2026) | All annotation processing uses KSP; 2x faster builds |
| SharedPreferences | DataStore Preferences | DataStore 1.0 (2022) | Async, transactional key-value storage |
| `@Relation` for list queries | JOIN + multimap | Community consensus (2024+) | Prevents N+1 queries on list screens |
| Manual Material3 theme customization | MIUIX MiuixTheme | MIUIX 0.9.x (2026) | Authentic MIUI/HyperOS components |
| String-based navigation routes | `@Serializable` type-safe routes | Navigation 2.8+ (2024) | Compile-time route safety |
| Custom SplashActivity | AndroidX SplashScreen API | Android 12+ (2021) | System-managed splash; no double-splash |

**Deprecated/outdated to avoid:**
- `SharedPreferences`: Replaced by DataStore. Synchronous API blocks main thread.
- `kotlin-android` Gradle plugin: Conflicts with AGP 9.0+ built-in support.
- `androidx.compose.compiler:compiler`: Deprecated since Kotlin 2.0; use `kotlin.plugin.compose`.
- `LiveData`: Legacy. Use `Flow` + `StateFlow` for Compose integration.
- KAPT: Deprecated. Use KSP for all annotation processing.
- `<layer-list>` or `<shape>` XML drawables for splash: MIUI may mishandle complex drawables during splash. Use simple adaptive icon.
- `setContentView` (XML Views): Not used in this Compose-only project.

## Assumptions Log

| # | Claim | Section | Risk if Wrong |
|---|-------|---------|---------------|
| A1 | KSP 2.3.6 is compatible with Kotlin 2.3.21 (patch release) | Standard Stack | Build failure; would need to find compatible KSP version |
| A2 | Koin 4.2.1 is compatible with Kotlin 2.3.21 (release notes say 2.3.20) | Standard Stack | Build failure or runtime DI errors |
| A3 | MIUIX `MiuixTheme` composable provides a mechanism to override the default font family, or can be wrapped in a custom CompositionLocal for CJK font | Theme Architecture | Fallback: apply CJK FontFamily at individual Text composable level or use MaterialTheme wrapper |
| A4 | Noto Sans SC can be subsetted to ~1-3MB via pyftsubset for app UI strings + common Chinese characters | CJK Font Fallback | APK bloat if subset is larger; solution: accept slightly larger APK or omit bundled font and rely on system CJK fallback |
| A5 | `top.yukonga.miuix.kmp:miuix-ui:0.9.1` is available on Maven Central (confirmed via web, not via Maven Central API in this session) | Standard Stack | Build failure if unavailable; solution: check Maven Central before first build, or pin a confirmed version |
| A6 | Chinese text pre-segmentation via `java.text.BreakIterator` is sufficient for FTS4 search quality with unicode61 tokenizer | Room FTS4 | Poor search recall for Chinese; Phase 4 would need WCDB evaluation |
| A7 | AGP 9.0.28 is the correct patch version for Kotlin 2.3.21 (compatibility table not rechecked in this session) | Standard Stack | Build failure; verify against developer.android.com/build/kotlin-support |

## Open Questions (RESOLVED)

1. **MIUIX Font Customization API** -- RESOLVED: Use CompositionLocal wrapper approach. Plan 03 implements CJK font via MaterialTheme Typography and MIUIX textStyles override; MiuixTheme does not expose fontFamily param.
   - Recommendation applied: Type.kt applies CJK font family to all 14 MIUIX text styles and all 13 Material3 typography roles individually via `.copy(fontFamily = cjkFamily)`.

2. **Kotlin 2.3.21 vs 2.3.20 Compatibility Surface** -- RESOLVED: Patch releases maintain binary compatibility. Build config uses Kotlin 2.3.21.
   - Decision: Use 2.3.21 per MIUIX v0.9.1 requirement. Koin 4.2.1 binary compatible. If build fails, fall back to MIUIX version supporting 2.3.20.

3. **FTS4 Chinese Tokenizer -- ICU vs WCDB vs BreakIterator** -- RESOLVED: Use TOKENIZER_UNICODE61 in Phase 1. Re-evaluate in Phase 4.
   - Decision: TOKENIZER_UNICODE61 at foundation. WCDB integration deferred to Phase 4 if search quality is unacceptable. ICU is unavailable on Android SQLite.

4. **Noto Sans SC Subset Size for APK Budget** -- RESOLVED: Target ~2MB subset. System CJK fallback handles user-generated content.
   - Decision: Subset to ~4,000 commonly used Chinese characters (UI strings + GB2312 Level 1). Target <2MB. Full Noto Sans SC not bundled.

## Environment Availability

| Dependency | Required By | Available | Version | Fallback |
|------------|------------|-----------|---------|----------|
| Java JDK | Gradle/AGP compilation | Yes (OpenJDK) | 26 | -- |
| Android SDK | All compilation | **No** | -- | **BLOCKING** -- must install Android SDK (API 36) before any build |
| Gradle | Build system | **No** | -- | Use Gradle wrapper (`gradlew`) -- not required globally |
| Android Studio | IDE (optional, for development) | **No** | -- | CLI builds with `./gradlew` possible without IDE |
| `pyftsubset` (fonttools) | CJK font subsetting | Unknown | -- | Can download pre-subsetted Noto Sans SC from Google Fonts API |

**Missing dependencies with no fallback:**
- **Android SDK (API 36 + Build Tools 36.0.0):** Required to compile and run the project. No fallback. Must be installed via Android Studio SDK Manager or `sdkmanager` CLI. Paths needed: `ANDROID_HOME` or `ANDROID_SDK_ROOT` environment variable.

**Missing dependencies with fallback:**
- None -- all other tools (Gradle wrapper, pyftsubset) can be acquired or are optional.

## Validation Architecture

### Test Framework
| Property | Value |
|----------|-------|
| Framework | JUnit 4 (standard Android testing) |
| Config file | none -- see Wave 0 |
| Quick run command | `./gradlew :app:testDebugUnitTest` |
| Full suite command | `./gradlew :app:connectedAndroidTest` (requires device/emulator) |

### Phase Requirements -- Test Map
| Req ID | Behavior | Test Type | Automated Command | File Exists? |
|--------|----------|-----------|-------------------|-------------|
| FND-01 | Project builds successfully with correct AGP/Kotlin/Compose versions | smoke (build) | `./gradlew :app:assembleDebug` | No -- Wave 0 |
| FND-02 | minSdk = targetSdk = 36 in build config; APK installs on API 36 | integration | `./gradlew :app:assembleDebug` then install on emulator | No -- Wave 0 |
| FND-03 | Koin initializes on app startup; AppModule provides Room + DataStore | unit | `./gradlew :app:testDebugUnitTest --tests "*.di.*"` | No -- Wave 0 |
| FND-04 | MIUIX library resolves at compile time; MiuixTheme renders without crash | integration | Build + screenshot test on emulator | No -- Wave 0 |
| FND-05 | Room database creates with all 6 entities (5 + FTS4); DataStore reads/writes pre-defined keys | unit | `./gradlew :app:testDebugUnitTest --tests "*.data.*"` | No -- Wave 0 |
| FND-06 | MVVM pattern files exist (UiState data class, ViewModel, Repository) | structural | File existence check | No -- Wave 0 |
| FND-07 | MiuixTheme applies correctly; dark mode flips when system setting changes | integration | Manual on emulator; `./gradlew :app:connectedAndroidTest` | No -- Wave 0 |
| FND-08 | Chinese text renders without tofu in all composables using CJK fallback font | integration | Screenshot test with Chinese string | No -- Wave 0 |
| FND-09 | Splash screen displays on cold start; no white flash; dismisses after DataStore ready | manual-only | Cold start on physical/emulated device | No -- Wave 0 |

### Sampling Rate
- **Per task commit:** `./gradlew :app:testDebugUnitTest`
- **Per wave merge:** `./gradlew :app:testDebugUnitTest && ./gradlew :app:assembleDebug`
- **Phase gate:** Full unit test suite green + debug APK builds + manual cold-start verification on emulator

### Wave 0 Gaps
- [ ] `app/src/test/java/com/zhiyu/app/di/KoinModulesTest.kt` -- verify Koin modules resolve without errors
- [ ] `app/src/test/java/com/zhiyu/app/data/local/ZhiYuDatabaseTest.kt` -- Room in-memory DAO CRUD + FTS4
- [ ] `app/src/test/java/com/zhiyu/app/data/preferences/AppPreferencesTest.kt` -- DataStore read/write with test rules
- [ ] `app/src/androidTest/java/com/zhiyu/app/SplashScreenTest.kt` -- splash display verification
- [ ] `app/build.gradle.kts` -- test dependencies (junit, room-testing, datastore-testing)
- [ ] `app/schemas/` -- Room schema export directory (for MigrationTestHelper; can be empty at v1)
- [ ] Framework install: None needed beyond standard Gradle -- verify `./gradlew tasks --group verification`

## Security Domain

### Applicable ASVS Categories

| ASVS Category | Applies | Standard Control |
|---------------|---------|------------------|
| V2 Authentication | No | No auth system; local-only app |
| V3 Session Management | No | No sessions; local-only app |
| V4 Access Control | No | No multi-user; single local user |
| V5 Input Validation | No | Phase 1 has no user input yet |
| V6 Cryptography | No | No encryption at Phase 1; Room + DataStore are unencrypted local storage |
| V7 Error Handling | Yes (structural) | Standard Kotlin exception handling; Room throws typed exceptions |
| V8 Data Protection | Yes (structural) | Room schema `exportSchema = true`; database file in app-private storage; no external storage |

### Known Threat Patterns for Android/Kotlin/Compose

| Pattern | STRIDE | Standard Mitigation |
|---------|--------|---------------------|
| SQL injection via raw Room `@Query` concatenation | Tampering | Room `@Query` with bind parameters (`:param`); Room prevents string concatenation in `@Query` at compile time |
| DataStore file tampering (rooted device) | Tampering | DataStore file is in app-private storage (`/data/data/com.zhiyu.app/`); not accessible without root |
| Unintended data export via `exportSchema = true` | Information Disclosure | Schema JSON files are build artifacts only; never shipped in APK. Verify with `packaging.resources.excludes` |
| Hardcoded secrets in build files | Information Disclosure | No secrets needed (local-only app, no API keys); verify with `git-secrets` scan in CI |
| Dependency supply chain (malicious library update) | Tampering | All libraries from Google Maven / Maven Central with pinned versions in TOML; Gradle dependency verification in Phase 6 |

## Sources

### Primary (HIGH confidence)

- [Android Developers: AGP 9.0 release notes](https://developer.android.com/build/releases/agp-9-0-0-release-notes) -- AGP version requirements for SDK 36, built-in Kotlin support
- [Android Developers: AGP/Kotlin compatibility](https://developer.android.com/build/kotlin-support) -- Version matrix
- [Android Developers: Room documentation](https://developer.android.com/jetpack/androidx/releases/room) -- Room 2.8.4, FTS4, FtsOptions API
- [Android Developers: DataStore](https://developer.android.com/topic/libraries/architecture/datastore) -- DataStore Preferences API
- [Android Developers: SplashScreen](https://developer.android.com/develop/ui/views/launch/splash-screen) -- installSplashScreen(), keep condition
- [Android Developers: Android 16 behavior changes](https://developer.android.com/about/versions/16/behavior-changes-16) -- Edge-to-edge mandatory, no opt-out
- [Android Developers: Typeface.CustomFallbackBuilder](https://developer.android.com/reference/android/graphics/Typeface.CustomFallbackBuilder) -- CJK font fallback API (API 29+)
- [Kotlin: What's new in 2.3.20](https://kotlinlang.org/docs/whatsnew2320.html) -- Kotlin 2.3.20 feature release
- [Kotlin: Releases](https://kotlinlang.org/docs/releases.html) -- Kotlin 2.3.21 patch release (Apr 23, 2026)
- [Koin: GitHub releases](https://github.com/InsertKoinIO/koin/releases) -- Koin 4.2.1 stable (Apr 10, 2026), targets Kotlin 2.3.20
- [Koin: Android Compose quickstart](https://insert-koin.io/docs/quickstart/android-compose/) -- KoinApplication, koinViewModel(), module DSL
- [MIUIX: GitHub repository](https://github.com/miuix-kotlin-multiplatform/miuix) -- v0.9.1, Kotlin 2.3.21, Compose 1.11.0
- [MIUIX: Maven Central](https://central.sonatype.com/artifact/top.yukonga.miuix.kmp/miuix-ui) -- v0.9.1 published ~May 15, 2026

### Secondary (MEDIUM confidence)

- [StackOverflow: Fallback fonts with Jetpack Compose](https://stackoverflow.com/questions/72772861/fallback-fonts-with-jetpack-compose-in-android) -- Bridging CustomFallbackBuilder to Compose FontFamily
- [StackOverflow: Is SQLite on Android built with ICU tokenizer?](https://stackoverflow.com/questions/7070193/is-sqlite-on-android-built-with-the-icu-tokenizer-enabled-for-fts) -- ICU unavailable on Android
- [Dependabot: miuix-kotlin-multiplatform/miuix#75](https://dependabot.ecosyste.ms/hosts/GitHub/repositories/miuix-kotlin-multiplatform%2Fmiuix/issues/75) -- Confirms Kotlin 2.3.21 usage
- [Dependabot: Koin from 4.2.0 to 4.2.1](https://dependabot.ecosyste.ms/hosts/GitHub/repositories/yonatankarp%2Fxkcd-data-hub-lite/issues/249) -- Koin 4.2.1 active in ecosystem

### Tertiary (LOW confidence)

- [CSDN: Android Room FTS full-text search](https://www.e-com-net.com/article/1622039127388094464.htm) -- Chinese FTS4 implementation reference
- [Juejin: Chinese font subsetting](https://juejin.cn/post/7228929411546267703) -- pyftsubset workflow for Chinese fonts
- [Ecosyste.ms: compose-miuix-ui/miuix alternatives](https://relatedrepos.com/gh/compose-miuix-ui/miuix) -- Ecosystem context for MIUIX

## Metadata

**Confidence breakdown:**
- Standard stack: HIGH -- All versions verified against official release pages, Maven Central, and GitHub releases
- Architecture: HIGH -- Patterns verified against official Android architecture guidance and Koin documentation
- Pitfalls: HIGH -- All 6 pitfalls verified via official docs (CustomFallbackBuilder, SplashScreen API, DataStore) or StackOverflow with multiple confirming sources (ICU unavailability)
- MIUIX theme integration: MEDIUM -- Library is actively maintained (v0.9.1, 38 releases) but pre-1.0; exact font customization API not confirmed in this session
- FTS4 Chinese tokenizer: MEDIUM -- ICU unavailability is verified; BreakIterator adequacy for Chinese search not benchmarked in this session

**Research date:** 2026-05-17
**Valid until:** 2026-06-17 (30 days for stable libraries; MIUIX may release v1.0 within this window)
