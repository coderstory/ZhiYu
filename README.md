<p align="center">
  <img src="app/src/main/res/drawable/ic_launcher_foreground.xml" alt="ZhiYu" width="96" height="96" style="max-width: 96px;">
</p>

<h1 align="center">知屿 · ZhiYu</h1>

<p align="center">
  <b>个人知识助手 — 信息看板 · 知识管理 · 工具集</b>
</p>

<p align="center">
  <img alt="Android" src="https://img.shields.io/badge/Android-16%2B-3DDC84?logo=android&logoColor=white">
  <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-2.3.21-7F52FF?logo=kotlin&logoColor=white">
  <img alt="Compose BOM" src="https://img.shields.io/badge/Compose-2026.04.01-4285F4?logo=jetpackcompose&logoColor=white">
  <img alt="License" src="https://img.shields.io/badge/License-MIT-yellow">
</p>

---

## 概览

知屿是一个个人效率 Android 应用，融合信息看板、知识管理和工具集。主界面采用类微信底部 Tab 布局，遵循 MIUI 设计风格。

打开 App 一眼看到当前时间状态，同时方便地记录和检索知识碎片 — 一个 App 搞定日常信息查看和知识管理。

## 截图

| 信息页 | 知识库 | 发现 | 我的 |
|--------|--------|------|------|
| 时间/日期/工作日 | 文章管理 | TBD | 设置 |
| 下班倒计时 | 分类/标签 | TBD | 主题切换 |

## 功能

### 信息看板
- 实时时钟显示（时:分:秒）
- 当前日期与中文星期
- 工作日下班倒计时 / 周末愉快提示
- 暗色/亮色主题跟随系统

### 知识库
- 文章管理（创建、编辑、分类）
- FTS4 全文搜索
- 分类/标签体系
- 多对多标签关联
- 快速笔记（QuickNote）

### 发现
- 预留扩展空间
- 后续迭代功能

### 个人设置
- 主题模式切换（亮色/暗色/跟随系统）
- 应用信息

## 技术栈

| 类别 | 技术 | 版本 |
|----------|---------|---------|
| 语言 | Kotlin | 2.3.21 |
| UI | Jetpack Compose + Material3 | BOM 2026.04.01 |
| MIUI 组件 | Miuix KMP | 0.9.1 |
| 导航 | Navigation Compose | 2.9.8 |
| 数据库 | Room | 2.8.4 |
| 全文搜索 | Room FTS4 | — |
| DI | Koin | 4.2.1 |
| 偏好存储 | DataStore Preferences | 1.2.1 |
| 构建 | Android Gradle Plugin | 9.2.1 |
| 最低 SDK | Android 16 (API 36) | — |

## 架构

```
com.zhiyu.app/
├── MainActivity.kt            # 入口 Activity + 底部导航
├── ZhiYuApplication.kt        # Application + Koin 初始化
├── data/
│   ├── local/
│   │   ├── ZhiYuDatabase.kt   # Room 数据库定义
│   │   ├── converter/         # 类型转换器
│   │   ├── dao/               # DAO 接口
│   │   └── entity/            # 实体类
│   └── preferences/           # DataStore 偏好
├── di/                        # Koin 依赖注入模块
├── model/                     # 数据模型
├── navigation/                # 导航图 + 路由定义
└── ui/
    ├── components/            # 共享组件
    ├── screens/               # 各页面
    │   ├── info/              # 信息看板
    │   ├── knowledge/         # 知识库
    │   ├── discover/          # 发现
    │   └── profile/           # 个人设置
    └── theme/                 # 主题（颜色/字体/形状/尺寸）
```

### 数据层

Room 数据库包含 6 张表：

| 表 | 说明 |
|-----|-------------|
| `articles` | 文章主体，外键关联分类 |
| `categories` | 分类目录 |
| `tags` | 标签定义 |
| `article_tag_cross_ref` | 文章-标签多对多关联 |
| `quick_notes` | 快速笔记 |
| `articles_fts` | FTS4 全文搜索虚拟表 |

### DI 模块

- **AppModule** — 全局单例（数据库、DataStore）
- **RepositoryModule** — Repository 层
- **ViewModelModule** — ViewModel 提供

## 设计

遵循 MIUI 设计语言定制：

- **强调色**: 暖橙 `#FF6B35`，深色模式变体 `#FF8A50`
- **字体**: `mi-sans` 圆润无衬线体
- **圆角**: 大值 M3Shape（卡片 16dp-24dp，底部弹窗 28dp）
- **底部导航**: MIUI 风格大图标 + 标签
- **状态栏**: 自适应亮/暗色文字着色

## 构建

```bash
# Debug
./gradlew assembleDebug

# Release
./gradlew assembleRelease
```

### 前提

- Android Studio Otter (2025.2.1+) 或 IntelliJ IDEA
- JDK 17+
- Android SDK 36+

## 路线图

- [x] Phase 1: 基础框架 — 主题、导航、数据层
- [ ] Phase 2: 信息看板完善
- [ ] Phase 3: 知识库 CRUD
- [ ] Phase 4: Markdown 渲染
- [ ] Phase 5: 工具集
- [ ] Phase 6: 搜索与设置

## 许可

```
MIT License

Copyright (c) 2026 ZhiYu

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files...
```
