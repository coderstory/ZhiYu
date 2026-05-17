# Technology Stack

**Project:** 知屿 (ZhiYu) — Android Personal Knowledge Assistant
**Researched:** 2026-05-17  
**App targets:** Android 16+ (API 36+), Kotlin + Jetpack Compose + Material3  
**Overall confidence:** HIGH (official docs and stable releases verified)

---

## Recommended Stack

### Core Framework

| Technology | Version | Purpose | Why |
|------------|---------|---------|-----|
| Android Gradle Plugin | **9.0.28+** (latest 9.0.x) | Build system | Required for compileSdk 36, built-in Kotlin support (no `kotlin-android` plugin needed) |
| Gradle | **9.3.1** | Build tool | AGP 9.0 requires Gradle 9.1+; 9.3.1 is latest stable |
| Kotlin | **2.3.20** | Language | Latest stable release matching AGP 9.0.x compatibility |
| KSP | **2.3.6** | Annotation processing | For Room; matches Kotlin 2.3.20 |
| JDK | **17** (or 21) | Compilation | AGP 9.0 minimum requirement; JDK 21 also compatible |
| compileSdk | **36** | SDK compilation | Android 16 (API 36); AGP 9.0 ships SDK Build Tools 36.0.0 |
| minSdk | **36** | Minimum support | Project requirement: Android 16+ only |
| targetSdk | **36** | Target behavior | Google Play now requires API 36 for new apps |

### Jetpack Compose (via BOM)

All Compose library versions are managed by the Bill of Materials. Do NOT specify individual versions.

| Library | Managed By | Purpose |
|---------|-----------|---------|
| Compose BOM | **2026.04.01** | Manages ALL Compose library versions |
| Compose UI | BOM | Core UI primitives (Modifier, Layout, etc.) |
| Compose UI Graphics | BOM | Canvas, image, rendering |
| Compose Foundation | BOM | Layout, gestures, text, LazyColumn |
| Material3 | BOM | Material Design 3 components (NavigationBar, Scaffold, etc.) |
| Material Icons Extended | BOM | Full Material icon set (for bottom tab icons) |
| Compose Compiler (Kotlin plugin) | Kotlin 2.3.20 built-in | Compose compiler as Kotlin compiler plugin (no separate dep) |

**BOM 2026.04.01 ships Compose 1.11.0** and includes Material3 as the primary design system.

### Database

| Library | Version | Purpose | Why |
|---------|---------|---------|-----|
| Room | **2.8.4** | Local database | Stable, well-documented, coroutines/Flow support |
| Room KTX | 2.8.4 | Coroutine extensions | Suspend DAO methods, Flow return types |
| Room Compiler (via KSP) | 2.8.4 | Code generation | KSP (not KAPT) for Room annotation processing |

**Why NOT Room 3.0:** Room 3.0.0-alpha04 exists but targets Kotlin Multiplatform (KMP) with breaking API changes (new `androidx.room3` package, Kotlin-only, KSP-only). For a single-platform Android app, Room 2.8.4 is stable, mature, and well-documented. Room 3.0 migration can happen later when the project (or Room 3.0) reaches stable.

### Navigation

| Library | Version | Purpose | Why |
|---------|---------|---------|-----|
| Navigation Compose | **2.9.8** | Screen navigation | Stable, type-safe navigation, well-documented |

**Why NOT Navigation 3:** Navigation 3 (1.2.0 stable) is a ground-up redesign with developer-managed back stack. It is compelling for new projects but has a smaller community, less documentation, and a fundamentally different API. Navigation Compose 2.9.8 is battle-tested with abundant examples for bottom navigation patterns. For a 4-tab bottom navigation app, the v2 API is more straightforward.

### Data / Preferences Storage

| Library | Version | Purpose | Why |
|---------|---------|---------|-----|
| DataStore Preferences | **1.2.1** | Key-value preferences | Modern replacement for SharedPreferences; coroutine/Flow-based, transactional |

**What goes in DataStore:** Theme preference (dark/light/system), UI state preferences, last used tab, settings.
**What goes in Room:** Knowledge base articles, tags, categories, notes — structured data.

### Splash Screen

| Library | Version | Purpose | Why |
|---------|---------|---------|-----|
| Core SplashScreen | **1.2.0** | Splash screen API | Standard AndroidX implementation supporting splash icon, animation, theme-based |

Although the app targets API 36 (which has platform SplashScreen API since API 31), the compat library provides:
- Consistent behavior across API levels
- `SplashScreenViewProvider` for custom exit animations
- Theme-based setup via `Theme.SplashScreen` in styles

