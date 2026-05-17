# Architecture Patterns: ZhiYu (知屿)

**Domain:** Android Personal Efficiency App
**Researched:** 2026-05-17
**Pattern:** MVVM + Unidirectional Data Flow (UDF) with feature-first packaging
**Confidence:** HIGH (official Android guides + widespread community consensus)

---

## 1. Overall Architecture: Three-Layer Clean Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                        UI LAYER                              │
│                                                              │
│  ┌─────────────┐  ┌─────────────┐  ┌──────────────────────┐ │
│  │  Composables  │  │  Screens    │  │  Navigation (NavHost) │ │
│  │  (Widgets)    │  │  (Layout)   │  │  + Bottom Bar        │ │
│  └──────┬───────┘  └──────┬──────┘  └──────────────────────┘ │
│         │                 │                                   │
│    state (read)      events (up)                             │
│         │                 │                                   │
│  ┌──────┴─────────────────┴──────────────────────────────┐  │
│  │                   VIEWMODEL LAYER                      │  │
│  │                                                       │  │
│  │  StateFlow<UiState>  ←  onIntent/Action methods       │  │
│  │                                                       │  │
│  │  - Exposes single StateFlow per screen                │  │
│  │  - Handles business logic (for local-only apps)       │  │
│  │  - Maps domain data to UI state                       │  │
│  └──────────────────────┬────────────────────────────────┘  │
│                         │                                    │
│                    calls repository                          │
│                         │                                    │
│  ┌──────────────────────┴────────────────────────────────┐  │
│  │                    DATA LAYER                          │  │
│  │                                                       │  │
│  │  ┌──────────────────┐  ┌───────────────────────────┐  │  │
│  │  │  Room Database    │  │  DataStore Preferences    │  │  │
│  │  │  (Entities, DAOs) │  │  (Settings, Theme)        │  │  │
│  │  └────────┬─────────┘  └───────────┬───────────────┘  │  │
│  │           │                        │                   │  │
│  │  ┌────────┴────────────────────────┴───────────────┐  │  │
│  │  │             REPOSITORY LAYER                     │  │  │
│  │  │  - Single source of truth                       │  │  │
│  │  │  - Maps entities ↔ domain models                │  │  │
│  │  │  - Exposes Flow<T> for reactive reads            │  │  │
│  │  └──────────────────────────────────────────────────┘  │  │
│  └─────────────────────────────────────────────────────────┘  │
│                                                              │
│  ┌──────────────────────────────────────────────────────────┐│
│  │              DEPENDENCY INJECTION (Koin)                 ││
│  │  - Koin modules registered in Application class          ││
│  │  - ViewModel modules, Repository modules, Room modules   ││
│  └──────────────────────────────────────────────────────────┘│
└──────────────────────────────────────────────────────────────┘
```

### Why This Architecture for a Local-Only App

For a pure local app with no backend, the primary architectural concerns are **data integrity**, **state consistency across configuration changes**, and **testability**. Three-layer MVVM with UDF addresses all three:

- **Data layer** ensures all database access goes through a single path - no scattered SQL queries.
- **ViewModel layer** survives rotation (via ViewModel's lifecycle), so in-flight operations and loaded state persist.
- **UI layer** is a pure function of state - recomposition is automatic and predictable.

**No UseCase layer** is recommended here. For a local-only CRUD app with no complex business rules, UseCases add indirection without value. ViewModels call Repositories directly. If business logic grows complex later, UseCases can be extracted without changing the architecture.

---

## 2. Package Structure: Feature-First

```
com.zhiyu.app/
├── ZhiYuApplication.kt              # Application class, Koin init
├── MainActivity.kt                  # Single Activity, entry point
│
├── navigation/
│   ├── AppNavHost.kt                # Root NavHost setup
│   ├── BottomNavBar.kt              # Bottom navigation bar composable
│   └── Route.kt                     # Type-safe route definitions
│
├── ui/                              # Shared UI components
│   ├── theme/
│   │   ├── Theme.kt                 # MIUI theme + dark mode
│   │   ├── Color.kt                 # Color palette
│   │   ├── Type.kt                  # Typography
│   │   └── Shape.kt                 # Shapes
│   └── components/
│       ├── TopBar.kt                # Shared top bar
│       ├── MarkdownEditor.kt        # Markdown editing composable
│       ├── MarkdownPreview.kt       # Markdown rendering composable
│       └── SearchBar.kt             # Shared search bar
│
├── feature/
│   ├── dashboard/                   # Tab 1: 信息 (Dashboard)
│   │   ├── DashboardScreen.kt       # Composable screen
│   │   ├── DashboardViewModel.kt    # ViewModel
│   │   └── DashboardUiState.kt     # UI state data class
│   │
│   ├── knowledge/                   # Tab 2: 知识库 (Knowledge Base)
│   │   ├── list/
│   │   │   ├── KnowledgeListScreen.kt
│   │   │   ├── KnowledgeListViewModel.kt
│   │   │   └── KnowledgeListUiState.kt
│   │   ├── detail/
│   │   │   ├── ArticleDetailScreen.kt
│   │   │   ├── ArticleDetailViewModel.kt
│   │   │   └── ArticleDetailUiState.kt
│   │   ├── editor/
│   │   │   ├── ArticleEditorScreen.kt
│   │   │   ├── ArticleEditorViewModel.kt
│   │   │   └── ArticleEditorUiState.kt
│   │   └── search/
│   │       ├── SearchScreen.kt
│   │       ├── SearchViewModel.kt
│   │       └── SearchUiState.kt
│   │
│   ├── notes/                       # Tab 3: 小记 (Quick Notes)
│   │   ├── QuickNoteScreen.kt
│   │   ├── QuickNoteViewModel.kt
│   │   └── QuickNoteUiState.kt
│   │
│   ├── tools/                       # Tab 4: 发现 (Tools)
│   │   ├── ToolsScreen.kt
│   │   └── ToolsViewModel.kt
│   │
│   └── profile/                     # Tab 5: 我的 (Profile / Settings)
│       ├── ProfileScreen.kt
│       ├── ProfileViewModel.kt
│       ├── SettingsScreen.kt
│       └── SettingsViewModel.kt
│
├── data/
│   ├── local/
│   │   ├── ZhiYuDatabase.kt        # Room database definition
│   │   ├── entity/
│   │   │   ├── ArticleEntity.kt    # Article table
│   │   │   ├── CategoryEntity.kt   # Category table
│   │   │   ├── TagEntity.kt        # Tag table
│   │   │   ├── ArticleTagCrossRef.kt
│   │   │   └── QuickNoteEntity.kt  # Quick notes table
│   │   ├── dao/
│   │   │   ├── ArticleDao.kt
│   │   │   ├── CategoryDao.kt
│   │   │   ├── TagDao.kt
│   │   │   └── QuickNoteDao.kt
│   │   ├── converter/
│   │   │   └── Converters.kt       # Type converters (Date, List<String>)
│   │   └── datastore/
│   │       └── SettingsDataStore.kt # DataStore wrapper
│   │
│   ├── repository/
│   │   ├── ArticleRepository.kt
│   │   ├── CategoryRepository.kt
│   │   ├── TagRepository.kt
│   │   └── SettingsRepository.kt
│   │
│   └── model/
│       ├── Article.kt              # Domain model
│       ├── Category.kt
│       ├── Tag.kt
│       └── QuickNote.kt
│
└── di/
    ├── AppModule.kt                 # Koin module: Room, DataStore
    ├── RepositoryModule.kt          # Koin module: Repositories
    └── ViewModelModule.kt           # Koin module: ViewModels
