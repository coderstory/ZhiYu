# Research Synthesis: 知屿 (ZhiYu)

**Project:** Android Personal Efficiency App
**Synthesized:** 2026-05-17
**Overall Confidence:** HIGH (stack, architecture, pitfalls) / MEDIUM (features -- feature-market fit not user-validated)

---

## Executive Summary

知屿 (ZhiYu) is a personal knowledge assistant for Android 16+ that combines an information dashboard (time, weekday, countdown to 6PM), a knowledge base with Markdown editing and full-text search (inspired by Yuque), a quick-notes scratch pad, a tool collection, and profile/settings. The design language targets MIUI/HyperOS users through customized Material3 theming.

The recommended approach is a single-module Android app built with Kotlin 2.3.20 + Jetpack Compose (BOM 2026.04.01) + Material3, using MVVM with Unidirectional Data Flow (UDF), Koin for dependency injection, Room 2.8.4 for local storage, DataStore Preferences for settings, and Navigation Compose 2.9.8 for type-safe routing. Feature-first packaging keeps each bottom-nav tab self-contained. The architecture is three-layer (UI-ViewModel-Data) with no UseCase layer -- ViewModels call Repositories directly for this local-only CRUD app.

The top critical risks are: (1) Chinese font fallback failure producing "tofu" characters on Xiaomi devices, (2) Markdown library instability/compatibility (the ecosystem is pre-1.0), (3) process death data loss in the editor without auto-save to Room, (4) recomposition firestorms from the real-time clock on the dashboard, and (5) Room N+1 query performance with article-tag relations. Each has a clear mitigation path documented in PITFALLS.md.

---

## Key Findings

### From STACK.md

| Decision | Recommendation | Rationale |
|----------|---------------|----------|
| Build system | AGP 9.0.28+ | Required for compileSdk 36; built-in Kotlin support |
| Language | Kotlin 2.3.20 | Stable (2.4 is beta); matches KSP/AGP compatibility |
| Compose | BOM 2026.04.01 (Compose 1.11.0) | Official Google BOM manages all versions |
| Database | Room 2.8.4 (NOT 3.0 alpha) | Stable; KMP-focused Room 3.0 has breaking API changes |
| Navigation | Navigation Compose 2.9.8 (NOT Nav3) | Battle-tested for bottom tabs; Nav3 has different API and less documentation |
| DI | Koin (NOT Hilt) | Personal app with ~10 ViewModels; Hilt build-time overhead unnecessary |
| Markdown | compose-richtext 1.0.0-alpha03 | Compose-native with Material3; but pre-1.0 alpha |
| Preferences | DataStore Preferences 1.2.1 | Modern replacement for SharedPreferences |
| Theme | Material3 with MIUI customization | No official MIUI Compose kit; approximate via colors/shapes/typography |
| Network | None | Local-only app; no Retrofit/OkHttp |

**Critical version constraint:** minSdk = 36 (Android 16) means edge-to-edge is mandatory with no opt-out. All content must render behind system bars, handled via Scaffold + WindowInsets handling.

### From FEATURES.md

| Feature Area | Complexity | Key Dependencies | Priority |
|-------------|------------|-----------------|----------|
| 信息 (Dashboard) | LOW | None -- self-contained UI logic | Phase 2 |
| 知识库 (Knowledge Base) | MEDIUM-HIGH | Room DB, Markdown renderer, FTS4 | Phase 3 |
| 小记 (Quick Notes) | LOW-MEDIUM | Room Note entity, shares Markdown renderer | Phase 3 |
| 发现 (Tools) | LOW | Room or hardcoded list | Phase 3 |
| 我的 (Profile/Settings) | LOW-MEDIUM | DataStore, theme-to-Activity wiring | Phase 3 |

**Total feature count:** Approximately 36 features across all tabs.

**Differentiators:** The core differentiator is combining info-at-a-glance (dashboard countdown) with knowledge management in a single app with MIUI design language, all offline-local with no backend dependency. This differentiates from cloud-dependent tools like Yuque and Notion.

**Anti-features (explicitly excluded from v1):** Cloud sync, collaboration, WYSIWYG editor, image attachments, AI auto-tagging, i18n, widgets, voice notes, backlinks/graph view.

