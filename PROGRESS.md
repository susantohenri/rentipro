# Rentipro — Progress Tracker

> Dokumen ini melacak kemajuan implementasi MVP Rentipro.
> Gunakan untuk melanjutkan pekerjaan dengan AI lain jika sesi terputus.

**Package ID:** `com.henrisusanto.rentipro`  
**Arsitektur:** Kotlin + Compose + Material 3 + Room + Manual DI + Navigation Compose  
**minSdk:** 26 | **targetSdk:** 35

---

## Status Keseluruhan

| Step | Deskripsi | Status |
|------|-----------|--------|
| 1 | Project setup & dependencies | ✅ Done |
| 2 | Theme + localization | ✅ Done |
| 3 | Room schema + repositories | ✅ Done |
| 4 | Onboarding + seed data | ✅ Done |
| 5 | Home + unit state management | ✅ Done |
| 6 | Start rental | ✅ Done |
| 7 | Timer engine | ✅ Done |
| 8 | Background notification/alarm scheduling | ✅ Done |
| 9 | Return / delete | ✅ Done |
| 10 | Extend rental | ✅ Done |
| 11 | Pause / resume | ✅ Done |
| 12 | Items screen | ✅ Done |
| 13 | History | ✅ Done |
| 14 | Settings | ✅ Done |
| 15 | AdMob + remote config | ✅ Done |
| 16 | UMP | ✅ Done |
| 17 | Polish + edge cases | ✅ Done |

**Legenda:** ⬜ Pending | 🔄 In Progress | ✅ Done

---

## Keputusan Arsitektur (Sudah Disetujui)

- **DI:** Manual DI (bukan Hilt/Koin) — container di `core/di/AppContainer.kt`
- **State:** StateFlow + ViewModel
- **Navigation:** Jetpack Navigation Compose
- **Persistence:** Room
- **Alarm:** AlarmManager.setExactAndAllowWhileIdle (bukan WorkManager)
- **Bahasa:** EN + ID via string resources, deteksi OS + override manual
- **Tema:** Light/Dark via DataStore

---

## Struktur Package

```
com.henrisusanto.rentipro/
├── RentiproApplication.kt
├── MainActivity.kt
├── core/
│   ├── database/
│   ├── data/
│   ├── di/                # Manual DI — AppContainer.kt
│   ├── model/
│   ├── timer/
│   ├── alarm/
│   ├── notification/
│   ├── locale/
│   ├── theme/
│   ├── ads/
│   └── util/
├── navigation/
├── feature/
│   ├── onboarding/
│   ├── home/
│   ├── items/
│   ├── history/
│   ├── settings/
│   └── rental/
└── ui/
    ├── components/
    └── theme/
```

---

## Step 1 — Project Setup (Detail)

### Yang sudah dibuat
- [x] `settings.gradle.kts`
- [x] Root `build.gradle.kts`
- [x] `gradle/libs.versions.toml`
- [x] Gradle wrapper (`gradlew`, `gradle-wrapper.properties`)
- [x] `app/build.gradle.kts`
- [x] `AndroidManifest.xml` (permissions + AdMob sample app ID)
- [x] `RentiproApplication.kt`
- [x] `MainActivity.kt`
- [x] `core/di/AppContainer.kt` (manual DI skeleton)
- [x] `core/di/ViewModelFactory.kt`
- [x] `core/ads/` (skeleton: AdsConfigRepository, AdsManager, UmpConsentManager)
- [x] `core/model/Enums.kt` (UnitStatus, RentalStatus)
- [x] `ui/theme/` (Theme, Typography)
- [x] `navigation/` (Routes, BottomNavItem, RentiproNavHost)
- [x] `feature/` placeholder screens (home, items, history, settings)
- [x] `res/values/strings.xml` + `res/values-in/strings.xml` (minimal)
- [x] `.gitignore` lengkap
- [x] Build verified: `./gradlew assembleDebug` ✅

### Catatan Step 1
- Belum ada Room, ViewModel bisnis, atau fitur rental
- AdMob app ID sample sudah di manifest
- Remote config URL: `https://raw.githubusercontent.com/susantohenri/admob-remote-configs/main/rentipro/ads_config.json`

---

## Step 2 — Theme + Localization ✅

