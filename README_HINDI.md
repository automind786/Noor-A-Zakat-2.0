# Noor Zakat Amanah — Android 1.0.0

यह नया Native Android app दोनों दिए गए ZIP की अच्छी बातों को मिलाकर बनाया गया है। Zakat calculator इसका मुख्य भाग है। Hindi default है; Hindi, Urdu, English और Arabic भाषा support मौजूद है।

## मुख्य सुविधाएँ

1. **मदवार Zakat हिसाब:** 12 तक अलग सोने के गहने, चाँदी, नक़द, बैंक, व्यापारिक माल, कच्चा माल, बिक्री के लिए संपत्ति, हिस्सेदारी, अलग क़र्ज़ और उधार/वसूली की entries।
2. **सही unit अलग-अलग:** बाजार तोला 10 ग्राम और शरई तोला 11.664 ग्राम; पत्थर/गैर-धातु घटाने के बाद कैरेट purity; Indian और Western comma format validation।
3. **Source-based Hanafi calculation engine:** 612.41 g चाँदी को Faizan-e-Zakat के jeweller approximation के आधार पर default रखा गया है। 612.36 g का project-research विकल्प भी उपलब्ध है। चुना गया आधार परिणाम में दिखेगा।
4. **शरई सावधानी:** जटिल क़र्ज़, वसूली, कमेटी/बीसी और कुछ mixed-asset cases में ऐप गलत रकम का दावा नहीं करता; Mufti review माँगता है।
5. **Encrypted local draft:** Zakat worksheet AES-256-GCM और Android Keystore से फोन में encrypted save होती है। Zakat की रकम किसी online prayer/event/rate provider को नहीं भेजी जाती।
6. **Namaz management:** Karachi / MWL / Umm al-Qura method, Hanafi Asr, permission के बाद location, पाँच वक्त का local checklist और पिछले 7 दिनों का self-recorded summary।
7. **Hijri calendar:** local ±2 day adjustment, cited Islamic events feed, offline fallback और चाँद देखने का note।
8. **और tools:** Fitrah quantity/value worksheet, Hawl helper, asset guide, masarif/Tamleek, references और Tasbih।
9. **Home widget:** Hijri date, Islamic observance और cached next-prayer line। Lock-screen category भी घोषित है, लेकिन lock-screen widget केवल उन्हीं Android/device launchers पर दिखेगा जो इसे support करते हों—इसकी guarantee नहीं है।
10. **Design:** animated opening, emerald/gold theme, नया high-resolution app icon और Urdu/Arabic RTL layout।

## जरूरी शरई सूचना

यह app **Fatwa नहीं है**। *Faizan-e-Zakat* के page 13 पर 7.5 तोला सोना, 52.5 तोला चाँदी, 2.5% और khums examples का आधार दिया गया है। लेकिन source link होना हर code rule की Mufti approval नहीं है। Public release से पहले qualified Sunni Hanafi Mufti / Dar-ul-Ifta से हर calculation branch, wording, debt, receivable, committee/BC, jewellery, Hawl और Fitrah rules की लिखित approval लेना अनिवार्य है। जटिल मामलों में ऐप जानबूझकर final रकम नहीं देता। देखें: `docs/FIQH_REVIEW.md` और `docs/SCHOLAR_REVIEW_CHECKLIST.md`.

## Build कैसे करें

पहले source files की जल्दी जाँच:

```bash
python3 tools/verify_source.py
```
यह Android Gradle build का विकल्प नहीं है।

JDK 17+, Android SDK Platform 36 और Gradle dependencies के लिए Internet आवश्यक है। Project में AGP 9.1.1 + Gradle 9.3.1 + Kotlin/Compose Compiler 2.4.21 है। `gradle.properties` में AGP 9 के built-in Kotlin/new DSL को अस्थायी रूप से बंद किया है क्योंकि matching external Kotlin/Compose plugins इस्तेमाल हो रहे हैं; AGP 10 पर जाने से पहले built-in Kotlin migration करें।

```bash
./gradlew clean testDebugUnitTest lintDebug assembleDebug
```

Release AAB के लिए पहले निजी signing keystore configure करें:

```bash
./gradlew bundleRelease
```

Keystore, password, token या `local.properties` ZIP/GitHub में शामिल न करें। इस ZIP में release signing key नहीं है।

## Official publish से पहले

`RELEASE_READINESS_HI.md` की पूरी checklist पूरी करें: Android CI build, physical-device tests, Mufti approval, public privacy-policy URL और contact email, Play Console declarations, signing, store listing और pre-launch report. 31 August 2026 से Google Play की नई apps/updates के लिए target API 36 या अधिक आवश्यक है; इस project में targetSdk 36 set है। Submission से पहले Play Console की current requirement फिर जाँचें।

**यह source package है—signed APK/AAB या पहले से Play Store-ready build नहीं।**

## App identity

- नाम: Noor Zakat Amanah
- Application ID: `com.noorazakat.app` (दिए गए native project का ID रखा है; Play publication से पहले ownership जाँचें)
- Version: `1.0.0` / versionCode `100`
- minSdk 26 · targetSdk 36 · compileSdk 36
