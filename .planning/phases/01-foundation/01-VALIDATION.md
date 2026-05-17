---
phase: 1
slug: foundation
status: draft
nyquist_compliant: false
wave_0_complete: false
created: 2026-05-17
---

# Phase 1 — Validation Strategy

> Per-phase validation contract for feedback sampling during execution.

---

## Test Infrastructure

| Property | Value |
|----------|-------|
| **Framework** | JUnit 4.13.2 + Compose UI Test |
| **Config file** | app/build.gradle.kts (dependencies block) |
| **Quick run command** | `./gradlew :app:testDebug` |
| **Full suite command** | `./gradlew :app:testDebug :app:connectedDebugAndroidTest` |
| **Estimated runtime** | ~120 seconds |

---

## Sampling Rate

- **After every task commit:** Run quick command
- **After every plan wave:** Run full suite
- **Before `/gsd:verify-work`:** Full suite must be green
- **Max feedback latency:** 180 seconds

---

## Per-Task Verification Map

| Task ID | Plan | Wave | Requirement | Threat Ref | Secure Behavior | Test Type | Automated Command | File Exists | Status |
|---------|------|------|-------------|------------|-----------------|-----------|-------------------|-------------|--------|
| 01-01-01 | 1 | 1 | FND-01 | — | N/A | unit | `./gradlew :app:testDebug` | ❌ W0 | ⬜ pending |
| 01-01-02 | 1 | 1 | FND-02 | — | N/A | unit | `./gradlew :app:testDebug` | ❌ W0 | ⬜ pending |
| 01-01-03 | 1 | 1 | FND-03 | — | N/A | unit | `./gradlew :app:testDebug` | ❌ W0 | ⬜ pending |
| 01-02-01 | 2 | 1 | FND-04 | — | N/A | unit | `./gradlew :app:testDebug` | ❌ W0 | ⬜ pending |
| 01-02-02 | 2 | 1 | FND-05 | — | N/A | unit | `./gradlew :app:testDebug` | ❌ W0 | ⬜ pending |
| 01-03-01 | 3 | 2 | FND-06 | — | N/A | unit | `./gradlew :app:testDebug` | ❌ W0 | ⬜ pending |
| 01-03-02 | 3 | 2 | FND-07 | — | N/A | unit | `./gradlew :app:testDebug` | ❌ W0 | ⬜ pending |
| 01-03-03 | 3 | 2 | FND-08 | — | N/A | unit | `./gradlew :app:testDebug` | ❌ W0 | ⬜ pending |
| 01-04-01 | 4 | 2 | FND-09 | — | N/A | unit | `./gradlew :app:testDebug` | ❌ W0 | ⬜ pending |

*Status: ⬜ pending · ✅ green · ❌ red · ⚠️ flaky*

---

## Wave 0 Requirements

- [ ] `app/src/test/java/com/zhiyu/app/` — test directory structure
- [ ] `app/src/androidTest/java/com/zhiyu/app/` — androidTest directory structure
- [ ] Room migration tests (`MigrationTestHelper`) — verify schema upgrades

*If none: "Existing infrastructure covers all phase requirements."*

---

## Manual-Only Verifications

| Behavior | Requirement | Why Manual | Test Instructions |
|----------|-------------|------------|-------------------|
| CJK font fallback renders Chinese text | FND-08 | Requires visual inspection on MIUI device | Launch app, check Chinese characters in splash and main screen show no tofu |
| Splash screen display and dismiss | FND-09 | Requires visual inspection | Cold start app, verify splash icon + "知屿" appears, then main screen loads |
| MIUI theme colors render correctly | FND-07 | Requires visual inspection | Toggle between light/dark, verify warm orange accent and rounded corners |

---

## Validation Sign-Off

- [ ] All tasks have `<automated>` verify or Wave 0 dependencies
- [ ] Sampling continuity: no 3 consecutive tasks without automated verify
- [ ] Wave 0 covers all MISSING references
- [ ] No watch-mode flags
- [ ] Feedback latency < 180s
- [ ] `nyquist_compliant: true` set in frontmatter

**Approval:** pending
