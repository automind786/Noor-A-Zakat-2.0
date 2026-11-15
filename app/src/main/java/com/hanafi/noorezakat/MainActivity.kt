package com.hanafi.noorezakat

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.provider.CalendarContract
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.webkit.WebViewAssetLoader
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import org.json.JSONObject
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    companion object {
        private const val APP_HOST = "appassets.androidplatform.net"
        private const val STATE_BG = "#060A0E"
    }

    private lateinit var webView: WebView
    private lateinit var adContainer: FrameLayout
    private lateinit var noorDb: NoorDatabase
    private lateinit var consentInformation: ConsentInformation

    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var adView: AdView? = null
    private val adsStarted = AtomicBoolean(false)
    private val speechSequence = AtomicLong(0)
    private val io = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        // Let Android fit the content below the status bar/cutout automatically.
        // Do not add manual inset padding to the WebView, otherwise the content can
        // be double-offset on devices with a camera cutout or gesture navigation.
        WindowCompat.setDecorFitsSystemWindows(window, true)
        setContentView(R.layout.activity_main)

        val root = findViewById<View>(R.id.root)
        webView = findViewById(R.id.webView)
        adContainer = findViewById(R.id.adContainer)

        WindowCompat.getInsetsController(window, root).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }

        noorDb = NoorDatabase(this)
        tts = TextToSpeech(this, this)

        setupWebView()
        setupBackHandling()
        startAdsFlow()
    }

    // ------------------------------------------------------------------ WebView

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        webView.setBackgroundColor(Color.parseColor(STATE_BG))

        val assetLoader = WebViewAssetLoader.Builder()
            .setDomain(APP_HOST)
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        with(webView.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = false
            allowContentAccess = false
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
        }

        webView.webChromeClient = WebChromeClient()
        webView.addJavascriptInterface(SmartBridge(), "AndroidBridge")
        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? = assetLoader.shouldInterceptRequest(request.url)

            // App ke bahar ke har link ko browser mein kholo (app WebView mein phans na jaye)
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                if (request.url.host == APP_HOST) return false
                openExternal(request.url)
                return true
            }
        }
        webView.loadUrl("https://$APP_HOST/assets/index.html")
    }

    private fun openExternal(uri: Uri) {
        val scheme = uri.scheme ?: return
        if (scheme != "https" && scheme != "http" && scheme != "market" && scheme != "mailto") return
        try {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (e: ActivityNotFoundException) {
            // koi app nahi mili - chup-chaap ignore
        }
    }

    private fun setupBackHandling() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                webView.evaluateJavascript(
                    "(window.handleAndroidBack ? window.handleAndroidBack() : false)"
                ) { res ->
                    if (res != "true") {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        })
    }

    // ------------------------------------------------------------------ Ads (AdMob + UMP consent)

    private fun startAdsFlow() {
        consentInformation = UserMessagingPlatform.getConsentInformation(this)
        val params = ConsentRequestParameters.Builder().build()
        consentInformation.requestConsentInfoUpdate(
            this,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(this) {
                    if (consentInformation.canRequestAds()) initAds()
                }
            },
            {
                if (consentInformation.canRequestAds()) initAds()
            }
        )
        // Pehle se consent mil chuka ho to foran shuru
        if (consentInformation.canRequestAds()) initAds()
    }

    private fun initAds() {
        if (!adsStarted.compareAndSet(false, true)) return
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                // Dini app hai: sirf family-friendly (G) ads
                .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
                .build()
        )
        MobileAds.initialize(this) { runOnUiThread { loadBanner() } }
    }

    private fun loadBanner() {
        if (isFinishing || isDestroyed) return
        val dm = resources.displayMetrics
        val widthDp = (dm.widthPixels / dm.density).toInt()
        val view = AdView(this).apply {
            adUnitId = BuildConfig.ADMOB_BANNER_ID
            setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this@MainActivity, widthDp))
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    adContainer.visibility = View.VISIBLE
                }
            }
        }
        adContainer.removeAllViews()
        adContainer.addView(view)
        adView = view
        view.loadAd(AdRequest.Builder().build())
    }

    // ------------------------------------------------------------------ Text-to-speech

    override fun onInit(status: Int) {
        ttsReady = (status == TextToSpeech.SUCCESS)
        if (ttsReady) {
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    callJs("if(window.onNativeSpeakState) window.onNativeSpeakState(true);")
                }

                override fun onDone(utteranceId: String?) {
                    callJs("if(window.onNativeSpeakState) window.onNativeSpeakState(false);")
                }

                override fun onError(utteranceId: String?) {
                    callJs("if(window.onNativeSpeakState) window.onNativeSpeakState(false);")
                }

                override fun onStop(utteranceId: String?, interrupted: Boolean) {
                    callJs("if(window.onNativeSpeakState) window.onNativeSpeakState(false);")
                }
            })
        }
    }

    private fun callJs(script: String) {
        runOnUiThread {
            if (!isFinishing && !isDestroyed) webView.evaluateJavascript(script, null)
        }
    }

    // ------------------------------------------------------------------ JS Bridge

    inner class SmartBridge {

        @JavascriptInterface
        fun calculateZakatNative(payloadJson: String): String {
            return try {
                ZakatCalculationEngine.calculateZakatJson(payloadJson)
            } catch (e: Exception) {
                JSONObject().put("error", e.message ?: "calc_error").toString()
            }
        }

        @JavascriptInterface
        fun saveEncryptedState(key: String, plainJson: String): Boolean {
            val enc = NoorSecurityManager.encryptPayload(plainJson) ?: return false
            return noorDb.saveState(key, enc)
        }

        @JavascriptInterface
        fun loadEncryptedState(key: String): String {
            val raw = noorDb.loadState(key) ?: return ""
            return NoorSecurityManager.decryptPayload(raw) ?: ""
        }

        /** Live international spot bhav; natija window.onLiveOnlineData(json) mein aata hai. */
        @JavascriptInterface
        fun fetchLiveOnlineData(currency: String) {
            io.execute {
                val json = LiveRates.fetch(currency)
                callJs("if(window.onLiveOnlineData) window.onLiveOnlineData(${JSONObject.quote(json)});")
            }
        }
        @JavascriptInterface
        fun fetchLiveHijri(dateIso: String, calendarMethod: String) {
            io.execute {
                try {
                    val date = java.time.LocalDate.parse(dateIso)
                    val day = HijriSync.fetchDay(date, calendarMethod.ifBlank { "UAQ" })
                    val json = if (day == null) {
                        JSONObject().put("ok", false).toString()
                    } else {
                        JSONObject()
                            .put("ok", true)
                            .put("gregorian", day.gregorian)
                            .put("hijriDay", day.hijriDay)
                            .put("hijriMonth", day.hijriMonth)
                            .put("hijriMonthName", day.hijriMonthName)
                            .put("hijriYear", day.hijriYear)
                            .put("holidays", day.holidays.joinToString("|"))
                            .put("method", day.method)
                            .toString()
                    }
                    callJs("if(window.onLiveHijriData) window.onLiveHijriData(${JSONObject.quote(json)});")
                } catch (_: Exception) {
                    callJs("if(window.onLiveHijriData) window.onLiveHijriData(${JSONObject.quote(JSONObject().put("ok", false).toString())});")
                }
            }
        }

        @JavascriptInterface
        fun refreshHijriWidget() {
            io.execute { HijriWidgetProvider.updateAll(this@MainActivity) }
        }

        @JavascriptInterface
        fun speakText(rawText: String, langCode: String) {
            if (!ttsReady) {
                callJs("if(window.onTtsUnavailable) window.onTtsUnavailable();")
                return
            }
            val clean = rawText.replace("₹", " rupaye ")
                .replace(Regex("[^\\p{L}\\p{N}\\p{P}\\s]"), " ")
                .trim()
            val locale = when (langCode) {
                "urdu" -> Locale("ur", "PK")
                "english" -> Locale.US
                "arabic" -> Locale("ar", "SA")
                else -> Locale("hi", "IN")
            }
            runOnUiThread {
                try {
                    val engine = tts ?: return@runOnUiThread
                    if (engine.isLanguageAvailable(locale) < TextToSpeech.LANG_AVAILABLE) {
                        callJs("if(window.onTtsUnavailable) window.onTtsUnavailable();")
                    } else {
                        engine.language = locale
                        // Android exposes no standard voice-gender property. Prefer an installed
                        // same-language voice whose provider name explicitly contains "female".
                        // If none exists, keep the user's system-selected voice (never force male).
                        val femaleVoice = engine.voices
                            ?.filter { it.locale.language == locale.language }
                            ?.firstOrNull { voice ->
                                val voiceName = voice.name.lowercase(Locale.ROOT)
                                voiceName.contains("female") || voiceName.contains("woman")
                            }
                        if (femaleVoice != null) engine.voice = femaleVoice
                        engine.setSpeechRate(0.94f)
                        engine.setPitch(1.04f)
                        val utteranceId = "NOOR_TTS_${speechSequence.incrementAndGet()}"
                        val result = engine.speak(
                            clean.take(1200),
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            utteranceId
                        )
                        if (result == TextToSpeech.ERROR) {
                            callJs("if(window.onNativeSpeakState) window.onNativeSpeakState(false); if(window.onTtsUnavailable) window.onTtsUnavailable();")
                        }
                    }
                } catch (e: Exception) {
                    callJs("if(window.onTtsUnavailable) window.onTtsUnavailable();")
                }
            }
        }

        @JavascriptInterface
        fun stopSpeaking() {
            runOnUiThread {
                tts?.stop()
                callJs("if(window.onNativeSpeakState) window.onNativeSpeakState(false);")
            }
        }

        /** Opens Android's TTS settings so the user can select an installed preferred voice. */
        @JavascriptInterface
        fun openSpeechSettings() {
            runOnUiThread {
                try {
                    startActivity(Intent("com.android.settings.TTS_SETTINGS"))
                } catch (e: ActivityNotFoundException) {
                    try { startActivity(Intent(Settings.ACTION_SETTINGS)) } catch (_: Exception) { }
                }
            }
        }

        /** Add a saved in-app event to the phone calendar through Android's standard insert UI. */
        @JavascriptInterface
        fun openSystemCalendarEvent(title: String, dateIso: String, description: String) {
            val parts = dateIso.split("-")
            if (parts.size != 3) return
            val startTime = try {
                Calendar.getInstance().apply {
                    set(Calendar.YEAR, parts[0].toInt())
                    set(Calendar.MONTH, parts[1].toInt() - 1)
                    set(Calendar.DAY_OF_MONTH, parts[2].toInt())
                    set(Calendar.HOUR_OF_DAY, 9)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
            } catch (_: Exception) { return }
            val endTime = startTime + 60L * 60L * 1000L
            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, title.take(80))
                putExtra(CalendarContract.Events.DESCRIPTION, description.take(240))
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime)
            }
            runOnUiThread {
                try {
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    callJs("if(window.toast) window.toast('Phone calendar app nahi mili.');")
                }
            }
        }

        @JavascriptInterface
        fun printBill() {
            runOnUiThread {
                try {
                    val pm = getSystemService(Context.PRINT_SERVICE) as PrintManager
                    val adapter = webView.createPrintDocumentAdapter("Noor_A_Zakat_Parcha")
                    pm.print("Noor-A-Zakat Report", adapter, PrintAttributes.Builder().build())
                } catch (e: Exception) {
                    // print service na ho to ignore
                }
            }
        }

        @JavascriptInterface
        fun shareReport(reportText: String) {
            runOnUiThread {
                try {
                    val send = Intent(Intent.ACTION_SEND).apply {
                        putExtra(Intent.EXTRA_TEXT, reportText)
                        type = "text/plain"
                    }
                    startActivity(Intent.createChooser(send, "Share Zakat Parcha"))
                } catch (e: Exception) {
                }
            }
        }

        @JavascriptInterface
        fun shareApp() {
            runOnUiThread {
                try {
                    val link = "https://play.google.com/store/apps/details?id=$packageName"
                    val send = Intent(Intent.ACTION_SEND).apply {
                        putExtra(Intent.EXTRA_TEXT, "Noor-A-Zakat — Zakat ka aasan aur sahi hisab: $link")
                        type = "text/plain"
                    }
                    startActivity(Intent.createChooser(send, "Share App"))
                } catch (e: Exception) {
                }
            }
        }

        @JavascriptInterface
        fun rateApp() {
            runOnUiThread {
                openExternal(Uri.parse("market://details?id=$packageName"))
            }
        }

        @JavascriptInterface
        fun openExternalUrl(url: String) {
            runOnUiThread { openExternal(Uri.parse(url)) }
        }

        @JavascriptInterface
        fun getVersion(): String = BuildConfig.VERSION_NAME

        /** GDPR wale ilaqon mein user ko ads-consent dobara badalne ka option dena zaruri hai. */
        @JavascriptInterface
        fun privacyOptionsRequired(): Boolean {
            return this@MainActivity::consentInformation.isInitialized &&
                consentInformation.privacyOptionsRequirementStatus ==
                ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
        }

        @JavascriptInterface
        fun showPrivacyOptions() {
            runOnUiThread {
                UserMessagingPlatform.showPrivacyOptionsForm(this@MainActivity) { }
            }
        }
    }

    // ------------------------------------------------------------------ Lifecycle

    override fun onPause() {
        // Stop read-aloud when the app leaves the foreground.
        try { tts?.stop() } catch (_: Exception) { }
        callJs("if(window.onNativeSpeakState) window.onNativeSpeakState(false);")
        adView?.pause()
        webView.onPause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
        adView?.resume()
    }

    override fun onDestroy() {
        try {
            io.shutdownNow()
            tts?.stop()
            tts?.shutdown()
            adView?.destroy()
            noorDb.close()
            webView.destroy()
        } catch (e: Exception) {
        }
        super.onDestroy()
    }
}
