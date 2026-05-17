<!-- GSD:project-start source:PROJECT.md -->
## 项目

**知屿 (ZhiYu) — 个人知识助手**

Android 个人效率应用，融合信息看板、知识管理和工具集。主界面采用类微信底部Tab布局（MIUI设计风格），提供信息概览（时间/星期/下班倒计时）、知识库管理（类似羽雀，支持分类/标签/小记）、工具集合和个人设置等核心功能。

**核心价值:** 用户打开App能一眼看到当前时间状态，同时方便地记录和检索知识碎片 — 一个 App 搞定日常信息查看和知识管理。

修改代码之前，请先执行一次评估，避免代码越改越错，尽可能直接修复问题而不是替换方案，最后导致项目和预期越来越远

任何报错都不能直接try-catch 忽略

### 约束条件

- **技术栈**: Kotlin + Jetpack Compose + Material3 — 当前 Android 主流技术栈
- **兼容性**: 仅 Android 16+（API 36+） — 利用最新平台特性，减少兼容负担
- **设计**: MIUI 设计风格 — 参考小米设计语言
- **存储**: 本地存储（Room 数据库） — 无后端
- **构建**: Gradle + Kotlin DSL — 标准 Android 构建工具
<!-- GSD:project-end -->

<!-- GSD:stack-start source:research/STACK.md -->
## 技术栈

