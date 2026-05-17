# Phase 1: Foundation - Discussion Log

> **Audit trail only.** Do not use as input to planning, research, or execution agents.
> Decisions are captured in CONTEXT.md — this log preserves the alternatives considered.

**Date:** 2026-05-17
**Phase:** 1-Foundation
**Areas discussed:** MIUIX 集成深度, Room 实体预定义, 启动屏设计, 包名与应用名

---

## MIUIX 集成深度

| Option | Description | Selected |
|--------|-------------|----------|
| 全量引入 | 添加所有 MIUIX 依赖，各阶段直接使用 | ✓ |
| 按需导入 | 只加核心依赖，后续再加 | |

**User's choice:** 全量引入 — full MIUIX dependency at foundation

| Option | Description | Selected |
|--------|-------------|----------|
| MIUIX 主题驱动 | MIUIX Theme + Material3 兜底 | ✓ |
| Material3 为主体 | Material3 主题 + MIUIX 特定组件 | |

**User's choice:** MIUIX 主题驱动 — MIUIX theme primary, Material3 fallback

| Option | Description | Selected |
|--------|-------------|----------|
| Foundation 配好 | 亮色/暗色一次配好 | ✓ |
| 后续再加 | 等 Phase 5 再补暗色 | |

**User's choice:** Foundation 配好 — dark mode at foundation

**Notes:** User chose full MIUIX integration depth with theme-driven approach and dark mode ready from foundation.

---

## Room 实体预定义

| Option | Description | Selected |
|--------|-------------|----------|
| 全部预创建 | 5 实体 + FTS4 一次建好 | ✓ |
| 只建基础表 | 先建 Article/QuickNote，后续再加 | |

**User's choice:** 全部预创建 — all entities at foundation

| Option | Description | Selected |
|--------|-------------|----------|
| Foundation 建好 | FTS4 + ICU tokenizer 一次到位 | ✓ |
| Phase 4 再加 | 后续再加 FTS4 | |

**User's choice:** Foundation 建好 — FTS4 at foundation

| Option | Description | Selected |
|--------|-------------|----------|
| 完整预定义 | theme_mode/last_active_tab/is_first_launch etc | ✓ |
| 仅基础项 | 只预定义 theme_mode | |

**User's choice:** 完整预定义 — all DataStore keys at foundation

**Notes:** User wants complete data layer at foundation for migration stability.

---

## 启动屏设计

| Option | Description | Selected |
|--------|-------------|----------|
| 图标 + 应用名 | 图标 + "知屿" 文字 | ✓ |
| 仅图标 | App 图标，无文字 | |

**User's choice:** 图标 + 应用名

| Option | Description | Selected |
|--------|-------------|----------|
| DataStore 就绪后 | setKeepOnCondition 监听初始化完成 | ✓ |
| 固定时长 | 固定 1-1.5s 关闭 | |

**User's choice:** DataStore 就绪后关闭

| Option | Description | Selected |
|--------|-------------|----------|
| MIUI 暖橙 (#FF6B35) | 暖橙背景 + 白色图标 | ✓ |
| 纯白/浅灰背景 | 浅色背景 + 橙色图标 | |

**User's choice:** MIUI 暖橙 (#FF6B35)

---

## 包名与应用名

| Option | Description | Selected |
|--------|-------------|----------|
| com.zhiyu.app | 简洁标准风格 | ✓ |
| com.zhiyu.zhangyu | 全拼风格 | |
| com.example.zhiyu | 占位包名 | |

**User's choice:** com.zhiyu.app

| Option | Description | Selected |
|--------|-------------|----------|
| 知屿 | 中文名称 | ✓ |
| ZhiYu | 英文名称 | |
| 知屿 ZhiYu | 中英双语 | |

**User's choice:** 知屿

---

## Claude's Discretion

- MIUIX component import granularity
- Room DAO method signatures
- Compose theme file organization
- Gradle task configuration details

## Deferred Ideas

None — discussion stayed within phase scope.