**Note on Android 16:** Edge-to-edge is **mandatory** (no opt-out) for apps targeting API 36. The splash screen must handle system bar insets properly.

### Markdown Rendering

| Library | Version | Purpose | Why |
|---------|---------|---------|-----|
| compose-richtext (richtext-ui, richtext-commonmark, richtext-ui-material3) | **1.0.0-alpha03** | Markdown rendering in Compose | Compose-native, Material3 theme integration, CommonMark parser |

**Dependency coordinates:**
```kotlin
implementation("com.halilibo.compose-richtext:richtext-ui:1.0.0-alpha03")
implementation("com.halilibo.compose-richtext:richtext-commonmark:1.0.0-alpha03")
implementation("com.halilibo.compose-richtext:richtext-ui-material3:1.0.0-alpha03")
```

**Usage:** `Markdown(content = markdownString)` composable renders fully styled Markdown.

**Why this over alternatives:**
- `jeziellago/compose-markdown:0.5.8` — hosted on JitPack (not Maven Central), less actively maintained, View interop issues
- WebView-based — not Compose-idiomatic, heavier, slower
- compose-richtext is the only Compose-native library with active maintenance and Material3 support

**Caveat (LOW confidence):** This library is still pre-1.0 (alpha). API may change. For a personal project this is acceptable. Monitor the GitHub repo for breaking changes.

### What We Do NOT Need

| Technology | Why Not |
|-----------|---------|
| `org.jetbrains.kotlin.android` plugin | AGP 9.0+ has **built-in Kotlin support** — applying this plugin causes build failure |
| Separate Compose Compiler (`androidx.compose.compiler:compiler`) | Compose compiler is **built into Kotlin 2.0+** — use `org.jetbrains.kotlin.plugin.compose` instead |
| KAPT | KSP replaces KAPT for all annotation processing |
| ViewBinding / DataBinding | Compose does not need XML view binding |
| Hilt / Dagger | Personal app with simple DI needs — manual dependency injection or a lightweight service locator is sufficient |
| Retrofit / OkHttp | No network calls — local-only app |
| Hilt | Unnecessary complexity for a single-user local app |

---

## Alternatives Considered

| Category | Recommended | Alternative | Why Not |
|----------|-------------|-------------|---------|
| Compose Compiler | Kotlin plugin (built-in) | `androidx.compose.compiler:compiler` | Separate compiler artifact is deprecated since Kotlin 2.0 |
| Navigation | Compose Navigation 2.9.8 | Navigation 3 1.2.0 | Less documentation; different API; overkill for bottom tabs |
| Database | Room 2.8.4 | Room 3.0.0-alpha04 | Room 3.0 is alpha, KMP-focused, breaking API changes |
| Annotation processing | KSP 2.3.6 | KAPT | KAPT is deprecated; KSP is 2x faster and AGP 9.0 compatible |
| Markdown | compose-richtext | WebView + marked.js | Not Compose-idiomatic; heavier; slower |
| Theme/Preferences | DataStore Preferences | SharedPreferences | SharedPreferences is legacy; synchronous API blocks main thread |
| Build Plugin | AGP 9.0.x + Kotlin 2.3.x | AGP 9.1.0 + Kotlin 2.4.0-Beta | Kotlin 2.4 is beta; AGP 9.1 requires it. For stability, use Kotlin 2.3 stable |
| DI Framework | Manual (no library) | Hilt | Hilt adds complexity (Dagger, kapt/ksp) for one-person app. Start simple. |

---

## Version Catalog (`gradle/libs.versions.toml`)

The industry standard for 2025/2026 Android projects is a **version catalog** in TOML format, managed at the project root.

Create `gradle/libs.versions.toml`:

```toml
[versions]
agp = "9.0.28"
kotlin = "2.3.20"
ksp = "2.3.6"
compose-bom = "2026.04.01"
room = "2.8.4"
navigation-compose = "2.9.8"
datastore-preferences = "1.2.1"
core-splashscreen = "1.2.0"
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
compose-navigation = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigation-compose" }

# Room
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }

# DataStore
datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastore-preferences" }

# Splash Screen
core-splashscreen = { group = "androidx.core", name = "core-splashscreen", version.ref = "core-splashscreen" }

# Markdown (compose-richtext)
richtext-ui = { group = "com.halilibo.compose-richtext", name = "richtext-ui", version.ref = "compose-richtext" }
richtext-commonmark = { group = "com.halilibo.compose-richtext", name = "richtext-commonmark", version.ref = "compose-richtext" }
richtext-material3 = { group = "com.halilibo.compose-richtext", name = "richtext-ui-material3", version.ref = "compose-richtext" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-compose-compiler = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
```

