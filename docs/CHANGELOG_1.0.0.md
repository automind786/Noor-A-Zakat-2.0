# Noor Zakat Amanah 1.0.0 — integrated release-candidate source

## Built from the two supplied projects

- Retained the native Jetpack Compose app from the newer v0.3.2 project instead of the old WebView shell.
- Retained the structured Fiqh Zakat Engine and detailed gold/jewellery, asset, debt, Fitrah, Hawl, prayer, calendar, event and reference components.
- Used the v2.3.0 app's richer UX direction and animated / themed presentation as design inspiration; did not reuse its automatic debt priority subtraction or unconditional mixed-net 2.5% rules.

## New integrations

- New brand: Noor Zakat Amanah; version 1.0.0, versionCode 100; API 36 compile/target configuration.
- New emerald-and-gold high-resolution launcher icon, adaptive icon foreground, density-specific legacy icons and 512px store icon.
- Home dashboard gives the Zakat worksheet the primary visual/action position.
- AES-256-GCM + Android Keystore encrypted Zakat draft autosave; no plaintext fallback; an unreadable existing encrypted draft is not silently overwritten.
- Prayer management gains a seven-day self-recorded Salah summary; prayer checklist values remain local.
- Hijri widget declares `home_screen|keyguard`; actual lock-screen availability remains platform/launcher-dependent.
- Silver nisab default changed to the source's published approximate 612.41 g; 612.36 g remains selectable and visible.
- Added extra engine regression cases for default nisab basis, pure-gold rates, missing rates, mixed category review gates, khums boundary arithmetic and negative input rejection.
- Added explicit Shariah audit, two-project comparison and public-release readiness documents.
- Added local calendar events with add/delete; no unreliable alarm-notification promise is made.

## Verification status in the authoring workspace

- Project archives inspected and differences documented.
- Kotlin core regression tests are prepared; local execution is recorded in `docs/BUILD_STATUS.md`.
- Android Gradle build could not be run in the authoring workspace because the Android SDK was not installed and `services.gradle.org` was not DNS reachable. This is an explicit outstanding verification item, not a successful build claim.
- No signed APK or AAB is included.

## Publication blockers

Qualified Sunni Hanafi Mufti written sign-off, a successful full Android CI build, physical-device / RTL / widget testing, a public privacy-policy URL and contact address, owner-controlled release signing, Play Console Data safety and pre-launch verification remain mandatory.
