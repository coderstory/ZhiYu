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

## 实际落地技术栈 (2026-05-18)
### 核心框架
| 技术 | 版本 | 用途 |
|------------|---------|---------|
| Android Gradle Plugin | **9.2.1** | 构建系统 |
| Gradle | **9.4.1** | 构建工具 |
| Kotlin | **2.3.21** | 语言 (MIUIX 0.9.1 要求) |
| KSP | **2.3.6** | 注解处理 (Room) |
| JDK | **26** | 编译 |
| compileSdk | **37 (preview)** | SDK 编译 |
| minSdk | **36** | 最低支持 (Android 16) |
| targetSdk | **36** | 目标行为 |

### Jetpack Compose (BOM 2026.04.01)
Material3 + Material Icons Extended + Foundation + UI Tooling

### 数据库
Room 2.8.4 + KTX + Compiler (KSP)

### 导航
Navigation Compose 2.9.8 + Kotlin Serialization (@Serializable routes)

### DI
Koin 4.2.1 (koin-android + koin-androidx-compose)

### 主题/偏好
DataStore Preferences 1.2.1 — `flowOn(Dispatchers.IO)` + `distinctUntilChanged()`

### MIUIX 组件
- `miuix-ui:0.9.1` — 基础 UI 组件 (NavigationBar, Scaffold, Card, Text, Icon, TopAppBar, LazyColumn 等)
- `miuix-icons:0.9.1` — 扩展图标集
- `miuix-preference:0.9.1` — 偏好设置组件 (ArrowPreference, RadioButtonPreference)

### Markdown (Phase 4)
compose-richtext 1.0.0-alpha03 (richtext-ui + richtext-commonmark + richtext-ui-material3)

### 闪屏
平台内置 SplashScreen API (API 31+)，`core-splashscreen` 1.2.0 在 compileSdk 37 预览版中不可用

### 不需要的技术
`kotlin-android` 插件 | KAPT | ViewBinding/DataBinding | Hilt/Dagger | Retrofit/OkHttp

## 版本目录 (`gradle/libs.versions.toml`)
包含所有依赖声明。通过 `alias(libs.*)` 引用。

## Gradle 关键配置
- AGP 9.x 内置 Kotlin 支持，不需要 `kotlin-android` 插件
- Compose 编译器通过 `org.jetbrains.kotlin.plugin.compose` 启用
- KSP 用于 Room 注解处理 (`ksp(libs.room.compiler)`)
- `android.overridePathCheck=true` — 项目路径含中文
- `android.experimental.enableAarMetadataCheck=false` — MIUIX 要求 compileSdk 37

## 关键设计决策 (实际落地)
### 1. AGP 9.2.1 (非 9.0.28)
9.0.28 在发布时将信息不可用，9.2.1 为最新稳定版，需要 Gradle 9.4.1+

### 2. Kotlin 2.3.21 (非 2.3.20)
MIUIX v0.9.1 要求 Kotlin 2.3.21。2.3.21 是补丁版本，无破坏性变更

### 3. MIUIX 主题驱动 + Material3 兜底
MIUIX 使用 `CompositionLocalProvider` 直接提供主题，不包装 MaterialTheme。
`ZhiYuTheme` 中 `MaterialTheme` 在 `MiuixTheme` 外层，确保两边组件都看到正确的主题值。

### 4. compileSdk 37 (预览版)
MIUIX 0.9.0/0.9.1 需要 compileSdk 37。使用 `build-tools;37.0.0` + `platforms;android-37.0`。

### 5. 闪屏使用平台 API
compileSdk 37 下 `core-splashscreen` 类路径冲突。使用 `Activity.getSplashScreen()` 平台 API。

### 6. MIUI 设计定制
- 颜色: MIUI 暖橙 `#FF6B35` (亮色) / `#FF8A50` (暗色)
- 圆角: 4dp-24dp (卡片 16dp，底部弹窗 28dp)
- 间距: 8 点网格 (4, 8, 16, 24, 32, 48, 64 dp)
- 状态栏: 透明 (Android 16 强制)

