# Noor-A-Zakat 4.0.2

Fixes
- **Bottom menu (dock) no longer moves while scrolling.** Cause: the top header `<div>` was never closed, so the whole app,
  the dock, the popups and the toast were inside the sticky header. That made the "fixed" dock move with the page. Fixed; a
  build-time check now fails if this ever happens again. The dock also had 6 grid columns for 5 buttons (fixed) and is hidden
  while the keyboard is open.
- **Live rates with location**: tapping "Live rates" asks once for *approximate* location. The country is used to suggest the
  matching currency (confirmation dialog, asked only once per suggestion); if permission is denied, the current currency is used.
  Rates always refresh (parallel requests, no-cache, fallback source, stale-answer handling).
- Removed the unused notification permission; `lang` attribute now uses real language codes.
- Privacy policy updated for the location permission.

Earlier (4.0.1)
- App-start/language crash fixed, real English, "Parcha Suniye" removed, live-rate robustness.

Build
- Version 4.0.2 (versionCode 10). CI runs: web validation (DOM structure, handlers, languages), UI smoke test, unit tests, APK build.
