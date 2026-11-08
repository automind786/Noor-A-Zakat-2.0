# Noor Zakat Amanah — Android 1.0.0

**A native Android app with a Shariah-sensitive Zakat worksheet at the center.** Built for Hindi-first use with Hindi, Urdu, English and Arabic localizations.

## Highlights

- Guided Zakat worksheet: multi-item gold jewellery (up to 12 rows), separate silver, cash/bank, trade stock, raw material, resale property, ownership percentage, separate debt and receivable ledgers, and clearly labelled excluded/review categories.
- Distinguishes 10 g market tola from 11.664 g Shar'i tola; deducts non-metal weight before applying carat purity; accepts valid Indian and Western number grouping and rejects malformed grouping.
- Source-parameterized nisab and khums engine with regression tests. Silver nisab defaults to the Faizan-e-Zakat jeweller approximation 612.41 g; the 612.36 g project-research value remains selectable.
- Complex debt, receivable, committee/BC, and above-category-nisab mixed cases are sent to Mufti review rather than forced through a generic net-assets formula.
- Encrypted Zakat draft autosave on-device using AES-256-GCM and Android Keystore. Financial worksheet data is not sent to the prayer, rates or event services.
- Prayer times with Karachi / MWL / Umm al-Qura methods and Hanafi Asr; explicit location consent; local Salah checklist and seven-day self-recorded summary.
- Hijri calendar with local date adjustment, curated cited event feed, add/delete personal calendar events and offline fallback, Fitrah worksheet, Hawl helper, asset guide, recipient/Tamleek guide, references and Tasbih.
- Home-screen Hijri widget; optional lock-screen widget category is declared for Android implementations/launchers that support it. Lock-screen availability is not guaranteed.
- Animated opening screen, emerald/gold design, new high-resolution launcher icon and RTL layouts.

## Shariah status — important

This app is a calculation-support tool, **not a fatwa**. The cited *Faizan-e-Zakat* page gives the 7.5 tola gold and 52.5 tola silver nisab, the 2.5% baseline and khums examples. Those source statements are not the same as independent approval of every code path. Every executable rule, asset classification, debt case, Fitrah detail and on-screen explanation must receive written approval from a qualified Sunni Hanafi Mufti / Dar-ul-Ifta before the app is publicly promoted as a religious calculator. See [`docs/FIQH_REVIEW.md`](docs/FIQH_REVIEW.md), [`docs/SCHOLAR_REVIEW_CHECKLIST.md`](docs/SCHOLAR_REVIEW_CHECKLIST.md) and [`PROJECT_COMPARISON_HI.md`](PROJECT_COMPARISON_HI.md).

The calculator deliberately returns **Mufti review required** for cases where the source and personal facts are not encoded with sufficient precision. Do not convert it into a one-line `assets - all debts × 2.5%` formula.

## Build

Quick source-package check (does not replace Android build/lint):

```bash
python3 tools/verify_source.py
```

Requirements: JDK 17+, Android SDK Platform 36, Internet access for Gradle dependencies. The project uses AGP 9.1.1 + Gradle 9.3.1 + Kotlin/Compose Compiler 2.4.21. `gradle.properties` opts out of AGP 9 built-in Kotlin/new DSL temporarily because this project uses the matching external Kotlin and Compose plugins; migrate to built-in Kotlin before AGP 10.

```bash
./gradlew clean testDebugUnitTest lintDebug assembleDebug
```

Debug APK output: `app/build/outputs/apk/debug/app-debug.apk`

Release bundle (configure signing privately first):

```bash
./gradlew bundleRelease
```

Do not commit `keystore.properties`, keystore files, passwords, tokens or `local.properties`. No release signing credentials are included in this ZIP.

## Publish checklist

Read [`RELEASE_READINESS_HI.md`](RELEASE_READINESS_HI.md) before release. The source package is not a signed Play Store artifact and is not yet verified on physical devices. It still requires a successful Android CI build, Mufti sign-off, privacy-policy URL/contact, Play Console declarations, signing, store assets and device testing. As of 31 August 2026, new Google Play apps and updates must target Android 16 / API 36 or higher; this project sets targetSdk 36. Recheck requirements in Play Console immediately before submission.

## Data and privacy

See [`PRIVACY_POLICY.md`](PRIVACY_POLICY.md). Zakat draft entries are encrypted on device; prayer location is requested only after user action and is sent to AlAdhan for prayer calculations. Prayer data is calculation output, not a mosque jamaat timetable. Gold/silver internet rates are reference-only spot estimates, not local jeweller quotations.

## Source structure

- `app/src/main/java/.../core/` — calculation models, Zakat engine, Hijri calendar, localization.
- `app/src/main/java/.../data/` — prayer-time adapter, rates, curated event feed, references and encrypted Zakat draft storage.
- `app/src/main/java/.../widget/` — Android App Widget provider.
- `app/src/test/` — calculation, conversion, date and localization regression tests.
- `.github/workflows/android.yml` — CI test/lint/debug build workflow.

## Identity

- Display name: Noor Zakat Amanah
- Application ID: `com.noorazakat.app` (kept stable from the supplied native project for update compatibility; verify ownership before Play publication)
- Version: `1.0.0` / versionCode `100`
- minSdk 26 · targetSdk 36 · compileSdk 36
