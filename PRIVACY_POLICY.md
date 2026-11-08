# Noor Zakat Amanah — Privacy Policy

Last updated: 8 November 2026

## Data stored on this device

- Zakat worksheet drafts (jewellery, assets, values, ownership shares, debts and selected calculation settings) are stored locally. The serialized draft is encrypted using AES-256-GCM with a non-exportable Android Keystore key. There is no cloud account or remote financial-data submission endpoint.
- The Salah checklist and its seven-day view are stored locally in app-private preferences. Personal calendar event titles and Gregorian dates are also stored locally so the user can view/delete them from the Hijri calendar. These entries are not uploaded.
- The app disables Android backup for its private application data. Uninstalling the app or clearing its storage removes local records. If Android Keystore becomes unavailable, the app avoids silently replacing an unreadable encrypted draft.

## Location and prayer times

The app asks for approximate or precise location only after the user chooses **Use current location** for prayer-time calculation. Coordinates are sent to AlAdhan (`api.aladhan.com`) to retrieve calculated times. With user permission, the selected coordinates and calculation method are retained locally so the app can refresh the schedule without repeatedly requesting GPS. The app does not request background-location permission and does not continuously track location. Saved coordinates can be cleared in Settings.

Prayer times are calculation outputs, not a local mosque's jamaat timetable. The selected method and Hanafi Asr setting are shown in the app. Verify timings with the user's local mosque or trusted religious authority.

## Market-rate estimates

Only when the user taps the market-rate refresh action, the app contacts `api.gold-api.com` for international gold/silver spot estimates and `open.er-api.com` for USD/INR conversion. These are reference estimates, not local jeweller quotations. The app does not send the user's Zakat worksheet values to these providers. The user should enter/confirm the locally applicable rate and valuation date.

## Islamic event feed

The app may download the public, curated `content/islamic-events.json` file from GitHub Raw. The feed contains event titles, descriptions, themes and source links. No Zakat figures or Salah checklist entries are sent with this request. Moon-sighting-dependent dates may differ by location.

## Widgets and lock screen

The Hijri widget displays the Hijri date, public observance information and a cached next-prayer line; it does not display financial worksheet data. Home-screen widgets are supported through Android App Widgets. The project declares the optional lock-screen widget category, but Android platform builds and device launchers are not required to expose third-party widgets on the lock screen. Lock-screen availability is therefore device-dependent and is not guaranteed.

## Network metadata

When the user uses online features, service providers may receive ordinary network metadata such as IP address, request time and user-agent. Review the privacy terms of AlAdhan, the market-data providers and GitHub before using their services.

## Data controls

- Location access can be denied; Zakat, Fitrah, calendar and other offline features remain available.
- Saved prayer coordinates can be cleared in Settings.
- Manual market rates can be entered without requesting online estimates.
- Zakat drafts are encrypted on-device; the app does not send entered wealth amounts to third parties.
- Clearing app storage or uninstalling removes local app data.

## Religious and financial notice

Noor Zakat Amanah is a calculation-support tool, not a fatwa or professional financial advice. Complex debt, receivable, committee, ownership, trade-intention and Fitrah-obligation cases require review by a qualified Sunni Hanafi Mufti. Do not rely on an unapproved calculation build as a final religious ruling.

## Contact and legal publication details

Before public release, the owner must add a monitored privacy contact email and publish this policy at a stable public URL. The developer must also complete Google Play Data safety and applicable legal disclosures. This repository document alone is not a complete substitute for those disclosures.
