# Noor-A-Zakat Pro v2.1.0

यह Android project source है। Package: `com.hanafi.noorezakat`। मौजूदा user data schema बरकरार रखा गया है; calendar events भी उसी encrypted local storage में save होते हैं।

## मुख्य बदलाव
- Hinglish, Hindi, Urdu, English और Arabic language selection; Settings से भाषा बदलने की सुविधा। मुख्य calculator labels और select options भी translate होते हैं। कुछ लंबी fiqh source entries अभी मूल Hinglish में हैं और scholarly review के बाद translate की जानी चाहिए।
- नया Islamic calendar: Gregorian month grid के साथ device-supported Hijri/Umm al-Qura date, Hijri offset, local event add/edit/delete और Android Calendar में event insert।
- Voice पढ़ते समय floating Stop control, modal Stop button, app background में जाने पर TTS stop और Android TTS Settings shortcut।
- नया emerald/gold launcher icon, clean header, 6-tab bottom navigation।
- Fitra में per-person weight editable और reference quantity/local price के बारे में स्पष्ट note।
- Asset ownership percentage तथा raw-material और strongly collectible receivable के लिए input options।
- Invalid jewellery stone weight और zero ownership inputs पर validation।

## Build
GitHub पर push करने के बाद Actions workflow `testDebugUnitTest` और `assembleDebug` चलाता है। APK artifact: `debug-apk`।

Termux से project push करने के लिए included `termux_push.sh` updated ZIP filename भी पहचानता है। ZIP को Downloads में रखें, फिर Termux में storage permission और Git tooling उपलब्ध कराकर script चलाएँ। GitHub Personal Access Token को private रखें और इस्तेमाल के बाद revoke/rotate करें यदि वह कहीं गलती से दिख गया हो।

## ज़रूरी सीमाएँ
- इस environment में Gradle distribution download DNS/network failure के कारण APK compile नहीं हो सका। इसलिए इस ZIP को GitHub Actions पर build करके APK install/test करना ज़रूरी है।
- Android TTS engine gender की standardized जानकारी नहीं देता। यदि phone में male voice installed है, Android Text-to-speech settings में उसे चुनें; app उसे स्वयं guarantee नहीं कर सकती।
- Calendar local events save करता है; background notifications schedule नहीं करता। Android Calendar में save करने पर phone/calendar app की reminder settings लागू होंगी।
- Fitra के weights केवल editable reference हैं; स्थानीय मान्य Hanafi Mufti से मात्रा की पुष्टि करें।
- यह app हिसाब और शैक्षिक मदद है, fatwa नहीं। जटिल मसलों में qualified Mufti से रुजू करें।

## v3.0.0 — Online Hijri + Home/Lock visibility
- Added an online Hijri verification bridge using the documented AlAdhan Islamic Calendar API; default source is Umm al-Qura (UAQ).
- Added a first-class Hijri source selector (UAQ / HJCoSA) and retained manual ±2-day correction for local moon-sighting differences.
- Added Islamic special-day data to the current-date presentation when the online source supplies an occasion.
- Added an Android home-screen Hijri/Gregorian widget with online status and special-day line.
- The widget refreshes after enabling and on a daily schedule; the app also refreshes its current Hijri verification when the calendar/home date is loaded.
- Important: calculated Hijri calendars cannot guarantee a local crescent-sighting decision. Ramadan/Eid and other observation-dependent dates must follow the trusted local authority/mosque; the correction control is intentional.
- Android does not support third-party widgets on the lock screen on Android 5+ as a general platform capability. The implementation therefore uses a home-screen widget; a lock-screen-visible persistent notification can be added as a separate opt-in surface if desired.

### Online calendar source
AlAdhan API is used only for online verification/special-day metadata; it is not treated as a religious authority. API failure never blocks the local calendar.