## 推荐技术栈
### 核心框架
| 技术 | 版本 | 用途 | 原因 |
|------------|---------|---------|-----|
| Android Gradle Plugin | **9.0.28+** (最新 9.0.x) | 构建系统 | compileSdk 36 必需，内置 Kotlin 支持（无需 `kotlin-android` 插件） |
| Gradle | **9.3.1** | 构建工具 | AGP 9.0 需要 Gradle 9.1+；9.3.1 为最新稳定版 |
| Kotlin | **2.3.20** | 语言 | 最新稳定版，兼容 AGP 9.0.x |
| KSP | **2.3.6** | 注解处理 | 用于 Room；匹配 Kotlin 2.3.20 |
| JDK | **17**（或 21） | 编译 | AGP 9.0 最低要求；JDK 21 也兼容 |
| compileSdk | **36** | SDK 编译 | Android 16 (API 36)；AGP 9.0 内置 SDK Build Tools 36.0.0 |
| minSdk | **36** | 最低支持 | 项目要求：仅 Android 16+ |
| targetSdk | **36** | 目标行为 | Google Play 新应用要求 API 36 |
### Jetpack Compose（通过 BOM）
| 库 | 版本管理 | 用途 |
|---------|-----------|---------|
| Compose BOM | **2026.04.01** | 管理所有 Compose 库版本 |
| Compose UI | BOM | 核心 UI 原语（Modifier、Layout 等） |
| Compose UI Graphics | BOM | Canvas、图片、渲染 |
| Compose Foundation | BOM | 布局、手势、文本、LazyColumn |
| Material3 | BOM | Material Design 3 组件（NavigationBar、Scaffold 等） |
| Material Icons Extended | BOM | 完整 Material 图标集（用于底部 Tab 图标） |
| Compose Compiler（Kotlin 插件） | Kotlin 2.3.20 内置 | Compose 编译器作为 Kotlin 编译器插件（无需单独依赖） |
### 数据库
| 库 | 版本 | 用途 | 原因 |
|---------|---------|---------|-----|
| Room | **2.8.4** | 本地数据库 | 稳定、文档完善、支持协程/Flow |
| Room KTX | 2.8.4 | 协程扩展 | Suspend DAO 方法、Flow 返回类型 |
| Room Compiler（通过 KSP） | 2.8.4 | 代码生成 | 使用 KSP（非 KAPT）进行 Room 注解处理 |
### 导航
| 库 | 版本 | 用途 | 原因 |
|---------|---------|---------|-----|
| Navigation Compose | **2.9.8** | 屏幕导航 | 稳定、类型安全导航、文档完善 |
### 数据/偏好存储
| 库 | 版本 | 用途 | 原因 |
|---------|---------|---------|-----|
| DataStore Preferences | **1.2.1** | 键值对存储 | SharedPreferences 的现代替代品；基于协程/Flow、事务性 |
### 闪屏
| 库 | 版本 | 用途 | 原因 |
|---------|---------|---------|-----|
| Core SplashScreen | **1.2.0** | 闪屏 API | 标准 AndroidX 实现，支持闪屏图标、动画、主题 |
- 各 API 级别行为一致
- `SplashScreenViewProvider` 支持自定义退出动画
- 通过 styles 中的 `Theme.SplashScreen` 进行主题配置
### Markdown 渲染
| 库 | 版本 | 用途 | 原因 |
|---------|---------|---------|-----|
| compose-richtext (richtext-ui, richtext-commonmark, richtext-ui-material3) | **1.0.0-alpha03** | Compose 中渲染 Markdown | Compose 原生、Material3 主题集成、CommonMark 解析器 |
- `jeziellago/compose-markdown:0.5.8` — 托管于 JitPack（非 Maven Central），维护较少，存在 View 互操作问题
- 基于 WebView — 非 Compose 惯用方式，更重、更慢
- compose-richtext 是唯一活跃维护且支持 Material3 的 Compose 原生库
### 不需要的技术
| 技术 | 原因 |
|-----------|---------|
| `org.jetbrains.kotlin.android` 插件 | AGP 9.0+ **内置 Kotlin 支持** — 应用此插件会导致构建失败 |
| 单独的 Compose Compiler (`androidx.compose.compiler:compiler`) | Compose 编译器 **内置于 Kotlin 2.0+** — 应使用 `org.jetbrains.kotlin.plugin.compose` |
| KAPT | KSP 已取代 KAPT 用于所有注解处理 |
| ViewBinding / DataBinding | Compose 不需要 XML 视图绑定 |
| Hilt / Dagger | 个人应用依赖注入需求简单 — 手动 DI 或轻量级服务定位器即可 |
| Retrofit / OkHttp | 无网络请求 — 纯本地应用 |
| Hilt | 单用户本地应用不需要这种复杂性 |
## 备选方案
| 类别 | 推荐方案 | 备选方案 | 不选原因 |
|----------|-------------|-------------|---------|
| Compose 编译器 | Kotlin 插件（内置） | `androidx.compose.compiler:compiler` | 独立编译器工件自 Kotlin 2.0 起已废弃 |
| 导航 | Compose Navigation 2.9.8 | Navigation 3 1.2.0 | 文档较少；API 不同；对底部 Tab 来说大材小用 |
| 数据库 | Room 2.8.4 | Room 3.0.0-alpha04 | Room 3.0 为 alpha 版，面向 KMP，API 有破坏性变更 |
| 注解处理 | KSP 2.3.6 | KAPT | KAPT 已废弃；KSP 快 2 倍且兼容 AGP 9.0 |
| Markdown | compose-richtext | WebView + marked.js | 非 Compose 惯用方式；更重；更慢 |
| 主题/偏好存储 | DataStore Preferences | SharedPreferences | SharedPreferences 已过时；同步 API 阻塞主线程 |
| 构建插件 | AGP 9.0.x + Kotlin 2.3.x | AGP 9.1.0 + Kotlin 2.4.0-Beta | Kotlin 2.4 是 beta 版；AGP 9.1 需要它。为稳定选择 Kotlin 2.3 |
| DI 框架 | 手动（无库） | Hilt | Hilt 为单人应用增加复杂性（Dagger、kapt/ksp）。从简单开始 |
## 版本目录 (`gradle/libs.versions.toml`)
# Compose BOM 及其管理的依赖
# 导航
# Room
# DataStore
# 闪屏
# Markdown (compose-richtext)
## Gradle 配置
### 项目级 `build.gradle.kts`
### 模块级 `app/build.gradle.kts`
### `gradle.properties`
### `settings.gradle.kts`
## 关键设计决策
### 1. AGP 9.0.x（非 8.x）
### 2. Kotlin 2.3.20（非 2.4 Beta）
### 3. Room 2.8.4（非 3.0）
### 4. Navigation Compose 2.9.8
### 5. Android 16 强制边到边
### 6. MIUI 设计定制
- **字体:** 通过 Material3 的 `Typography` 使用 `mi-sans` 或类似圆角无衬线字体
- **颜色:** MIUI 强调色为暖橙红色 (`#FF6B35`)；主表面为浅灰白色
- **底部 Tab:** MIUI 风格大图标，支持角标
- **圆角:** 高 `M3Shape` 圆角值（卡片 16dp-24dp，底部弹窗 28dp）
- **状态栏:** 透明（Android 16 强制要求），支持深色/浅色文字着色
### 7. Compose 编译器（Kotlin 插件）
## Android Studio 兼容性
| 组件 | 所需版本 |
|-----------|-----------------|
| Android Studio | **Otter (2025.2.1)** 或更高版本（支持 AGP 9.0+） |
| Gradle JDK | JetBrains Runtime 17+ 或 Oracle JDK 17+ |
## 模块结构（推荐）
## 参考来源
| 来源 | 链接 | 可信度 |
|--------|-----|------------|
| Compose BOM 2026.04.01 公告 | https://android-developers.googleblog.com/2026/04/jetpack-compose-april-2026-updates.html | 高 |
| AGP 9.0 发布说明 | https://developer.android.com/build/releases/agp-9-0-0-release-notes | 高 |
| AGP 9.1.0 发布说明 | https://developer.android.com/build/releases/agp-9-1-0-release-notes | 高 |
| AGP/Kotlin 兼容性表 | https://developer.android.com/build/kotlin-support | 高 |
| Room 发布 | https://developer.android.com/jetpack/androidx/releases/room | 高 |
| Navigation Compose | https://developer.android.com/develop/ui/compose/navigation | 高 |
| DataStore 发布 | https://developer.android.com/jetpack/androidx/releases/datastore | 高 |
| SplashScreen API | https://developer.android.com/develop/ui/views/launch/splash-screen | 高 |
| Core SplashScreen 发布 | https://developer.android.com/jetpack/androidx/releases/core | 高 |
| Kotlin 2.3.20 发布 | https://github.com/JetBrains/kotlin/releases | 高 |
| KSP 2.3.6 发布 | https://github.com/google/ksp/releases | 高 |
| Gradle 9.3.0 发布说明 | https://docs.gradle.org/9.3.0/release-notes.html | 高 |
| compose-richtext | https://github.com/halilozercan/compose-richtext | 中（alpha） |
| compose-richtext Maven | https://halilibo.com/compose-richtext | 中（alpha） |
| AGP 9 内置 Kotlin（JetBrains 博客） | https://blog.jetbrains.com/kotlin/2026/01/update-your-projects-for-agp9/ | 高 |
| Android 16 强制边到边 | https://developer.android.com/about/versions/16/behavior-changes-16 | 高 |
| Android 16 特性与 API | https://developer.android.com/about/versions/16/features | 高 |
## 可信度评估
| 领域 | 级别 | 原因 |
|------|-------|--------|
| AGP / Gradle / Kotlin 版本 | 高 | 已验证官方兼容性表 |
| Compose BOM 和 Jetpack 库 | 高 | 官方 Android Developers Blog 和文档 |
| Room + KSP | 高 | 官方发布说明，稳定版本 |
| Navigation Compose | 高 | 官方文档，稳定版本 |
| DataStore | 高 | 官方文档，稳定版本 |
| SplashScreen | 高 | 官方文档，稳定版本 |
| compose-richtext | 中 | 1.0 前 alpha 版；API 可能变化。替代方案为 `jeziellago/compose-markdown`（JitPack，维护较少） |
| MIUI 设计通过 Material3 实现 | 低 | 无官方 MIUI Compose 设计套件；基于视觉近似 |
## 阶段特定注意事项
| 阶段 | 注意事项 |
|-------|---------|
| 构建配置 | 在固定版本前验证 AGP 9.0.x 补丁版本支持 Kotlin 2.3.x。9.0.28+ 阈值来自 AGP/Kotlin 兼容性页面 |
| 闪屏 | Android 16 强制边到边。闪屏必须处理系统栏插入 |
| 导航 | 在底部导航项上使用 `saveState = true` / `restoreState = true` 以保持切换时标签页状态 |
| Markdown | compose-richtext 是 alpha 版；早期测试边缘渲染情况（代码块、图片、表格）。准备回退到基于 WebView 的渲染方案 |
| Room | 尽早定义实体和 DAO；在添加数据前测试迁移 |
<!-- GSD:stack-end -->