```

### Feature-First vs Layer-First Rationale

For this project, feature-first is the better choice because:

| Consideration | Feature-First | Layer-First |
|---------------|--------------|-------------|
| Navigation to file mapping | One feature = one package | Scattered across layers |
| Onboarding new features | Add package | Touch 4+ packages |
| Dead code detection | Package disappears cleanly | Orphaned classes linger |
| Compose + ViewModel cohesion | Side by side | Split across trees |

The "feature" here is roughly one bottom-nav tab plus its sub-screens. Each feature package contains everything that screen needs: its Composable, its ViewModel, and its UI state class. No feature package imports UI widgets from another feature - shared components live in `ui/components/`.

---

## 3. Navigation Architecture

### Route Definitions (Type-Safe with Kotlin Serialization)

```kotlin
// navigation/Route.kt
import kotlinx.serialization.Serializable

// -- Bottom nav tab roots --
@Serializable object DashboardRoute       // Tab 1: 信息
@Serializable object KnowledgeBaseRoute   // Tab 2: 知识库
@Serializable object QuickNotesRoute      // Tab 3: 小记
@Serializable object ToolsRoute           // Tab 4: 发现 (Tools)
@Serializable object ProfileRoute         // Tab 5: 我的 (Profile)

// -- Sub-routes --
@Serializable data class ArticleDetailRoute(val articleId: Long)
@Serializable data class ArticleEditorRoute(val articleId: Long? = null)  // null = new article
@Serializable object SettingsRoute
@Serializable object AboutRoute
```

### NavHost with Bottom Navigation

```kotlin
// navigation/AppNavHost.kt
@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = DashboardRoute
    ) {
        // Tab 1: Dashboard
        composable<DashboardRoute> {
            DashboardScreen(
                onNavigateToSettings = { navController.navigate(SettingsRoute) }
            )
        }

        // Tab 2: Knowledge Base (with nested navigation)
        composable<KnowledgeBaseRoute> {
            KnowledgeListScreen(
                onArticleClick = { id -> navController.navigate(ArticleDetailRoute(id)) },
                onNewArticle = { navController.navigate(ArticleEditorRoute()) }
            )
        }
        composable<ArticleDetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<ArticleDetailRoute>()
            ArticleDetailScreen(
                articleId = route.articleId,
                onEdit = { navController.navigate(ArticleEditorRoute(route.articleId)) },
                onBack = { navController.popBackStack() }
            )
        }
        composable<ArticleEditorRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<ArticleEditorRoute>()
            ArticleEditorScreen(
                articleId = route.articleId,
                onBack = { navController.popBackStack() }
            )
        }

        // Tab 3: Quick Notes
        composable<QuickNotesRoute> {
            QuickNoteScreen()
        }

        // Tab 4: Tools
        composable<ToolsRoute> {
            ToolsScreen()
        }

        // Tab 5: Profile
        composable<ProfileRoute> {
            ProfileScreen(
                onNavigateToSettings = { navController.navigate(SettingsRoute) }
            )
        }

        // Settings (presented as a detail screen, keeps bottom nav)
        composable<SettingsRoute> {
            SettingsScreen(onBack = { navController.popBackStack() })
        }

        // About
        composable<AboutRoute> {
            AboutScreen(onBack = { navController.popBackStack() })
        }
    }
}
```

### Bottom Navigation Bar

```kotlin
// navigation/BottomNavBar.kt
data class BottomNavItem(
    val route: Any,               // Type-safe route object
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(DashboardRoute,     "信息", Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
    BottomNavItem(KnowledgeBaseRoute, "知识库", Icons.Filled.Book, Icons.Outlined.Book),
    BottomNavItem(QuickNotesRoute,    "小记", Icons.Filled.EditNote, Icons.Outlined.EditNote),
    BottomNavItem(ToolsRoute,         "发现", Icons.Filled.GridView, Icons.Outlined.GridView),
    BottomNavItem(ProfileRoute,       "我的", Icons.Filled.Person, Icons.Outlined.Person)
)

@Composable
fun BottomNavBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Only show bottom bar on tab roots (not detail screens)
    val bottomBarRoutes = bottomNavItems.map { it.route::class.qualifiedName }
    val showBottomBar = currentDestination?.route in bottomBarRoutes

    if (showBottomBar) {
        NavigationBar {
            bottomNavItems.forEach { item ->
                val selected = currentDestination?.hierarchy?.any {
                    it.route == item.route::class.qualifiedName
                } == true

                NavigationBarItem(
                    selected = selected,
                    icon = {
                        Icon(
                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.label
                        )
                    },
                    label = { Text(item.label) },
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}
```

### MainActivity Wiring

```kotlin
// MainActivity.kt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            ZhiYuTheme {  // Theme wrapper handles dark mode via DataStore
                val navController = rememberNavController()

                Scaffold(
                    bottomBar = { BottomNavBar(navController) }
                ) { innerPadding ->
                    AppNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
```

### Navigation Architecture Rationale

| Decision | Why |
|----------|-----|
| Type-safe routes with `@Serializable` | Compile-time safety, no string route typos, no Safe Args XML |
| Flat NavHost (no nested graphs) | For this app's depth (1-2 levels max), nested graphs add complexity without benefit |
| Bottom bar shown only on tab roots | Detail screens (article editor, settings) get full screen without bottom bar distraction |
| `saveState`/`restoreState` on tab switch | Each tab preserves its scroll position and back stack independently |
| `launchSingleTop` | Prevents duplicate tab instances in the back stack |

---

## 4. State Management Pattern

### UiState: Single Source of Truth per Screen

Each screen defines a single immutable data class representing ALL possible UI states:

```kotlin
// feature/knowledge/list/KnowledgeListUiState.kt
@Immutable
data class KnowledgeListUiState(
    val articles: List<ArticleUiModel> = emptyList(),
    val categories: List<CategoryUiModel> = emptyList(),
    val selectedCategoryId: Long? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

data class ArticleUiModel(
    val id: Long,
    val title: String,
    val summary: String,
    val categoryName: String,
    val tags: List<String>,
    val updatedAt: String,
    val isFavorite: Boolean
)
```

### ViewModel: State Production

```kotlin
// feature/knowledge/list/KnowledgeListViewModel.kt
class KnowledgeListViewModel(
    private val articleRepository: ArticleRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _filterState = MutableStateFlow(FilterState())
    
    private data class FilterState(
        val selectedCategoryId: Long? = null,
        val searchQuery: String = ""
    )

    val uiState: StateFlow<KnowledgeListUiState> = combine(
        articleRepository.observeAllArticles(),
        categoryRepository.observeAllCategories(),
        _filterState
    ) { articles, categories, filter ->
        val filteredArticles = articles
            .filter { a -> filter.selectedCategoryId?.let { a.categoryId == it } ?: true }
            .filter { a -> filter.searchQuery.isEmpty() || a.title.contains(filter.searchQuery, ignoreCase = true) }
            .map { it.toUiModel() }

        KnowledgeListUiState(
            articles = filteredArticles,
            categories = categories.map { it.toUiModel() },
            selectedCategoryId = filter.selectedCategoryId,
            searchQuery = filter.searchQuery,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = KnowledgeListUiState(isLoading = true)
    )

    fun onCategorySelected(categoryId: Long?) {
        _filterState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun onSearchQueryChanged(query: String) {
        _filterState.update { it.copy(searchQuery = query) }
    }
}
```

### Composable: State Consumption

```kotlin
// feature/knowledge/list/KnowledgeListScreen.kt
@Composable
fun KnowledgeListScreen(
    onArticleClick: (Long) -> Unit,
    onNewArticle: () -> Unit,
    viewModel: KnowledgeListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    KnowledgeListContent(
        uiState = uiState,
        onCategorySelected = viewModel::onCategorySelected,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onArticleClick = onArticleClick,
        onNewArticle = onNewArticle
    )
}

// Pure composable — previewable, testable, reusable
@Composable
fun KnowledgeListContent(
    uiState: KnowledgeListUiState,
    onCategorySelected: (Long?) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onArticleClick: (Long) -> Unit,
    onNewArticle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Category chips
        CategoryChipRow(
            categories = uiState.categories,
            selectedId = uiState.selectedCategoryId,
            onSelected = onCategorySelected
        )
        // Article list
        LazyColumn {
            items(uiState.articles, key = { it.id }) { article ->
                ArticleCard(
                    article = article,
                    onClick = { onArticleClick(article.id) }
                )
            }
        }
    }
}

// Preview
@Preview(showBackground = true)
@Composable
fun KnowledgeListContentPreview() {
    KnowledgeListContent(
        uiState = KnowledgeListUiState(
            articles = listOf(
                ArticleUiModel(1, "Android Architecture", "Notes on MVVM...", "Tech", listOf("Android"), "2026-05-17", false)
            )
        ),
        onCategorySelected = {},
        onSearchQueryChanged = {},
        onArticleClick = {},
        onNewArticle = {}
    )
}
```

### State Management Rules

| Rule | Enforcement |
|------|------------|
| Single state object per screen | UiState data class, not scattered `var` fields |
| State is immutable | UiState uses `val` + `data class copy()` for updates |
| ViewModel produces state | Composables receive, never manipulate state directly |
| `collectAsStateWithLifecycle()` | Lifecycle-aware collection, stops when UI is invisible |
| `WhileSubscribed(5000)` | Keep upstream alive through rotation, stop after 5s background |
| Flow operators in ViewModel | `combine`, `flatMapLatest`, `map` — never in Compose |
| Events use SharedFlow | Navigation, Snackbar — one-shot, survives rotation via `repeatOnLifecycle` |

---

## 5. Dependency Injection with Koin

### Why Koin for This Project

For a single-developer, local-only app of this scale, **Koin** is the pragmatic choice:

| Criterion | Koin | Hilt |
|-----------|------|------|
| Build time impact | Negligible | +30-60 seconds per build (annotation processing) |
| Setup complexity | 1 Application class + 3 module files | Annotations across files + HiltApplication + HiltViewModel |
| Runtime validation | Startup crash if missing | Compile-time failure |
| ViewModel integration | `koinViewModel()` extension | `@HiltViewModel` + `hiltViewModel()` |
| Scale limit | Medium apps | Any scale |
| Mental overhead | Low (just DSL functions) | Moderate (scopes, components, annotations) |

Hilt's compile-time safety is valuable at enterprise scale. For a personal app with fewer than 10 ViewModels, Koin's simplicity wins decisively.

### Koin Modules

```kotlin
// di/AppModule.kt
val appModule = module {
    // Room Database
    single {
        Room.databaseBuilder(
            get<Context>(),
            ZhiYuDatabase::class.java,
            "zhiyu_database"
        ).build()
    }
    single { get<ZhiYuDatabase>().articleDao() }
    single { get<ZhiYuDatabase>().categoryDao() }
    single { get<ZhiYuDatabase>().tagDao() }
    single { get<ZhiYuDatabase>().quickNoteDao() }

    // DataStore
    single { get<Context>().settingsDataStore }
}

// di/RepositoryModule.kt
val repositoryModule = module {
    single { ArticleRepository(get(), get(), get()) }
    single { CategoryRepository(get()) }
    single { TagRepository(get()) }
    single { SettingsRepository(get()) }
}

// di/ViewModelModule.kt
val viewModelModule = module {
    viewModel { DashboardViewModel(get()) }
    viewModel { KnowledgeListViewModel(get(), get()) }
    viewModel { ArticleDetailViewModel(get(), get()) }
    viewModel { ArticleEditorViewModel(get(), get(), get()) }
    viewModel { QuickNoteViewModel(get()) }
    viewModel { ToolsViewModel() }
    viewModel { ProfileViewModel() }
    viewModel { SettingsViewModel(get()) }
}
```

### Application Class

```kotlin
// ZhiYuApplication.kt
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

### ViewModel Retrieval in Compose

```kotlin
// No @HiltViewModel, no injection in constructor
// Just use the Koin extension function:

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = koinViewModel(),
    onNavigateToSettings: () -> Unit
) {
    // ...
}

// In tests, swap modules:
val testModule = module {
    single<DashboardRepository> { FakeDashboardRepository() }
    viewModel { DashboardViewModel(get()) }
}
```

---

## 6. Data Layer Design

### Room Database

```kotlin
// data/local/ZhiYuDatabase.kt
@Database(
    entities = [
        ArticleEntity::class,
        CategoryEntity::class,
        TagEntity::class,
        ArticleTagCrossRef::class,
        QuickNoteEntity::class
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

### Entities

```kotlin
// data/local/entity/ArticleEntity.kt
@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,              // Markdown content stored as string
    val categoryId: Long?,
    val isFavorite: Boolean = false,
    val createdAt: Long,              // epoch millis
    val updatedAt: Long
)

// data/local/entity/CategoryEntity.kt
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val color: Int,                   // ARGB color for the category chip
    val sortOrder: Int = 0
)

// data/local/entity/TagEntity.kt
@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)

// data/local/entity/ArticleTagCrossRef.kt
@Entity(
    tableName = "article_tag_cross_ref",
    primaryKeys = ["articleId", "tagId"],
    foreignKeys = [
        ForeignKey(entity = ArticleEntity::class, parentColumns = ["id"], childColumns = ["articleId"], onDelete = CASCADE),
        ForeignKey(entity = TagEntity::class, parentColumns = ["id"], childColumns = ["tagId"], onDelete = CASCADE)
    ]
)
data class ArticleTagCrossRef(
    val articleId: Long,
    val tagId: Long
)

// data/local/entity/QuickNoteEntity.kt
@Entity(tableName = "quick_notes")
data class QuickNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long
)
```

### DAOs (Coroutines-First)

```kotlin
// data/local/dao/ArticleDao.kt
@Dao
interface ArticleDao {
    // Observable read — auto-re-emits on table changes
    @Query("SELECT * FROM articles ORDER BY updatedAt DESC")
    fun observeAllArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE id = :id")
    fun observeArticleById(id: Long): Flow<ArticleEntity?>

    // Full-text search (LIKE for local use, FTS4 for scale)
    @Query("SELECT * FROM articles WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    fun searchArticles(query: String): Flow<List<ArticleEntity>>

    // One-shot query
    @Query("SELECT * FROM articles WHERE categoryId = :categoryId ORDER BY updatedAt DESC")
    suspend fun getArticlesByCategory(categoryId: Long): List<ArticleEntity>

    // Writes — suspend functions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: ArticleEntity): Long

    @Update
    suspend fun update(article: ArticleEntity)

    @Delete
    suspend fun delete(article: ArticleEntity)

    // Transactional operations
    @Transaction
    suspend fun insertWithTags(article: ArticleEntity, tags: List<TagEntity>) {
        val articleId = insert(article)
        // Insert tags and cross-refs...
    }
}
```

### Repository Pattern

```kotlin
// data/repository/ArticleRepository.kt
class ArticleRepository(
    private val articleDao: ArticleDao,
    private val tagDao: TagDao,
    private val articleTagDao: ArticleTagDao  // cross-ref DAO
) {
    // Observable — return Flow directly from Room
    fun observeAllArticles(): Flow<List<Article>> =
        articleDao.observeAllArticles()
            .map { entities -> entities.map { it.toDomain() } }
            .distinctUntilChanged()

    fun observeArticleById(id: Long): Flow<Article?> =
        articleDao.observeArticleById(id)
            .map { it?.toDomain() }
            .distinctUntilChanged()

    fun searchArticles(query: String): Flow<List<Article>> =
        articleDao.searchArticles(query)
            .map { entities -> entities.map { it.toDomain() } }

    // Writes
    suspend fun saveArticle(article: Article): Long {
        return articleDao.insert(article.toEntity())
    }

    suspend fun updateArticle(article: Article) {
        articleDao.update(article.toEntity())
    }

    suspend fun deleteArticle(id: Long) {
        articleDao.delete(ArticleEntity(id = id))
    }
}

// Domain model vs Entity mapping
fun ArticleEntity.toDomain() = Article(
    id = id,
    title = title,
    content = content,
    categoryId = categoryId,
    isFavorite = isFavorite,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Article.toEntity() = ArticleEntity(
    id = id,
    title = title,
    content = content,
    categoryId = categoryId,
    isFavorite = isFavorite,
    createdAt = createdAt,
    updatedAt = updatedAt
)
```

### Data Layer Rules

| Rule | Rationale |
|------|-----------|
| DAOs expose `Flow<T>` for reads | Automatic re-emission when underlying table changes |
| DAOs expose `suspend` for writes | Blocking operations in coroutine context |
| Repositories map entities to domain models | UI layer never depends on Room annotations |
| `distinctUntilChanged()` on repository flows | Prevents duplicate recomposition |
| No `LiveData` in DAOs | LiveData is legacy; Flow integrates natively with Compose |
| Entity-to-domain mapping in repository package | Mapping lives close to where it's used |

---

## 7. Settings / Preferences Architecture (DataStore)

### DataStore Definition

```kotlin
// data/local/datastore/SettingsDataStore.kt
private val Context.settingsDataStore by preferencesDataStore(name = "settings")

// Preference keys
private val KEY_DARK_MODE = booleanPreferencesKey("dark_mode")
private val KEY_FOLLOW_SYSTEM = booleanPreferencesKey("follow_system")

class SettingsRepository(private val dataStore: DataStore<Preferences>) {

    // Observable reads — StateFlow from DataStore Flow
    val darkModeSetting: Flow<DarkModeSetting> = dataStore.data.map { prefs ->
        val followSystem = prefs[KEY_FOLLOW_SYSTEM] ?: true
        val isDark = prefs[KEY_DARK_MODE] ?: false

        when {
            followSystem -> DarkModeSetting.FOLLOW_SYSTEM
            isDark -> DarkModeSetting.DARK
            else -> DarkModeSetting.LIGHT
        }
    }.distinctUntilChanged()

    suspend fun setDarkMode(setting: DarkModeSetting) {
        dataStore.edit { prefs ->
            when (setting) {
                DarkModeSetting.FOLLOW_SYSTEM -> {
                    prefs[KEY_FOLLOW_SYSTEM] = true
                }
                DarkModeSetting.DARK -> {
                    prefs[KEY_FOLLOW_SYSTEM] = false
                    prefs[KEY_DARK_MODE] = true
                }
                DarkModeSetting.LIGHT -> {
                    prefs[KEY_FOLLOW_SYSTEM] = false
                    prefs[KEY_DARK_MODE] = false
                }
            }
        }
    }
}

enum class DarkModeSetting {
    FOLLOW_SYSTEM,
    LIGHT,
    DARK
}
```

### Theme Integration

```kotlin
// ui/theme/Theme.kt
@Composable
fun ZhiYuTheme(
    darkModeSetting: DarkModeSetting = DarkModeSetting.FOLLOW_SYSTEM,
    content: @Composable () -> Unit
) {
    val darkMode = when (darkModeSetting) {
        DarkModeSetting.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        DarkModeSetting.DARK -> true
        DarkModeSetting.LIGHT -> false
    }

    val colorScheme = if (darkMode) darkZhiYuColorScheme else lightZhiYuColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ZhiYuTypography,
        shapes = ZhiYuShapes,
        content = content
    )
}

// In Activity:
setContent {
    val settings: SettingsRepository = koinViewModel()  // or retrieve from DI
    val darkMode by settings.darkModeSetting.collectAsStateWithLifecycle(
        initialValue = DarkModeSetting.FOLLOW_SYSTEM
    )

    ZhiYuTheme(darkModeSetting = darkMode) {
        // ...
    }
}
```

### DataStore Rules

| Rule | Rationale |
|------|-----------|
| Single DataStore instance per file | Defined as top-level extension property on `Context` |
| Repository wraps DataStore | ViewModel never touches DataStore directly |
| `Flow` for reads with `distinctUntilChanged()` | Prevents unnecessary emissions |
| `suspend` for writes via `edit {}` | Atomic read-modify-write |
| Keys in companion or top-level | Avoid magic strings scattered across files |

---

## 8. Build Order: Component Dependencies

The build order is determined by dependency direction. Build bottom-up:

```
Phase 1: Foundation
├── Gradle setup (Kotlin, Compose, Room, Koin, Navigation deps)
├── Application class + Koin initialization
├── Room database (entities, DAOs, database class)
├── DataStore setup
└── Theme (Color, Type, Shape, Theme composable)

Phase 2: Data Layer
├── Domain models (Article, Category, Tag, QuickNote)
├── Entity ↔ Domain mappers (toDomain, toEntity extensions)
├── Repositories (ArticleRepository, CategoryRepository, SettingsRepository)
├── DI modules (AppModule, RepositoryModule)
└── Test: In-memory Room DB DAO tests

Phase 3: Navigation Shell
├── Route definitions (type-safe sealed routes)
├── NavHost composable (empty screens as placeholders)
├── BottomNavBar composable
├── MainActivity (Scaffold + NavHost + bottom bar wiring)
├── Screen placeholder stubs (Box with tab name text)
└── Test: Navigation works, bottom bar switches tabs

Phase 4: Feature Tabs (build independently)
├── Tab 1: Dashboard (信息)
│   ├── DashboardViewModel (uses SettingsRepository for countdown config)
│   ├── DashboardUiState
│   ├── DashboardScreen composable
│   └── Test: Dashboard shows time, week, countdown
│
├── Tab 2: Knowledge Base (知识库) — highest complexity
│   ├── KnowledgeList feature (list, filter, search)
│   │   ├── ViewModel + UiState
│   │   ├── Category chip row composable
│   │   └── Article list composable
│   ├── ArticleDetail feature
│   │   ├── ViewModel loads single article
│   │   └── Markdown rendering (compose-markdown library)
│   ├── ArticleEditor feature
│   │   ├── ViewModel (new/edit modes)
│   │   ├── Markdown editor composable
│   │   └── Save action
│   └── Search feature
│       ├── SearchViewModel (debounced text search)
│       └── Search results composable
│
├── Tab 3: Quick Notes (小记)
│   ├── QuickNoteViewModel
│   ├── QuickNoteScreen (create + list notes)
│   └── Test: CRUD operations
│
├── Tab 4: Tools (发现)
│   ├── ToolsViewModel
│   ├── ToolsScreen (grid of tool entries)
│   └── Individual tool composables
│
└── Tab 5: Profile (我的)
    ├── ProfileScreen (avatar, name, stats)
    ├── SettingsScreen (theme toggle, about link)
    └── AboutScreen

Phase 5: Polish
├── Splash screen (AndroidX SplashScreen API)
├── Dark mode toggle wiring (SettingsRepository -> Theme)
├── MIUI design refinement (rounded corners, blur, specific colors)
├── Edge-to-edge (SystemBars handling)
├── App icon
└── ProGuard / R8 rules for release build

Phase 6: Build & Release
├── APK/AAB generation
├── Manual testing on device
└── Signing config
```

### Dependency Graph (Simplified)

```
Theme ──────────────┐
Koin (Application) ─┤
Room ───────────────┼──> Repositories ──> ViewModels ──> Screens
DataStore ──────────┘       │
                            └──> Navigation (Routes, NavHost, BottomBar)
                                               │
                                               └──> MainActivity (Scaffold)
```

### What to Build First

**Start with Phase 1 + Phase 2** because:
1. Without Room and repositories, ViewModels have no data
2. Without Koin, nothing is wired together
3. Without theme, UI looks wrong

**Phase 4 tabs can be built in any order** since they are independent feature packages that share nothing except the data layer. This is a key advantage of feature-first packaging.

---

## 9. Splash Screen

```kotlin
// MainActivity.kt — using AndroidX SplashScreen API
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()  // Must be before super.onCreate()
        super.onCreate(savedInstanceState)

        // Keep splash visible until theme is loaded from DataStore
        var isReady by mutableStateOf(false)
        splashScreen.setKeepOnScreenCondition { !isReady }

        setContent {
            val settingsRepository: SettingsRepository = remember {
                // Retrieve from Koin
                getKoin().get()
            }
            val darkMode by settingsRepository.darkModeSetting
                .collectAsStateWithLifecycle(initialValue = DarkModeSetting.FOLLOW_SYSTEM)

            // Mark ready after first emission from DataStore
            LaunchedEffect(darkMode) {
                isReady = true
            }

            ZhiYuTheme(darkModeSetting = darkMode) {
                val navController = rememberNavController()
                Scaffold(bottomBar = { BottomNavBar(navController) }) { padding ->
                    AppNavHost(
                        navController = navController,
                        modifier = Modifier.padding(padding)
                    )
                }
            }
        }
    }
}
```

No custom splash composable or SplashActivity needed. The `core-splashscreen` library provides the native splash on Android 12+ and emulates it on older versions. The keep condition ensures the splash stays visible only as long as DataStore initialization takes.

---

## 10. Patterns to Follow

### Pattern 1: Screen / Content Split
Every screen is split into two functions: one that takes the ViewModel and one that takes state + callbacks.

```kotlin
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = koinViewModel(),
    onNavigateToSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DashboardContent(uiState, viewModel::onRefresh, onNavigateToSettings)
}

@Composable
fun DashboardContent(
    uiState: DashboardUiState,
    onRefresh: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) { /* Pure UI, previewable */ }
```

### Pattern 2: StateFlow with WhileSubscribed(5000)
Every `stateIn()` call uses 5-second grace period to handle rotation:

```kotlin
.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = UiState()
)
```

### Pattern 3: One-Shot Events via SharedFlow
Navigation commands and Snackbar triggers use SharedFlow:

```kotlin
private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
val navigationEvent: SharedFlow<NavigationEvent> = _navigationEvent.asSharedFlow()

