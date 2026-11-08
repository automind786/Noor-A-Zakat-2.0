# दोनों दिए गए Apps का Audit — Noor Zakat Amanah 1.0.0

## पुराने native app: `Noor-A-Zakat v0.3.2`

**जो रखा गया:** Kotlin + Jetpack Compose architecture, structured `FiqhZakatEngine`, अलग-अलग सोने के गहनों की entry, stone/lac weight exclusion, carat conversion, 10 g market tola और 11.664 g Shar'i tola का फर्क, asset/debt ledgers, source/review gates, Hijri calendar, curated event feed, prayer API adapter, five-prayer tracker, Fitrah/Hawl/masarif guides, RTL और unit-test baseline.

**जो सुधारा गया:** Zakat draft का long-term persistence पहले स्पष्ट रूप से उपलब्ध नहीं था; अब encrypted local autosave जोड़ा है। Home पर Zakat worksheet को सबसे prominent action बनाया, weekly Salah history जोड़ी, personal calendar events add/delete और Hijri date markers जोड़े, नया icon बनाया, version/API target update किया, silver nisab default को selected published source conversion से स्पष्ट किया।

## पुराने WebView app: `NoorEZakat v2.3.0`

**जो सीखा/रखा:** richer emerald/gold visual direction, animated entry, local data-security की जरूरत, home/calendar event-management की जरूरत और widget/UX ideas.

**जो calculation engine में दोबारा इस्तेमाल नहीं किया:** इसके `bd()` parser में malformed/negative value zero में बदल जाती थी, invalid carat को 24K और zero/out-of-range ownership को 100% default किया जाता था। `LONG_EMI` amount को 12 से गुणा किया जाता था; यदि user outstanding loan amount भरता, तो amount 12 गुना हो सकती थी। इसके बाद total debt को cash/business → silver → gold क्रम में घटाया जाता था, जबकि हर debt की शरई deductibility और due timing की case-specific जाँच आवश्यक है। Mixed wealth में एक generic total 2.5% formula भी था, जिसमें category-wise khums/combination rules flatten हो जाते हैं। इसलिए उस engine को source of truth नहीं बनाया गया।

## नया integrated app क्या करता है

- Native Compose app ही रखा; WebView-only shell नहीं।
- Calculator में malformed number/negative value errors को रोकने वाला typed input model और BigDecimal calculation core रखा।
- Debt automatic deduction नहीं; uncertain liabilities/receivables/committee/mixed asset cases को Mufti review में भेजा जाता है।
- Zakat drafts AES-GCM + Android Keystore के साथ local encrypted storage में रखे जाते हैं।
- Calendar में curated Islamic dates और user-added local events एक साथ दिखते हैं; personal events delete भी हो सकते हैं। App alarm reminders का झूठा दावा नहीं करता।
- Prayer screen में 7-day self-recorded summary; prayer times को local mosque jamaat timetable नहीं बताया जाता।
- Launcher icon, primary Zakat home action, optional lock-screen widget category और publication checklist शामिल हैं।

## शरई सावधानी

*Faizan-e-Zakat* page 13 पर gold/silver nisab और khums surplus examples; page 14 पर mixed-wealth combination rules; page 15 पर year-end market-value/2.5% cash-payment method का विवरण है. Page 15 also distinguishes payment in cash vs in physical gold/silver and notes that the excess in its easy 2.5% method may be nafl. इसलिए amount, method and exact minimum for every category must be reviewed together by a qualified Sunni Hanafi Mufti. This codebase is an indicative calculation aid, not a fatwa or scholar-certified product.

## Release status

Source-level tests and checks passed as recorded in `docs/BUILD_STATUS.md`; full Android Gradle/Compose build and device tests were blocked by missing Android SDK and unavailable Gradle distribution network. Mufti approval, signed release, privacy URL/contact and Play testing remain outstanding. No zero-bug guarantee is claimed.
