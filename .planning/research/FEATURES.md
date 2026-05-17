# Feature Landscape

**Domain:** Android Personal Efficiency / Knowledge Management App (知屿 ZhiYu)
**Researched:** 2026-05-17
**Confidence:** MEDIUM (research based on web sources + competitor analysis; not validated with real users of this specific app)

## Table Stakes

Features that users expect as the baseline. Missing these means the app feels incomplete or broken.

### Tab Navigation (Bottom Navigation Bar)

| Feature | Why Expected | Complexity | Notes |
|---------|--------------|------------|-------|
| Bottom navigation with 3-5 tabs | Primary navigation pattern in Chinese apps (WeChat, Alipay, Yuque) | LOW | Material3 NavigationBar with `alwaysShowLabel = false` for MIUI-style |
| Active tab visual indicator | Must show which screen is active | LOW | Colored icon + optional pill indicator below |
| Tab state preservation on switch | Users expect to return to same scroll position | MEDIUM | Requires NavBackStackEntry state saving via `saveState`/`restoreState` in NavHost |
| MIUI-style accent color | Confirmed requirement; MIUI orange (#FF6B35) accent for active tab | LOW | Configure NavigationBarItem colors with MIUI accent palette |

**Dependencies:** Tab 4 (我的) is the settings entry point; all other tabs depend on the navigation framework being in place first.

### 信息 Tab (Info Dashboard)

| Feature | Why Expected | Complexity | Notes |
|---------|--------------|------------|-------|
| Current date and weekday display | Core dashboard purpose — "see current time state at a glance" | LOW | `java.time.LocalDate` + formatter; update every minute |
| Current time display (real-time clock) | Users expect a live clock | LOW | Use `LaunchedEffect` with delay loop or `Flow.interval` for second-level updates |
| Countdown timer to 18:00 (6PM) | Confirmed requirement | LOW | Calculate `Duration.between(now, target)`; format hours:minutes:seconds |
| Positive countdown (before 18:00) | Logical — shows remaining work time | LOW | Only show when current time < 18:00 |
| Graceful zero state (after 18:00 / weekends) | Users would see negative countdown or stale value | LOW | Show "已下班" (already off work) or "周末愉快" (happy weekend) instead |
| Time auto-refresh every second | Users expect live countdown | LOW | Same `LaunchedEffect` as time display |

**Dependencies:** None (this is the simplest tab, fully self-contained UI logic).

### 知识库 Tab (Knowledge Base)

| Feature | Why Expected | Complexity | Notes |
|---------|--------------|------------|-------|
| Category tree / folder organization | Core to knowledge management; Yuque uses book-style TOC | MEDIUM | Room parent-child relationship; LazyColumn with indentation or sticky headers |
| Tag assignment on articles | Lightweight cross-cutting categorization | MEDIUM | Many-to-many (Article <-> Tag) via Room junction table |
| Article CRUD (create, read, update, delete) | Basic content management | MEDIUM | ViewModel + Repository pattern; Room DAO operations |
| Markdown rendering in article detail | Confirmed requirement; standard for KM tools | MEDIUM | Need a Compose-compatible Markdown renderer library (e.g., compose-richtext or Markwon) |
| Markdown editing (raw text input) | Confirmed requirement | MEDIUM | Basic TextField with monospace font; or use a code editor composable |
| Full-text search across articles | Confirmed requirement | MEDIUM-HIGH | Room FTS4 (full-text search) virtual table; query with `WHERE body MATCH :query` |
| Article list view with title + preview | Standard list pattern | LOW | Simple LazyColumn with Card items showing title + first line |
| Empty state (no articles yet) | Expected UX pattern | LOW | Illustration + "写第一篇知识" CTA button |
| Search result highlighting | Expected search UX | MEDIUM | AnnotatedString with highlighted spans in search results |
| Pull-to-refresh | Common Android pattern | LOW | `pullToRefresh` modifier (Material3) — good to have but Room queries are local and instant |

**Dependencies:** Room database setup, FTS4 table for search, Markdown renderer library choice.

### 小记 (Quick Notes / Scratch Pad)

| Feature | Why Expected | Complexity | Notes |
|---------|--------------|------------|-------|
| One-tap quick note creation | #1 table stake across all PKM apps | LOW | FAB with edit icon; navigates to minimal note editor |
| Inbox-style note list | Dump notes without deciding category | LOW | Chronological list (newest first) in a separate section within 知识库 tab |
| Minimal editor (title + body) | Quick capture = minimal friction | LOW | Single screen with title TextField + body TextField |
| Timestamp on each 小记 | Default in every note-taking app | LOW | `createdAt` + `updatedAt` fields in Room entity |
| Delete quick note | Standard | LOW | Swipe-to-delete or long-press menu |
| Convert 小记 to full article | Power users expect to promote quick notes | MEDIUM | "Move to category" action that creates Article from Note while preserving content |

**Dependencies:** Room database with Note entity. Can share the same Markdown renderer as articles.

### 发现 Tab (Discover / Tool Collection)

| Feature | Why Expected | Complexity | Notes |
|---------|--------------|------------|-------|
| Tool entry list (grid or list) | Confirmed requirement | LOW | LazyVerticalGrid or LazyColumn with icon + name |
| Tool item tap to navigate/action | Expected interaction | LOW | Each item opens an external app/URL or navigates internally |
| Empty/default tools state | First-launch experience | LOW | Pre-populated with placeholder tool entries or "暂无工具" |

**Dependencies:** App navigation (internal or `ACTION_VIEW` intent). Consider if tools are hardcoded or configurable by user.

### 我的 Tab (Profile)

| Feature | Why Expected | Complexity | Notes |
|---------|--------------|------------|-------|
| Personal settings entry | Tab serves as primary settings hub | LOW | List of setting items |
| App name + version display | Standard about section | LOW | `BuildConfig.VERSION_NAME` |
| Theme mode selector | Confirmed requirement | LOW | FilterChip row: 跟随系统 / 浅色 / 深色 |
| Dark mode follow system | Confirmed requirement | LOW | Leverage `isSystemInDarkTheme()` + `darkTheme` parameter on MaterialTheme |
| About page with app info | Confirmed requirement | LOW | Card with app icon, version, description |
| Settings persistence across app restarts | Expected | MEDIUM | DataStore Preferences for all settings |

**Dependencies:** DataStore setup for persistence; theme state must propagate to Activity-level theming.

### Cross-Cutting Table Stakes

| Feature | Why Expected | Complexity | Notes |
|---------|--------------|------------|-------|
| Splash screen (SplashScreen API) | Modern Android standard; cold start UX | LOW | `core-splashscreen:1.2.0`; `installSplashScreen()` before `super.onCreate()` |
| Material 3 / MIUI design consistency | Users notice visual inconsistency | MEDIUM | Theme applied globally; MIUI accent vs Material You dynamic colors |
| Offline-first (all data local) | Project requirement; Room database | MEDIUM | Room + DataStore; no network calls |
| Data persistence on config change | Expected; retained via ViewModel | LOW | ViewModel scoped to NavBackStackEntry |
| Back navigation | Standard Android UX | LOW | `NavController.navigateUp()` or system back |

## Differentiators

Features that create competitive advantage. Not expected by users but valued.

| Feature | Value Proposition | Complexity | Notes |
|---------|-------------------|------------|-------|
| **Integrated countdown + knowledge** | Combines two use cases (info glance + knowledge capture) in one app; users don't need two separate tools | MEDIUM | Core concept of 知屿; the 信息 + 知识库 combo IS the differentiator |
| **MIUI design language on non-Xiaomi devices** | Xiaomi users feel at home; non-Xiaomi users get a well-designed alternative to stock Material You | MEDIUM | Using miuix-kmp library or custom styled Material3 components |
| **Countdown variations** (custom time, custom message) | Users could set their own end-of-day time (flexible work hours) | LOW | Configurable target time in settings; stored in DataStore |
| **Countdown to weekend / holiday** | Gamified anticipation of time off | LOW | Calculate days until next Saturday |
| **Article Markdown preview with live toggle** | "Edit" / "Preview" toggle — standard in Yuque and Markdown editors | MEDIUM | Tab row: 编辑 | 预览; Compose state manages which is visible |
| **小记 with character count** | Shows note-taking progress at a glance | LOW | Simple `text.length` display |
| **小记 with one-tap from home screen widget** | Blazing-fast capture; top complaint about Obsidian/Notion mobile | HIGH | AppWidget with EditText; or use Android 14+ `RoleManager` for default notes |
| **Category collapse/expand in knowledge base** | Tree organization like Yuque's book TOC | MEDIUM | AnimatedVisibility for sub-items; stores expanded state per category |
| **Article category breadcrumb navigation** | Shows current location in hierarchy | LOW | "根分类 > 子分类 > 文章名" text at top |
| **Settings with DataStore + reactive UI** | Theme change applies immediately without restart | MEDIUM | Settings change triggers recomposition in real time |
| **"发现" tool collection — user-customizable** | Users can add/edit tools beyond defaults | MEDIUM | CRUD for tool entries stored in Room |
| **Search with recent queries suggestion** | Improves discovery UX | LOW | Store last 5-10 search terms in DataStore |
| **Article word count** | Simple but satisfying data point | LOW | Count words (Chinese characters + English words) in body |
| **Schedule visibility** (current class/event period) | Additional info dashboard context if user sets schedule | MEDIUM | Optional feature; not in MVP scope but adds value |

## Anti-Features

Features to explicitly NOT build (at least in v1).

| Anti-Feature | Why Avoid | What to Do Instead |
|--------------|-----------|-------------------|
| **Cloud sync / account system** | Out of scope; adds auth complexity, backend, privacy concerns | Pure local storage (Room only) |
| **Real-time collaboration** | Requires backend, WebSocket, conflict resolution | Single-user app only |
| **Rich text editor (WYSIWYG)** | High complexity; replaces simpler Markdown | Raw Markdown editing + rendered preview |
| **Image/file attachments in articles** | Adds storage permission, file picker, media store complexity; out of scope for v1 | Text-only Markdown articles |
| **Web clipper** | Requires Android service, content observation, Chrome extension; HIGH complexity | User manually pastes content |
| **Backlinks / graph view** | Requires bidirectional link parsing and graph rendering library; impressive but high effort for v1 | Simple tag-based cross-referencing |
| **AI features (auto-tagging, summaries)** | Requires on-device ML model or API call; over-engineered for v1 | Manual tagging is adequate |
| **Multiple vaults / workspaces** | Adds complexity to navigation and data model; Obsidian-level feature | Single knowledge base |
| **Voice notes / audio recording** | Requires audio permission, recording, storage management | Text-based notes only |
| **Handwriting / sketch input** | High complexity with Ink API integration | Text + Markdown code blocks |
| **PDF / document export** | Requires document generation library | Screen reader / copy-paste |
| **Home screen widget for clock/countdown** | Widget requires AppWidget provider, configuration Activity, periodic updates | Countdown lives inside the app (信息 Tab) |
| **Internationalization (i18n)** | Out of scope per PROJECT.md | Chinese-only UI |
| **Deep customization (font size, layout density, custom colors)** | Increases settings complexity exponentially | Standard Material3 typography + MIUI accent theme |
| **Built-in RSS reader / web content aggregator** | Entirely separate product scope | User manually saves content as articles |
| **Task management / todo lists** | Scope creep; this is a KM app, not a task manager | Markdown checkbox (`- [ ]`) is sufficient for basic todos |

## Feature Dependency Map

```
Phase 0 (Foundation):
  SplashScreen API setup
  Room database (Article, Tag, Category, Note, Tool entities + DAOs)
  DataStore Preferences (theme_mode, dynamic_color)
  Material Theme with MIUI accent colors
    └─ Must decide: MIUI static theme vs Material You dynamic colors
  
Phase 1 (Navigation Shell):
  Bottom NavigationBar (4 tabs) + NavHost
  └─ Requires: Tab icons (MIUI-style outlined/filled)
  
Phase 2 (Tab Screens):
  信息 Screen ──── No dependencies (self-contained)
  知识库 Screen ── Depends on: Room database, Markdown renderer
  发现 Screen ──── Depends on: Room database (Tool entity) or hardcoded list
  我的 Screen ──── Depends on: DataStore, theme state

Phase 3 (Feature Completion):
  Full-text search ──────── Depends on: Room FTS4 table (must be created alongside main tables)
  Markdown editor/preview ─ Depends on: Markdown renderer library
  小记 quick notes ──────── Depends on: Room Note entity
  Theme/dark mode switch ── Depends on: DataStore + Activity-level theme recomposition
  About page ────────────── Depends on: settings page structure
```

### Critical Path

```
DataStore ──> Theme settings (我的 Tab)
    |
Room DB ──> 知识库 Tab (articles, categories, tags, notes)
    |
Markdown renderer ──> Article detail view
    |
FTS4 table ──> Search functionality
```

## Complexity Summary

| Area | Features | Overall Complexity | Risk Factors |
|------|----------|-------------------|--------------|
| 信息 Tab | 6 features | LOW | Self-contained UI logic; no database dependency |
| 知识库 Tab | 10 features | MEDIUM-HIGH | FTS4 search is trickiest part; Markdown renderer library needs vetting |
| 小记 | 6 features | LOW-MEDIUM | Separate Room entity; shares Markdown renderer with articles |
| 发现 Tab | 3 features | LOW | Hardcoded list or simple Room CRUD |
| 我的 Tab | 6 features | LOW-MEDIUM | Theme-to-Activity wiring is the only complexity |
| Cross-cutting | 5 features | MEDIUM | Splash API straightforward; MIUI design consistency requires attention |
| **Total** | **~36 features** | **MEDIUM** | Scope is manageable; FTS4 and Markdown rendering are the unknowns |

## MIUI / Chinese App UX Patterns to Follow

1. **Bottom tab labels always visible?** Chinese apps (WeChat, Yuque) show labels on all tabs, not just active. Consider `alwaysShowLabel = true` for v1.
2. **Red badge / unread indicator** on tab icons — common in WeChat-style apps. Can skip for v1 but keep in mind.
3. **Card-style settings list** — MIUI settings use independent cards per item with rounded corners and subtle shadows. Use Material3 `Card` with `elevatedCardColors`.
4. **About page with card header** — MIUI style: app icon + name + version in a prominent card at top; legal info in card sections below.
5. **Splash screen with app icon** — Simple icon on branded background; no animation needed for v1.

## Sources

- [Yuque (语雀) official features](https://www.yuque.com/about/groups)
- [Notion Knowledge Base template](https://www.notion.com/templates/corporate-knowledge-base)
- [Android Police PKM app comparison](https://www.androidpolice.com/tried-notion-obsidian-capacities-anytype-for-month/)
- [Koder.ai PKM development guide](https://koder.ai/blog/build-mobile-app-personal-knowledge-management)
- [Koder.ai PKM snippets app guide](https://koder.ai/blog/create-mobile-app-personal-knowledge-snippets)
- [Android SplashScreen API docs](https://developer.android.google.cn/develop/ui/views/launch/splash-screen)
- [compose-miuix-ui/miuix GitHub](https://github.com/compose-miuix-ui/miuix)
- [Cahier — Google's note-taking sample](https://github.com/android/cahier)
- [Material3 theming guide — dev.to](https://dev.to/blamsa0mine/building-dark-mode-dynamic-theming-with-kotlin-jetpack-compose-advanced-settings-datastore--39d7)
- [XiaomiTime — HyperOS 2-inspired settings](https://xiaomitime.com/android-16-beta-3-unveils-hyperos-2-inspired-settings-menu-29990/)
