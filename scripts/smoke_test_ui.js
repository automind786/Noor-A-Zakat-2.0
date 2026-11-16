'use strict';
// Boots the real WebView page inside a lightweight fake DOM and exercises:
//  - app start, every language switch (this used to crash for English/Hinglish)
//  - English really shows English text, no Hinglish left in switched labels
//  - live-rate flow: a second/new rate must replace the first one
//  - no read-aloud / TTS leftovers
const fs = require('fs');
const path = require('path');
const vm = require('vm');

const htmlPath = path.resolve(__dirname, '..', 'app', 'src', 'main', 'assets', 'index.html');
const html = fs.readFileSync(htmlPath, 'utf8');
const script = html.match(/<script>([\s\S]*?)<\/script>/i)[1];
const body = html.slice(html.indexOf('<body'), html.indexOf('<script>'));

const errors = [];
const fail = (msg) => { errors.push(msg); };

// ---------- fake DOM ----------
function makeEl(id, attrs) {
  const el = {
    id: id || '', _attrs: Object.assign({}, attrs || {}), style: {}, dataset: {}, value: '', innerHTML: '',
    innerText: '', textContent: '', className: '', children: [], parentNode: null,
    classList: { add() {}, remove() {}, toggle() {}, contains() { return false; } },
    setAttribute(k, v) { this._attrs[k] = String(v); },
    getAttribute(k) { return Object.prototype.hasOwnProperty.call(this._attrs, k) ? this._attrs[k] : null; },
    removeAttribute(k) { delete this._attrs[k]; },
    addEventListener() {}, removeEventListener() {}, appendChild() {}, removeChild() {},
    querySelector() { return makeEl('q'); }, querySelectorAll() { return []; },
    scrollIntoView() {}, focus() {}, click() {}, getBoundingClientRect() { return { top: 0 }; },
  };
  return el;
}
const byId = {};
function getEl(id) { return byId[id] || (byId[id] = makeEl(id)); }

// data-i18n elements discovered in the real HTML
const i18nEls = [];
for (const m of body.matchAll(/<(\w+)([^>]*\sdata-i18n="([^"]+)"[^>]*)>/g)) {
  const idm = m[2].match(/\sid="([^"]+)"/);
  const el = idm ? getEl(idm[1]) : makeEl('');
  el._attrs['data-i18n'] = m[3];
  i18nEls.push(el);
}
const placeholderEls = [];
for (const m of body.matchAll(/<(\w+)([^>]*\sdata-i18n-placeholder="([^"]+)"[^>]*)>/g)) {
  const el = makeEl(''); el._attrs['data-i18n-placeholder'] = m[3]; placeholderEls.push(el);
}
// make sure every id that exists in the page resolves
for (const m of body.matchAll(/\sid="([^"]+)"/g)) getEl(m[1]);

const document = {
  getElementById: getEl,
  querySelectorAll(sel) {
    if (sel === '[data-i18n]') return i18nEls;
    if (sel === '[data-i18n-placeholder]') return placeholderEls;
    return [];
  },
  querySelector() { return makeEl('q'); },
  addEventListener() {}, createElement() { return makeEl('c'); },
  body: makeEl('body'), documentElement: makeEl('html'), visibilityState: 'visible',
};

const bridgeCalls = [];
const AndroidBridge = {
  calculateZakatNative(payload) {
    const p = JSON.parse(payload);
    const missing = !(p.goldRate10g > 0 && p.silverRate10g > 0);
    return JSON.stringify({
      isFarz: false, statusTitle: missing ? 'BHAV DARJ KAREIN' : 'ZAKAT FARZ NAHI (0)',
      statusReason: 'hinglish reason', statusCode: missing ? 'RATES_MISSING' : 'GOLD_BELOW', netGoldPureG: '80.00',
      goldNisabVal: 1, silverNisabVal: 1, grossZakatable: 0, totalExempt: 0, totalDebt: 0, netZakatable: 0,
      zakatPayable: 0, zakatExact: 0, ratePct: '2.5000', processedJewels: [],
    });
  },
  loadEncryptedState() { return ''; }, saveEncryptedState() { return true; },
  fetchLiveOnlineData(cur) { bridgeCalls.push(cur); },
  getVersion() { return 'test'; }, privacyOptionsRequired() { return false; },
  shareReport(t) { bridgeCalls.push('share:' + t); },
};

const ctx = {
  document, AndroidBridge, console, setTimeout, clearTimeout, performance: { now: () => Date.now() },
  requestAnimationFrame: (f) => setTimeout(() => f(Date.now()), 0),
  localStorage: { getItem() { return null; }, setItem() {} },
  navigator: {}, Date, Math, JSON, Number, String, Array, Object, parseFloat, parseInt, isFinite,
};
ctx.window = ctx; ctx.addEventListener = () => {}; ctx.matchMedia = undefined;
vm.createContext(ctx);

try { vm.runInContext(script, ctx, { filename: 'index.html:inline-script' }); }
catch (e) { fail('App boot threw: ' + e.stack); }

function run(code) { return vm.runInContext(code, ctx); }

