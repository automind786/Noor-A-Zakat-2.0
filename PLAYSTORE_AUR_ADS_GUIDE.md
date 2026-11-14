# Noor-A-Zakat Pro v2.1.0 — Play Store + AdMob Guide

## 1. Build kaise karein (Android Studio)
1. **Android Studio (Ladybug ya naya)** mein project kholein, Gradle sync hone dein. JDK 17 chahiye (Studio ke saath aata hai).
2. Pehle **unit tests** chalayein: `./gradlew testDebugUnitTest` (Zakat engine ke 8 tests).
3. Phone par test: Run ▶ (debug build, TEST ads dikhengi).

## 2. Apni AdMob IDs lagana
1. https://admob.google.com par account banayein → **Apps > Add app** (Android) → **App ID** milega (`ca-app-pub-XXXX~YYYY`).
2. **Ad units > Add ad unit > Banner** → **Banner Ad unit ID** milega (`ca-app-pub-XXXX/ZZZZ`).
3. `gradle.properties` mein dono bharein:
   ```
   ADMOB_APP_ID=ca-app-pub-XXXX~YYYY
   ADMOB_BANNER_ID=ca-app-pub-XXXX/ZZZZ
   ```
4. **Debug build hamesha test ads dikhata hai** (apni real ads par khud click karna AdMob account ban karwa sakta hai).
5. AdMob Console mein **Privacy & messaging** mein GDPR consent message (UMP) banakar publish karein, warna EEA/UK users ko ads nahi dikhengi.

## 3. Signed AAB banana
1. Keystore banayein (ek baar, **isko aur password ko kahin safe backup karein**):
   `keytool -genkey -v -keystore noor-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias noor`
2. Project root mein `keystore.properties` banayein (git mein commit na karein):
   ```
   storeFile=noor-release.jks
   storePassword=....
   keyAlias=noor
   keyPassword=....
   ```
3. `./gradlew bundleRelease` → file: `app/build/outputs/bundle/release/app-release.aab`
4. Play Console mein **Play App Signing** on rakhein.

## 4. Play Console mein bharna
- **App ID `com.hanafi.noorezakat` permanent hai** — upload se pehle tay kar lein.
- **Privacy Policy:** `PRIVACY_POLICY.md` mein apna naam/email daal kar kisi website/GitHub Pages/Google Sites par host karein, phir link (a) Play Console mein aur (b) `app/src/main/assets/index.html` mein `var PRIVACY_URL = "..."` mein daalein (tab Settings mein button dikhega).
- **Ads declaration:** "Contains ads" = **Yes**.
- **Data safety:** Device or other IDs (Advertising ID) → collected, shared with Google AdMob, purpose: Advertising. App ka hisab developer ko collect nahi hota (on-device encrypted).
- **App category:** Books & Reference ya Lifestyle. **Target audience:** 13+ / All ages (Families Program na lein, warna ads rules sakht hote hain).
- **Content rating** questionnaire bharein.
- **Graphics:** `playstore/icon_512.png`, `playstore/feature_graphic_1024x500.png` (placeholder hain, chahein to behtar banwa lein). Phone **screenshots** khud lein (kam az kam 2).
- **Nayi personal developer accounts** ke liye Google ki shart: production se pehle closed test (kam az kam 12 testers, 14 din). Console mein latest rules zaroor dekh lein.

## 5. AIDE ke baare mein sach
AIDE bohot purane Gradle/Android version tak hi chalta hai. Play Store ab **API 36** maangta hai (31 Aug 2026 se) aur AdMob/AGP 8 ke liye naya toolchain zaruri hai, isliye **AIDE mein ye project build nahi hoga**. Is project ko Android Studio (laptop) ya GitHub Actions se build karein. Agar sirf phone hai to batayein, koi cloud-build rasta nikalte hain.

## 6. Release se pehle zaruri
- Kisi **Sunni Hanafi Mufti/Dar-ul-Ifta se app ke masail ka jaiza** karwa lein (neeche "Ilmi review" dekhein).
- Asli phone par test: offline mode, Urdu/Hindi TTS, Print/PDF, WhatsApp share, back button.
