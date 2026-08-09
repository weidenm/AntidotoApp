# Changelog

All notable changes to the Antídoto project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

**MVP feature-complete** — todas as fases 1.0–1.5 implementadas. Pendências antes do `1.0.0-mvp`: teste final em dispositivo físico e submissão à Play Store.

### Phase 1.0 — Infra & Setup
- [x] Android project scaffolding with Kotlin + Jetpack Compose
- [x] Material 3 theme setup (light + dark mode)
- [x] Build configuration (Gradle + version catalog)
- [x] AndroidManifest with permissions (`PACKAGE_USAGE_STATS`, `POST_NOTIFICATIONS`)
- [x] CI/CD setup (GitHub Actions)
- [x] Architecture documentation (CLAUDE.md)
- [x] README with setup instructions
- [x] ProGuard rules for release builds

### Phase 1.1 — Data Layer
- [x] Room database setup with SQLite (`AppDatabase`, `LocalDate` converters)
- [x] Entity definitions (AppUsageEntry, CheckIn, Lesson, Goal) + Streak domain model
- [x] DAO implementations (usage, check-ins, lessons, goals) with explicit column projections
- [x] UsageStatsRepository wrapping UsageStatsManager (event-based aggregation)
- [x] UsageEventAggregator: pure, JVM-testable session aggregation with open-count merge gap
- [x] Repositories: CheckInRepository, GoalRepository, LessonRepository
- [x] SyncUsageWorker (WorkManager + Hilt, 30 min periodic, retry up to 3x)
- [x] AntidotoApplication provides HiltWorkerFactory and schedules background sync
- [x] Manifest `<queries>` limited to launchable apps (no `QUERY_ALL_PACKAGES`)
- [x] Unit tests: usage aggregator (10 cases), LocalDate converters, week-start logic (16 total)

### Phase 1.2 — Dashboard Core
- [x] `DashboardViewModel` (Hilt) combining today/weekly usage + active goals into UI state
- [x] Dashboard data models (`DashboardData`, `DailyUsage`, `AttentionCost`) and `DashboardUiState`
- [x] `CalculateCostOfAttention` use case (yearly projection → books/courses), unit-tested
- [x] `HomeScreen` Composable with Loading / Error / Ready states
- [x] Attention Cost card, weekly usage trend chart, and per-app usage breakdown list
- [x] Usage-access permission banner with deep-link to system Settings + refresh-on-resume
- [x] Switched app theme to NoActionBar so Compose owns the full screen

### Phase 1.3 — Habit Loop
- [x] Bottom-navigation shell (Início / Check-in / Metas / Ajustes) with Navigation Compose
- [x] CheckInScreen: mood + trigger selection, recorded via `CheckInRepository`
- [x] Streak tracking (`StreakCalculator`, unit-tested) shown on Home and Check-in
- [x] GoalsScreen: set weekly per-app targets (upsert via unique app+week index)
- [x] SettingsScreen: daily-reminder toggle, reminder hour, usage-access shortcut
- [x] NotificationHelper + `CheckInReminderWorker` + `ReminderScheduler` (daily reminder)
- [x] POST_NOTIFICATIONS runtime request; WorkManager-scheduled daily reminder

### Phase 1.4 — Lessons & Trail
- [x] 15 micro-lessons content (`LessonSeed`, pt-BR algorithmic-literacy)
- [x] `LessonsTrailScreen` with completion progress and per-lesson status
- [x] `LessonDetailScreen` with content and "Entendi" completion action
- [x] Lesson completion tracking via `LessonRepository`
- [x] Sequential unlock logic (`BuildLessonTrail`, unit-tested); idempotent seeding
- [x] "Lições" navigation tab + lesson detail route

### Phase 1.5 — Polish & App Store
- [x] Expanded unit tests (repositories + ViewModels) with kotlinx-coroutines-test
- [x] JaCoCo coverage report (`jacocoTestReport`); domain+data ≈ 65% instruction coverage
- [x] Instrumented Room DAO test suite (in-memory database)
- [x] Accessibility: content description for the weekly chart, merged lesson-row semantics
- [x] Release signing config (keystore.properties / ANTIDOTO_* env, debug fallback)
- [x] Play Store materials: LGPD privacy policy, pt-BR store listing, release checklist
- [ ] Final testing on physical devices (manual, pre-launch)

---

## [1.0.0-mvp] — 2026-09-XX (Expected)

### Initial Release
- Complete MVP with dashboard, check-in, goals, streaks, and lessons
- Local-first architecture with Room + SQLite
- Hilt dependency injection
- Material 3 design system
- LGPD-compliant privacy
- Play Store submission

---

## Notes

- Fases 1.0–1.5 (MVP) implementadas; ver o status de cada uma acima
- Antes do release `1.0.0-mvp`: teste em dispositivo físico e envio à Play Store
- Fases pós-MVP (2–4): intervenções de fricção (Atalho de Intenção), importador de dados LGPD e monetização — ver `CLAUDE.md`
