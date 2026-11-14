# Noor-A-Zakat Pro v2.1.0 — Source Audit & Upgrade Report

## 1. Uploaded ZIP ka audit

- `debug-apk.zip` mein sirf `app-debug.apk` mila; editable source code nahi tha.
- `NoorEZakat_v2.zip` mein poora Android project mila: Kotlin activity, Zakat calculation engine, WebView UI, Android manifest, Gradle wrapper, tests aur GitHub Actions workflow. Upgrade isi source project par kiya gaya hai.
- Package name `com.hanafi.noorezakat` aur pehle se maujood encrypted state-storage approach ko badla nahi gaya.

## 2. v2.1.0 mein kiye gaye kaam

1. **Language system:** Hinglish, Hindi, Urdu, English aur Arabic. Setup ke saath Settings se bhi language badli ja sakti hai. 150 marked UI text nodes aur 3 placeholders ke liye paanchon languages ke translations maujood hain. Hijri/Gregorian date display aur calendar labels bhi selected language ke mutabiq dikhte hain. Lambi dalil/source entries aur kuch generated explanatory text abhi poori tarah translate nahi hue; unki fiqhi proofreading aur translation alag se honi chahiye.
2. **Hijri smart calendar:** Gregorian month grid par Hijri date, month navigation, local event add/edit/delete, aur event ko phone ke Android Calendar insert screen mein bhejne ka option. Events app ke existing encrypted local state mein save hote hain.
3. **Voice controls:** Read-aloud chalte waqt floating Stop Voice button, dialog Stop button, app background mein jaane par TTS stop, aur Android Text-to-speech Settings shortcut. App same-language voice ka naam explicitly `male` batata ho to use pehle try karti hai; warna Android ki selected voice use hoti hai.
4. **Design/icon:** Header ke top-left decorative mark ko hataaya, emerald/gold visual refresh kiya, adaptive/fallback/splash launcher icon aur Play Store 512px icon update kiya.
5. **Fitra input:** Per-person weight editable hai; grain ke reference weight se start hota hai, lekin user local Mufti ki batayi miqdaar daal sakta hai. Per-kg local price bhi user enter karta hai. Ghar ke members ka default 1 rakha hai taaki 5 ka accidental total na bane.
6. **Zakat assets:** Ownership percentage, business raw-material aur strongly collectible receivable options add kiye. Calculation engine pehle se in asset types ko handle karta tha; ab UI se bhi select kiye ja sakte hain. Jewellery mein stone weight total weight se zyada ho to entry reject hoti hai.
7. **Currencies:** Setup aur Settings dono mein 14 currency options.
8. **Fiqhi-age guard:** 15 saal se kam age par status ko automatic `NABALIGH` force nahi karta. App caution dikhati hai; user ko shar‘i status select karna hota hai, kyunki age alone har case ka final test nahi.
9. **Tests and guide:** Raw-material, ownership percentage aur receivable ke liye Kotlin unit tests add kiye. Termux upload script ko existing GitHub repo ke commits preserve karne ke liye update kiya; PAT remote URL mein store nahi hota. Hindi build guide add/update ki.

## 3. Verification

- HTML JavaScript syntax check: PASS.
- HTML IDs duplicate check: PASS.
- UI translation-key coverage: PASS — 150 tagged text nodes + 3 placeholders, paanchon languages mein matching keys.
- XML resource and AndroidManifest parse: PASS.
- Calendar logic harness: PASS — month grid, event create/update/delete aur Android Calendar bridge.
- Fitra calculation/invalid weight harness: PASS.
- Age/puberty status guard harness: PASS.
- Termux shell script syntax: PASS.
- Kotlin unit tests: add kiye gaye, lekin is environment mein run nahi ho sake.

## 4. APK build status — important

Is workspace mein Gradle 8.13 wrapper download karne ki koshish DNS/network restriction ke kaaran fail hui; wrapper distribution download 0-byte `.part` file par ruk gayi. Isliye yahan naya APK compile ya device par install karke verify nahi hua. Purana `debug-apk.zip` naya build nahi hai. Updated source ko GitHub par push karke **Actions → latest Build run → `debug-apk` artifact** download karna hoga. Workflow pehle `testDebugUnitTest`, phir `assembleDebug` chalata hai.

