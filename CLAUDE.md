# Antídoto — MVP Android Architecture & Development Guide

**Document Version:** 1.0  
**Last Updated:** 09/07/2026  
**Project Stage:** Phase 1 — MVP Android (Weeks 1-8)

---

## Project Overview

**Antídoto** is an algorithmic immunity trainer for Brazilian users—not a blocker. It combines:
- Digital wellness awareness via `UsageStatsManager` (official Android API)
- Gentle, consensual friction (check-ins, goals, educational lessons)
- 100% local-first: no data leaves the device
- LGPD-compliant: zero server-side data collection in MVP

**Success Metrics:**
- D1 Retention ≥35%
- D30 Retention ≥12%
- ≥50% active users complete ≥3 lessons in week 1
- NPS ≥40
- 0 crashes for >100 users

---

## Architecture Overview

### Layered Architecture

```
┌─ UI Layer (Compose) ────────────────────────────┐
│ • Screens: Home, Dashboard, CheckIn, Goals       │
│ • Components: Cards, Charts, Buttons             │
│ • ViewModels: State management via UiState       │
├─ Domain Layer (Business Logic) ─────────────────┤
│ • UseCases: RecordCheckIn, UpdateGoal            │
│ • Models: DashboardData, Goal, Streak            │
│ • Services: NotificationService, AnalyticsHelper │
├─ Data Layer (Repository + DB) ──────────────────┤
│ • Repository: UsageStatsRepository, GoalRepo     │
│ • Room DAO: AppUsageEntryDao, CheckInDao, etc   │
│ • Database: AppDatabase (SQLite)                 │
├─ Workers (Background Tasks) ────────────────────┤
│ • SyncUsageWorker: Periodic sync of UsageStats   │
│ • NotificationWorker: Daily check-in reminder    │
└─────────────────────────────────────────────────┘
```

### Module Structure

```
app/
├── src/main/kotlin/com/antidoto/
│   ├── MainActivity.kt
│   ├── ui/
│   │   ├── screens/          (HomeScreen, CheckInScreen, etc)
│   │   ├── components/       (Reusable UI components)
│   │   ├── viewmodels/       (State management)
│   │   └── theme/            (Colors, Typography, Theme)
│   ├── data/
│   │   ├── db/
│   │   │   ├── entities/     (Room entities)
│   │   │   ├── dao/          (Room DAOs)
│   │   │   └── AppDatabase.kt
│   │   └── repository/       (Data layer abstractions)
│   ├── domain/
│   │   ├── model/            (Business models)
│   │   ├── usecase/          (Business logic)
│   │   └── service/          (Services)
│   └── workers/              (WorkManager tasks)
└── src/main/res/
    ├── values/               (strings, dimens, colors)
    └── xml/                  (backup rules, data extraction)
```

---

## Kotlin & Compose Conventions

### Naming
- **Composables:** `PascalCase` (e.g., `HomeScreen`, `AppUsageCard`)
- **Functions:** `camelCase` (e.g., `calculateCostOfAttention()`)
- **Constants:** `UPPER_SNAKE_CASE` (e.g., `MAX_HOURS_PER_YEAR`)
- **Classes/Data Classes:** `PascalCase`
- **Packages:** `lowercase.nounderscores`

### Code Style
- **Null safety:** Use `?` sparingly; prefer non-null models with defaults
- **Immutability:** Data classes for models, `@Immutable` for Composables
- **Coroutines:** Use `viewModelScope` for UI-bound jobs, `CoroutineScope` for workers
- **No comments unless WHY is non-obvious**

### Compose Best Practices
- Keep Composables pure (no side effects)
- Use `remember` for local state; avoid `mutableStateOf` in production if a ViewModel exists
- Compose lambda slots for flexibility, not boolean flags
- Use `derivedStateOf` to prevent unnecessary recompositions

---

## Room Database

### Entities

All entities inherit from `BaseEntity` (timestamp + id defaults):