### Yang sudah dibuat
- [x] `core/model/AppLanguage.kt` — EN / ID
- [x] `core/model/ThemeMode.kt` — Light / Dark
- [x] `core/data/SettingsRepository.kt` — DataStore (language, theme, dueSoonMinutes default 5)
- [x] `core/locale/LocaleManager.kt` — deteksi OS + `AppCompatDelegate.setApplicationLocales`
- [x] `feature/settings/SettingsViewModel.kt`
- [x] `feature/settings/SettingsScreen.kt` — UI pilih bahasa & tema
- [x] `ui/components/SettingsChoiceRow.kt`
- [x] `MainActivity` — observe themeMode → `RentiproTheme(darkTheme)`
- [x] `RentiproApplication` — init default + apply locale saat startup
- [x] String resources EN (`values/`) + ID (`values-in/`)
- [x] `res/xml/locales_config.xml` — per-app language (Android 13+)
- [x] Dependency: `androidx.appcompat` untuk locale API
- [x] Build verified: `./gradlew assembleDebug` ✅

### Perilaku
- **First launch bahasa:** deteksi OS — `in`/`id` → Indonesia, selain itu → English
- **Override manual:** pilihan di Settings disimpan + `language_manual = true`
- **First launch tema:** ikuti system night mode, lalu persist
- **Tema:** Light/Dark via Settings, reactive di seluruh app

---

## Step 3 — Room Schema + Repositories ✅

### Entities (`core/database/entity/Entities.kt`)
- [x] `RentalUnitEntity` — id, name, status, createdAt, updatedAt
- [x] `RentalPresetEntity` — id, durationMinutes, price, sortOrder
- [x] `RentalEntity` — id, unitId, presetId?, durationMinutes, price, startedAt, scheduledEndAt, returnedAt?, status, isPaused, pausedAt, dueSoonNotified, overdueNotified
- [x] `RentalExtensionEntity` — id, rentalId, presetId?, addedDurationMinutes, addedPrice, extendedAt

### DAOs
- [x] `RentalUnitDao` — CRUD, observe by status, available units
- [x] `RentalPresetDao` — CRUD, ordered by sortOrder
- [x] `RentalDao` — active rentals, history, today stats, finalize
- [x] `RentalExtensionDao` — insert + observe by rental

### Relations
- [x] `ActiveRentalWithUnit` — rental + unit join
- [x] `HistoryRentalWithDetails` — rental + unit + extensions

### Repositories
- [x] `UnitRepository` — CRUD unit, status update
- [x] `PresetRepository` — CRUD preset
- [x] `RentalRepository` — start, extend, pause, resume, return, delete, sync status, today stats

### Utilitas pendukung
- [x] `RentalTimer` — remaining/overdue/due-soon (timestamp-based)
- [x] `TimeFormatter` — format countdown & overdue string
- [x] `Converters` — UnitStatus, RentalStatus enum
- [x] `RentiproDatabase` v1 — wired di `AppContainer`
- [x] Build verified: `./gradlew assembleDebug` ✅

### Catatan
- App settings (language, theme, dueSoon) tetap di DataStore (`SettingsRepository`), bukan Room
- Business logic rental (start/extend/pause/return) sudah di repository, UI di step berikutnya
- `dueSoonNotified` / `overdueNotified` flags siap untuk Step 8 (alarms)

---

## Step 4 — Onboarding + Seed Data ✅

### Yang sudah dibuat
- [x] `onboarding_completed` flag di `SettingsRepository` (DataStore)
- [x] `OnboardingViewModel` — 3 step wizard + validasi + resume jika terputus
- [x] `OnboardingScreen` — Welcome, Units, Presets
- [x] Seed unit: `#01`–`#05` (bisa rename sebelum lanjut)
- [x] Seed preset: `15→5`, `30→10`, `60→15` (CRUD sebelum finish)
- [x] Navigasi: belum onboarding → wizard, selesai → Home + bottom nav
- [x] String resources EN + ID untuk onboarding
- [x] Build verified: `./gradlew assembleDebug` ✅

### Alur onboarding
1. **Welcome** — "Rentipro" + subtitle + "Get Started"
2. **Units** — 5 unit default, rename, simpan ke Room saat Continue
3. **Presets** — 3 preset default, add/edit/delete, simpan + mark complete saat Finish

