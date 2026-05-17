# 知屿 (ZhiYu) — 个人知识助手

## What This Is

Android 个人效率应用，融合信息看板、知识管理和工具集。主界面采用类微信底部Tab布局（MIUI设计风格），提供信息概览（时间/星期/下班倒计时）、知识库管理（类似羽雀，支持分类/标签/小记）、工具集合和个人设置等核心功能。

## Core Value

用户打开App能一眼看到当前时间状态，同时方便地记录和检索知识碎片 — 一个 App 搞定日常信息查看和知识管理。

## Requirements

### Validated

(None yet — ship to validate)

### Active

- [ ] KNO-01: 应用使用 Kotlin + Jetpack Compose 技术栈
- [ ] KNO-02: 仅兼容 Android 16+（minSdk = 36, targetSdk = 36）
- [ ] KNO-03: 启动页（Splash Screen）
- [ ] KNO-04: 主界面底部4个Tab导航（MIUI设计风格）
- [ ] KNO-05: "信息" Tab — 显示当前星期、时间、下班倒计时（18:00）
- [ ] KNO-06: "知识库" Tab — 文章管理，支持分类和标签整理
- [ ] KNO-07: 知识库支持 Markdown 编辑和预览
- [ ] KNO-08: 知识库支持全文搜索
- [ ] KNO-09: "小记"功能 — 零碎知识快速记录
- [ ] KNO-10: "发现" Tab — 工具集合入口
- [ ] KNO-11: "我的" Tab — 个人中心
- [ ] KNO-12: 设置页面 — 主题切换 + 暗色模式（跟随系统配置）
- [ ] KNO-13: 关于页面
- [ ] KNO-14: 可生成 APK/AAB 安装包

### Out of Scope

- 多语言支持 — v1仅中文
- 云端同步/账号系统 — 纯本地应用
- 富文本编辑器 — Markdown 足够
- 附件/图片上传 — v1不包含
- iOS 版本 — 仅 Android

## Context

用户需要一个融合信息查看和知识管理的轻量级Android应用。参考了微信的底部Tab布局和羽雀的知识组织方式。整体风格参照小米MIUI设计语言。

此应用为个人使用，纯本地存储，无后端服务依赖。

## Constraints

- **Tech Stack**: Kotlin + Jetpack Compose + Material3 — 当前 Android 主流技术栈
- **Compatibility**: 仅 Android 16+（API 36+） — 利用最新平台特性，减少兼容负担
- **Design**: MIUI 设计风格 — 参考小米设计语言
- **Storage**: 本地存储（Room 数据库） — 无后端
- **Build**: Gradle + Kotlin DSL — 标准 Android 构建工具

## Key Decisions

| Decision | Rationale | Outcome |
|----------|-----------|---------|
| Kotlin + Jetpack Compose | 官方推荐的现代 Android 开发方式 | — Pending |
| Only Android 16+ | 减少兼容性测试，利用新 API | — Pending |
| MIUI Design Style | 用户偏好小米设计风格 | — Pending |
| Local-only storage | 个人使用，无需同步 | — Pending |
| Markdown for articles | 标准版功能，平衡复杂度与体验 | — Pending |

---

*Last updated: 2026-05-17 after project initialization*
