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
| 8 | Background notification/alarm scheduling | ⬜ Pending |
| 9 | Return / delete | ⬜ Pending |
| 10 | Extend rental | ⬜ Pending |
| 11 | Pause / resume | ⬜ Pending |
| 12 | Items screen | ⬜ Pending |
| 13 | History | ⬜ Pending |
| 14 | Settings | ⬜ Pending |
| 15 | AdMob + remote config | ⬜ Pending |
| 16 | UMP | ⬜ Pending |
| 17 | Polish + edge cases | ⬜ Pending |

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

## Step 8 — Background Notification/Alarm (Belum Dimulai)

### Tugas
- [ ] `AlarmScheduler` + `setExactAndAllowWhileIdle`
- [ ] Due-soon + overdue notifications (sekali per rental)
- [ ] `BOOT_COMPLETED` reschedule
- [ ] Permission handling (POST_NOTIFICATIONS, SCHEDULE_EXACT_ALARM)

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
- [ ] Default 5 menit, configurable
- [ ] Notifikasi sekali

### Overdue
- [ ] Status OVERDUE, timer naik, harga tidak naik
- [ ] Notifikasi sekali

### Return / Extend / Pause / Delete
- [ ] Semua workflow sesuai PRD

### Items / History / Settings
- [ ] CRUD unit, history, settings lengkap

### Ads / UMP / Localization / Offline
- [ ] Sesuai PRD §24–27, §4, §3

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

*Terakhir diupdate: Step 7 selesai — 2026-08-16*