---

## Gradle Configuration

### Project-level `build.gradle.kts`

```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose.compiler) apply false
    alias(libs.plugins.ksp) apply false
}
```

### Module-level `app/build.gradle.kts`

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose.compiler)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.zhiyu.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.zhiyu.app"
        minSdk = 36
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Compose UI Core
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.foundation)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)

    // Material3 + Icons
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)

    // Navigation
    implementation(libs.compose.navigation)

    // Database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Preferences
    implementation(libs.datastore.preferences)

    // Splash Screen
    implementation(libs.core.splashscreen)

    // Markdown
    implementation(libs.richtext.ui)
    implementation(libs.richtext.commonmark)
    implementation(libs.richtext.material3)

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(libs.compose.ui.test.junit4)
}
```

### `gradle.properties`

```properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
org.gradle.configuration-cache=true
```

### `settings.gradle.kts`

```kotlin
pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ZhiYu"
include(":app")
```

---

## Key Design Decisions

### 1. AGP 9.0.x (not 8.x)
AGP 8.x supports compileSdk up to 35 only. Since the app targets compileSdk 36 (Android 16), **AGP 9.0+ is required**. AGP 9.0 also brings built-in Kotlin support, eliminating the `kotlin-android` plugin entirely.

### 2. Kotlin 2.3.20 (not 2.4 Beta)
Kotlin 2.4 is in beta as of May 2026. Using a beta language version in a project that depends on KSP and Compose compiler (both tightly coupled to Kotlin version) introduces risk. Kotlin 2.3.20 is stable with known KSP/AGP compatibility.

### 3. Room 2.8.4 (not 3.0)
Room 3.0 is alpha, targets KMP, and uses a new `androidx.room3` package namespace. It offers KMP support the app does not need. Room 2.8.4 is the last stable 2.x release and will continue receiving bug fixes in maintenance mode. Migration to Room 3.0 can be planned when it reaches stable.

### 4. Navigation Compose 2.9.8
For a 4-tab bottom navigation layout, Navigation Compose 2.9.8 provides the well-established `NavHost` + `NavController` pattern with `saveState`/`restoreState` for tab switching. Type-safe navigation (Kotlin Serialization) is available in this version for typed route arguments.

### 5. Android 16 Edge-to-Edge Mandatory
Apps targeting API 36 **cannot opt out** of edge-to-edge rendering. All content renders behind system bars. The `Scaffold` composable with proper `WindowInsets` handling is essential:

```kotlin
Scaffold(
    modifier = Modifier.windowInsetsPadding(
        WindowInsets.systemBars.only(WindowInsets.Type.statusBars())
    ),
    bottomBar = {
        NavigationBar(
            windowInsets = NavigationBarDefaults.windowInsets
        ) { ... }
    }
) { innerPadding ->
    NavHost(
        modifier = Modifier.padding(innerPadding),
        ...
    )
}
```

### 6. MIUI Design Customization
MIUI design can be approximated through Material3 theme customization:
- **Fonts:** Use `mi-sans` or similar rounded sans-serif via `Typography` in Material3
- **Colors:** MIUI accent is a warm orange-red (`#FF6B35`); primary surfaces are light gray-white
- **Bottom tabs:** MIUI-style large icons with badge support
- **Rounded corners:** High `M3Shape` corner radius values (16dp-24dp for cards, 28dp for bottom sheets)
- **Status bar:** Transparent (now mandatory in Android 16) with dark/light text tinting

### 7. Compose Compiler (Kotlin Plugin)
Since Kotlin 2.0, the Compose compiler is integrated into the Kotlin compiler. Use `org.jetbrains.kotlin.plugin.compose` — do NOT add `androidx.compose.compiler:compiler` as a dependency.

---

## Android Studio Compatibility

| Component | Required Version |
|-----------|-----------------|
| Android Studio | **Otter (2025.2.1)** or later (supports AGP 9.0+) |
| Gradle JDK | JetBrains Runtime 17+ or Oracle JDK 17+ |

---

## Module Structure (Recommended)