### Resume logic
- Units tersimpan tapi preset belum → lanjut ke step Presets
- Units + presets tersimpan tapi flag belum → auto-complete onboarding

---

## Step 5 — Home + Unit State Management ✅

### Yang sudah dibuat
- [x] `HomeViewModel` — combine rentals, units, dueSoon, today stats + tick 1 detik
- [x] `HomeUiState` + `HomeRentalItem` — kategorisasi Overdue / Due Soon / Rented
- [x] Sync unit status otomatis (`RENTED` / `OVERDUE`) via `RentalRepository.syncUnitStatusFromRental`
- [x] `HomeScreen` — summary, sections prioritas, available units, empty states
- [x] `HomeComponents` — summary card, rental card, section header, start button
- [x] Timer countdown real-time (timestamp-based via `RentalTimer` + `TimeFormatter`)
- [x] Tombol **Returned** di kartu overdue (aksi di Step 9)
- [x] CTA **+ Start Rental** (disabled jika tidak ada unit tersedia; flow di Step 6)
- [x] String resources EN + ID
- [x] Build verified: `./gradlew assembleDebug` ✅

---

## Step 6 — Start Rental ✅

### Yang sudah dibuat
- [x] `StartRentalUiState` + `StartRentalBottomSheet` — bottom sheet 2 langkah
- [x] Flow: pilih unit → pilih preset → start langsung (tanpa konfirmasi ekstra)
- [x] Skip pilih unit jika hanya 1 unit tersedia
- [x] `HomeViewModel.openStartRental()` + `startRentalWithPreset()` → `RentalRepository.startRental`
- [x] Validasi: no units / no presets → pesan error di sheet
- [x] Cegah double-start jika unit sudah punya rental aktif
- [x] Wire tombol **+ Start Rental** di Home
- [x] String resources EN + ID (`rental_preset_format`, dll.)
- [x] Build verified: `./gradlew assembleDebug` ✅

### Catatan
- Alarm scheduling untuk rental baru → Step 8
- Unit langsung jadi RENTED + muncul di Home (reactive Room)

---

## Step 7 — Timer Engine ✅

### Yang sudah dibuat
- [x] `RentalTimerSnapshot` + `RentalTimerPhase` — model timer terpusat
- [x] `RentalTimer.snapshot()` — satu API untuk remaining/overdue/pause/due-soon
- [x] `TimerTicker` — Flow tick 1 detik, selalu baca `System.currentTimeMillis()` (aman background/resume)
- [x] `RentalTimerText` — composable reusable untuk label timer
- [x] `HomeRentalMapper` — kategorisasi Home dari snapshot
- [x] Refactor `HomeViewModel` + `HomeComponents` pakai snapshot
- [x] `ItemsViewModel` + `ItemsScreen` — daftar unit dengan status + timer live
- [x] Unit tests: `RentalTimerTest` — countdown, overdue, pause, extend, due-soon, background
- [x] Build + tests verified ✅

### Edge cases terverifikasi (unit test)
- Countdown dari `scheduledEndAt - now`
- Overdue naik setelah `scheduledEndAt`
- Pause: remaining beku, bukan overdue
- Extend: `scheduledEndAt` diperpanjang → remaining benar
- Due soon: dalam ambang menit (default 5)
- Background: waktu dihitung ulang dari clock, bukan counter UI

---

## Step 9 — Return / Delete ✅

### Yang sudah dibuat
- [x] `ActiveRentalBottomSheet` — tap kartu rental aktif → sheet dengan aksi **Return** + **Delete**
- [x] Tombol **Returned** cepat di kartu overdue (langsung return tanpa sheet)
- [x] Delete pakai konfirmasi `AlertDialog` (title + pesan unit jadi tersedia)
- [x] `HomeViewModel` — `openRentalSheet` / `returnRental` / `requestDeleteRental` / `cancelDeleteRental` / `confirmDeleteRental` + guard double-tap (`isProcessing`)
- [x] `HomeUiState.activeRentals` — daftar gabungan overdue + due-soon + rented untuk lookup sheet
- [x] `RentalRepository.returnRental` / `deleteActiveRental` — guard `status != ACTIVE` (anti double-execute)
- [x] Return → rental `COMPLETED`, unit `AVAILABLE` · Delete → rental `DELETED`, unit `AVAILABLE` · keduanya cancel alarm (Step 8)
- [x] String resources EN + ID (`action_cancel`, `delete_rental_title`, `delete_rental_message`)
- [x] Build + tests verified: `./gradlew assembleDebug testDebugUnitTest` ✅

