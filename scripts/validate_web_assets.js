'use strict';

const fs = require('fs');
const path = require('path');
const vm = require('vm');

const root = path.resolve(__dirname, '..');
const htmlPath = path.join(root, 'app', 'src', 'main', 'assets', 'index.html');
const html = fs.readFileSync(htmlPath, 'utf8');
const scriptMatch = html.match(/<script>([\s\S]*?)<\/script>/i);
if (!scriptMatch) throw new Error('WebView JavaScript block not found.');
new vm.Script(scriptMatch[1], { filename: 'index.html:inline-script' });

// Catch duplicate IDs early; repeated IDs can silently update the wrong UI element.
const ids = [...html.matchAll(/\sid=["']([^"']+)["']/g)].map((m) => m[1]);
const seen = new Set();
const duplicates = new Set();
for (const id of ids) {
  if (seen.has(id)) duplicates.add(id);
  seen.add(id);
}
if (duplicates.size) throw new Error(`Duplicate HTML IDs: ${[...duplicates].join(', ')}`);

// Confirm that bundled images referenced by the page exist in the Android assets folder.
const assetDir = path.dirname(htmlPath);
for (const match of html.matchAll(/<img\b[^>]*\bsrc=["']([^"']+)["']/gi)) {
  const src = match[1];
  if (/^(?:https?:|data:|\/\/)/i.test(src)) continue;
  const localPath = path.resolve(assetDir, src);
  if (!localPath.startsWith(assetDir + path.sep) || !fs.existsSync(localPath)) {
    throw new Error(`Missing or unsafe local image asset: ${src}`);
  }
}

// Ensure the new calculator/intro labels exist in every supported language.
const marker = 'var UI_TRANSLATIONS = {';
if (!html.includes(marker)) throw new Error('UI_TRANSLATIONS map not found.');
const translationSource = html.split(marker, 2)[1];
const newKeys = [
  'intro_skip', 'calc_hero_kicker', 'calc_live_status', 'calc_gold_nisab',
  'calc_silver_nisab', 'calc_hero_note', 'calc_journey_title',
  'calc_journey_note', 'calc_step_rates', 'calc_step_jewels',
  'calc_step_assets', 'calc_step_debts'
];
for (const locale of ['urdu', 'hindi', 'hinglish', 'english', 'arabic']) {
  const start = translationSource.indexOf(`  ${locale}: {`);
  if (start < 0) throw new Error(`Missing translation locale: ${locale}`);
  const tail = translationSource.slice(start);
  const close = locale === 'arabic' ? tail.indexOf('\n};') : tail.indexOf('\n  },');
  if (close < 0) throw new Error(`Could not close translation locale: ${locale}`);
  const block = tail.slice(0, close);
  const missing = newKeys.filter((key) => !block.includes(`${key}:`));
  if (missing.length) throw new Error(`${locale} missing keys: ${missing.join(', ')}`);
}

// Exercise the exact presentation helper used by renderResult().
const helperStart = scriptMatch[1].indexOf('function calculateAssetPresentation(asset)');
if (helperStart < 0) throw new Error('Zakat asset presentation helper missing.');
const helperEnd = scriptMatch[1].indexOf('\n}\n', helperStart);
if (helperEnd < 0) throw new Error('Zakat asset presentation helper is incomplete.');
const sandbox = {};
vm.runInNewContext(
  scriptMatch[1].slice(helperStart, helperEnd + 2) + '\nthis.calculateAssetPresentation = calculateAssetPresentation;',
  sandbox
);
const cases = [
  [{ type: 'RAW_MATERIAL', amount: 100000, ownershipPct: 50 }, true, 50000],
  [{ type: 'UDHAR_QAWI', amount: 20000, ownershipPct: 75 }, true, 15000],
  [{ type: 'CASH_BANK', amount: 1000, ownershipPct: 0 }, true, 1000],
  [{ type: 'HOME_USE', amount: 500000, ownershipPct: 100 }, false, 500000],
  [{ type: 'SHOP_STOCK', amount: -100, ownershipPct: 100 }, true, 0]
];
for (const [asset, expectedZakatable, expectedAmount] of cases) {
  const result = sandbox.calculateAssetPresentation(asset);
  if (result.isZakatable !== expectedZakatable || Math.abs(result.effectiveAmount - expectedAmount) > 0.001) {
    throw new Error(`Asset display mismatch for ${JSON.stringify(asset)}: ${JSON.stringify(result)}`);
  }
}

if (/HijriSync|HijriWidgetProvider|HijriWidgetUpdateReceiver|hijriOffset|hijriCalendarMethod|calendarEvents|fetchLiveHijri|refreshHijriWidget|openSystemCalendarEvent|TextToSpeech|speakText|stopSpeaking|openSpeechSettings|ACTION_TTS_SETTINGS|sec-calendar|dock-calendar|readAloudParcha|listen_report|stop_voice|settings_voice|onNativeSpeakState|speechSynthesis/i.test(html)) {
  throw new Error('Calendar/Hijri/TTS remnants detected in WebView HTML.');
}

// The bottom dock has 5 tabs; every locale that defines dock labels must give exactly 5 (a 6th "calendar" label shifts all names).
for (const m of html.matchAll(/dock:\s*\[([^\]]*)\]/g)) {
  const n = m[1].split(',').filter((x) => x.trim()).length;
  if (n !== 5) throw new Error(`dock label list must have 5 entries, found ${n}: ${m[0]}`);
}
// Every locale (Hinglish and English included) must define dock labels, otherwise applyLanguageUI() crashes.
const i18nSource = html.slice(html.indexOf('var I18N = {'), html.indexOf('var UI_TRANSLATIONS = {'));
for (const locale of ['urdu', 'hindi', 'hinglish', 'english', 'arabic']) {
  const at = i18nSource.indexOf(`  ${locale}: {`);
  if (at < 0) throw new Error(`I18N locale missing: ${locale}`);
  const next = i18nSource.indexOf('\n  },', at);
  const block = i18nSource.slice(at, next < 0 ? undefined : next);
  if (!/dock:\s*\[/.test(block)) throw new Error(`I18N.${locale} has no dock labels (language switch would crash)`);
}

if (!/<div class=\"bismillah-intro\"[^>]*id=\"bismillahIntro\"/.test(html) || !/noor-bismillah-mark\.svg/.test(html)) throw new Error('Bismillah intro missing.');

const svgPath = path.join(assetDir, 'noor-bismillah-mark.svg');
const svg = fs.readFileSync(svgPath, 'utf8');
if (!/<svg\b/.test(svg) || !/<\/svg>\s*$/.test(svg)) throw new Error('Bismillah SVG is invalid or incomplete.');

console.log(`Web asset validation passed: ${ids.length} IDs, 5 locales, ${cases.length} Zakat asset cases, local SVG present.`);