```
ZhiYu/
├── app/
│   ├── src/main/
│   │   ├── java/com/zhiyu/app/
│   │   │   ├── ZhiYuApplication.kt
│   │   │   ├── MainActivity.kt
│   │   │   ├── navigation/
│   │   │   │   └── AppNavigation.kt        # NavHost + bottom nav routes
│   │   │   ├── ui/
│   │   │   │   ├── theme/
│   │   │   │   │   ├── Theme.kt            # MIUI-style Material3 theme
│   │   │   │   │   ├── Color.kt
│   │   │   │   │   ├── Type.kt
│   │   │   │   │   └── Shape.kt
│   │   │   │   ├── screens/
│   │   │   │   │   ├── info/               # Tab 1: Information dashboard
│   │   │   │   │   ├── knowledge/          # Tab 2: Knowledge base
│   │   │   │   │   ├── discover/           # Tab 3: Tools
│   │   │   │   │   └── profile/            # Tab 4: Profile/Settings
│   │   │   │   └── components/             # Shared composables
│   │   │   ├── data/
│   │   │   │   ├── local/
│   │   │   │   │   ├── ZhiYuDatabase.kt    # Room database
│   │   │   │   │   ├── dao/
│   │   │   │   │   └── entity/
│   │   │   │   └── preferences/
│   │   │   │       └── UserPreferences.kt  # DataStore wrapper
│   │   │   └── model/
│   ├── src/main/res/
│   │   ├── values/
│   │   │   ├── themes.xml                  # SplashScreen theme + base theme
│   │   │   └── colors.xml
│   │   └── drawable/
│   └── proguard-rules.pro
├── gradle/
│   └── libs.versions.toml
├── build.gradle.kts                        # Root project
├── settings.gradle.kts
└── gradle.properties
```

---

## Sources

| Source | URL | Confidence |
|--------|-----|------------|
| Compose BOM 2026.04.01 announcement | https://android-developers.googleblog.com/2026/04/jetpack-compose-april-2026-updates.html | HIGH |
| AGP 9.0 release notes | https://developer.android.com/build/releases/agp-9-0-0-release-notes | HIGH |
| AGP 9.1.0 release notes | https://developer.android.com/build/releases/agp-9-1-0-release-notes | HIGH |
| AGP/Kotlin compatibility table | https://developer.android.com/build/kotlin-support | HIGH |
| Room releases | https://developer.android.com/jetpack/androidx/releases/room | HIGH |
| Navigation Compose | https://developer.android.com/develop/ui/compose/navigation | HIGH |
| DataStore releases | https://developer.android.com/jetpack/androidx/releases/datastore | HIGH |
| SplashScreen API | https://developer.android.com/develop/ui/views/launch/splash-screen | HIGH |
| Core SplashScreen release | https://developer.android.com/jetpack/androidx/releases/core | HIGH |
| Kotlin 2.3.20 release | https://github.com/JetBrains/kotlin/releases | HIGH |
| KSP 2.3.6 release | https://github.com/google/ksp/releases | HIGH |
| Gradle 9.3.0 release notes | https://docs.gradle.org/9.3.0/release-notes.html | HIGH |
| compose-richtext | https://github.com/halilozercan/compose-richtext | MEDIUM (alpha) |
| compose-richtext Maven | https://halilibo.com/compose-richtext | MEDIUM (alpha) |
| AGP 9 built-in Kotlin (JetBrains blog) | https://blog.jetbrains.com/kotlin/2026/01/update-your-projects-for-agp9/ | HIGH |
| Android 16 edge-to-edge mandatory | https://developer.android.com/about/versions/16/behavior-changes-16 | HIGH |
| Android 16 features & APIs | https://developer.android.com/about/versions/16/features | HIGH |

---

## Confidence Assessment

| Area | Level | Reason |
|------|-------|--------|
| AGP / Gradle / Kotlin versions | HIGH | Verified against official compatibility tables |
| Compose BOM and Jetpack libraries | HIGH | Official Android Developers Blog and docs |
| Room + KSP | HIGH | Official release notes, stable versions |
| Navigation Compose | HIGH | Official docs, stable release |
| DataStore | HIGH | Official docs, stable release |
| SplashScreen | HIGH | Official docs, stable release |
| compose-richtext | MEDIUM | Pre-1.0 alpha; API may change. Alternative is `jeziellago/compose-markdown` (JitPack, less active) |
| MIUI design via Material3 | LOW | No official MIUI design kit for Compose; approach is based on visual approximation |

---

## Phase-Specific Warnings

| Phase | Warning |
|-------|---------|
| Build setup | Verify AGP 9.0.x patch version supports Kotlin 2.3.x before pinning. The 9.0.28+ threshold comes from the AGP/Kotlin compatibility page. |
| Splash screen | Android 16 edge-to-edge is mandatory. Splash screen must handle system bar insets. |
| Navigation | Use `saveState = true` / `restoreState = true` on bottom nav items to preserve tab state across switches. |
| Markdown | compose-richtext is alpha; test rendering edge cases (code blocks, images, tables) early. Have fallback plan to switch to WebView-based rendering if issues arise. |
| Room | Define entities and DAOs early; test migrations before adding data. |
