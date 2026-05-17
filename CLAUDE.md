<!-- GSD:project-start source:PROJECT.md -->
## Project

**知屿 (ZhiYu) — 个人知识助手**

Android 个人效率应用，融合信息看板、知识管理和工具集。主界面采用类微信底部Tab布局（MIUI设计风格），提供信息概览（时间/星期/下班倒计时）、知识库管理（类似羽雀，支持分类/标签/小记）、工具集合和个人设置等核心功能。

**Core Value:** 用户打开App能一眼看到当前时间状态，同时方便地记录和检索知识碎片 — 一个 App 搞定日常信息查看和知识管理。

### Constraints

- **Tech Stack**: Kotlin + Jetpack Compose + Material3 — 当前 Android 主流技术栈
- **Compatibility**: 仅 Android 16+（API 36+） — 利用最新平台特性，减少兼容负担
- **Design**: MIUI 设计风格 — 参考小米设计语言
- **Storage**: 本地存储（Room 数据库） — 无后端
- **Build**: Gradle + Kotlin DSL — 标准 Android 构建工具
<!-- GSD:project-end -->

<!-- GSD:stack-start source:research/STACK.md -->
## Technology Stack

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
| Library | Managed By | Purpose |
|---------|-----------|---------|
| Compose BOM | **2026.04.01** | Manages ALL Compose library versions |
| Compose UI | BOM | Core UI primitives (Modifier, Layout, etc.) |
| Compose UI Graphics | BOM | Canvas, image, rendering |
| Compose Foundation | BOM | Layout, gestures, text, LazyColumn |
| Material3 | BOM | Material Design 3 components (NavigationBar, Scaffold, etc.) |
| Material Icons Extended | BOM | Full Material icon set (for bottom tab icons) |
| Compose Compiler (Kotlin plugin) | Kotlin 2.3.20 built-in | Compose compiler as Kotlin compiler plugin (no separate dep) |
### Database
| Library | Version | Purpose | Why |
|---------|---------|---------|-----|
| Room | **2.8.4** | Local database | Stable, well-documented, coroutines/Flow support |
| Room KTX | 2.8.4 | Coroutine extensions | Suspend DAO methods, Flow return types |
| Room Compiler (via KSP) | 2.8.4 | Code generation | KSP (not KAPT) for Room annotation processing |
### Navigation
| Library | Version | Purpose | Why |
|---------|---------|---------|-----|
| Navigation Compose | **2.9.8** | Screen navigation | Stable, type-safe navigation, well-documented |
### Data / Preferences Storage
| Library | Version | Purpose | Why |
|---------|---------|---------|-----|
| DataStore Preferences | **1.2.1** | Key-value preferences | Modern replacement for SharedPreferences; coroutine/Flow-based, transactional |
### Splash Screen
| Library | Version | Purpose | Why |
|---------|---------|---------|-----|
| Core SplashScreen | **1.2.0** | Splash screen API | Standard AndroidX implementation supporting splash icon, animation, theme-based |
- Consistent behavior across API levels
- `SplashScreenViewProvider` for custom exit animations
- Theme-based setup via `Theme.SplashScreen` in styles
### Markdown Rendering
| Library | Version | Purpose | Why |
|---------|---------|---------|-----|
| compose-richtext (richtext-ui, richtext-commonmark, richtext-ui-material3) | **1.0.0-alpha03** | Markdown rendering in Compose | Compose-native, Material3 theme integration, CommonMark parser |
- `jeziellago/compose-markdown:0.5.8` — hosted on JitPack (not Maven Central), less actively maintained, View interop issues
- WebView-based — not Compose-idiomatic, heavier, slower
- compose-richtext is the only Compose-native library with active maintenance and Material3 support
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
## Version Catalog (`gradle/libs.versions.toml`)
# Compose BOM and managed dependencies
# Navigation
# Room
# DataStore
# Splash Screen
# Markdown (compose-richtext)
## Gradle Configuration
### Project-level `build.gradle.kts`
### Module-level `app/build.gradle.kts`
### `gradle.properties`
### `settings.gradle.kts`
## Key Design Decisions
### 1. AGP 9.0.x (not 8.x)
### 2. Kotlin 2.3.20 (not 2.4 Beta)
### 3. Room 2.8.4 (not 3.0)
### 4. Navigation Compose 2.9.8
### 5. Android 16 Edge-to-Edge Mandatory
### 6. MIUI Design Customization
- **Fonts:** Use `mi-sans` or similar rounded sans-serif via `Typography` in Material3
- **Colors:** MIUI accent is a warm orange-red (`#FF6B35`); primary surfaces are light gray-white
- **Bottom tabs:** MIUI-style large icons with badge support
- **Rounded corners:** High `M3Shape` corner radius values (16dp-24dp for cards, 28dp for bottom sheets)
- **Status bar:** Transparent (now mandatory in Android 16) with dark/light text tinting
### 7. Compose Compiler (Kotlin Plugin)
## Android Studio Compatibility
| Component | Required Version |
|-----------|-----------------|
| Android Studio | **Otter (2025.2.1)** or later (supports AGP 9.0+) |
| Gradle JDK | JetBrains Runtime 17+ or Oracle JDK 17+ |
## Module Structure (Recommended)
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
## Phase-Specific Warnings
| Phase | Warning |
|-------|---------|
| Build setup | Verify AGP 9.0.x patch version supports Kotlin 2.3.x before pinning. The 9.0.28+ threshold comes from the AGP/Kotlin compatibility page. |
| Splash screen | Android 16 edge-to-edge is mandatory. Splash screen must handle system bar insets. |
| Navigation | Use `saveState = true` / `restoreState = true` on bottom nav items to preserve tab state across switches. |
| Markdown | compose-richtext is alpha; test rendering edge cases (code blocks, images, tables) early. Have fallback plan to switch to WebView-based rendering if issues arise. |
| Room | Define entities and DAOs early; test migrations before adding data. |
<!-- GSD:stack-end -->

<!-- GSD:conventions-start source:CONVENTIONS.md -->
## Conventions

Conventions not yet established. Will populate as patterns emerge during development.
<!-- GSD:conventions-end -->

<!-- GSD:architecture-start source:ARCHITECTURE.md -->
## Architecture

Architecture not yet mapped. Follow existing patterns found in the codebase.
<!-- GSD:architecture-end -->

<!-- GSD:skills-start source:skills/ -->
## Project Skills

No project skills found. Add skills to any of: `.claude/skills/`, `.agents/skills/`, `.cursor/skills/`, `.github/skills/`, or `.codex/skills/` with a `SKILL.md` index file.
<!-- GSD:skills-end -->

<!-- GSD:workflow-start source:GSD defaults -->
## GSD Workflow Enforcement

Before using Edit, Write, or other file-changing tools, start work through a GSD command so planning artifacts and execution context stay in sync.

Use these entry points:
- `/gsd-quick` for small fixes, doc updates, and ad-hoc tasks
- `/gsd-debug` for investigation and bug fixing
- `/gsd-execute-phase` for planned phase work

Do not make direct repo edits outside a GSD workflow unless the user explicitly asks to bypass it.
<!-- GSD:workflow-end -->



<!-- GSD:profile-start -->
## Developer Profile

> Profile not yet configured. Run `/gsd-profile-user` to generate your developer profile.
> This section is managed by `generate-claude-profile` -- do not edit manually.
<!-- GSD:profile-end -->
