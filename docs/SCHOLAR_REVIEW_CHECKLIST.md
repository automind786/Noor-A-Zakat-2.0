# Mufti review checklist — Noor Zakat Amanah 1.0.0

Do not mark complete without dated written approval from a qualified Sunni Hanafi Mufti / Dar-ul-Ifta. The 612.41 g Faizan-e-Zakat jeweller conversion is the current default; 612.36 g remains a selectable alternate.

This checklist is intentionally separate from UI text. A UI label or online citation is not scholarly approval. The current engine returns an *indicative calculation* only for limited arithmetic branches; it is not cleared for independent religious decisions.

| Rule group | Required evidence before release | Current app behavior |
|---|---|---|
| Gold-only nisab and khums surplus examples | Verify original Fatawa Razawiyyah edition/vol. 10/page 85 and every threshold/example in Faizan-e-Zakat | Implements the cited examples for 8, 9, 10 and 10.5 Shar'i tola as indicative, pending sign-off |
| Silver-only nisab and exact conversion | Confirm the owner's 612.36 g measure against the published approximate 612.41 g measure and select the adopted local standard | User can select the research parameter or published approximation; result remains indicative |
| Cash/trade nisab and khums increments | Confirm which valuation benefits Fuqara in each case and whether the surplus increments apply to the chosen class | Limited currency/trade-only branch uses explicit selected gold/silver valuations; needs approval |
| Mixed categories each below their individual nisab | Verify category boundaries, value conversion, chosen nisab and khums handling against exact text | An indicative branch exists only when each category is individually below its nisab; needs approval |
| Any category individually at/above nisab while other categories are present | Verify all category-wise khums combinations and residual gold/silver treatment | Deliberately returns Mufti review required |
| Hawl start, interruption, growth and anniversary | Verify asset-specific Hawl, continuity, local ru'yat and new-acquisition rules | Hijri anniversary helper only; no automatic eligibility decision |
| Every type of debt / repayment schedule | Verify immediately due debts, future installments, debt after Hawl, mahr, tax and other liabilities | Recorded separately; not automatically deducted |
| Receivables: strong, medium and weak | Verify source definitions, payment trigger, recovery amount and new Hawl requirements | Recorded separately; not automatically included |
| Committee/BC saved or taken; contract and qabd | Review the exact 2026 Dar-ul-Ifta committee document and each contract scenario | Recorded as review-required; not automatically classified |
| Jewellery: stones, workmanship, GST, carat and settings | Verify exact source and case, including ornament use, non-metal weight and purity evidence | Gross-to-pure weight arithmetic only; fiqh eligibility remains subject to review |
| Business inventory, raw materials and resale property | Verify purchase intention, valuation date and valuation basis | Input categories provided; intent-sensitive cases still need review |
| Rental property, equipment and agricultural assets | Verify rent retained, work tools, crops and Ushr rules | Educational guide only; no automatic Zakat/Ushr ruling |
| Interest / impermissible income | Verify source and disposal treatment | Kept separate in guide; no automatic zakat classification |
| Fitrah: nisab, obligation date, payer and dependants | Verify exact payer, dependants, wealth at Fajr of Eid and local commodity/weight standard | Quantity × local-price estimate only; obligation and payer are not decided |
| Fitrah commodity equivalence | Verify Sa' conversion, food alternatives and any local weight differences | Source-based approximate quantities only; requires sign-off |
| Recipients, relatives, institutions and Tamleek | Verify all categories, ownership transfer and institutional handling | Educational guide only; no automatic eligibility approval |
| Solar / financial-year treatment | Verify exact ruling, date-count convention, leap years and rounding | Implements the supplied document's exact `(365/354) × 2.5%` arithmetic only; approval required |

For every approved rule, record: source title, author/organization, edition, volume/part, page/mas'ala, exact Arabic/Urdu passage, rule summary, edge cases, scholar name/qualification, Dar-ul-Ifta, date, written approval, code rule ID, expected result and test cases, and version of the rule.