<!-- GSD:conventions-start source:CONVENTIONS.md -->
## 约定

约定尚未建立。将在开发过程中随模式出现后补充。
<!-- GSD:conventions-end -->

<!-- GSD:architecture-start source:ARCHITECTURE.md -->
## 架构

架构尚未映射。请遵循代码库中已有的模式。
<!-- GSD:architecture-end -->

<!-- GSD:skills-start source:skills/ -->
## 项目技能

未找到项目技能。将技能添加到以下任一目录：`.claude/skills/`、`.agents/skills/`、`.cursor/skills/`、`.github/skills/` 或 `.codex/skills/`，并包含 `SKILL.md` 索引文件。
<!-- GSD:skills-end -->

<!-- GSD:workflow-start source:GSD defaults -->
## GSD 工作流强制

在使用 Edit、Write 或其他文件修改工具前，先通过 GSD 命令开始工作，以确保规划工件和执行上下文保持同步。

入口点：
- `/gsd-quick` — 小修复、文档更新和临时任务
- `/gsd-debug` — 调查和修复 Bug
- `/gsd-execute-phase` — 执行计划阶段

除非用户明确要求绕过，否则请勿在 GSD 工作流之外直接编辑仓库。
<!-- GSD:workflow-end -->

<!-- GSD:profile-start -->
## 开发者画像

