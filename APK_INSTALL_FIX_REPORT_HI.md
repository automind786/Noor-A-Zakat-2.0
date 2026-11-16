# Noor-A-Zakat 2.4.1 — APK Install Fix Report

## Aapke bheje hue `debug-apk (1).zip` mein kya mila
- Outer artifact ZIP mein `app-debug.apk` naam ki ek file thi.
- `unzip -t` ne `invalid compressed data to inflate` report kiya.
- APK extract karne par expected 10,169,606 bytes ke bajay sirf lagbhag 2.9 MB data nikla; APK ka internal ZIP central directory bhi nahi mila.
- Isliye woh uploaded APK installable/valid nahi thi. Uska certificate ya package metadata confirm nahi kiya ja sakta.

## Is source update mein kya badla
- Debug build ka package ID `com.hanafi.noorezakat.debug` hai. Ye original Play Store package `com.hanafi.noorezakat` se alag install hoga aur existing app/data ko replace nahi karega.
- Debug app launcher label `Noor-A-Zakat Pro (Debug)` hoga.
- GitHub Actions ab APK artifact upload karne se pehle `zip -T`, `apksigner verify` aur `aapt dump badging` chalata hai.
- Artifact upload compression level `0` par hai aur APK ke saath SHA-256 checksum aur Hindi install note bhi dega.
- Release workflow mein valid signing secrets hone par signed release APK aur Play Store AAB dono build/verify hote hain.

## Is response mein kya verify hua
- WebView JavaScript syntax aur Zakat display test script pass.
- Android Manifest aur resource XML parse pass.
- GitHub Actions workflow YAML parse pass.
- Gradle wrapper aur Termux script shell syntax pass.
- Source ZIP structure aur archive integrity pass.

## Abhi kya verify nahi hua
Is runtime mein Gradle 8.13 distribution download DNS failure (`Could not resolve host: services.gradle.org`) ke karan nahi hua, aur Android SDK/Build Tools yahan installed nahi the. Isliye yahan APK/AAB compile nahi hua. Real Android build aur signature checks GitHub Actions runner par chalenge. Workflow ke `Verify debug APK integrity, package and signature` step ka green hona zaroori hai.

## Phone par install karne ka tarika
1. GitHub Actions ke `Noor-A-Zakat-2.4.1-debug-apk` artifact ko download karein.
2. Download hua artifact ZIP File Manager mein **Extract** karein.
3. Extract folder ke andar `Noor-A-Zakat-2.4.1-debug.apk` par tap karein.
4. ZIP ko seedha install na karein; Android ZIP file ko app ki tarah install nahi karta.
5. Agar Android unknown-app permission maange to trusted File Manager ke liye hi permission enable karein.

Debug build ek alag testing app hai. Play Store update ke liye `release` workflow aur wahi original signing key use karni hogi jisse aapki app publish hui thi.
