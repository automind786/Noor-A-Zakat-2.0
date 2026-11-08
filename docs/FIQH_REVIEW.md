# Zakat engine — Shariah audit notes (release gate)

## Chosen source baseline

This version uses the user's requested Sunni Hanafi-oriented source baseline. The official Dawat-e-Islami Al-Madina Library page for *Faizan-e-Zakat*, page 13, states:

- Gold nisab: 20 mithqal / 7.5 tola; silver nisab: 200 dirham / 52.5 tola.
- Standard Zakat share: one-fortieth (2.5%).
- It explains khums increments above nisab: 8 tola gold → 7.5 tola is counted; 9 tola → 9 tola; 10 tola → 9 tola; 10.5 tola → 10.5 tola.
- Its jeweller conversion footnote gives approximately 87.48 g gold and 612.41 g silver.
- Page 14 explains combining different wealth categories when each is below its individual nisab and discusses which valuation is more beneficial to the poor.
- Page 15 distinguishes payment in cash from payment in physical gold/silver. For cash it describes 2.5% of the year-end value as a simple complete method; for metal payment it describes one-fortieth of the metal quantity and notes that using the full quantity when slightly above nisab can include a nafl excess.

Source URLs:
- https://www.dawateislami.net/bookslibrary/ur/faizan-e-zakat/page-13
- https://www.dawateislami.net/bookslibrary/ur/faizan-e-zakat/page-14
- https://www.dawateislami.net/bookslibrary/ur/faizan-e-zakat/page-15

The app therefore defaults to the source's 612.41 g approximate silver measure while retaining the owner's alternate 612.36 g parameter as an explicit selectable basis. The user must be able to see which measure was used.

## What the calculator may report

- The pure-gold arithmetic path and the silver-only arithmetic path are source-parameterized and tested. They are still labelled indicative pending written scholarly approval.
- Currency/trade-only and narrowly defined mixed cases are computed using the currently encoded documented decision branches. These also remain indicative and must be signed off by the reviewing Mufti.
- `FiqhZakatEngine.khumsEligibleBase` counts only full one-fifth surplus increments, not partial increments. The gold examples are directly shown in the source. Application to silver-only, currency-only and combined valuation paths must be explicitly checked against the complete source editions by the reviewing Mufti.
- The current code returns a khums-based indicative amount for the encoded exact-minimum branches. Page 15 also gives a simpler full-value 2.5% cash-payment method whose excess may be nafl. The app must explain this distinction correctly; the Mufti must confirm that the minimum/optional-extra presentation and every affected category are valid before publication.
- A solar-year rate is an optional project-research arithmetic convention, not a default. The lunar 354-day / 2.5% basis remains the default.

## Cases deliberately not auto-decided

Debt eligibility/deduction, instalment schedules, strong/medium/weak receivables, committee/BC contracts, assets whose trading intention or ownership is unclear, mixed categories where one category already reaches its own nisab, special-purpose property, interest income and unusual ownership are routed to Mufti review or educational guidance. The app must not replace that review with a universal `assets - debts × 2.5%` shortcut.

Fitrah quantity/value is a worksheet only; it does not decide the obligation date, who pays for whom, dependant status or final legal amount.

## Mandatory scholarly approval before public religious use

A qualified Sunni Hanafi Mufti / Dar-ul-Ifta must review the complete source editions, passages, all decision branches, unit conversions, edge cases, displayed wording, test cases and every release change. Record the scholar, institution, date, signed approval, reference edition/volume/page/mas'ala, and the code/test version. Do not mark a review complete from an online citation alone.

This file records source research and software policy. It is **not** a fatwa and does **not** certify that this app's rules are already approved.