> 由 GSD 通过问卷生成。运行 `/gsd-profile-user --refresh` 更新。

| 维度 | 评级 | 置信度 |
|-----------|--------|------------|
| 沟通风格 | conversational | MEDIUM |
| 决策方式 | deliberate-informed | MEDIUM |
| 解释深度 | concise | MEDIUM |
| 调试方式 | fix-first | MEDIUM |
| UX 理念 | design-conscious | MEDIUM |
| 供应商选择 | conservative | MEDIUM |
| 挫折触发点 | regression | MEDIUM |
| 学习风格 | documentation-first | MEDIUM |

**指令:**
- **沟通:** 使用自然的对话语气。在代码旁简要解释推理过程。回应用户的问题。
- **决策:** 用结构化对比表呈现选项（含优缺点）。让用户做最终决定。
- **解释:** 代码附上简短说明（1-2 句），保持简洁。
- **调试:** 优先给修复方案。先显示修正后的代码，再选择性解释原因。最小化诊断铺垫。
- **UX:** 重视 UX 质量：合理的间距、平滑过渡、响应式布局。把设计当一等公民。
- **供应商:** 推荐成熟、广泛采用、社区支持强的工具。避免前沿实验性选项。
- **挫折:** 修改工作代码前确认安全。潜在回归风险要明确标识。
- **学习:** 引用官方文档和对应章节。以参考手册风格组织解释。
<!-- GSD:profile-end -->