## 5. Jo cheezein jaan-boojhkar guarantee nahi ki gayi

- **Male voice:** Android TTS mein standardized gender field nahi hai. App sirf tab explicitly male-labelled voice choose karti hai jab engine ke voice name mein `male` ho aur `female` na ho. Agar engine aisi voice expose nahi karta, phone Settings mein voice manually choose karni hogi.
- **Calendar notifications:** App events local save karti hai aur Android Calendar mein event insert kholti hai. Yeh app background alarm/notification schedule nahi karti; reminders ke liye Android Calendar mein Save karke uski notification setting use karein.
- **Islamic dates:** Device ke `Intl`/ICU Umm al-Qura calendar ka use hota hai jahan supported ho. Local moon-sighting 1–2 din alag ho sakti hai; Hijri offset setting di gayi hai. Yeh official moon-sighting announcement service nahi hai.
- **Fitra amounts:** Existing grain reference options ko blindly delete/change nahi kiya. Unki quantity editable rakhi hai; local Hanafi Mufti se exact amount confirm karein.
- **AI intelligence:** Is release mein koi paid AI/API service ya fabricated fatwa feature add nahi kiya. Smart validation, event handling aur calculation input checks add kiye gaye hain. AI-based fiqhi answer engine ke liye verified source corpus, citations aur scholar review chahiye.

**Religious disclaimer:** Noor-A-Zakat calculation aur educational aid hai, fatwa nahi. Complex zakat, debt, receivable, jewellery aur Fitra cases mein qualified Hanafi Mufti se confirmation zaroor karein.


## v2.2.0 Smart UI अपडेट

- पूरे ऐप में dark emerald + warm gold आधारित एकसमान visual styling, बेहतर cards, spacing, fields और bottom navigation लागू की गई।
- Calendar अब Gregorian महीने के बजाय Hijri महीने के हिसाब से grid बनाता है; हर cell में Hijri दिन प्रमुख और Gregorian तारीख सहायक रूप में दिखती है।
- Calendar के पिछला/अगला बटन Hijri महीने के अनुसार चलते हैं। Saved events की वास्तविक Gregorian तारीख सुरक्षित रहती है।
- पुराने TTS कोड में गलती से male नाम वाली voice चुनी जा रही थी। अब उपलब्ध होने पर उसी भाषा की female नाम वाली voice प्राथमिकता से चुनी जाती है। Android में voice gender का standard API नहीं है; यदि फोन female-labelled voice उपलब्ध नहीं कराता तो system-selected voice चलेगी।
- JavaScript syntax check पास हुआ। इस वातावरण में Gradle download के लिए network उपलब्ध न होने से Android APK build/test सत्यापित नहीं हो सका। AndroidIDE/GitHub Actions में build करके फोन पर voice और Hijri calendar की जाँच करें।

## v3.0.0 — Online Hijri + Home/Lock visibility
- Added an online Hijri verification bridge using the documented AlAdhan Islamic Calendar API; default source is Umm al-Qura (UAQ).
- Added a first-class Hijri source selector (UAQ / HJCoSA) and retained manual ±2-day correction for local moon-sighting differences.
- Added Islamic special-day data to the current-date presentation when the online source supplies an occasion.
- Added an Android home-screen Hijri/Gregorian widget with online status and special-day line.
- The widget refreshes after enabling and on a daily schedule; the app also refreshes its current Hijri verification when the calendar/home date is loaded.
- Important: calculated Hijri calendars cannot guarantee a local crescent-sighting decision. Ramadan/Eid and other observation-dependent dates must follow the trusted local authority/mosque; the correction control is intentional.
- Android does not support third-party widgets on the lock screen on Android 5+ as a general platform capability. The implementation therefore uses a home-screen widget; a lock-screen-visible persistent notification can be added as a separate opt-in surface if desired.
