# Roadmap: 知屿 (ZhiYu)

**Created:** 2026-05-17
**Milestone:** MVP
**Granularity:** Fine

## Core Value

用户打开App能一眼看到当前时间状态，同时方便地记录和检索知识碎片 — 一个 App 搞定日常信息查看和知识管理。

## Phases

- [ ] **Phase 1: Foundation** — Project scaffolding, Gradle config, Koin DI, Room + DataStore, MIUIX integration, MIUI theme with CJK font fallback, Splash Screen
- [ ] **Phase 2: Navigation Shell** — Bottom 4-tab NavHost, Scaffold wiring, Edge-to-Edge, tab state preservation
- [ ] **Phase 3: Info Dashboard** — 信息 Tab with real-time clock, date/weekday display, 18:00 countdown with after-hours/weekend states
- [x] **Phase 4: Knowledge Base + Quick Notes** — 知识库 Tab with article CRUD, categories/tags, Markdown editor/preview, FTS4 full-text search; 小记 feature
- [ ] **Phase 5: Profile + Settings + Tools** — 我的 Tab with profile, settings (theme/dark mode), about page; 发现 Tab with tool collection (calendar, calculator, weather)
- [ ] **Phase 6: Build & Release** — ProGuard/R8 config, APK/AAB generation, final verification

## Phase Details

### Phase 1: Foundation

**Goal**: Development environment, build system, dependency injection, data layer, and theming ready for all feature development.

**Depends on**: Nothing (first phase)

**Requirements**: FND-01, FND-02, FND-03, FND-04, FND-05, FND-06, FND-07, FND-08, FND-09

**Success Criteria** (what must be TRUE):

1. Project builds successfully with AGP 9.0.28, Kotlin 2.3.21, Compose BOM 2026.04.01 via version catalog (`gradle/libs.versions.toml`)
2. Koin initializes on app startup with AppModule, RepositoryModule, and ViewModelModule wired correctly; ViewModels can be injected via `koinViewModel()`
3. MIUI-styled Material3 theme renders Chinese text correctly with CJK font fallback (Typeface.CustomFallbackBuilder + bundled Noto Sans SC) -- no tofu characters on any screen
4. Room database schema (all entities: Article, Category, Tag, ArticleTagCrossRef, QuickNote) and DataStore Preferences are initialized and ready for read/write
5. Splash screen displays on cold start via AndroidX SplashScreen API (`installSplashScreen()` before `super.onCreate()`) and dismisses only after DataStore is ready, with no white flash on MIUI devices

**Plans**: 4 plans

**Plan list:**
- [x] `01-01-PLAN.md` — Build System Foundation (version catalog, Gradle config, manifest, resources)
- [x] `01-02-PLAN.md` — Data Layer + DI Wiring (Room entities, DAOs, Database, DataStore, Koin modules)
- [x] `01-03-PLAN.md` — MIUI Theme System + CJK Font Fallback (Color, Type, Shape, Dimens, Theme)
- [x] `01-04-PLAN.md` — Splash Screen + MainActivity + FoundationPlaceholder

**UI hint**: yes

---

### Phase 2: Navigation Shell

**Goal**: Four-tab bottom navigation with type-safe routing, tab state preservation, and edge-to-edge display.

**Depends on**: Phase 1

**Requirements**: NAV-01, NAV-02, NAV-03, NAV-04

**Success Criteria** (what must be TRUE):

1. User sees four bottom navigation tabs (信息/知识库/发现/我的) with MIUI-style NavigationBar using MIUIX library
2. Tapping a tab switches the main content area to the corresponding screen; each tab shows its placeholder content
3. Each tab preserves its scroll position and back stack when switching between tabs (via `saveState=true` / `restoreState=true`)
4. Content renders edge-to-edge with proper system bar insets (status bar, navigation bar) on Android 16+
5. Type-safe routes compile without errors using `@Serializable` route definitions (no string-based route typos)

**Plans**: TBD

**UI hint**: yes

---

### Phase 3: Info Dashboard

**Goal**: 信息 Tab delivers the app's core "glanceable info" value with real-time clock, date, weekday, and work countdown.

**Depends on**: Phase 2

**Requirements**: DSH-01, DSH-02, DSH-03, DSH-04

**Success Criteria** (what must be TRUE):

1. User sees current date (年/月/日) and weekday (星期X) displayed prominently on the 信息 tab
2. User sees a live clock updating every second without jank or excessive recomposition (clock is isolated in its own composable with `derivedStateOf` for computed values)
3. Before 18:00 on weekdays, user sees countdown showing remaining hours:minutes:seconds until 18:00
4. After 18:00 on weekdays, user sees "已下班" message; on weekends, user sees "周末愉快" message
5. Clock and countdown update in real-time without causing other parts of the UI to recompose

**Plans**: TBD

**UI hint**: yes

---

### Phase 4: Knowledge Base + Quick Notes

**Goal**: 知识库 Tab delivers the app's knowledge management value with full article CRUD, Markdown editing/preview, categories/tags, full-text search, and 小记 quick notes.

**Depends on**: Phase 2 (navigation shell), Phase 3 (sequential execution)

**Requirements**: KNW-01, KNW-02, KNW-03, KNW-04, KNW-05, KNW-06, KNW-07, KNW-08, QNT-01, QNT-02, QNT-03

**Success Criteria** (what must be TRUE):

