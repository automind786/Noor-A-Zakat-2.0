# Noor-A-Zakat Pro 2.1.0 — Sirf mobile se build (GitHub Actions)

## A. Existing GitHub repo ko update karna (Termux)
1. F-Droid se **Termux** install karein (Play Store wala purana Termux avoid karein).
2. Aapka existing GitHub repository `NoorEZakat` ho to naya repo banane ki zaroorat nahi. Script existing history clone karke project files update karegi.
3. GitHub > Settings > Developer settings > Personal access tokens > **Tokens (classic)** > `repo` + `workflow` scopes ke saath token banayein. Token kisi ko na bhejein.
4. Updated ZIP ko phone ke Downloads folder mein rakhein. Termux mein ZIP extract karke uske project folder mein jaiye:
   ```
   termux-setup-storage
   pkg install -y unzip
   mkdir -p ~/NoorEZakat_Updater
   unzip -q ~/storage/downloads/NoorEZakat_v2.1.0_Upgraded.zip -d ~/NoorEZakat_Updater
   cd ~/NoorEZakat_Updater
   bash termux_push.sh
   ```
   Username aur token sirf prompt par dein. Script token ko remote URL mein store nahi karti aur existing Git history ko force-push se overwrite nahi karti. Naya repo ho to script private repo create karegi.

## B. Test APK
Push hote hi build chalta hai. Repo > **Actions** > latest run > **debug-apk** artifact download karke install karein (test ads dikhengi).

## C. Keystore (ek baar)
1. Actions > **Make Keystore** > Run workflow > password likhein.
2. Artifact **keystore-BACKUP-KARO** download karke Drive wagairah mein **hamesha ke liye backup** rakhein. Ye kho gaya to app update nahi kar paenge.
3. Repo > Settings > Secrets and variables > Actions > New secret:
   - `KEYSTORE_BASE64` = keystore_base64.txt ka poora text
   - `KEYSTORE_PASSWORD` = wahi password
   - `ADMOB_APP_ID`, `ADMOB_BANNER_ID` = AdMob ki IDs
4. Zip ke andar ki artifact files se `keystore_base64.txt` kholne ke baad uska artifact GitHub se delete kar dein.

## D. Play Store AAB
Actions > **Build** > Run workflow > release = `true` > artifact **release-aab** download karke Play Console mein upload karein.


## v2.1.0 ke naye features
- App language onboarding aur Settings dono se badlein (Hinglish, Hindi, Urdu, English, Arabic).
- Islamic calendar mein Hijri + Gregorian month view, local events add/edit/delete, aur Android Calendar mein event bhejne ka option.
- Read-aloud ke liye floating Stop Voice aur Android Text-to-speech settings shortcut.
- Naya emerald/gold launcher icon; header ka ornamental logo hata diya gaya.
- Fitra mein per-person weight editable; local grain price manually enter karein.
- Build ke baad GitHub Actions > latest run > Artifacts > `debug-apk` se APK download karein.

**Reminder:** calendar event app mein save hota hai, lekin app background notification schedule nahi karti. Reliable alerts ke liye event ko Android Calendar mein save karein. Android build ke liye GitHub Actions internet-enabled runner par Gradle aur Android SDK download/install karega.