**Discrepancy found:** FEATURES.md lists 小记 as a section within the 知识库 tab. ARCHITECTURE.md models it as a separate 5th tab. Per PROJECT.md which specifies 4 bottom tabs (信息, 知识库, 发现, 我的), 小记 should be integrated into the knowledge base tab, NOT as a standalone bottom-nav destination.

### From ARCHITECTURE.md

**Pattern:** MVVM + Unidirectional Data Flow with feature-first packaging.

**Core architecture decisions:**

| Decision | Choice | Why |
|----------|--------|-----|
| Package structure | Feature-first | Each bottom-nav tab is self-contained; dead code removal is clean |
| State management | Single StateFlow<UiState> per screen | Immutable, predictable; compile-time safe state combinations |
| Data layer | Room DAOs return Flow<T> for reads, suspend for writes | Reactive UI, automatic re-emission on DB changes |
| Repositories | ViewModel calls Repository directly (no UseCase layer) | Local-only CRUD app does not need extra abstraction |
| Navigation | Type-safe @Serializable routes with flat NavHost | Compile-time safety; 1-2 level depth does not need nested graphs |
| Tab switching | saveState=true / restoreState=true | Each tab preserves scroll position and back stack independently |
| Theme | DataStore-driven DarkModeSetting via StateFlow | Real-time theme switch without app restart |

**Build order dependency chain:**
```
Theme + Koin + Room + DataStore -> Repositories -> ViewModels -> Navigation -> Screens -> MainActivity
```

Each feature tab can be built independently once the data layer and navigation shell are in place.

**Correction needed:** The architecture document shows 5 tabs in navigation (including QuickNotes as Tab 3). The project requires 4 tabs. The notes feature should be integrated into the knowledge base tab as a sub-section, not a separate bottom-nav destination.

### From PITFALLS.md

**Top 5 critical pitfalls:**

1. **Chinese font fallback (Phase 1 - Theme):** Using a custom English font without CJK fallback produces "tofu" (empty boxes). Prevention: Use Typeface.CustomFallbackBuilder (API 29+) at MaterialTheme level with bundled Noto Sans SC. Test on a Xiaomi device without Google Play Services.

2. **Markdown library instability (Phase 3 - Knowledge Base):** The Compose-native Markdown ecosystem is pre-1.0. STACK.md recommends compose-richtext (alpha), while PITFALLS.md recommends mikepenz/multiplatform-markdown-renderer. **This needs resolution during Phase 3 planning.** Evaluate both against realistic Chinese Markdown content before committing.

3. **Process death data loss in editor (Phase 3 - Editor):** Editor content lost on process death is catastrophic for a note-taking app. Prevention: Auto-save draft to Room every 5 seconds via LaunchedEffect, save on DisposableEffect disposal, handle MIUI aggressive background process kill.

4. **Recomposition firestorms from clock (Phase 1 - Dashboard):** Reading currentTimeMillis at a high composable level causes the entire UI tree to recompose every 16ms. Prevention: Isolate the clock into a standalone composable; use derivedStateOf for computed countdown values.

5. **Room N+1 queries (Phase 3 - Data Schema):** Using @Relation for article-tag lookups generates N+1 SQL queries. Prevention: Use JOIN queries with multimap returns for list screens; reserve @Relation for single-article detail loading.

**Key architectural pitfall resolved:** PITFALLS.md references Navigation 3 contentKey collision problems. Since STACK.md recommends Navigation Compose 2.9.8 (not Nav3), this pitfall does not apply. The standard saveState/restoreState pattern in Nav2 handles tab state correctly.

---

## Implications for Roadmap

### Phase 1: Foundation and Scaffold

