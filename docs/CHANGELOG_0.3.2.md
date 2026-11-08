# Noor-A-Zakat 0.3.2 — Zakat input restoration and validation

## Implemented

1. **Multi-item gold jewellery** — the calculator now supports up to 12 separate jewellery rows. Each row has an optional name, gross weight, stone/non-metal weight and carat. Items can be added or removed independently.
2. **Correctly distinguished tola units** — every jewellery row can use grams, market tola (10.0 g) or Shar'i tola (11.664 g). Conversion happens before stones are deducted and before carat purity is applied.
3. **Combined jewellery result** — all valid rows are combined into total 24K-equivalent gold grams and an indicative value using the entered 24K per-gram rate.
4. **Safer number parsing** — valid Indian and western thousands grouping are accepted. Ambiguous or malformed comma grouping is rejected; the parser no longer strips every comma and silently changes a number such as `1,25` into `125`.
5. **Itemised asset ledger** — up to 30 asset entries can be named and classified individually. Each line captures full value and ownership percentage, then calculates only the user's share. Categories cover cash, bank, stock, raw material, resale property, saved committee, receivables (strong/medium/weak), personal-use exclusions, rental-property principal, agricultural land, bank interest and an uncertain/other review route.
6. **Debt ledger** — up to 20 separate obligations can be named, classified (demandable, instalment/EMI, committee balance, mahr/other) and totalled for review. No debt is automatically deducted.
7. **Four-language UI coverage** — added Hindi, Urdu, English and Arabic labels for jewellery, asset/debt ledgers and weight units.
8. **Regression tests** — added tests for Indian/western grouping, invalid grouping, market/Shar'i tola conversion, gold calculation after unit conversion, and asset ownership-share arithmetic.

## Existing fiqh safeguards preserved

- No universal "subtract all debt" shortcut has been introduced.
- Cases with entered debt, receivables, committee savings, or mixed categories that need case-specific review continue to return a Mufti-review status.
- The supplied 612.36 g silver measure and the separately documented approximately 612.41 g measure remain distinct selectable source values.
- Internet gold/silver prices remain reference estimates, not local jeweller quotations.

## Remaining work (not claimed complete)

- Add more detailed receivable maturity/collection-state fields and preserve those details in a printable result sheet.
- Verify the UI on Android devices and RTL layouts, and run the complete Gradle/Android build in GitHub Actions. This build environment has no Android SDK and could not resolve the Gradle distribution host, so no new APK is claimed here.
- Obtain the signed Sunni Hanafi Mufti review required by `docs/SCHOLAR_REVIEW_CHECKLIST.md`.