```kotlin
@Entity(tableName = "app_usage_entries")
data class AppUsageEntry(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val packageName: String,
    val appName: String,
    val durationMs: Long,
    val openCount: Int,
    @ColumnInfo(name = "timestamp_millis") val timestampMillis: Long = System.currentTimeMillis(),
    val date: LocalDate,  // indexed for day-based queries
)

@Entity(tableName = "check_ins")
data class CheckIn(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val mood: Mood,  // enum: WELL, NEUTRAL, ANXIOUS, TIRED
    val trigger: Trigger,  // enum: BOREDOM, ANXIETY, HABIT, WORK
    @ColumnInfo(name = "timestamp_millis") val timestampMillis: Long = System.currentTimeMillis(),
)

@Entity(tableName = "lessons")
data class Lesson(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val imageUrl: String? = null,  // local asset reference
    var completedAt: Long? = null,
    val orderIndex: Int,
)

@Entity(
    tableName = "goals",
    indices = [Index(value = ["appId", "weekStartDate"])]
)
data class Goal(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val appId: String,  // package name
    val weekStartDate: LocalDate,
    val targetMinutes: Int,
    val userSetMinutes: Int,
    val weekProgress: Int = 0,
)

data class Streak(
    val checkInCount: Int,
    val lessonCount: Int,
    val lastDate: LocalDate,
)
```

### DAO Conventions

- **Suspend functions** for all write operations (insert, update, delete)
- **Flow<T>** for read operations (auto-observe for UI)
- **@Query with explicit SELECT** (no SELECT *)
- Always index by date/userId when filtering time-based data

Example:
```kotlin
@Dao
interface CheckInDao {
    @Insert
    suspend fun recordCheckIn(checkIn: CheckIn)

    @Query("SELECT * FROM check_ins WHERE date(timestamp_millis / 1000, 'unixepoch') = :date")
    fun getCheckInsForDay(date: LocalDate): Flow<List<CheckIn>>

    @Query("SELECT * FROM check_ins WHERE timestamp_millis >= :weekStartMs ORDER BY timestamp_millis DESC")
    fun getCheckInsThisWeek(weekStartMs: Long): Flow<List<CheckIn>>
}
```

### Migrations

Future migrations:
- Version 1 (current): Initial schema
- Version 2+: Deprecate fields, add columns—never drop
- Use `Migration` interface, test with `MigrationTestHelper`

---

## Permissions Policy

### ✅ ALLOWED

- **`PACKAGE_USAGE_STATS`** — Digital Wellbeing, official Android API
- **`POST_NOTIFICATIONS`** — Daily check-in reminders

### 🚫 FORBIDDEN (Hard Constraints)

- **`ACCESSIBILITY_SERVICE`** — Violates Play Store policy; reserved for a11y apps
- **`SYSTEM_ALERT_WINDOW`** (overlays) — Forbidden; violates app interference policy
- **`QUERY_ALL_PACKAGES`** — Restricted; use explicit package lists instead
- **`ACCESS_BACKGROUND_LOCATION`** — Not needed; violates privacy

### ⚠️ Permission Flows

1. **PACKAGE_USAGE_STATS Flow (on first launch):**
   - Show consent screen explaining why (wellbeing dashboard)
   - Deep-link to Settings (user does the grant, not forced)
   - Graceful fallback if user denies (show education screens instead)

2. **POST_NOTIFICATIONS (on first reminder setup):**
   - Request via `requestPermission()` callback
   - Respect user's deny (disable notifications, don't nag)

---

## State Management

### ViewModel Pattern

Use Hilt-injected `ViewModel` with `UiState` sealed class:

```kotlin
sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success(val data: DashboardData) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val usageRepository: UsageStatsRepository,
    private val goalRepository: GoalRepository,
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            try {
                val usage = usageRepository.getAppsForToday()
                val goals = goalRepository.getActiveGoals()
                _uiState.value = DashboardUiState.Success(
                    DashboardData(usage = usage, goals = goals)
                )
            } catch (e: Exception) {
                _uiState.value = DashboardUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
```

### Compose State Collection

```kotlin
@Composable
fun HomeScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        is DashboardUiState.Loading -> LoadingScreen()
        is DashboardUiState.Success -> DashboardContent((uiState as Success).data)
        is DashboardUiState.Error -> ErrorScreen((uiState as Error).message)
    }
}
```

---

## Background Tasks (WorkManager)

### SyncUsageWorker (Every 30 min)

```kotlin
@HiltWorker
class SyncUsageWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val usageRepository: UsageStatsRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            usageRepository.syncWithDeviceStats()
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}
```

Schedule in `Application.onCreate()`:
```kotlin
PeriodicWorkRequestBuilder<SyncUsageWorker>(30, TimeUnit.MINUTES)
    .build()
    .also { WorkManager.getInstance(this).enqueueUniquePeriodicWork(
        "sync_usage",
        ExistingPeriodicWorkPolicy.KEEP,
        it
    )}
```

