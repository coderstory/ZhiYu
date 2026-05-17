# Domain Pitfalls: Android Personal Efficiency App (ZhiYu / )

**Domain:** Android Jetpack Compose personal efficiency / knowledge management app
**Researched:** 2026-05-17
**Overall confidence:** HIGH (verified against official docs and community sources)

---

## Critical Pitfalls

Mistakes that cause rewrites, data loss, or major user-facing issues.

### Pitfall 1: Recomposition Firestorms from Top-Level State Reads

**What goes wrong:** Reading high-frequency state (current time, scroll position) at the root composable causes the entire UI tree to recompose every frame. For a personal efficiency app that displays real-time clock, work countdown, and scrollable content lists, this is extremely dangerous.

**Example of the mistake:**
```kotlin
// BAD: currentTimeMs changes every ~16-100ms, recomposes entire app
@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val currentTime by viewModel.currentTimeMs.collectAsState()
    val scrollState by viewModel.scrollState.collectAsState()
    // Entire tree below recomposes on every time tick
    InfoCard(currentTime)         // Only this needs time
    KnowledgeList(scrollState)    // Only this needs scroll
    BottomNavigationBar()         // This needs neither!
}
```

**Why it happens:** Compose recomposes any composable that reads a changed `State<T>`. If the state is read at a high level, the entire subtree recomposes, even if only a leaf composable needs that state.

**Consequences:**
- Janky scrolling (60fps drops on mid-range Xiaomi devices)
- Excessive CPU usage, battery drain
- Missed frames on animations

**Prevention:**
- Scope state reads to the **lowest composable** that needs them. Isolate the clock into its own composable that reads time internally.
- Use `derivedStateOf` for computed values (e.g., "is it past 18:00 for countdown?"):
  ```kotlin
  val showCountdown by remember {
      derivedStateOf { currentHour in 9..17 }
  }
  ```
- For LazyList scroll indices, read inside a dedicated composable with `derivedStateOf`.

**Detection:**
- Use Android Studio Layout Inspector's recomposition counter
- Look for composables showing "recomposed X times" that shouldn't change
- Enable `-P compose:showRecompositionHighlights=true` during development

**Phase to address: Phase 1 (Info Dashboard) — the clock + countdown feature is the first UI screen built. Must establish correct state scoping patterns from day one.**

**Recovery:** Refactor by extracting the time-reading composable into a standalone `@Composable fun ClockDisplay()` that reads `currentTimeMillis` internally via `LaunchedEffect`, and moves all derived calculations (countdown remaining, day-of-week) into `remember` blocks with `derivedStateOf`. This is surgical but requires touching state wiring.

---

### Pitfall 2: Navigation 3 Multi-Back-Stack `contentKey` Collision

**What goes wrong:** When using Navigation 3 (Nav3) with bottom navigation tabs, different tab destinations that have the same `toString()` representation (e.g., both `Home.Feed` and `Profile.Feed` render `"Feed"`) cause **state collisions** between back stacks. A user's scroll position on Tab A's feed is incorrectly restored on Tab B's feed.

```
Tab A (Info):    NavKey = "Info" + "Settings"   <- scroll saved
Tab B (Knowledge): NavKey = "Knowledge" + "Settings" <- scroll restored from Tab A!
```

**Why it happens:** Nav3's `NavEntry` default `contentKey` is derived from `key.toString()`. When different `NavKey` types produce identical strings, the state decorator treats them as the same destination and reuses `rememberSaveable` state.

**Consequences:** Users see incorrect UI state when switching tabs — scroll positions, text inputs, and search queries leak between unrelated tabs.

**Prevention:** Always override `contentKey` with a **fully qualified unique identifier**:
```kotlin
val uniqueContentKey = "${key::class.qualifiedName}:$key"
```

Or use explicit content keys:
```kotlin
NavEntry(key = route, contentKey = "tab_knowledge_${route.id}") { ... }
```

**Detection:**
- Switch between tabs and verify scroll positions / text inputs are independent
- Unit test: navigate to tab A, scroll, switch to tab B, switch back — assert original tab A state

**Phase to address: Phase 2 (Navigation Shell + Bottom Tabs) — must be designed correctly at the moment bottom navigation is wired up.**

**Recovery:** If collision is already in production, wrap all `NavEntry` instantiations with explicit content keys. This is a mechanical find-and-replace but requires auditing every screen in every tab to confirm keys are unique.

---

### Pitfall 3: Database Migration with Mixed autoMigrations and Manual Migrations

**What goes wrong:** Defining both `@Database(version = 3, autoMigrations = [...])` AND a manual `Migration(2, 3)` for the same version range causes Room to execute **both** — the auto-generated migration first, then the manual one. This produces `SQLiteException: duplicate column name` and crashes the app on upgrade.

**Why it happens:** Room does not prevent you from registering overlapping migrations. The framework applies all registered migrations in order, unaware that the auto-generated one has already made the schema change.

