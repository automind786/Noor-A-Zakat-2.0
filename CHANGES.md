# Noor-A-Zakat 4.0.1

Fixes
- **App-start crash fixed**: language switching (and the first start in Hinglish/English) threw
  `Cannot read properties of undefined` because Hinglish/English had no tab labels. Because of that the rest of the start-up
  code never ran (no first calculation, no automatic live-rate refresh). Fixed, and a 6th "calendar" label that shifted the
  tab names in Urdu/Hindi/Arabic was removed.
- **English is now real English**: tab names, bill/report headings, ledger, status messages from the Zakat engine,
  rates line, toasts, confirm/reset texts, jewellery messages, dalil (evidence) sheets, Masarif list and Books list.
  Everything re-renders immediately when the language is changed.
- **"Parcha Suniye / Read report" (read-aloud) removed completely** (button, texts, leftovers). The button used to call a
  function that no longer existed.
- **Live rates**: requests run in parallel with no-cache headers, a second source is used if the first one fails,
  absurd values are rejected, new rates always replace old ones, a result for a currency that was changed meanwhile is
  re-requested instead of being dropped, button can no longer get stuck, rates older than 15 minutes refresh on app start
  and when the app comes back to the foreground, and the rates line now shows per 10g, per tola and per gram.
- The bill's "Zakat due" line now shows the real rate (2.5% / 2.5775%) instead of always 2.5%.

Build
- GitHub Actions: `chmod +x gradlew`, version read from `app/build.gradle.kts`, new UI smoke test, reports uploaded on failure.
- Version: 4.0.1 (versionCode 9).
