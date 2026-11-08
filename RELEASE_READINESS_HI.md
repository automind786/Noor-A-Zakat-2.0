# Public-release readiness checklist — Noor Zakat Amanah 1.0.0

**Current state: source package prepared; not yet a verified Play release.**

## Non-negotiable Shariah sign-off

- [ ] Qualified Sunni Hanafi Mufti reviews every executable Zakat and Fitrah rule and approves the exact user-facing wording.
- [ ] Confirm adopted gold/silver weight conventions and local source editions.
- [ ] Verify the 612.41 g default and 612.36 g alternate for the intended community.
- [ ] Review debts, receivables, BC/committee, jewellery, trading intention, property, ownership, Hawl and Fitrah edge cases.
- [ ] Obtain a dated written approval and preserve the exact reviewed source/test version.

## Software and device verification

- [ ] Run `./gradlew clean testDebugUnitTest lintDebug assembleDebug` in Android Studio / CI with Android SDK Platform 36 and JDK 17 or newer as required by the Gradle toolchain.
- [ ] Current AGP 9.1.1 setup opts out of built-in Kotlin and the new DSL to keep external Kotlin/Compose compiler plugins aligned at 2.4.21. Before moving to AGP 10, migrate to built-in Kotlin and remove those temporary opt-out properties.
- [ ] Run all tests on CI; fix every compile, lint or test failure.
- [ ] Install on physical devices at minSdk 26 and current Android versions; test small screens, large fonts, Hindi, Urdu/Arabic RTL, keyboard behavior and accessibility.
- [ ] Verify calculator draft save/restore, Android Keystore failure handling, app-data clearing and device restart.
- [ ] Verify prayer calculations against the chosen local mosque timetable for each supported calculation method and timezone. Confirm that Hanafi Asr is selected. Prayer API output is not a jamaat timetable.
- [ ] Test offline use, permission denial, location removal, event-feed validation, widget pinning, widget resizing, reboot and battery-restricted refresh.
- [ ] Test lock-screen widget only on devices that explicitly expose third-party lock-screen widgets. It is optional OS/launcher behavior, not a guaranteed feature.
- [ ] Perform security, privacy, accessibility, localization and regression testing.

## Google Play publication

- [ ] Confirm application ID, app title, icon, screenshots, feature graphic, store listing and content rating.
- [ ] Publish a stable privacy-policy URL and add a monitored privacy contact email.
- [ ] Complete Play Console Data safety, permission declarations, target audience, ads declarations (no ad SDK is included in this source) and any applicable financial/religious-content declarations.
- [ ] Google Play's requirement starting 31 August 2026 is Android 16 / API 36 or higher for new apps and updates; this project sets targetSdk 36. Re-check Play Console before submission.
- [ ] Configure the owner's release keystore outside the source tree; do not include passwords/keys in the ZIP or Git repository.
- [ ] Build and sign a release AAB; run Play's pre-launch report and staged internal testing before production rollout.
- [ ] Add a software license and verify third-party asset/data licenses.

## Remaining owner inputs

The owner must provide the public privacy contact email and final privacy-policy URL, review the chosen application ID, obtain Mufti sign-off and configure signing credentials in their private build environment. Without these, the project is not ready for official Play publication.