// Collect in composable:
LaunchedEffect(Unit) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.GoBack -> navController.popBackStack()
            }
        }
    }
}
```

---

## 11. Anti-Patterns to Avoid

| Anti-Pattern | Why Bad | Instead |
|--------------|---------|---------|
| `mutableStateOf` in ViewModel | Couples ViewModel to Compose, breaks testing | `StateFlow` with `stateIn()` |
| String-based routes | Typos, no refactoring safety | `@Serializable` type-safe routes |
| Loading data in `init` block | Hard to test, no lifecycle awareness | `stateIn(WhileSubscribed)` with flow chain |
| `collectAsState()` without lifecycle | Collects in background, wastes battery | `collectAsStateWithLifecycle()` |
| One entity class used as UI model | Tight coupling, schema changes break UI | Separate `UiModel` data classes |
| Nested `NavHost` graphs | Added complexity for this app's depth | Flat `NavHost` with all routes |
| Hilt for a small personal app | Slow builds, annotation overhead | Koin |
| UseCases for a local-only CRUD app | Extra abstraction without tangible benefit | ViewModel calls Repository directly |

---

## 12. Scalability Considerations

| Concern | Current Approach | Future Scaling Path |
|---------|-----------------|-------------------|
| **Module count** | Single module (`:app`) | Extract `:core:database`, `:core:ui`, `:feature:*` modules |
| **DI** | Koin (single module) | Koin multi-module with `module { includes() }` |
| **Search** | SQL `LIKE` queries | Room FTS4 table, or Sqlite `FTS5` |
| **Navigation** | Flat routes | Nested navigation graphs per tab |
| **State** | Single StateFlow per screen | Orbit MVI or Mavericks for complex screens |
| **Build time** | Standard | Gradle build cache, module parallelization |

---

## Sources

- Android Developers: [App Architecture Guide](https://developer.android.com/topic/architecture) — HIGH confidence
- Android Developers: [Navigation Type Safety](https://developer.android.google.cn/guide/navigation/design/type-safety) — HIGH confidence (2026-02-26)
- Android Developers: [Room Async Queries](https://developer.android.google.cn/training/data-storage/room/async-queries) — HIGH confidence
- Android Developers: [DataStore Guide](https://developer.android.google.cn/topic/libraries/architecture/datastore) — HIGH confidence
- Android Developers: [ViewModel Overview](https://developer.android.com/topic/libraries/architecture/viewmodel) — HIGH confidence
- Google's "Now in Android" sample app — MEDIUM confidence (community reference)
- DeepWiki: [AndroidProject-Compose Architecture](https://deepwiki.com/Joker-x-dev/AndroidProject-Compose) — MEDIUM confidence (community project)
- Pixact Technologies: [Future-Proof Mobile Architecture](https://pixacttechnologies.com/build-future-proof-mobile-app-architecture-mvvm-mvi-unidirectional-data-flow/) — MEDIUM confidence (industry article)
- proandroiddev.com: [Hilt vs Koin](https://proandroiddev.com/hilt-vs-koin-the-hidden-cost-of-runtime-injection-and-why-compile-time-di-wins-3d8c522a073b) — LOW confidence (opinion article, used for counterpoint only)
- proandroiddev.com: [Compose Offline-First](https://proandroiddev.com/jetpack-compose-offline-first-architectures-5495ec6ddfa8) — LOW confidence (article, pattern verified against official docs)
