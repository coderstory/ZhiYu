---
gsd_state_version: 1.0
milestone: v1.0
milestone_name: milestone
status: unknown
last_updated: "2026-05-17T15:45:41.980Z"
progress:
  total_phases: 6
  completed_phases: 0
  total_plans: 4
  completed_plans: 2
  percent: 50
---

# State: 知屿 (ZhiYu)

**Created:** 2026-05-17
**Milestone:** MVP
**Mood:** This is going to be great

---

## Project Reference

- **Core Value**: 用户打开App能一眼看到当前时间状态，同时方便地记录和检索知识碎片 — 一个 App 搞定日常信息查看和知识管理。
- **Tech Stack**: Kotlin + Jetpack Compose + Material3 | Android 16+ (API 36) | Room + DataStore | Koin DI | MIUIX
- **Constraints**: Local-only, no backend, Chinese UI, MIUI design language
- **Dependencies**: AGP 9.0.28, Kotlin 2.3.20, Compose BOM 2026.04.01, Room 2.8.4, Navigation Compose 2.9.8, DataStore 1.2.1

---

## Current Position

| Property | Value |
|----------|-------|
| **Phase** | 1 - Foundation |
| **Plan** | 2 - Data Layer + DI Wiring (Complete) |
| **Status** | In Progress |
| **Progress** | [#####               ] 3/38 requirements |
| **Granularity** | Fine |

### Phase Details

| Phase | Goal | Requirements | Status |
|-------|------|--------------|--------|
| 1 - Foundation | Build system, DI, data layer, theme, splash screen | FND-01~FND-09 | In Progress (2/4 plans) |
| 2 - Navigation Shell | 4-tab bottom NavHost, edge-to-edge, tab state preservation | NAV-01~NAV-04 | Not started |
| 3 - Info Dashboard | Real-time clock, date, 18:00 countdown | DSH-01~DSH-04 | Not started |
| 4 - Knowledge Base + Quick Notes | Article CRUD, Markdown, FTS4 search, categories/tags, 小记 | KNW-01~KNW-08, QNT-01~QNT-03 | Not started |
| 5 - Profile + Settings + Tools | Profile, theme settings, about, tools (calendar/calculator/weather) | PRF-01~PRF-04, TLS-01~TLS-04 | Not started |
| 6 - Build & Release | ProGuard/R8, APK/AAB generation | REL-01~REL-02 | Not started |

---

## Performance Metrics

| Metric | Measurement | Target |
|--------|-------------|--------|
| Cold start time | -- | < 1.5s on mid-range Xiaomi device |
| Dashboard recomposition | -- | Only clock composable recomposes on time tick |
| FTS4 search | -- | < 200ms for 500 articles |
| APK size | -- | < 15MB release APK |

---

## Accumulated Context

### Key Decisions Made

| Decision | Rationale | Outcome |
|----------|-----------|---------|
| Kotlin 2.3.20 (not 2.4 beta) | Stability; KSP/AGP compatibility | Implementation |
| Room 2.8.4 (not 3.0 alpha) | KMP-focused Room 3.0 has breaking API changes | Implementation |
| Navigation Compose 2.9.8 (not Nav3) | Battle-tested for bottom tabs; more docs | Implementation |
| Koin (not Hilt) | Personal app with ~10 ViewModels; Hilt build-time overhead unnecessary | Implementation |
| Single module (`:app`) | Fits project scope; can extract later | Implementation |
| Feature-first packaging | Each tab self-contained; clean dead code removal | Implementation |
| MVVM + UDF | Official Android guidance; predictable state flow | Implementation |
| DataStore (not SharedPreferences) | Modern replacement; coroutine/Flow-based | Implementation |
| 4 bottom tabs (not 5) | PROJECT.md requirement; 小记 inside 知识库 tab | Implementation |
| All 5 entities created upfront with schema version 1 (D-04) | Phase 1 has full entity picture; avoids schema migration churn | Implementation |
| FTS4 uses TOKENIZER_UNICODE61, not ICU (D-05) | ICU tokenizer unavailable on Android SQLite; would crash at runtime | Implementation |
| DataStore keys: theme_mode, last_active_tab, is_first_launch (D-06) | Pre-defined at foundation for cross-feature consistency | Implementation |
| No auto-migration; manual migration only (D-07) | MigrationTestHelper requires exportSchema=true; auto-migration is error-prone | Implementation |

### Open TODOs

- Choose Markdown library (compose-richtext vs multiplatform-markdown-renderer) during Phase 4 planning
- Implement CJK font fallback with bundled Noto Sans SC during Phase 1
- Define Room FTS4 schema with Chinese tokenizer consideration during Phase 4
- Test on physical Xiaomi/Redmi device before Phase 6 release

### Known Blockers

None yet.

### Risk Register

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| Chinese font fallback failure (tofu) | Medium | Critical | Bundle Noto Sans SC; use CustomFallbackBuilder (PITFALL-6) |
| Markdown library pre-1.0 instability | Medium | High | Evaluate alternatives before Phase 4; have fallback plan (PITFALL-4) |
| Process death data loss in editor | Medium | Critical | Auto-save draft to Room every 5s (PITFALL-12) |
| Clock recomposition firestorms | High | Medium | Isolate clock in standalone composable; use derivedStateOf (PITFALL-1) |
| Room N+1 queries on article-tag | Medium | Medium | Use JOIN queries, not @Relation for lists (PITFALL-10) |
| MIUI background process kill | Medium | High | Save on DisposableEffect, onPause, back press (PITFALL-14) |
| DataStore main thread blocking | Low | Medium | Always use flowOn(Dispatchers.IO) (PITFALL-8) |

---

## Session Continuity

### Next Session

1. Execute Plan 01-03: MIUI Theme System + CJK Font Fallback (Color, Type, Shape, Dimens, Theme)
2. Then execute Plan 01-04: Splash Screen + MainActivity + FoundationPlaceholder
3. After all Phase 1 plans complete, proceed to Phase 2

### Handoff Notes

- Plan 01-01 (Build System Foundation) is complete -- Gradle config, version catalog, manifest, resources exist
- Plan 01-02 (Data Layer + DI Wiring) is complete -- Room entities/DAOs/database, DataStore AppPreferences, 3 Koin modules, ZhiYuApplication, ThemeMode enum
- All 18 Kotlin source files created under `com.zhiyu.app` package
- FTS4 uses TOKENIZER_UNICODE61 (ICU unavailable on Android SQLite)
- All AppPreferences flows use `flowOn(Dispatchers.IO)` to prevent main thread blocking
- Koin modules: AppModule (singletons), RepositoryModule (empty stub), ViewModelModule (empty stub)
- ZhiYuApplication registered in manifest, Koin starts in onCreate()
- Next: Plan 01-03 creates MIUI-style Material3 theme with CJK font fallback

---

*Last updated: 2026-05-17*