### NotificationWorker (Daily reminder)

Scheduled via `OneTimeWorkRequest` with initial delay to user's chosen time.

---

## Testing Strategy

### Unit Tests
- **Data layer:** Room migrations, DAO queries (use `RoomTestDatabase`)
- **Domain:** `CalculateCostOfAttentionTest`, `StreakLogicTest`
- **ViewModel:** Mock repositories, test state emission

### Instrumented Tests
- **Permission flows:** Verify consent screen → Settings deep-link
- **End-to-end:** Open app → check-in → verify streak increment
- **Database persistence:** Kill app → restart → data persists

### Coverage Target
- Minimum 60% on `domain/` and `data/` layers
- All public APIs tested
- Edge cases: empty lists, null safety, dates at boundaries

---

## Play Store Compliance Checklist

- [ ] **Manifest:** Only `PACKAGE_USAGE_STATS` + `POST_NOTIFICATIONS` declared
- [ ] **Permissions:** No Accessibility Service, no overlays, no VPN
- [ ] **Consent Flow:** In-app dialog before redirecting to Settings
- [ ] **Privacy Policy:** LGPD-compliant, hosted at public URL
- [ ] **Descriptions:** Explain "digital wellbeing" purpose clearly
- [ ] **Category:** Set to "Health & Fitness" or "Lifestyle"
- [ ] **Ratings:** Age rating: 3+ (no ads, no harmful content)
- [ ] **Review Video:** 30s demo of dashboard + check-in flow
- [ ] **Minification:** ProGuard enabled; test release build locally

---

## Git & Release Workflow

### Branch Naming
- Feature: `feature/F1.1-dashboard` or `feature/F1.2-checkin`
- Bugfix: `fix/issue-123`
- Always branch from `main` (or current release branch)

### Commits
- **Atomic commits:** One logical change per commit
- **Message format:** `[Phase][Feature] Short description`
  - Example: `[Phase 1.0] Add Room database setup and migrations`
- **Do not squash** initial phase commits; preserve history

### Pull Request
- **Description:** What was built, why, testing steps
- **Title:** `[Phase 1.x] Feature name`
- **Review:** Self-review for architecture, tests, lint
- **Merge:** Squash-merge to `main` only if approved

### Release
- Tag: `v1.0.0-mvp` (semantic versioning)
- Changelog: List features by phase
- APK signing: Keystore stored securely (GitHub Secrets)

---

## Debugging Tips

### Accessing Device Logs
```bash
adb logcat | grep "antidoto"
```

### Room Database Inspection
```bash
adb shell
run-as com.antidoto
sqlite3 /data/data/com.antidoto/databases/app.db
.tables
SELECT * FROM app_usage_entries LIMIT 10;
```

### WorkManager Status
```kotlin
WorkManager.getInstance(context).getWorkInfosForTag("sync_usage").observe(this) {
    Log.d("WorkManager", "Work state: ${it.state}")
}
```

### Profiling (CPU, Memory)
- Android Studio Profiler: Run → Profile
- Focus on `SyncUsageWorker` and Room queries under load

---

## Future Considerations (Beyond MVP)

- **Phase 2:** Atalho de Intenção (partial app shortcut with 10s breathing prompt)
- **Phase 3:** LGPD Data Importer (Instagram/TikTok/YouTube export parser)
- **Phase 4:** Freemium monetization, B2B features, iOS
- **Backend (optional):** Sync encrypted data to cloud (opt-in, phase 4+)

---

## Resources & References

- [Android Usage Stats API](https://developer.android.com/reference/android/app/usage/UsageStatsManager)
- [Jetpack Compose Best Practices](https://developer.android.com/jetpack/compose)
- [Room Persistence Library](https://developer.android.com/training/data-storage/room)
- [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
- [Hilt Dependency Injection](https://developer.android.com/training/dependency-injection/hilt-android)
- [Play Store Policy: Wellbeing & Controls](https://play.google.com/about/privacy-security-deception/user-security/permissions/)

---

## Contact & Contributions

**Maintainer:** Weiden (Kairos Tecnologia)  
**Feedback:** [Add GitHub issues link once public]

For questions on architecture, refactoring, or new features, consult this document first.