### Catatan Step 9
- Extend (Step 10) & Pause/Resume (Step 11) akan ditambahkan ke `ActiveRentalBottomSheet` yang sama
- Rental DELETED tetap muncul di History (status `DELETED` di-query oleh `observeHistory`) — sesuai desain awal

---

## Step 8 — Background Notification/Alarm ✅

### Yang sudah dibuat
- [x] `core/alarm/AlarmScheduler.kt` — interface + `AlarmSchedulerImpl` (AlarmManager)
- [x] Due-soon alarm: `scheduledEndAt - dueSoonMinutes` · overdue alarm: `scheduledEndAt`
- [x] `setExactAndAllowWhileIdle` + fallback `setAndAllowWhileIdle` jika `SCHEDULE_EXACT_ALARM` tidak di-grant (Android 12+)
- [x] `core/alarm/AlarmReceiver.kt` — terima alarm, cek status/flag, kirim notifikasi, set flag `dueSoonNotified` / `overdueNotified` (sekali per rental)
- [x] `core/alarm/AlarmReceiver` juga set unit status OVERDUE saat alarm overdue
- [x] `core/alarm/BootReceiver.kt` — reschedule semua alarm aktif saat `BOOT_COMPLETED` + `MY_PACKAGE_REPLACED`
- [x] `core/alarm/AlarmIds.kt` — request code / notification id per rental (pure + unit tested, anti-collision)
- [x] `core/notification/NotificationHelper.kt` — channel `rental_reminders` + notifikasi due-soon/overdue, tap → buka MainActivity
- [x] `res/drawable/ic_notification.xml` — small icon timer
- [x] String resources EN + ID untuk channel & notifikasi
- [x] `RentalRepository` — schedule/cancel di start, extend, pause, resume, return, delete + `rescheduleAllAlarms()`
- [x] `RentiproApplication` — create channel + reschedule saat startup
- [x] Manifest — receiver `AlarmReceiver` (exported=false), `BootReceiver` (exported=true, intent-filter boot + package replaced)
- [x] `MainActivity` — request `POST_NOTIFICATIONS` (Android 13+) saat masuk main app
- [x] Unit test: `AlarmIdsTest` (anti-collision request code)
- [x] Build + tests verified: `./gradlew assembleDebug testDebugUnitTest` ✅

### Catatan Step 8
- Paused rental: alarm di-cancel saat pause, di-schedule ulang saat resume (sesuai `scheduledEndAt` baru)
- Extend: flag notifikasi di-reset → kedua alarm di-arm ulang
- Due-soon untuk durasi ≤ threshold: alarm fire segera (AlarmManager jalankan trigger di masa lalu langsung)
- Jika `dueSoonMinutes` diubah di Settings (Step 14), alarm butuh reschedule — tambahkan saat Step 14
- `SCHEDULE_EXACT_ALARM` tidak diminta via UI; fallback ke inexact sudah cukup untuk MVP

---

## Step 10 — Extend Rental ✅

### Yang sudah dibuat
- [x] `ActiveRentalSheetUiState` — add `showExtendSelection` flag + `presets` list
- [x] `HomeViewModel.openExtendSelection()` — load presets, show extend mode
- [x] `HomeViewModel.cancelExtendSelection()` — close extend selection, reset state
- [x] `HomeViewModel.extendRentalWithPreset(presetId)` — fetch rental, call `extendRental`, close sheet
- [x] `ActiveRentalBottomSheet` — refactor dengan 2 mode: actions vs extend preset selection
- [x] `ActiveRentalActionsContent` — Return + **Extend** + Delete buttons
- [x] `ExtendPresetSelectionContent` — preset list dengan back button, tap untuk extend
- [x] Wire `onExtendRequest`, `onSelectPresetForExtend`, `onCancelExtend` di `HomeScreen`
- [x] String resources EN + ID (`rental_action_extend`, `rental_extend_select_preset`)
- [x] `RentalRepository.extendRental()` sudah ada dari Step 3 — insert extension record, update rental fields
- [x] Extend logic: update `durationMinutes`, `price`, `scheduledEndAt`; reset notification flags; reschedule alarms
- [x] Build verified: `./gradlew assembleDebug` ✅