1. User can create, view, edit, and delete articles with Markdown content (CRUD via Room DAOs)
2. User can organize articles into categories (tree/directory structure) and assign tags (many-to-many via junction table)
3. User can write Markdown in raw text mode and toggle to preview rendered output; editor auto-saves draft to Room every 5 seconds to prevent data loss on process death
4. User can search across all articles by title and content using Room FTS4 full-text search; results are returned quickly
5. User can create quick notes (小记) from within the knowledge base tab as lightweight, uncategorized entries (text only); notes appear in a chronological list
6. User sees a list of articles with title and preview summary; empty state shows guidance when no articles exist
7. Article-tag queries use JOIN-based multimap returns (not `@Relation` N+1 pattern) for list performance

**Plans**: TBD

**UI hint**: yes

---

### Phase 5: Profile + Settings + Tools

**Goal**: 我的 Tab with profile display, theme/dark mode settings, about page; 发现 Tab with accessible tools collection.

**Depends on**: Phase 2 (navigation shell), Phase 4 (sequential execution)

**Requirements**: PRF-01, PRF-02, PRF-03, PRF-04, TLS-01, TLS-02, TLS-03, TLS-04

**Success Criteria** (what must be TRUE):

1. User can view profile page with avatar/nickname placeholders under 我的 tab
2. User can switch theme mode (light / dark / follow system) in settings; changes apply immediately without app restart via reactive DataStore flow
3. User can view about page showing app version name, version code, and open-source license information
4. User can access calendar (simple month view), calculator (basic arithmetic), and weather (location-based display) tools from 发现 tab tool grid
5. Dark mode and theme settings persist across app restarts via DataStore Preferences

**Plans**: TBD

**UI hint**: yes

---

### Phase 6: Build & Release

**Goal**: Production-ready build artifacts with code shrinking, signing, and verification.

**Depends on**: Phase 1, Phase 2, Phase 3, Phase 4, Phase 5 (all features built)

**Requirements**: REL-01, REL-02

**Success Criteria** (what must be TRUE):

1. Release APK and Android App Bundle (AAB) can be generated with ProGuard/R8 minification enabled (`isMinifyEnabled = true`)
2. Release build installs and runs correctly on Android 16+ (API 36) devices with no crashes
3. No major visual regressions or functionality loss in release build compared to debug build

**Plans**: TBD

**UI hint**: yes

---

## Progress Table

| Phase | Plans Complete | Status | Completed |
|-------|----------------|--------|-----------|
| 1. Foundation | 4/4 | In Progress|  |
| 2. Navigation Shell | 0/0 | Not started | - |
| 3. Info Dashboard | 0/0 | Not started | - |
| 4. Knowledge Base + Quick Notes | 1/1 | Complete | |
| 5. Profile + Settings + Tools | 0/0 | Not started | - |
| 6. Build & Release | 0/0 | Not started | - |

---

## Coverage Map

| Requirement | Phase | Status |
|-------------|-------|--------|
| FND-01 | Phase 1 | Pending |
| FND-02 | Phase 1 | Pending |
| FND-03 | Phase 1 | Complete |
| FND-04 | Phase 1 | Pending |
| FND-05 | Phase 1 | Complete |
| FND-06 | Phase 1 | Complete |
| FND-07 | Phase 1 | Pending |
| FND-08 | Phase 1 | Pending |
| FND-09 | Phase 1 | Pending |
| NAV-01 | Phase 2 | Pending |
| NAV-02 | Phase 2 | Pending |
| NAV-03 | Phase 2 | Pending |
| NAV-04 | Phase 2 | Pending |
| DSH-01 | Phase 3 | Pending |
| DSH-02 | Phase 3 | Pending |
| DSH-03 | Phase 3 | Pending |
| DSH-04 | Phase 3 | Pending |
| KNW-01 | Phase 4 | Pending |
| KNW-02 | Phase 4 | Pending |
| KNW-03 | Phase 4 | Pending |
| KNW-04 | Phase 4 | Pending |
| KNW-05 | Phase 4 | Pending |
| KNW-06 | Phase 4 | Pending |
| KNW-07 | Phase 4 | Pending |
| KNW-08 | Phase 4 | Pending |
| QNT-01 | Phase 4 | Pending |
| QNT-02 | Phase 4 | Pending |
| QNT-03 | Phase 4 | Pending |
| TLS-01 | Phase 5 | Pending |
| TLS-02 | Phase 5 | Pending |
| TLS-03 | Phase 5 | Pending |
| TLS-04 | Phase 5 | Pending |
| PRF-01 | Phase 5 | Pending |
| PRF-02 | Phase 5 | Pending |
| PRF-03 | Phase 5 | Pending |
| PRF-04 | Phase 5 | Pending |
| REL-01 | Phase 6 | Pending |
| REL-02 | Phase 6 | Pending |

**Coverage: 38/38 requirements mapped (100%)**

---

## Research Flags to Resolve During Planning

| Flag | Phase | Area | Description |
|------|-------|------|-------------|
| RESOLVE | Phase 1 | Font fallback | Implement CJK fallback using Typeface.CustomFallbackBuilder at theme level; bundle Noto Sans SC |
| RESOLVE | Phase 1 | Tab count | 小记 integrated into 知识库 tab (4 tabs total, not 5) |
| RESOLVE | Phase 4 | Markdown library | Decide between compose-richtext (STACK.md) and multiplatform-markdown-renderer (PITFALLS.md) |
| INVESTIGATE | Phase 4 | FTS4 Chinese tokenizer | Evaluate ICU tokenizer vs custom for Chinese word segmentation |
| INVESTIGATE | Phase 4 | Room schema | Determine whether FTS4 virtual table should include tag names for search |
| MONITOR | Phase 4 | compose-richtext releases | If chosen, monitor for breaking changes; alpha-stage library |
