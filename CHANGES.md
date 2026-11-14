# Noor-A-Zakat Pro 2.1.0 — Upgrade notes

## v2.1.0
- Added an offline Hijri/Gregorian monthly calendar with personal event storage and an option to open events in the Android calendar app. Hijri dates use the device's Umm al-Qura calendar implementation where supported; local moon-sighting adjustment remains available.
- Added Arabic as a fifth app language and moved language selection into Settings. Major calculator labels, field names, selector choices, navigation and calendar controls now use the selected language. Some scholarly source text and detailed explanations remain in the source language and should be translated/reviewed by a qualified editor.
- Added a persistent Stop Voice control tied to Android TTS utterance state, plus a shortcut to Android Text-to-speech settings. Voice gender is controlled by the installed Android engine; the app does not falsely claim it can identify every voice as male/female.
- Removed the ornamental header mark, redesigned the launcher/splash icon, and refreshed the dark emerald/gold palette.
- Fitra now lets users edit the per-person kilogram measure and local price; reference weights are explicitly labelled as estimates and should be confirmed with a qualified local scholar.
- Added business raw-material / collectible receivable options, a user ownership percentage, extra currency choices, and validation for impossible jewellery / ownership inputs.
- Updated app version to 2.1.0 (versionCode 3).

## Important notes
- The calendar stores events locally. It does not run background notifications; for reliable reminders, use the Android Calendar hand-off.
- Gold/silver rates remain international spot estimates, not guaranteed local shop rates.
- This app is a calculator and learning aid, not a fatwa. Complex cases should be checked with a qualified Hanafi Mufti.

---

# Kya badla (v1 → v2.0)

**Play Store / build**
- targetSdk/compileSdk 36, naye AndroidX libraries, JDK 17, R8 shrink + proguard rules
- `android.aapt2FromMavenOverride=/usr/bin/aapt2` aur `local.properties` (sandbox ke raste) hata diye — ye dusri machine par build tod dete the
- 585 faltu build-cache files hata di, release signing (`keystore.properties`), AdMob IDs gradle se
- Manifest fix: TTS `<queries>` ka galat action theek, AD_ID permission, cleartext band
- Adaptive + monochrome icon, splash screen, Play graphics

**Ads**
- AdMob adaptive banner (WebView ke bahar, native), Google UMP consent + "Ads Privacy Options", content rating G, debug mein test ads

**Security / bugs**
- WebViewAssetLoader (file:// access band), bahar ke links browser mein, JS bridge safe JSON
- Data ab plain localStorage mein copy nahi hota (pehle encrypt ke saath plaintext bhi save hota tha)
- Back button ab sahi (pehle app foran band ho jata tha), JS dialog ki jagah apna confirm sheet
- Saved sone/chandi ka bhav aur selections restart par wapas aate hain (pehle 78000/950 se overwrite ho jate the)
- "Live Bhav" button pehle sirf "Updated" likhta tha, bhav nahi badalta tha — ab sach mein spot bhav laata hai (currency ke hisab se); purane hardcoded bhav hata diye
- Fitra ab sirf ₹ nahi, har currency mein; keemat khud daalte hain
- Zoom/accessibility allow, Print mein sirf Parcha

**Hataya (kaam ka nahi tha)**
- `NoorAIService` (koi UI nahi thi), SQLite FTS knowledge table, PIN-hash/backup helper, Hello-World layout, sample tests, "VERIFIED" ke daave jo kisi aalim ne verify nahi kiye the

**Naya**
- Quran ke 8 masarif, About/Disclaimer, Rate/Share app, Currency + Hijri adjust, toast/animations/premium polish, engine ke 8 unit tests


## Noor-A-Zakat v2.2.0 — Smart UI & Hijri Calendar update

- Reworked the visual layer with a calmer, consistent dark-emerald and warm-gold design system, improved spacing, cards, inputs, navigation dock, and calendar cells.
- Rebuilt the calendar grid to follow the Hijri month (Umm al-Qura where the Android WebView ICU supports it). Gregorian dates remain visible in each cell for practical reference.
- Previous/next calendar navigation now advances by Hijri month; saved events remain attached to their Gregorian dates.
- Corrected the TTS voice preference: the previous implementation accidentally preferred explicitly labelled **male** voices. The app now prefers an explicitly labelled same-language **female** voice when the installed Android TTS engine exposes one, and never deliberately selects a male voice. If the engine does not expose a female-labelled voice, Android's currently selected voice remains in use.
- Improved TTS speech rate/pitch defaults and unique utterance IDs for cleaner playback/stop handling.
- Preserved existing calculation engine, local encrypted state, live-rate bridge, event storage, Fitra and other app modules.
- Version bumped to 2.2.0 (versionCode 4).

**Verification:** embedded JavaScript passed `node --check`. Android Gradle tests/build could not be run in this environment because Gradle 8.13 could not be downloaded (network DNS unavailable). Please build with AndroidIDE or GitHub Actions and test installed TTS voice availability on the target phone. Hijri dates may differ from local moon-sighting by 1–2 days; the existing offset setting remains available.

## v3.0.0 — Online Hijri + Home/Lock visibility
- Added an online Hijri verification bridge using the documented AlAdhan Islamic Calendar API; default source is Umm al-Qura (UAQ).
- Added a first-class Hijri source selector (UAQ / HJCoSA) and retained manual ±2-day correction for local moon-sighting differences.
- Added Islamic special-day data to the current-date presentation when the online source supplies an occasion.
- Added an Android home-screen Hijri/Gregorian widget with online status and special-day line.
- The widget refreshes after enabling and on a daily schedule; the app also refreshes its current Hijri verification when the calendar/home date is loaded.
- Important: calculated Hijri calendars cannot guarantee a local crescent-sighting decision. Ramadan/Eid and other observation-dependent dates must follow the trusted local authority/mosque; the correction control is intentional.
- Android does not support third-party widgets on the lock screen on Android 5+ as a general platform capability. The implementation therefore uses a home-screen widget; a lock-screen-visible persistent notification can be added as a separate opt-in surface if desired.

## v3.0.0 workflow fix
- Replaced the problematic workflow structure with a simpler GitHub Actions workflow.
- Removed the unnecessary Android SDK setup action; GitHub Ubuntu runners already provide the Android SDK.
- Kept automatic debug APK integrity/signature verification.
- Kept direct APK publication to GitHub Releases.
- Kept optional signed Play Store AAB workflow.
