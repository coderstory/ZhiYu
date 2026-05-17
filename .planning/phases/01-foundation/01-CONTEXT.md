# Phase 1: Foundation - Context

**Gathered:** 2026-05-17
**Status:** Ready for planning

<domain>
## Phase Boundary

Project scaffolding, build system (AGP 9.0.28, Kotlin 2.3.20, Compose BOM 2026.04.01), version catalog, Koin DI, Room database with all entities, DataStore Preferences, MIUIX UI library integration, MIUI-style Material3 theme with CJK font fallback, dark mode support, Splash Screen. Everything downstream phases depend on.

</domain>

<decisions>
## Implementation Decisions

### MIUIX Integration
- **D-01:** Full MIUIX (`top.yukonga.miuix.kmp:miuix-ui`) library added at foundation, all component categories available to downstream phases
- **D-02:** Theme driven by MIUIX theme system primarily, Material3 as fallback
- **D-03:** Dark mode theme configured at foundation alongside light theme — ready for immediate use by all phases

### Room Database & DataStore
- **D-04:** All 5 entities (Article, Category, Tag, ArticleTagCrossRef, QuickNote) created upfront with schema version 1
- **D-05:** FTS4 virtual table for full-text search created at foundation with Chinese ICU tokenizer
- **D-06:** DataStore pre-defines all preference keys: `theme_mode` (SYSTEM/LIGHT/DARK), `last_active_tab`, `is_first_launch`
- **D-07:** Room auto-migration disabled — use manual migrations via `MigrationTestHelper`

### Splash Screen
- **D-08:** Shows adaptive app icon + "知屿" text on MIUI warm orange (#FF6B35) background
- **D-09:** Closes via `setKeepOnCondition` after DataStore initialization completes (not fixed duration)
- **D-10:** Uses AndroidX SplashScreen API `installSplashScreen()` before `super.onCreate()`

### Package & Naming
- **D-11:** Application ID: `com.zhiyu.app`
- **D-12:** App display name: "知屿"
- **D-13:** Root project name: `ZhiYu`

### Claude's Discretion
- MIUIX component import granularity — specific imports vs wildcard, as long as full library dependency is declared
- Room DAO method signatures — standard CRUD patterns
- Compose theme file organization (Color.kt, Type.kt, Shape.kt, Theme.kt split)
- Gradle task configuration details

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### Project requirements
- `CLAUDE.md` — Full technology stack versions, constraints, design decisions, version catalog
- `.planning/REQUIREMENTS.md` §Foundation — FND-01 through FND-09 requirement details

### Research artifacts
- `.planning/research/STACK.md` — Complete version catalog, Gradle config, module structure, dependency coordinates
- `.planning/research/SUMMARY.md` — Synthesized architecture decisions, build order, phase implications
- `.planning/research/PITFALLS.md` — Critical pitfalls for Phase 1: Chinese font fallback (PITFALL-6), white screen on MIUI (PITFALL-7), DataStore main thread blocking (PITFALL-8), Room migration strategy (PITFALL-3)
- `.planning/research/ARCHITECTURE.md` — Package structure (feature-first), MVVM + UDF pattern, data layer design

### Libraries
- `top.yukonga.miuix.kmp:miuix-ui` — MIUIX UI component library for Compose

</canonical_refs>

<code_context>
## Existing Code Insights

### Reusable Assets
- No existing code — greenfield project

### Established Patterns
- No established patterns yet — foundation establishes all conventions

### Integration Points
- Koin modules (AppModule, RepositoryModule, ViewModelModule) are the wiring points for all downstream features
- Room database singleton is single integration point for all DAOs
- DataStore is single preferences entry point

</code_context>

<specifics>
## Specific Ideas

- MIUI design language prioritization: warm orange-red accent (#FF6B35), rounded corners (16dp-24dp cards, 28dp bottom sheets), light gray-white surfaces
- CJK font fallback via `Typeface.CustomFallbackBuilder` with bundled Noto Sans SC
- Splash screen must avoid white flash on MIUI devices (PITFALL-7 mitigation)

</specifics>

<deferred>
## Deferred Ideas

None — discussion stayed within phase scope.

</deferred>

---

*Phase: 1-Foundation*
*Context gathered: 2026-05-17*
