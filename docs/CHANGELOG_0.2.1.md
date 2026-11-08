# Noor-A-Zakat 0.2.1 — review revision

This revision responds to the owner's review of the 0.1 starter and comparison with the older v2.2 source project.

## Main changes

- Replaced the single scholar-confirmed-total field with a structured worksheet for gold, jewellery stones/purity, silver, cash, bank balances, saved committee contributions, trade stock, raw materials, resale property, debts and receivables.
- Restored the richer asset-entry workflow as a product/UX reference from v2.2, but did not reuse its automatic debt shortcuts.
- Added a source-aware bounded Zakat decision engine: gold-only, silver-only, currency/trade-only and individually-below-nisab mixed cases; complex debt, receivable, committee and above-category-nisab cases are explicitly sent for Mufti review.
- Added the published khums example tests for 8, 9, 10 and 10.5 Shar'i tola; added a choice between the supplied 612.36 g silver measure and a published approximate 612.41 g measure.
- Added a Fitrah food-weight/value worksheet using the cited quantities; it does not infer personal obligation or payer responsibility without the relevant circumstances.
- Added international gold/silver spot estimates as reference-only values and a manual local-rate confirmation path; no claim that an international spot quote is a local jeweller's rate.
- Added curated, localized Islamic event feed with source links, schema validation, offline bundled fallback and periodic best-effort refresh. Dates remain subject to local moon sighting; the app does not scrape or invent religious occasions.
- Improved expandable Home Screen event card and resizable Hijri widget with automatic, emerald/gold, night, Ramadan and Eid display styles. Tapping toggles detail content; the launcher controls the widget's physical grid size, so users resize it using the launcher where supported.
- Added opt-in location-based prayer time calculation, Karachi/MWL/Umm al-Qura methods, Hanafi Asr, scheduled refresh after explicit permission, plus local daily prayer checklist. Prayer times are not represented as a mosque's jamaat timetable.
- Improved language and RTL coverage for Hindi, Urdu, English and Arabic; removed duplicate widget entry point and updated Hindi/Urdu/Arabic widget hints.

## Verification at this revision

- 26 pure Kotlin test methods passed using the local JUnit-compatible runner.
- 21 Islamic event entries validated for IDs, dates, themes, source links and all four locales.
- Android manifest and resource XML parsed successfully.
- Full Gradle Android build, Lint, APK and AAB have not been produced/verified in the workspace because Android SDK is not installed and the Gradle distribution host was not reachable.
- This revision is not a scholar-approved or Play Store-ready release. See `BUILD_STATUS.md` and `SCHOLAR_REVIEW_CHECKLIST.md`.
