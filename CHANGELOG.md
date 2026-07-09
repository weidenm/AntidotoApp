# Changelog

All notable changes to the Antídoto project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Phase 1.0 — Infra & Setup
- [x] Android project scaffolding with Kotlin + Jetpack Compose
- [x] Material 3 theme setup (light + dark mode)
- [x] Build configuration (Gradle + version catalog)
- [x] AndroidManifest with permissions (`PACKAGE_USAGE_STATS`, `POST_NOTIFICATIONS`)
- [x] CI/CD setup (GitHub Actions)
- [x] Architecture documentation (CLAUDE.md)
- [x] README with setup instructions
- [x] ProGuard rules for release builds

### Phase 1.1 — Data Layer (In Progress)
- [ ] Room database setup with SQLite
- [ ] Entity definitions (AppUsageEntry, CheckIn, Lesson, Goal, Streak)
- [ ] DAO implementations
- [ ] UsageStatsRepository wrapper
- [ ] WorkManager for background sync
- [ ] Unit & instrumented tests

### Phase 1.2 — Dashboard Core (Upcoming)
- [ ] HomeScreen Composable
- [ ] Dashboard data models
- [ ] Attention Cost calculations
- [ ] Weekly usage trend chart
- [ ] App list with usage breakdown
- [ ] DashboardViewModel

### Phase 1.3 — Habit Loop (Upcoming)
- [ ] CheckInScreen (mood + trigger)
- [ ] GoalsScreen (set weekly targets)
- [ ] Streak tracking & display
- [ ] NotificationService
- [ ] Daily reminder scheduling
- [ ] SettingsScreen

### Phase 1.4 — Lessons & Trail (Upcoming)
- [ ] 15 micro-lessons content
- [ ] LessonsTrailScreen
- [ ] Lesson detail screen
- [ ] Lesson completion tracking
- [ ] Daily lesson unlocking logic

### Phase 1.5 — Polish & App Store (Upcoming)
- [ ] Unit test suite (60%+ coverage target)
- [ ] Instrumented test suite
- [ ] Accessibility audit (WCAG AA)
- [ ] Performance profiling
- [ ] Release build signing
- [ ] Play Store listing (screenshots, descriptions, privacy policy)
- [ ] Final testing on physical devices

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

- Each phase corresponds to 1-2 weeks of development
- Total timeline: 6-8 weeks for MVP (Phase 1.0-1.5)
- Future phases (2-4) will add friction interventions, LGPD data importer, and monetization