### Perilaku Extend
1. Tap kartu rental aktif → sheet actions (Return + Extend + Delete)
2. Tap **Extend** → preset selection sheet
3. Tap preset → rental duration + price naik, scheduledEndAt diperpanjang
4. Notifikasi flags reset → alarm baru di-arm untuk due-soon dan overdue
5. Extension record tersimpan di `RentalExtensionEntity` untuk history/audit

### Catatan Step 10
- Flow extend mirip start rental: pilih preset dari bottom sheet
- Extend available untuk semua status aktif (overdue, due-soon, rented)
- Pause/Resume (Step 11) akan ikuti pola yang sama: add buttons + action flows

---

## Step 11 — Pause / Resume ✅

### Yang sudah dibuat
- [x] `ActiveRentalBottomSheet` — refactor signature untuk terima `onPauseRequest` + `onResumeRequest`
- [x] `ActiveRentalActionsContent` — add Pause/Resume button, show dinamis based on `item.isPaused`
  - Jika paused: tombol "Resume" → `onResumeRequest()`
  - Jika not paused: tombol "Pause" → `onPauseRequest()`
- [x] `HomeViewModel.pauseRental()` — fetch rental, call `rentalRepository.pauseRental`, close sheet
- [x] `HomeViewModel.resumeRental()` — fetch rental, call `rentalRepository.resumeRental`, close sheet
- [x] Wire callbacks di `HomeScreen` — `onPauseRequest = viewModel::pauseRental`, `onResumeRequest = viewModel::resumeRental`
- [x] String resources EN + ID (`rental_action_pause`, `rental_action_resume`)
- [x] `RentalRepository.pauseRental()` + `resumeRental()` sudah ada dari Step 8
  - Pause: cancel alarm, mark `isPaused = true`, record `pausedAt`
  - Resume: add pause duration ke `scheduledEndAt`, reschedule alarm
- [x] Build verified: `./gradlew assembleDebug` ✅

### Perilaku Pause/Resume
1. Tap kartu rental aktif → sheet actions
2. Tap **Pause** (jika not paused) → rental pause, timer frozen, alarm cancelled
3. Tap **Resume** (jika paused) → pause duration added to endTime, alarm rescheduled
4. Unit status tetap RENTED atau OVERDUE (tidak berubah saat pause)

### Catatan Step 11
- Pause tidak mempengaruhi due-soon/overdue status
- Pause hanya freeze timer, bukan reset
- Alarms sepenuhnya di-manage oleh repository (cancel on pause, schedule on resume)
- Items screen (Step 12) bisa menampilkan "Paused" indicator via `item.isPaused` 

---

## Step 12 — Items Screen ✅

### Yang sudah dibuat
- [x] `ItemsUiState` — add dialog state (rename, delete confirmation + form state)
- [x] `ItemsViewModel` — refactor uiState combine logic untuk merge dialog state
- [x] `openRenameDialog(unitId)` — load current name, show rename dialog
- [x] `dismissRenameDialog()` — close rename dialog, reset state
- [x] `updateRenameText(text)` — real-time text update di dialog
- [x] `confirmRename()` — fetch unit by ID, call `unitRepository.renameUnit()`, close dialog + guard double-tap
- [x] `openDeleteConfirm(unitId)` — show confirmation dialog dengan unit name
- [x] `cancelDeleteConfirm()` — dismiss delete dialog
- [x] `confirmDelete()` — call `unitRepository.deleteUnit(id)`, close dialog + guard double-tap
- [x] `UnitRepository.deleteUnit(id)` — add overload untuk accept ID (existing method accept RentalUnitEntity)
- [x] `ItemsScreen` — add dialogs (rename + delete confirmation)
- [x] `ItemRowCard` — clickable, tap → `openRenameDialog()`
- [x] `RenameUnitDialog` — AlertDialog dengan TextField, validate non-empty
- [x] `DeleteUnitConfirmDialog` — confirmation dialog dengan unit name
- [x] String resources EN + ID untuk dialogs (`items_rename_dialog_title`, `items_delete_dialog_*`, `action_confirm`)
- [x] Build verified: `./gradlew assembleDebug` ✅