**Consequences:** Users on old database versions crash on launch after app update. Since this is a local-only app, all user data is at risk if crash-loop prevents the user from opening the app.

**Prevention:**
- Use **either** `autoMigrations` **or** manual `addMigrations` for each version interval — never both
- For v1 to v2: choose autoMigrations (simple additive changes only)
- For v2 to v3: if the change is destructive (rename column, change type), always write a manual Migration
- Add `MigrationTestHelper` tests for ALL upgrade paths:
  ```kotlin
  @Test
  fun migrateFrom1To3() {
      helper.createDatabase(TEST_DB_NAME, 1).apply {
          // insert test data
      }
      Room.databaseBuilder(context, AppDatabase::class.java, TEST_DB_NAME)
          .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
          .build()
      // verify schema AND data integrity
  }
  ```

**Detection:**
- Pre-release: run `MigrationTestHelper` against all version combinations
- Production: Firebase Crashlytics will show `SQLiteException` with "duplicate column" on the specific version path
- CI: add a Gradle task that auto-generates migration tests for all version pairs

**Phase to address: Phase 3 (Room Database Setup) — the database schema is defined in this phase. Migration strategy must be chosen upfront.**

**Recovery:** If deployed with overlapping migrations, the fix depends on whether the app is still crash-looping:
1. If user can still open app: ship a fix version that removes the overlapping migration and provides a clean migration from the current version to the next
2. If user is crash-looping: ship an emergency update with `fallbackToDestructiveMigration()` (data loss acceptable?) and re-create schema
3. For local-only apps with no sync, **always test migrations locally before shipping**

---

### Pitfall 4: Markdown Rendering with Unmaintained / Incompatible Library

**What goes wrong:** Choosing a Markdown rendering library that is unmaintained, doesn't support Compose natively, or has unfixed rendering bugs for Chinese text / code blocks. The app's core "knowledge base" feature becomes unusable because articles render incorrectly.