### 7. Room FTS4
使用 `TOKENIZER_UNICODE61` (ICU 在 Android SQLite 中不可用)。独立 FTS4 实体，不使用 `contentEntity`。
<!-- GSD:stack-end -->

<!-- GSD:conventions-start source:CONVENTIONS.md -->
## 约定

### 代码风格
- Compose 代码使用 PascalCase 命名可组合函数
- Room 实体使用 `Long` 类型主键，不使 `Int`
- 使用 JOIN 查询而非 `@Relation` 避免 N+1 问题
- DataStore Flow 必须加 `.flowOn(Dispatchers.IO)` + `.distinctUntilChanged()`
- ViewModel 使用 `StateFlow<UiState>` 不可变状态模型

### 包结构 (feature-first)
```
com.zhiyu.app/
├── MainActivity.kt          # 单 Activity 入口
├── ZhiYuApplication.kt      # Koin 初始化
├── navigation/              # 路由定义 + NavHost
├── di/                      # Koin 模块
├── model/                   # 共享模型
├── data/
│   ├── local/               # Room (database, dao, entity, converter)
│   └── preferences/         # DataStore 包装器
├── ui/
│   ├── theme/               # Color, Type, Shape, Dimens, Theme
│   ├── screens/
│   │   ├── info/            # 信息 Tab (Dashboard)
│   │   ├── knowledge/       # 知识库 Tab (Article CRUD, Markdown)
│   │   ├── discover/        # 发现 Tab (工具集合)
│   │   └── profile/         # 我的 Tab (设置, 关于)
│   └── components/          # 共享可组合组件
```

### 导航约定
- 使用 `@Serializable` 类型安全路由
- Bottom Tab 使用 `launchSingleTop = true` + `restoreState = true`
- 子路由使用 `toRoute<T>()` 读取参数

### 提交风格
- 使用 Conventional Commits 格式: `feat|fix|docs(scope): message`
- 保持主题行 ≤ 50 字符

### 代理执行规则
- 子任务必须使用 `isolation: "worktree"` 避免并行 agent 冲突
- 修改代码前先评估当前实现，避免替换方案
- 禁止用 try-catch 隐藏错误

<!-- GSD:conventions-end -->

<!-- GSD:architecture-start source:ARCHITECTURE.md -->
## 架构

### 模式: MVVM + UDF
```
UI (Composable) → ViewModel (StateFlow<UiState>) → Repository → Room DAO / DataStore
```

### 依赖顺序
```
ZhiYuTheme → Koin → Room DB + DataStore → DAOs → Repositories → ViewModels → Screens → MainActivity
```

### 主题树
```
ZhiYuTheme(themeMode)
├── MaterialTheme (colorScheme, typography, shapes)  ← 外层: Material3 组件
│   └── MiuixTheme (colors, textStyles)              ← 内层: MIUIX 组件
│       └── Scaffold (MIUIX)
│           ├── TopAppBar (MIUIX)
│           ├── NavigationBar (MIUIX) + NavigationBarItem
│           └── NavHost
│               ├── InfoScreen
│               ├── KnowledgeScreen
│               ├── DiscoverScreen
│               └── ProfileScreen
```

### Koin 模块
- **AppModule**: Room 数据库单例, 4 个 DAO, AppPreferences
- **RepositoryModule**: ArticleRepository (Phase 4)
- **ViewModelModule**: InfoViewModel, KnowledgeViewModel, ArticleDetailViewModel, EditorViewModel, ProfileViewModel

### 数据流
- Room DAO 返回 `Flow<T>` 用于读取, `suspend` 用于写入
- DataStore 通过 Flow 实现设置即时生效
- 主题切换(`ThemeMode.SYSTEM/LIGHT/DARK`) 通过 DataStore Flow 传播

### 渲染需求注意事项
- 实时时钟隔离在 InfoViewModel 的 `flow { while(true) { ...; delay(1000) } }` 中
- Markdown 编辑器自动保存: 每5秒保存草稿到 Room
- FTS4 搜索: 300ms debounce + unicode61 tokenizer

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
