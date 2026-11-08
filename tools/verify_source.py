#!/usr/bin/env python3
"""Fast source-package checks; does not replace Gradle, lint or device tests."""
from pathlib import Path
import json
import re
import sys
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
ANDROID = "http://schemas.android.com/apk/res/android"
errors: list[str] = []
checks: list[str] = []

def require(condition: bool, message: str) -> None:
    if condition:
        checks.append(message)
    else:
        errors.append(message)

# Parse every Android resource/manifest XML.
xml_files = sorted((ROOT / "app/src/main").rglob("*.xml"))
for path in xml_files:
    try:
        ET.parse(path)
    except ET.ParseError as exc:
        errors.append(f"Malformed XML: {path.relative_to(ROOT)}: {exc}")
require(len(xml_files) > 0, f"Android XML parsed ({len(xml_files)} files)")

# Verify event-feed JSON files are valid JSON.
json_files = sorted((ROOT / "content").glob("*.json"))
for path in json_files:
    try:
        json.loads(path.read_text(encoding="utf-8"))
    except Exception as exc:  # report malformed files without a traceback
        errors.append(f"Malformed JSON: {path.relative_to(ROOT)}: {exc}")
require(len(json_files) > 0, f"Content JSON parsed ({len(json_files)} files)")

manifest_path = ROOT / "app/src/main/AndroidManifest.xml"
manifest_text = manifest_path.read_text(encoding="utf-8")
manifest = ET.parse(manifest_path).getroot()
application = manifest.find("application")
require(application is not None and application.get(f"{{{ANDROID}}}allowBackup") == "false",
        "Android app backup disabled")
require("ACCESS_BACKGROUND_LOCATION" not in manifest_text,
        "No background-location permission")
require('android:usesCleartextTraffic="true"' not in manifest_text,
        "No explicit cleartext-traffic opt-in")

widget_path = ROOT / "app/src/main/res/xml/hijri_widget_info.xml"
widget = ET.parse(widget_path).getroot()
require("home_screen" in widget.get(f"{{{ANDROID}}}widgetCategory", "") and
        "keyguard" in widget.get(f"{{{ANDROID}}}widgetCategory", ""),
        "Hijri widget declares home-screen and optional keyguard categories")

build = (ROOT / "app/build.gradle.kts").read_text(encoding="utf-8")
for token in ("compileSdk = 36", "targetSdk = 36", "versionCode = 100", 'versionName = "1.0.0"'):
    require(token in build, f"Gradle config contains {token}")
gradle_properties = (ROOT / "gradle.properties").read_text(encoding="utf-8")
require("android.builtInKotlin=false" in gradle_properties and "android.newDsl=false" in gradle_properties,
        "AGP 9 external Kotlin compatibility mode is explicit")

# Launcher and store icon assets must exist.
require((ROOT / "playstore/icon_512.png").is_file(), "512px Play Store icon exists")
require((ROOT / "app/src/main/res/drawable-nodpi/noor_launcher_foreground.png").is_file(),
        "Adaptive launcher foreground exists")
for density in ("mdpi", "hdpi", "xhdpi", "xxhdpi", "xxxhdpi"):
    require((ROOT / f"app/src/main/res/mipmap-{density}/ic_launcher.png").is_file(),
            f"{density} launcher icon exists")

# A source archive must never contain signing material or local machine configuration.
for pattern in ("*.jks", "*.keystore", "*.p12", "*.pfx", "local.properties", "keystore.properties"):
    found = list(ROOT.rglob(pattern))
    require(not found, f"No private signing/local files ({pattern})")

for item in checks:
    print(f"PASS  {item}")
for item in errors:
    print(f"FAIL  {item}", file=sys.stderr)
print(f"\n{len(checks)} checks passed; {len(errors)} failed.")
sys.exit(1 if errors else 0)