// ---------- 1. every language switches without throwing ----------
const maps = run('UI_TRANSLATIONS');
for (const lang of ['urdu', 'hindi', 'hinglish', 'english', 'arabic', 'english']) {
  try {
    run(`selectLang(${JSON.stringify(lang)})`);
    if (run('currentLang') !== lang) fail('currentLang not updated for ' + lang);
    // every data-i18n element must now hold this language's text (or the Hinglish default when a key is missing)
    for (const el of i18nEls) {
      const key = el.getAttribute('data-i18n');
      const expect = Object.prototype.hasOwnProperty.call(maps[lang], key) ? maps[lang][key] : null;
      if (expect !== null && el.textContent !== expect) fail(`${lang}: "${key}" shows "${el.textContent}" instead of "${expect}"`);
    }
    const dock = run('I18N[currentLang].dock');
    if (!dock || dock.length !== 5) fail(`${lang}: dock must have exactly 5 labels (rules, calc, masarif, books, settings)`);
    if (!dock || getEl('dtxt-calc').innerText !== dock[1]) fail(`${lang}: dock label for Calculator wrong`);
  } catch (e) { fail(`Switching to ${lang} threw: ${e.stack}`); }
}

// ---------- 2. English must be English ----------
run("selectLang('english')");
const hinglishLeftovers = [/\bhai\b/i, /\bkaren?\b/i, /\bbhav\b/i, /\bnahi\b/i, /\bkarein\b/i, /\bdaalein\b/i];
const checkEnglish = (label, text) => { for (const re of hinglishLeftovers) if (re.test(text)) fail(`English ${label} still has Hinglish: "${text}"`); };
if (getEl('dtxt-calc').innerText !== 'Calculator') fail('English dock label missing');
checkEnglish('rate status', getEl('rateStatus').innerText);
checkEnglish('rishta list', getEl('masarifFullList').innerHTML);
checkEnglish('books', getEl('booksLibraryContainer').innerHTML);
checkEnglish('hero status', getEl('heroStatusPill').innerText);
for (const code of ['R01', 'R03', 'R04', 'R05', 'R07', 'R13']) {
  run(`showDalil('${code}')`);
  checkEnglish('dalil ' + code, getEl('dalilModalBody').innerText);
}
run("showDalil('R01')");
if (!/^📖 Being Baligh/.test(getEl('dalilModalTitle').innerText)) fail('English dalil title wrong: ' + getEl('dalilModalTitle').innerText);

// engine status text is localised via statusCode
run(`renderResult(${JSON.stringify(JSON.parse(AndroidBridge.calculateZakatNative(JSON.stringify({ goldRate10g: 1, silverRate10g: 1 }))))})`);
checkEnglish('status banner', getEl('billStatusBanner').innerHTML);
if (!/ZAKAT NOT DUE/.test(getEl('heroStatusPill').innerText)) fail('English status pill not localised: ' + getEl('heroStatusPill').innerText);

// ---------- 3. live rates: first and NEW rate both show ----------
// the page auto-fetches once at start-up; let that request finish (offline) before testing taps
run("onLiveOnlineData(" + JSON.stringify(JSON.stringify({ ok: false })) + ")");
bridgeCalls.length = 0;
run("appStore.currency='INR'; fetchOnlineMarketRates(false)");
if (bridgeCalls[0] !== 'INR') fail('Live fetch did not reach the native bridge');
run(`onLiveOnlineData(${JSON.stringify(JSON.stringify({ ok: true, currency: 'INR', gold10g: 150000.4, silver10g: 2000.55 }))})`);
if (getEl('goldRate10g').value !== 150000) fail('First live gold rate not shown: ' + getEl('goldRate10g').value);
if (!/\/tola/.test(getEl('rateStatus').innerText)) fail('Rate status must show per-tola rate: ' + getEl('rateStatus').innerText);
bridgeCalls.length = 0;
run("fetchOnlineMarketRates(false)");
if (bridgeCalls[0] !== 'INR') fail('Second live fetch was blocked (button stuck)');
run(`onLiveOnlineData(${JSON.stringify(JSON.stringify({ ok: true, currency: 'INR', gold10g: 151234.4, silver10g: 2100.1 }))})`);
if (getEl('goldRate10g').value !== 151234) fail('NEW live gold rate did not replace the old one: ' + getEl('goldRate10g').value);
if (getEl('silverRate10g').value !== 2100.1) fail('NEW live silver rate did not replace the old one: ' + getEl('silverRate10g').value);

// stale-currency answer must trigger a fresh request instead of being dropped silently
bridgeCalls.length = 0;
run("fetchOnlineMarketRates(false)");
run("appStore.currency='PKR'");
run(`onLiveOnlineData(${JSON.stringify(JSON.stringify({ ok: true, currency: 'INR', gold10g: 1, silver10g: 1 }))})`);
if (bridgeCalls.filter((c) => c === 'PKR').length !== 1) fail('Stale-currency answer did not re-request in the new currency');
run("onLiveOnlineData(" + JSON.stringify(JSON.stringify({ ok: false })) + ")");

// ---------- 4. no read-aloud leftovers ----------
if (/readAloudParcha|listen_report|stop_voice|settings_voice|onNativeSpeakState|speechSynthesis/.test(html)) fail('Read-aloud (TTS) leftovers still in index.html');

if (errors.length) {
  console.error('UI smoke test FAILED:\n - ' + errors.join('\n - '));
  process.exit(1);
}
console.log('UI smoke test passed: 5 languages switch cleanly, English is English, live rates refresh, no TTS leftovers.');
process.exit(0);