| Component | Key Decisions | Pitfalls to Avoid |
|-----------|--------------|-------------------|
| Gradle setup (AGP 9.0.28, version catalog) | Pin Kotlin 2.3.20 (not 2.4 beta) | Pitfall 17: AGP version must support compileSdk 36 |
| Application class + Koin initialization | 3 Koin modules: AppModule, RepositoryModule, ViewModelModule | -- |
| Room database (entities, DAOs, FTS4 table) | Define ALL entities upfront including FTS4 for future search | Pitfall 3: Choose autoMigrations OR manual migrations per range. Do NOT mix both. |
| DataStore Preferences | Single file for all settings; flowOn(Dispatchers.IO) on all reads | Pitfall 8: Main thread blocking on first read |
| Theme system (Color, Type, Shape) | MIUI accent (#FF6B35), custom rounded shapes, CJK-capable typography | Pitfall 6: Chinese font fallback is CRITICAL. Bundle Noto Sans SC. |
| SplashScreen API | installSplashScreen() BEFORE super.onCreate() | Pitfall 7: White screen on MIUI devices |

**Rationale:** Everything depends on these foundations. Data layer must exist before ViewModels. Theme must exist before any UI rendering. Room schema must be settled before migration testing.

**Research needed during Phase 1 planning:** CJK font fallback implementation details for Compose at the theme level.

---

### Phase 2: Navigation Shell and Tab Stubs

| Component | Key Decisions | Pitfalls to Avoid |
|-----------|--------------|-------------------|
| Type-safe routes (@Serializable) | 4 tab roots + sub-routes (ArticleDetail, ArticleEditor, Settings, About) | -- |
| Bottom NavigationBar | Material3 NavigationBar; alwaysShowLabel=true (Chinese UX convention) | Pitfall 1: Establish state scoping patterns for clock BEFORE building dashboard |
| MainActivity + Scaffold | Edge-to-edge with proper insets; single Activity | Pitfall 5: Theme switching should use animateColorAsState for smooth transitions |
| 4 screen stubs | Placeholder Box composable per tab | -- |

**Rationale:** Navigation shell must be in place before any feature tab is built. Validates routing, bottom bar, and theming pipeline.

**No additional research needed.** Navigation Compose 2.x patterns are well-documented.

---

### Phase 3: Feature Tabs (parallel builds)

Recommended build order within this phase:

| Order | Tab | Rationale | Features Delivered | Pitfalls |
|------|-----|-----------|-------------------|----------|
| 1 | 信息 (Dashboard) | No DB dependency; quick win; validates scoping patterns | Clock, weekday, 6PM countdown, weekend/after-hours states | Pitfall 1, Pitfall 11 |
| 2 | 知识库 + 小记 | Highest complexity; needs early validation | Category tree, article CRUD, Markdown editor/preview, FTS4 search, quick notes | Pitfall 4, 10, 12, 14, 15 |
| 3 | 我的 (Profile/Settings) | Depends on DataStore; wires theme switch | Theme mode selector, about page, version info | Pitfall 5 |
| 4 | 发现 (Tools) | Simplest tab; lowest risk | Tool entry grid, tap-to-navigate | -- |

**Research needed during Phase 3 planning:**
- **RESEARCH FLAG (HIGH):** Markdown library selection. compose-richtext vs multiplatform-markdown-renderer.
- **RESEARCH FLAG (MEDIUM):** Room FTS4 Chinese tokenizer. Standard tokenizers handle Chinese poorly.
- **RESEARCH FLAG (MEDIUM):** Room schema for article-tag many-to-many with FTS4.

**Well-documented patterns (skip research):**
- Dashboard time/countdown: Simple LaunchedEffect + java.time
- Bottom nav tab state: Navigation Compose 2.x saveState/restoreState
- Settings DataStore: Official Android docs are comprehensive

---

### Phase 4: Polish and Refinement

| Area | What to Do |
|------|-----------|
| MIUI design refinement | Fine-tune shapes, colors, typography; test against MIUI system UI |
| Dark mode transitions | Verify smooth theme switching; test on Xiaomi with dark mode enabled system-wide |
| Edge-to-edge verification | Ensure all screens render correctly behind system bars |
| Performance audit | Check recomposition counts; verify FTS4 query times under load |
| Xiaomi device testing | Test on at least one physical Redmi/Note device |
| App icon | MIUI-style round icon with proper adaptive mask |

---

### Phase 5: Build and Release

| Task | Details |
|------|---------|
| ProGuard/R8 | isMinifyEnabled=true for release; test release build on device |
| APK/AAB generation | Standard Android signing |
| Migration tests | Run MigrationTestHelper for all Room version upgrade paths |

---

## Confidence Assessment

| Area | Level | Notes |
|------|-------|-------|
| **Stack versions** | HIGH | All versions verified against official compatibility tables. Room 2.8.4, Navigation 2.9.8, DataStore 1.2.1 are stable releases. |
| **Compose BOM** | HIGH | Official Android Developers Blog post for BOM 2026.04.01. |
| **compose-richtext** | MEDIUM-LOW | Pre-1.0 alpha. API may break. PITFALLS recommends a different library (multiplatform-markdown-renderer). This is the lowest-confidence dependency. |
| **MIUI design via Material3** | LOW | No official MIUI design kit for Compose. Research based on visual approximation. |
| **Feature set** | MEDIUM | Based on competitor analysis, not user research. Feature-market fit is unvalidated. |
| **Architecture** | HIGH | Official Android architectural guidance. Feature-first packaging is well-established community convention. |
| **Pitfalls** | HIGH | Sources include official docs, Google Issue Tracker, verified community reports. |

---

## Research Flags

| Flag | Phase | Area | Description |
|------|-------|------|-------------|
| RESOLVE | Phase 3 | Markdown library | Decide between compose-richtext (STACK.md) and multiplatform-markdown-renderer (PITFALLS.md) |
| RESOLVE | Phase 1 | Font fallback | Implement CJK fallback using Typeface.CustomFallbackBuilder at theme level; bundle Noto Sans SC |
| RESOLVE | Phase 1 | Tab count | ARCHITECTURE.md assumes 5 tabs; PROJECT.md requires 4; integrate 小记 into 知识库 tab |
| INVESTIGATE | Phase 3 | FTS4 Chinese tokenizer | Evaluate ICU tokenizer vs custom for Chinese word segmentation |
| INVESTIGATE | Phase 3 | Room schema | Determine whether FTS4 virtual table should include tag names for search |
| INVESTIGATE | Phase 1 | MIUI design library | Evaluate compose-miuix-ui/miuix as alternative to manual Material3 customization |
| MONITOR | Phase 3 | compose-richtext releases | If chosen, monitor for breaking changes; alpha-stage library |

---

## Gaps to Address During Planning

1. **Tab count: 4 vs 5.** ARCHITECTURE.md defines 5 bottom tabs with QuickNotes as a separate tab. PROJECT.md requires 4. Build the feature data model but route it as a sub-section of the knowledge base tab.

2. **Markdown library: two conflicting recommendations.** STACK.md recommends compose-richtext. PITFALLS.md recommends multiplatform-markdown-renderer. Evaluate both against Chinese Markdown content, code blocks, and Material3 theming before choosing.

3. **MIUI design depth.** Currently met through Material3 customization. The compose-miuix-ui/miuix library exists but was not evaluated. Phase 4 should evaluate if deeper MIUI fidelity is needed.

4. **No user validation.** Acceptable for a personal project, but if broader adoption is intended, share a prototype with Xiaomi users.

5. **No testing strategy defined.** Formalize Xiaomi device testing and migration test helpers during Phase 1 planning.

---

## Sources

Primary sources used across all research files:

- Android Developers: App Architecture Guide, Navigation Type Safety, Room Async Queries, DataStore Guide
- Android Developers Blog: Compose BOM 2026.04.01, Navigation 3 Stable Announcement
- AGP 9.0.0 Release Notes, AGP/Kotlin Compatibility Table
- Kotlin 2.3.20 Release Notes, KSP 2.3.6 Release Notes
- JetBrains Blog: AGP 9 Built-in Kotlin Support
- Android Developers: Android 16 Behavior Changes (Edge-to-Edge Mandatory)
- Google Now in Android Sample App
- GitHub: compose-richtext, mikepenz/multiplatform-markdown-renderer, compose-miuix-ui/miuix
- Android Police: PKM App Comparison
- Xiaomi Developer Documentation
- StackOverflow: Room N+1 Query, Compose Font Fallback, SplashScreen on Xiaomi

Full source lists with confidence levels and URLs are available in each individual research file.