**Why it happens:** The Markdown-in-Compose ecosystem is still maturing. Libraries go unmaintained, have version conflicts with the Compose BOM, or have known issues (e.g., `compose-rich-editor` lacks triple-backtick code blocks; some libraries don't handle Chinese mixed with Markdown syntax well).

**Consequences:**
- Rendered Chinese text has wrong font or is unreadable
- Code blocks don't render
- Lists, tables, or headings are broken
- Recomposition performance is terrible for large Markdown documents

**Prevention:**
- **Recommended:** Use `mikepenz/multiplatform-markdown-renderer` (pure renderer, actively maintained, supports code block syntax highlighting and CJK text via proper font fallback) or the newer `Compose Markdown` library (dedicated Compose renderer with `LazyMarkdownView` for large documents)
- **Do NOT use:** WebView-based Markdown renderers (performance overhead, heavy) or `compose-rich-editor` for rendering (lacks code block support, has known `StringIndexOutOfBoundsException` on Xiaomi HyperOS devices)
- Test with realistic Chinese Markdown content including:
  - Mixed Chinese/English text
  - Code blocks with Chinese comments
  - Tables with CJK characters
  - Long documents (1000+ lines) to test lazy loading
- Bundle a rendering test suite: render test Markdown strings and screenshot-compare

**Detection:**
- Visual inspection: render a known-good Markdown file and compare with a reference renderer (VS Code preview)
- Performance: benchmark rendering a 500-line Markdown document; skip frames = unacceptable
- Xiaomi-specific: test on physical Redmi device running HyperOS

**Phase to address: Phase 4 (Knowledge Base — Markdown Renderer) — the renderer is the core UX component for reading articles.**

**Recovery:** If the chosen library has unfixable bugs, switch to `multiplatform-markdown-renderer` as a drop-in replacement. Both libraries produce Compose `@Composable` output, so the migration is wrapping a new composable. However, custom styling/theming code tied to the old library must be reimplemented.

---

### Pitfall 5: Theme Switching Triggers Full Recomposition Without Smooth Transition

**What goes wrong:** Switching between light/dark mode causes a jarring flash or full-app recomposition because the theme change is not handled smoothly. The entire UI tree recomposes, losing animation states and scroll positions.

**Why it happens:** Many implementations change the `MaterialTheme` color scheme at the root, which causes all composables reading `MaterialTheme.colorScheme` to recompose. Without `animateColorAsState`, the transition is instant and jarring.

**Consequences:**
- Poor UX: users see a flash when switching themes
- Lost animation/interaction state during transition
- Frame drops on mid-range Xiaomi devices

**Prevention:**
- Use `animateColorAsState` for theme-related colors that change:
  ```kotlin
  val targetBg = if (isDark) darkBackground else lightBackground
  val animatedBg by animateColorAsState(
      targetValue = targetBg,
      animationSpec = tween(300)
  )
  ```
- Scope theme changes to `MaterialTheme` only — do not pass colorScheme down as individual parameters
- On first app launch, initialize with system theme BEFORE rendering any UI to avoid a flash from default light to user's dark preference
- Store theme preference in DataStore as a `Flow` and use `collectAsStateWithLifecycle()` in the theme composable

**Detection:**
- Enable "Show layout bounds" in Developer Options and observe whether a theme change triggers mass relayout
- Profile with Macrobenchmark: measure frame timing during a theme switch
- Visual: look for white flash when device is in dark mode and app launches

**Phase to address: Phase 1 (Theme/Material3 Setup) — the theme system is foundational and should be correct from the start.**

**Recovery:** Wrap the theme switching logic in `animateColorAsState` at the `MaterialTheme` color scheme level. This is a surgical change to the theme composable only. Test by rapidly switching themes 10 times and verifying no visual glitches.

---

### Pitfall 6: Chinese Text Rendering with Wrong Font Fallback

**What goes wrong:** The app uses a custom English font (for MIUI design consistency) that lacks CJK glyphs. Compose does not automatically fall back to a Chinese font for characters the primary font can't render. Users see "tofu" (empty boxes) for Chinese characters.

**Why it happens:** Compose's `FontFamily` treats multiple `Font` entries as a **weight/style resolution list**, not a **character-level fallback chain**. Unlike the platform `Typeface.CustomFallbackBuilder`, Compose doesn't try the next font if the first one lacks a glyph.

**Consequences:** Unreadable Chinese text throughout the app. Since this is a Chinese-language app targeting MIUI users, this is a **complete UX failure**.

**Prevention:**
- Use `Typeface.CustomFallbackBuilder` (API 29+, available on all targets since minSdk=36) to build a proper fallback chain:
  ```kotlin
  @Composable
  fun rememberChineseCapableFontFamily(
      primaryFontRes: Int,
      chineseFontRes: Int = R.font.noto_sans_sc_regular
  ): FontFamily {
      val context = LocalContext.current
      return remember {
          val primaryTypeface = ResourcesCompat.getFont(context, primaryFontRes)!!
          val chineseTypeface = ResourcesCompat.getFont(context, chineseFontRes)!!
          val platformTypeface = Typeface.CustomFallbackBuilder(
              FontFamily.Builder(Font.Builder(primaryTypeface).build()).build()
          )
              .addCustomFallback(
                  FontFamily.Builder(Font.Builder(chineseTypeface).build()).build()
              )
              .setSystemFallback("sans-serif")
              .build()
          FontFamily(platformTypeface)
      }
  }
  ```
- Bundle Noto Sans SC (or similar CJK font) in `res/font/` for offline use
- Apply the font family at the `MaterialTheme` level so all `Text` composables inherit it
- Do NOT rely on downloadable Google Fonts for Chinese fallback — they require Google Play Services, which is often missing on Chinese Xiaomi devices

**Detection:**
- Visually scan every screen for "tofu" (missing glyph boxes)
- Programmatic test: render a string with Chinese characters, measure rendered width; if width equals width of placeholder glyphs, fallback is broken
- Test on a device with no Google Play Services (most Chinese Xiaomi devices)

**Phase to address: Phase 1 (Theme/Material3 Setup) — font fallback must be configured at the theme level before any text is rendered.**

**Recovery:** Replace `FontFamily` at the theme level with the `CustomFallbackBuilder` approach. If fonts are currently specified only on individual `Text` composables, remove per-composable font specifications and centralize at `MaterialTheme`.

---

### Pitfall 7: SplashScreen API Incomplete Implementation (White Screen / Double Splash on MIUI)

**What goes wrong:** The app shows a white screen or double splash screen on startup, particularly on Xiaomi MIUI/HyperOS devices. Users see either a flash of white between splash and content, or two sequential splash screens (system splash + app splash).

**Why it happens:** Three common causes:
1. `installSplashScreen()` called AFTER `super.onCreate()` (doesn't work)
2. Missing `postSplashScreenTheme` in the splash theme
3. MIUI's custom launcher injects an additional splash icon if app icon/theme is not properly configured

**Consequences:** Unprofessional first impression. On some Xiaomi devices, the white screen can last 2-3 seconds, making the app feel broken.

**Prevention (specific to minSdk=36 / Android 16):**
- Since targeting only Android 16+, use the native `SplashScreen` API directly (no compat library needed), but the compat library `androidx.core:core-splashscreen` is still recommended for consistency
- **Critical ordering:**
  ```kotlin
  class MainActivity : ComponentActivity() {
      override fun onCreate(savedInstanceState: Bundle?) {
          val splashScreen = installSplashScreen()  // 1st: MUST be before super.onCreate
          enableEdgeToEdge()                         // 2nd: after installSplashScreen
          super.onCreate(savedInstanceState)          // 3rd: after both
          setContent { /* your app */ }
      }
  }
  ```
- Set `setKeepOnScreenCondition` to keep splash visible only during **critical** initialization (Room DB open, DataStore read) — NOT for data loading
- Do NOT create a separate `SplashActivity` — the SplashScreen API handles everything
- On Xiaomi devices, use a solid background color + simple icon in the splash theme; avoid complex XML drawables that MIUI might mishandle

**Detection:**
- Launch the app and observe: is there a white flash between splash and first frame?
- Record a slow-motion video of app startup (120fps) to detect brief white frames
- Check Play Console pre-launch reports for "double splash screen" warnings
- Test specifically on Xiaomi Redmi Note series running HyperOS

**Phase to address: Phase 1 (Project Scaffold + Splash Screen) — the splash screen is one of the first features implemented.**

**Recovery:** If white screen is already shipping:
1. Verify `installSplashScreen()` is called before `super.onCreate()`
2. Add `postSplashScreenTheme` to the splash theme XML
3. Add `enableEdgeToEdge()` between `installSplashScreen()` and `super.onCreate()`
4. If using a custom Activity theme, ensure `android:windowBackground` is set to avoid white flash during cold start

---

### Pitfall 8: DataStore Blocking the Main Thread on First Read

**What goes wrong:** The app freezes or shows a blank screen for 100-500ms on first launch (or after data-cleared cold start) because DataStore's first `data` emission blocks while reading the file.

**Why it happens:** DataStore reads and parses the entire preference file synchronously on first access. If the file is large or the device has slow I/O (common on mid-range Xiaomi devices), this blocks the coroutine — and if `flowOn(Dispatchers.IO)` is missing, it blocks the main thread.

**Consequences:**
- App appears frozen on first launch
- Splash screen dismisses but main UI is unresponsive
- Users may think the app crashed

**Prevention:**
- Always use `flowOn(Dispatchers.IO)` on DataStore flows:
  ```kotlin
  val settings: Flow<UiSettings> = context.dataStore.data
      .map { prefs -> parseSettings(prefs) }
      .flowOn(Dispatchers.IO)
  ```
- In the SplashScreen `setKeepOnScreenCondition`, include DataStore initialization:
  ```kotlin
  splashScreen.setKeepOnScreenCondition {
      !viewModel.isDataStoreReady.value
  }
  ```
- For this project (pure local app, no sync), **Preferences DataStore is sufficient** — Proto DataStore adds complexity (protobuf compilation, schema management) with no benefit since settings are simple key-value pairs (theme mode, font scale).

**Detection:**
- Profile cold start with Macrobenchmark; look for gaps between `installSplashScreen` and first frame
- Add `Hugo`-style logging: `Log.d("DataStore", "read took ${elapsed}ms")`
- On first launch, check if UI thread shows a "blocked" state in the profiler

**Phase to address: Phase 3 (DataLayer — DataStore Setup) — DataStore is initialized alongside Room.**

**Recovery:** Add `flowOn(Dispatchers.IO)` to all DataStore reads. If the file has grown large from accumulated preference writes, clear and regenerate (acceptable for local-only user preferences). Consider splitting frequently-changed values (e.g., theme toggle) into a separate DataStore file from rarely-changed values (e.g., font scale).

---

### Pitfall 9: Room Flow Collected in Wrong Lifecycle Scope (Memory Leak / Extra DB Reads)

**What goes wrong:** A `Flow` from Room's DAO is collected in a composable scope without proper lifecycle management. When the user navigates away and back, a new collector is attached without canceling the old one, leading to multiple active database observers.

**Why it happens:** `collectAsState()` in Compose attaches to the composition's lifecycle. If the composable is removed and re-added (tab switch in bottom nav), the old collector may not be properly cancelled, or a new one starts before the old one finishes.

**Consequences:**
- Room keeps the database connection alive with multiple observers
- Extra disk I/O from redundant queries
- Memory leak: old ViewModel retains stale data for navigated-away screens

**Prevention:**
- Collect Room Flows in the ViewModel using `stateIn` with proper sharing strategy:
  ```kotlin
  class KnowledgeViewModel : ViewModel() {
      val articles: StateFlow<List<Article>> = articleDao.getAllFlow()
          .stateIn(
              scope = viewModelScope,
              started = SharingStarted.WhileSubscribed(5_000), // 5s stop timeout
              initialValue = emptyList()
          )
  }
  ```
- In Compose, use `collectAsStateWithLifecycle()` instead of `collectAsState()`:
  ```kotlin
  val articles by viewModel.articles.collectAsStateWithLifecycle()
  ```
- For search queries, use `flatMapLatest` to cancel previous search query when a new one is typed:
  ```kotlin
  val searchResults = searchQuery
      .flatMapLatest { query -> articleDao.search(query) }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
  ```

**Detection:**
- Enable `StrictMode` and look for leaked SQLiteCloseable objects
- Monitor Room's query counter in the debug build — if queries increase without new user actions, collectors are leaking
- Log ViewModel.onCleared() — if it's not called when navigating away, the ViewModel is retained

**Phase to address: Phase 3 (DataLayer — Room DAO + Flow) — correct Flow collection patterns must be established at the same time the DAO layer is built.**

**Recovery:** Replace `collectAsState()` with `collectAsStateWithLifecycle()` in all composables collecting Room Flows. Add `SharingStarted.WhileSubscribed(5000)` to all `stateIn()` calls. This is a systematic audit of all collection sites.

---

### Pitfall 10: Room Relation N+1 Query Performance

**What goes wrong:** Using Room's `@Relation` annotation without awareness that it generates N+1 queries. For a knowledge base that shows article lists with their tags, `@Relation` generates 1 query for articles + N queries for tags. With 100 articles, that's 101 queries.

**Why it happens:** Room's `@Relation` annotation on `@Embedded` POJOs generates a separate SQL query for each parent row to fetch child rows. This is transparent in code but expensive at runtime.

**Consequences:**
- Slow article list loading as knowledge base grows
- UI jank when scrolling through article list (each visible item triggers tags query)
- Battery drain from excessive SQLite reads

**Prevention:**
- Avoid `@Relation` for list-of-articles screens. Use JOIN queries with multimap return types instead:
  ```kotlin
  @Query("""
      SELECT a.*, t.* FROM article a
      LEFT JOIN article_tag_join atj ON a.id = atj.articleId
      LEFT JOIN tag t ON atj.tagId = t.id
      ORDER BY a.updatedAt DESC
  """)
  fun loadArticlesWithTags(): Map<Article, List<Tag>>
  ```
- Only use `@Relation` when loading a single parent (e.g., "load this one article with all its tags for editing")
- For the full-text search feature (KNO-08), use FTS4 tables and `MATCH` queries — do not rely on `@Relation`-based post-filtering

**Detection:**
- Enable Room query logging: `Room.databaseBuilder(...).enableQueryCallback { sql, args -> Log.d("Room", sql) }`
- Count queries: for a list of 50 articles, if you see 50+ tag queries, the N+1 problem is active
- Use Android Studio's Database Inspector to monitor query count

**Phase to address: Phase 4 (Knowledge Base — Database Schema) — the article-tag schema and query patterns are designed here.**

**Recovery:** Replace `@Relation`-based DAO methods with JOIN-based multimap queries. The data class changes are localized to the DAO layer — the ViewModel and UI don't need to change since they receive `List<Article>` and `List<Tag>` either way.

---

### Pitfall 11: LaunchedEffect Infinite Loop / Wrong Dependency Keys

**What goes wrong:** `LaunchedEffect` is used with a constantly-changing key (e.g., a Flow emission or a recomputing value), causing the effect to restart on every change and potentially creating infinite loops.

**Example:**
```kotlin
// BAD: LaunchedEffect restarts every time currentTimeMs changes (every 16ms!)
val currentTimeMs by viewModel.currentTimeMs.collectAsState()
LaunchedEffect(currentTimeMs) {
    viewModel.updateCountdown()  // called 60 times/second
}
```

**Why it happens:** `LaunchedEffect(key)` restarts whenever `key` changes. If the key changes on every recomposition, the effect restarts every frame, cancelling and relaunching the coroutine.

**Consequences:**
- Infinite loop: effect launches -> changes state -> state change triggers recomposition -> key changes -> effect relaunches
- ANR or battery drain from the coroutine restart overhead
- Lost data if the effect was mid-operation (e.g., mid-way through saving to Room)

**Prevention:**
- Use `LaunchedEffect(Unit)` for one-shot operations (e.g., initial data load)
- Use `LaunchedEffect(Unit)` with internal `snapshotFlow` for observing State:
  ```kotlin
  LaunchedEffect(Unit) {
      snapshotFlow { viewModel.currentTimeMs.value }
          .collect { viewModel.updateCountdown() }
  }
  ```
- Use `LaunchedEffect(true)` with boolean literals — never pass a mutable State as the key
- When dependencies ARE needed, use stable identifiers (UUID, article ID) not the entire data object:
  ```kotlin
  LaunchedEffect(articleId) { // stable String, not article object
      viewModel.loadArticle(articleId)
  }
  ```

**Detection:**
- Add log statements inside `LaunchedEffect` blocks; if they fire more than expected, the key is wrong
- Check for infinite recomposition loops in Layout Inspector
- Enable "Don't keep activities" in Developer Options and verify effects survive process death correctly

**Phase to address: Phase 1 (foundational patterns) — all developers need to understand this pattern from the first `LaunchedEffect` usage.**

**Recovery:** Replace the mutable key with `Unit` + `snapshotFlow` for state observation. The fix is localized to the specific `LaunchedEffect` block.

---

### Pitfall 12: Process Death State Loss for Editor / Note-Taking

**What goes wrong:** User is writing a long Markdown article in the knowledge base editor. Phone runs low on memory, Android kills the process. When the user returns, the editor content is gone because it was only held in ViewModel memory.

**Why it happens:** ViewModel survives configuration changes but NOT process death. `rememberSaveable` only works for simple types stored in Bundle (~500KB limit). A full Markdown article can exceed this easily.

**Consequences:** Complete loss of user-typed content. This is catastrophic for a note-taking / knowledge management app. Users will not trust the app again.

**Prevention:**
- **Auto-save drafts to Room every 5-10 seconds** while the editor is open:
  ```kotlin
  LaunchedEffect(articleId) {
      while (isActive) {
          delay(5_000) // auto-save every 5 seconds
          if (currentContent != savedContent) {
              viewModel.saveDraft(currentContent)
              savedContent = currentContent
          }
      }
  }
  ```
- Restore draft from Room on editor open:
  ```kotlin
  LaunchedEffect(articleId) {
      val draft = articleDao.getDraft(articleId)
      if (draft != null) currentContent = draft.content
  }
  ```
- Use `SavedStateHandle` for the article ID being edited (small state, survives process death):
  ```kotlin
  class EditorViewModel(
      savedStateHandle: SavedStateHandle,
      private val articleDao: ArticleDao
  ) : ViewModel() {
      private val articleId: String = savedStateHandle["articleId"] ?: ""
      // ...
  }
  ```
- For unsaved editor content, show a "draft recovery" dialog on cold start

**Detection:**
- Enable "Don't keep activities" in Developer Options
- Open editor, type substantial content, background the app, force-stop via `adb shell am force-stop`, then relaunch
- Check if content is restored

**Phase to address: Phase 5 (Markdown Editor) — the editor is built in this phase and MUST include draft auto-save.**

**Recovery:** Add Room-based draft table and auto-save mechanism. If process death already caused data loss, the draft recovery feature will only help going forward — lost drafts are gone.

---

## Moderate Pitfalls

### Pitfall 13: Bottom Navigation Tab Content Not Surviving Tab Switch

**What goes wrong:** Switching between bottom tabs destroys the previous tab's navigation state. User was browsing deeply in the Knowledge tab, switches to Info tab, switches back — Knowledge tab resets to root.

**Why it happens:** Without `saveState`/`restoreState` (in Nav2) or proper multi-back-stack management (in Nav3), each tab switch removes the previous tab's destinations from the back stack.

**Prevention (Nav3):**
- Use separate back stack instances for each tab root
- When switching tabs, store the current back stack and restore the target tab's back stack
- Use the "Segmented Single Stack" pattern with anchor markers for each tab root:
  ```kotlin
  // Tab roots act as markers in a single stack
  // When switching tabs, reorder segments so target tab's segment is on top
  fun bringTabToTop(tab: TabKey) {
      val segment = stack.dropWhile { it !is TabRoot || it != tab }
      stack.removeAll(segment.toSet())
      stack.addAll(segment)
  }
  ```

**Detection:** Navigate deeply in a tab (e.g., Knowledge > Category > Article), switch to another tab, switch back. If the first tab resets, the multi-back-stack is not working.

**Phase to address: Phase 2 (Navigation Shell + Bottom Tabs)**

---

### Pitfall 14: MIUI Background Process Kill Breaking Auto-Save

**What goes wrong:** Xiaomi's MIUI/HyperOS aggressively kills background processes, including the foreground app when the user switches away for a few minutes. This terminates the auto-save coroutine before it can save the latest editor content.

**Why it happens:** MIUI/HyperOS does not fully comply with Android's process lifecycle. Even with a foreground notification, MIUI may kill the process when the app is in recents. `viewModelScope` coroutines are cancelled when the process is killed.

**Prevention:**
- Save draft immediately when the user presses the system back button (do not rely solely on periodic auto-save)
- Use `DisposableEffect` in the editor composable to save on composition disposal:
  ```kotlin
  DisposableEffect(Unit) {
      onDispose {
          // Save draft synchronously on composition disposal
          viewModel.saveDraftOnExit()
      }
  }
  ```
- Save to Room on `onPause()` via `LifecycleEventObserver`:
  ```kotlin
  LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
      viewModel.saveDraftOnExit()
  }
  ```
- For critical unsaved state, use `ProcessLifecycleOwner` to detect when the app goes to background

**Detection:** Open editor, type content, quickly switch to another app, wait 30 seconds, reopen. Check if latest content is preserved.

**Phase to address: Phase 5 (Markdown Editor) — auto-save must handle MIUI's aggressive kill behavior.**

---

### Pitfall 15: Markdown Full-Text Search Using LIKE Instead of FTS4

**What goes wrong:** Implementing article search with `LIKE '%keyword%'` which is O(n) per article, non-indexed, and doesn't support Chinese word segmentation. The search becomes unusable as the knowledge base grows beyond a few hundred articles.

**Prevention:**
- Use Room FTS4 (Full-Text Search) extension:
  ```kotlin
  @Entity(tableName = "articles_fts")
  @Fts4(contentEntity = Article::class)
  data class ArticleFts(
      val title: String,
      val content: String
  )
  ```
- Query with `MATCH`:
  ```kotlin
  @Query("SELECT a.* FROM articles a JOIN articles_fts fts ON a.id = fts.rowid WHERE articles_fts MATCH :query")
  fun search(query: String): Flow<List<Article>>
  ```
- For Chinese text, use a custom tokenizer or ICU tokenizer to support word segmentation
- FTS4 is available in SQLite on all Android versions, no extra dependency needed

**Detection:** Test search with 500+ articles. If search takes more than 500ms, the query is likely a full table scan.

**Phase to address: Phase 4 (Knowledge Base — Full-Text Search)**

---

### Pitfall 16: Strong Skipping Mode Changes Stability Rules

**What goes wrong:** Developers write `@Stable` / `@Immutable` annotations based on old Compose compiler rules. With Strong Skipping Mode (default since Compose Compiler 1.5.4+), unstable parameters are now compared by instance identity (`===`) instead of always triggering recomposition, which can mask problems during development but cause bugs in production.

**Prevention:**
- Know that Strong Skipping Mode auto-memoizes lambdas inside `@Composable` functions
- BUT it does NOT auto-memoize lambdas inside `LazyListScope` — these still need explicit `remember`
- Test in release + R8 build on a real device: debug builds use different compiler settings
- Remove `@Stable` from non-essential data classes; let Strong Skipping Mode handle identity comparison

**Detection:** Layout Inspector's recomposition count will show lower values than expected — verify correctness, not just performance.

**Phase to address: Phase 1 (Project Setup) — configure the Compose compiler and educate the team on Strong Skipping Mode implications.**

---

### Pitfall 17: Gradle Build Configuration for Android 16 (API 36)

**What goes wrong:** Build configuration doesn't match the minSdk=36 / targetSdk=36 requirements, causing build failures or unexpected behavior. Outdated AGP version doesn't support API 36 level features.

**Prevention:**
- Use AGP 8.7+ (required for API 36 support)
- Set `compileSdk = 36` and `targetSdk = 36` in the app module
- Ensure all Compose BOM dependencies are on versions compatible with API 36
- Use Kotlin 2.0+ with the compose compiler plugin (not the separate `kotlin-compose-compiler` artifact)

**Detection:** Build fails with "failed to find platform SDK 36" or similar. Watch for deprecation warnings about `compileSdk` being lower than available.

**Phase to address: Phase 1 (Project Scaffold)**

---

### Pitfall 18: MIUI Launcher Resetting Default Launcher

**What goes wrong:** This app is not a launcher, but the general issue of MIUI overriding user preferences means developers targeting Xiaomi need to be aware that MIUI may interfere with app defaults (e.g., opening links, handling file types).

**Prevention:** For this app specifically (not a launcher), this is less relevant but still worth knowing: if the app registers for custom URL schemes or file types, test that MIUI correctly routes intents to your app.

**Phase to address: Phase 6 (Integration Testing on MIUI)**

---

## Minor Pitfalls

### Pitfall 19: Hardcoding Colors Instead of Using MaterialTheme.colorScheme

**What goes wrong:** Individual composables use `Color(0xFF...)` instead of `MaterialTheme.colorScheme.{primary,secondary,background,etc}`. Dark mode fails because the hardcoded colors don't change.

**Prevention:** Always use `MaterialTheme.colorScheme` semantic colors. Never hardcode colors in composables.

### Pitfall 20: Not Using `contentType` in LazyColumn with Mixed Item Types

**What goes wrong:** A LazyColumn with different item types (headers, articles, dividers) doesn't use `contentType`, causing unnecessary recompositions when items move or change.

**Prevention:** Always provide `contentType` when using multiple item types:
```kotlin
items(items, key = { it.id }, contentType = { it.type }) { item ->
    when (item) {
        is ListItem.Article -> ArticleCard(item)
        is ListItem.Header -> SectionHeader(item)
    }
}
```

### Pitfall 21: Not Testing on Physical Xiaomi Device Before Release

**What goes wrong:** Everything works on the emulator / Pixel device but breaks on the target Xiaomi device.

**Prevention:** Buy a mid-range Xiaomi Redmi device (Note series) for physical testing. Test:
- App cold start (splash screen)
- Theme switching
- Chinese text rendering
- Background process behavior
- Navigation state preservation across tab switches

### Pitfall 22: Forgetting to Call `super.onCreate()` After `installSplashScreen()`

**What goes wrong:** If `super.onCreate()` is called before `installSplashScreen()`, the splash screen API doesn't work properly, leading to a white screen on startup.

**Prevention:** Make it a team convention: in `MainActivity.onCreate()`, the order is always:
```kotlin
installSplashScreen()
enableEdgeToEdge()
super.onCreate(savedInstanceState)
```

---

## Phase-Specific Warnings

| Phase Topic | Likely Pitfall | Mitigation | Severity |
|-------------|---------------|------------|----------|
| Phase 1: Project Scaffold | Gradle config for API 36 | Use AGP 8.7+, Compose BOM 2025.x | Medium |
| Phase 1: Theme Setup | Font fallback for Chinese | Use `Typeface.CustomFallbackBuilder` at theme level | **Critical** |
| Phase 1: Splash Screen | White screen on MIUI | `installSplashScreen()` before `super.onCreate()`, add `postSplashScreenTheme` | **Critical** |
| Phase 2: Bottom Navigation | Tab state lost on switch | Nav3 multi-back-stack with unique `contentKey` | **Critical** |
| Phase 2: Navigation | Nav3 experimental APIs | Pinned to stable Nav3 v1.0 release | High |
| Phase 3: Room Schema | Migration strategy not set | Choose autoMigrations xor manual per version range; test with MigrationTestHelper | **Critical** |
| Phase 3: DataStore | Main thread blocking on first read | `flowOn(Dispatchers.IO)` on all DataStore flows | High |
| Phase 3: Room Flows | Memory leak from uncancelled collectors | `collectAsStateWithLifecycle()` + `WhileSubscribed(5000)` | Medium |
| Phase 4: Knowledge List | N+1 queries with @Relation | Use JOIN queries with multimap returns | High |
| Phase 4: Full-Text Search | LIKE query performance | Use Room FTS4 with MATCH | High |
| Phase 5: Markdown Renderer | Wrong library selection | Prefer `multiplatform-markdown-renderer` or `Compose Markdown` | **Critical** |
| Phase 5: Editor | Process death data loss | Auto-save draft to Room every 5s; save on back/onPause | **Critical** |
| Phase 6: MIUI Testing | Splash/background/settings issues | Physical Xiaomi device testing | High |

---

## Sources

- [Jetpack Compose Performance Best Practices (Android Developers)](https://developer.android.com/develop/ui/compose/performance/bestpractices)
- [Compose Stability (Android Developers)](https://developer.android.com/develop/ui/compose/performance/stability)
- [Compose performance: Strong Skipping Mode](https://dev.to/software_mvp-factory/jetpack-compose-recomposition-at-scale-how-strong-skipping-mode-changes-the-stability-rules-you-4a80)
- [Navigation 3 Stable Announcement](https://android-developers.googleblog.com/2025/11/jetpack-navigation-3-is-stable.html)
- [Nav3 contentKey collision issue](https://github.com/android/nav3-recipes/issues/247)
- [Nav3 multi-back-stack recipes](https://github.com/android/nav3-recipes)
- [Room AutoMigration vs Manual Migration conflict (Juejin)](https://juejin.cn/post/7515503402240770058)
- [Room cross-version migration pitfalls (Juejin)](https://juejin.cn/post/7515474893853818930)
- [Room query relationships (Android Developers)](https://developer.android.com/training/data-storage/room/relationships)
- [Room N+1 query problem (StackOverflow)](https://stackoverflow.com/questions/79583866/android-room-efficiently-querying-relationships-with-large-datasets-and-avoidi)
- [multiplatform-markdown-renderer (GitHub)](https://github.com/mikepenz/multiplatform-markdown-renderer)
- [Compose Markdown (CSDN)](https://blog.csdn.net/a582816317/article/details/152009587)
- [Compose Rich Editor known bugs on Xiaomi](https://github.com/MohamedRejeb/compose-rich-editor/issues/327)
- [Xiaomi SplashScreen white screen (StackOverflow)](https://stackoverflow.com/questions/74274235/android-12s-splash-screen-api-getting-white-screen-after-splash-screen-on-some)
- [HyperOS bug report — white flash in dark mode](https://zh-cn.xiaomi-miui.gr/hyperos-weekly-bug-report-vol-209/)
- [Typeface.CustomFallbackBuilder (Android Developers)](https://developer.android.com/reference/android/graphics/Typeface.CustomFallbackBuilder)
- [Compose font fallback (StackOverflow)](https://stackoverflow.com/questions/72772861/fallback-fonts-with-jetpack-compose-in-android)
- [DataStore best practices (Android Developers)](https://developer.android.com/topic/libraries/architecture/datastore)
- [SavedStateHandle with Compose (Android Developers)](https://developer.android.com/reference/androidx/lifecycle/SavedStateHandle)
- [MIUI developer ecosystem policy](https://dev.mi.com/xiaomihyperos/documentation/detail?pId=1321)
- [MIUI background process kill (Google Issue Tracker)](https://issuetracker.google.com/issues/430616163)
- [Compose performance audit (Dev.to)](https://dev.to/vio_di_code/jetpack-compose-performance-audit-4okp)
- [Room Crash bug with ORDER BY RANDOM + @Relation](https://deepwiki.com/chigichan24/RoomCrash)
