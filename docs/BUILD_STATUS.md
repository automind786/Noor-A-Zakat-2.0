# Build and verification status — Noor Zakat Amanah 1.0.0

## Environment checks performed

- Both user-provided ZIP source trees were extracted and compared.
- Source-level inspection identified a key safety issue in the older WebView engine: it automatically deducted debts in a single priority order and returned a generic mixed-wealth 2.5% total. That calculation shortcut was not carried into this native build.
- The current source uses the native Compose app and guarded FiqhZakatEngine from the newer project.
- Added encrypted local Zakat draft persistence, personal calendar events, seven-day Salah tracking, a 612.41 g published-source default, the page-15 full-value method explanation, widget keyguard category metadata, and regression test cases.
- All 37 pure-Kotlin project unit-test methods (calculation, Fitrah, weight units, parser, Hijri calendar and all four UI/reference localizations) passed through a local JUnit-compatible runner. This is not a substitute for the standard Gradle/JUnit task.
- All 16 Android XML resource/manifest files parsed successfully; the event-feed JSON parsed. `python3 tools/verify_source.py` completed 24 source-package checks with zero failures, including manifest/backup/location permissions, widget categories, API/version settings, launcher icons and absence of signing secrets/local machine files. The encrypted vault source also passed a type/syntax compile against minimal Android API stubs; the MainActivity source passed a Kotlin parser-only check, but Compose/Android type resolution still requires the full Android build. Android Keystore behavior itself requires device testing.

## Full Android build not completed here

The Android SDK is not installed in this workspace. Gradle 9.3.1 was not already cached, and the wrapper could not download it because `services.gradle.org` did not resolve (`UnknownHostException`). Therefore none of the following is claimed as passed here: Gradle Android compile, Compose compilation, lint, debug APK assembly, release AAB, emulator test or physical-device test.

Run in Android Studio or network-enabled CI:

```bash
./gradlew clean testDebugUnitTest lintDebug assembleDebug
```

Use Android SDK Platform 36 and JDK 17. The included AGP 9.1.1 + Gradle 9.3.1 pair is current; Kotlin and Compose Compiler use 2.4.21. AGP9 built-in Kotlin/new DSL are deliberately opted out in `gradle.properties` because the project retains the matched external Kotlin Android and Compose compiler plugins. Migrate to built-in Kotlin before AGP10. Resolve every CI failure before publication.

## Shariah release blockers

- No qualified Mufti has signed off the current codebase in this workspace.
- Complete `SCHOLAR_REVIEW_CHECKLIST.md` and `FIQH_REVIEW.md` with actual review evidence.
- Do not represent the calculator as a fatwa or a scholar-approved tool before written approval.

## Google Play release blockers

- Configure owner-controlled signing credentials outside the source archive.
- Confirm app identity / package ownership.
- Publish a privacy policy URL and add a monitored contact email.
- Complete Play Console Data safety and all applicable declarations.
- Test on physical devices, including RTL, font scaling, offline mode, reboot, location denial, widget resizing and lock-screen behavior where supported.
- Build and test a signed AAB; run internal testing and Play pre-launch report.

This source package is a release-candidate foundation, not a certified zero-defect build or ready-to-submit signed app bundle.