### Fitur Items Screen
- **Display**: Prioritas sort (overdue → due-soon → paused → available), alphabetical by name
- **Status**: Available (primary), Rented (onSurface), Overdue (error color)
- **Timer display**: Rental countdown via `RentalTimerText` (if active)
- **Pause indicator**: "Paused" label if `item.isPaused`
- **Rename**: Tap kartu → dialog, edit name, confirm (reusable `renameUnit` method)
- **Delete**: Tap kartu → confirm dialog, deletes unit by ID
- **Reactive**: Unit list auto-update via `observeAllUnits()` Flow

### Catatan Step 12
- Rename/delete tidak affect active rentals (unit hanya direname/dihapus dari unit table)
- Active rentals tetap track unit via `unitId` foreign key
- Delete unit tidak delete rentals — orphaned rentals tetap valid (sesuai design)
- Dialog state merge dengan items flow untuk single reactive source

---

## Step 13 — History ✅

### Yang sudah dibuat
- [x] `HistoryViewModel` — flow dari `rentalRepository.observeHistory()`, map ke `HistoryUiState`
- [x] `HistoryUiState` — items list dari `HistoryRentalWithDetails`
- [x] `HistoryRentalWithDetails` model sudah ada (rental + unit + extensions list)
- [x] `HistoryScreen` — LazyColumn rental cards, sorted newest-first (via query)
- [x] `HistoryRentalCard` — display:
  - Unit name + status color (completed/deleted/active)
  - Start date (MMM d, HH:mm format)
  - Duration (minutes)
  - Total price
  - Extensions list (if any) dengan duration + price + date per extension
- [x] Status colors: completed (primary), deleted (onSurfaceVariant), active (onSurface)
- [x] `RentiproNavHost` — wire HistoryViewModel factory + pass to HistoryScreen
- [x] String resources EN + ID untuk semua labels + status labels
- [x] Build verified: `./gradlew assembleDebug` ✅

### Fitur History Screen
- **Display**: Rental history dengan detail lengkap (unit, dates, price)
- **Extensions**: Show history perpanjangan per rental (durasi + harga tambahan + tanggal)
- **Status Colors**: Completed (primary) vs Deleted (onSurfaceVariant) vs Active (onSurface)
- **Date Format**: MMM d, HH:mm (localized via Locale.getDefault)
- **Empty State**: "No rental history yet"
- **Reactive**: Auto-update via Flow dari Room query

### Catatan Step 13
- History query di `RentalDao.observeHistory()` sudah return sorted descending by startedAt (newest first)
- Extensions are embedded dalam `HistoryRentalWithDetails` via Room relation
- Delete/completed rentals tetap tampil di history (status-based filtering bisa di Step 17 polish)
- Rental dapat status ACTIVE tapi tidak ada active rental record (orphaned, misal unit dihapus)

---

## Step 14 — Settings ✅

### Yang sudah dibuat
- [x] `SettingsViewModel` — extended dengan dueSoonMinutes StateFlow
- [x] `SettingsViewModel.setDueSoonMinutes(minutes)` — calls SettingsRepository + reschedules alarms
- [x] `SettingsRepository.setDueSoonMinutes(minutes)` — persist ke DataStore
- [x] `RentalRepository.rescheduleAllAlarms()` — sudah ada, reads latest dueSoonMinutes + reschedules
- [x] `SettingsScreen` — added "Rental Settings" section dengan dueSoonMinutes TextField
- [x] UI Control: TextField dengan validation (1-120 minutes range)
- [x] String resources EN + ID untuk rental settings section
- [x] `RentiproNavHost` — updated SettingsViewModel factory to inject rentalRepository
- [x] Build verified: `./gradlew assembleDebug` ✅

### Fitur Settings - Rental Section
- **Display**: Current dueSoonMinutes value (default 5)
- **Input**: TextField dengan number keyboard
- **Validation**: Accept 1-120 minutes (guards against invalid input)
- **Action**: On valid input → setDueSoonMinutes() → reschedule all active rentals
- **Persistence**: Value saved di DataStore, reactive via SettingsRepository.dueSoonMinutes Flow
- **Alarm Reschedule**: When user changes value, RentalRepository.rescheduleAllAlarms() re-arms all alarms with new threshold

