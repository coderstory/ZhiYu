# Requirements: 知屿 (ZhiYu)

**Defined:** 2026-05-17
**Core Value:** 一个 App 搞定日常信息查看和知识管理

## v1 Requirements

### 项目基础 (FOUNDATION)

- [ ] **FND-01**: Kotlin + Jetpack Compose 项目配置，Gradle Kotlin DSL + 版本目录
- [ ] **FND-02**: Android 16+ 兼容（minSdk = targetSdk = 36）
- [ ] **FND-03**: Koin 依赖注入框架集成
- [ ] **FND-04**: MIUIX UI 库集成（`top.yukonga.miuix.kmp:miuix-ui`）
- [ ] **FND-05**: Room 数据库 + DataStore 偏好存储
- [ ] **FND-06**: MVVM + UDF 架构模式
- [ ] **FND-07**: 主题系统 — MIUI 风格 + 暗色模式（跟随系统配置）
- [ ] **FND-08**: 中文字体回退支持（CJK font fallback）
- [ ] **FND-09**: Splash 启动屏

### 导航壳子 (NAVIGATION)

- [ ] **NAV-01**: 底部4Tab导航（信息/知识库/发现/我的）
- [ ] **NAV-02**: Tab 状态保存与恢复
- [ ] **NAV-03**: Navigation Compose 类型安全路由
- [ ] **NAV-04**: Android 16 Edge-to-Edge 适配

### 信息Tab (DASHBOARD)

- [ ] **DSH-01**: 当前日期和星期显示
- [ ] **DSH-02**: 实时时钟显示（秒级刷新）
- [ ] **DSH-03**: 下班倒计时（18:00，显示时:分:秒）
- [ ] **DSH-04**: 18:00后/周末显示友好状态（"已下班" / "周末愉快"）

### 知识库Tab (KNOWLEDGE)

- [ ] **KNW-01**: 文章 CRUD（创建/阅读/更新/删除）
- [ ] **KNW-02**: 分类管理（树形/目录组织）
- [ ] **KNW-03**: 标签管理（多对多关联）
- [ ] **KNW-04**: Markdown 渲染展示
- [ ] **KNW-05**: Markdown 编辑（纯文本+预览模式）
- [ ] **KNW-06**: 全文搜索（Room FTS4）
- [ ] **KNW-07**: 文章列表（标题+预览摘要）
- [ ] **KNW-08**: 空状态引导

### 小记功能 (QUICKNOTE)

- [ ] **QNT-01**: 快速记录零碎知识（纯文本）
- [ ] **QNT-02**: 小记列表查看
- [ ] **QNT-03**: 小记删除

### 发现Tab (TOOLS)

- [ ] **TLS-01**: 工具集合入口页面
- [ ] **TLS-02**: 日历工具（简单日历展示）
- [ ] **TLS-03**: 计算器工具
- [ ] **TLS-04**: 天气工具（定位+展示）

### 我的Tab (PROFILE)

- [ ] **PRF-01**: "我的"页面展示（头像/昵称占位）
- [ ] **PRF-02**: 设置页面 — 主题切换（亮色/暗色/跟随系统）
- [ ] **PRF-03**: 设置页面 — 暗色模式开关
- [ ] **PRF-04**: 关于页面（版本号/开源许可）

### 构建发布 (RELEASE)

- [ ] **REL-01**: ProGuard/R8 混淆配置
- [ ] **REL-02**: 生成 APK/AAB 安装包

## v2 Requirements

### 知识库增强

- **KNW-09**: Rich text editing 富文本模式
- **KNW-10**: 附件/图片插入
- **KNW-11**: 文章分享导出

### 云同步

- **SYN-01**: 账号系统
- **SYN-02**: 云端备份/同步

## Out of Scope

| Feature | Reason |
|---------|--------|
| iOS/Desktop 平台 | 仅 Android v1 |
| 云端同步/账号 | 纯本地应用 |
| 富文本编辑器 | Markdown 足够 v1 |
| AI 功能 | 不必要复杂度 |
| 多语言 | v1 仅中文 |
| 第三方登录 | 无账号系统 |

## Traceability

| Requirement | Phase | Status |
|-------------|-------|--------|
| FND-01~FND-09 | Phase 1 | Pending |
| NAV-01~NAV-04 | Phase 2 | Pending |
| DSH-01~DSH-04 | Phase 3 | Pending |
| KNW-01~KNW-08 | Phase 4 | Pending |
| QNT-01~QNT-03 | Phase 4 | Pending |
| TLS-01~TLS-04 | Phase 5 | Pending |
| PRF-01~PRF-04 | Phase 5 | Pending |
| REL-01~REL-02 | Phase 6 | Pending |

**Coverage:**
- v1 requirements: 38 total
- Mapped to phases: 38
- Unmapped: 0 ✓

---
*Requirements defined: 2026-05-17*
*Last updated: 2026-05-17 after traceability update*