### String Resources (Step 14)
- English: `settings_rental`, `settings_due_soon_minutes`, `settings_due_soon_minutes_desc`, `settings_due_soon_minutes_label`
- Indonesian: Equivalent translations for all above

### Catatan Step 14
- TextField onValueChange calls setDueSoonMinutes hanya jika value valid (1-120)
- Invalid input (>120, <1, non-numeric) diabaikan UI-wise tapi state tetap update display
- rescheduleAllAlarms() idempotent — aman dipanggil berkali-kali
- Saat user ubah dueSoonMinutes, semua active rentals' alarms di-reschedule (re-arm dengan threshold baru)

---

## Acceptance Criteria Checklist (PRD §38)

Gunakan checklist ini saat testing akhir:

### Setup
- [x] Fresh install → onboarding
- [x] 5 unit default (#01–#05), bisa rename
- [x] 3 preset default (15→5, 30→10, 60→15), CRUD

### Rental
- [x] Start rental cepat (unit + preset)
- [x] Unit jadi RENTED, timer timestamp-based

### Due Soon
- [x] Default 5 menit, configurable (UI settings → Step 14)
- [x] Notifikasi sekali

### Overdue
- [x] Status OVERDUE, timer naik, harga tidak naik
- [x] Notifikasi sekali

### Return / Extend / Pause / Delete
- [x] Return: kartu overdue → Returned cepat + sheet Return (semua status aktif)
- [x] Delete: sheet → konfirmasi dialog
- [x] Extend: sheet → preset selection → duration + price naik
- [x] Pause/Resume: sheet → button berubah, timer frozen, alarm managed

### Items Screen
- [x] Display semua unit, sorted by priority + name
- [x] Status color: available, rented, overdue
- [x] Timer live untuk aktif rentals
- [x] Rename: tap kartu → dialog input
- [x] Delete: confirm dialog dengan unit name

### History Screen
- [x] Display completed/deleted rentals dengan full details
- [x] Extensions history per rental
- [x] Status color untuk completed/deleted
- [x] Date + time display

### Settings
- [x] Language: EN / ID switcher
- [x] Theme: Light / Dark switcher
- [x] Due-soon minutes: input field (default 5)

---

### Setup
- [x] Fresh install → onboarding
- [x] 5 unit default (#01–#05), bisa rename
- [x] 3 preset default (15→5, 30→10, 60→15), CRUD

### Rental
- [x] Start rental cepat (unit + preset)
- [x] Unit jadi RENTED, timer timestamp-based

### Due Soon
- [x] Default 5 menit, configurable (UI settings → Step 14)
- [x] Notifikasi sekali

### Overdue
- [x] Status OVERDUE, timer naik, harga tidak naik
- [x] Notifikasi sekali

### Return / Extend / Pause / Delete
- [x] Return: kartu overdue → Returned cepat + sheet Return (semua status aktif)
- [x] Delete: sheet → konfirmasi dialog
- [x] Extend: sheet → preset selection → duration + price naik
- [x] Pause/Resume: sheet → button berubah, timer frozen, alarm managed

### Items Screen
- [x] Display semua unit, sorted by priority + name
- [x] Status color: available, rented, overdue
- [x] Timer live untuk aktif rentals
- [x] Rename: tap kartu → dialog input
- [x] Delete: confirm dialog dengan unit name

### History / Settings
- [x] History: display rentals selesai + extension history
- [x] Settings: language, theme, due-soon minutes

---

## Perintah Build

```bash
cd /Users/macbook/AndroidStudioProjects/rentipro
./gradlew assembleDebug
```

---

## Catatan untuk AI Lanjutan

1. Baca PRD lengkap di chat history atau minta user paste ulang
2. Cek status Step di tabel atas sebelum mulai
3. **Jangan lanjut ke step berikutnya tanpa perintah eksplisit user** (kecuali user minta lanjut)
4. Ikuti prinsip: minimal scope, no over-engineering, no hardcoded UI strings
5. Manual DI: tambahkan dependency baru di `AppContainer.kt`, inject via constructor ViewModel factory atau parameter NavHost
6. Update file ini setiap step selesai

---

*Terakhir diupdate: Step 14 selesai — 2026-08-17*
