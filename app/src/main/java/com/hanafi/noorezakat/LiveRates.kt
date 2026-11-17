package com.hanafi.noorezakat

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/**
 * International spot rate (gold/silver, USD per troy ounce) + currency exchange rate se
 * 10 gram ka bhav nikalta hai. Ye LOCAL bazar bhav nahi hai (duty/premium shamil nahi).
 *
 * Har baar taza (no-cache) data mangta hai, teeno requests ek saath chalti hain, aur agar
 * primary source fail ho to doosra (fallback) source try hota hai.
 */
object LiveRates {

    private const val TROY_OUNCE_G = 31.1034768
    private const val TIMEOUT_MS = 8000
    private const val FALLBACK_URL =
        "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/usd.json"

    private fun httpGet(url: String): String? {
        var conn: HttpURLConnection? = null
        return try {
            conn = (URL(url).openConnection() as HttpURLConnection).apply {
                connectTimeout = TIMEOUT_MS
                readTimeout = TIMEOUT_MS
                useCaches = false
                defaultUseCaches = false
                setRequestProperty("User-Agent", "NoorEZakat/${BuildConfig.VERSION_NAME}")
                setRequestProperty("Accept", "application/json")
                setRequestProperty("Cache-Control", "no-cache, no-store, max-age=0")
                setRequestProperty("Pragma", "no-cache")
            }
            if (conn.responseCode == 200) conn.inputStream.bufferedReader().use { it.readText() } else null
        } catch (e: Exception) {
            null
        } finally {
            conn?.disconnect()
        }
    }

    private fun spotUsdPerOunce(symbol: String): Double {
        val body = httpGet("https://api.gold-api.com/price/$symbol") ?: return 0.0
        return try { JSONObject(body).optDouble("price", 0.0) } catch (e: Exception) { 0.0 }
    }

    private fun usdTo(currency: String): Double {
        if (currency == "USD") return 1.0
        val body = httpGet("https://open.er-api.com/v6/latest/USD") ?: return 0.0
        return try { JSONObject(body).getJSONObject("rates").optDouble(currency, 0.0) } catch (e: Exception) { 0.0 }
    }

    /** Fallback: ek hi file se USD->currency, XAU aur XAG (dono "1 USD = x ounce" ki shakl mein). */
    private class Fallback(val fx: Double, val xauUsd: Double, val xagUsd: Double)

    private fun fallback(currency: String): Fallback? {
        val body = httpGet(FALLBACK_URL) ?: return null
        return try {
            val usd = JSONObject(body).getJSONObject("usd")
            val fx = if (currency == "USD") 1.0 else usd.optDouble(currency.lowercase(), 0.0)
            val xauPerUsd = usd.optDouble("xau", 0.0)
            val xagPerUsd = usd.optDouble("xag", 0.0)
            Fallback(
                fx,
                if (xauPerUsd > 0) 1.0 / xauPerUsd else 0.0,
                if (xagPerUsd > 0) 1.0 / xagPerUsd else 0.0
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun await(f: Future<Double>): Double =
        try { f.get(TIMEOUT_MS * 2L, TimeUnit.MILLISECONDS) } catch (e: Exception) { 0.0 }

    // Be-matlab (ghalat) jawab ko pakadne ke liye mauqool hadd.
    private fun sane(fx: Double, xau: Double, xag: Double): Boolean =
        fx > 0.0 && xau in 300.0..50000.0 && xag in 3.0..2000.0

    fun fetch(currency: String): String {
        val cur = currency.trim().uppercase()
        val out = JSONObject()
        val pool = Executors.newFixedThreadPool(3)
        try {
            val fxF = pool.submit(Callable { usdTo(cur) })
            val xauF = pool.submit(Callable { spotUsdPerOunce("XAU") })
            val xagF = pool.submit(Callable { spotUsdPerOunce("XAG") })
            var fx = await(fxF)
            var xau = await(xauF)
            var xag = await(xagF)
            var source = "gold-api.com + open.er-api.com"

            if (!sane(fx, xau, xag)) {
                val fb = fallback(cur)
                if (fb != null) {
                    if (fx <= 0.0) fx = fb.fx
                    if (xau !in 300.0..50000.0) xau = fb.xauUsd
                    if (xag !in 3.0..2000.0) xag = fb.xagUsd
                    source = "fallback (currency-api)"
                }
            }

            if (sane(fx, xau, xag)) {
                out.put("ok", true)
                out.put("currency", cur)
                out.put("gold10g", xau / TROY_OUNCE_G * 10.0 * fx)
                out.put("silver10g", xag / TROY_OUNCE_G * 10.0 * fx)
                out.put("goldUsdPerOz", xau)
                out.put("silverUsdPerOz", xag)
                out.put("fetchedAt", System.currentTimeMillis())
                out.put("source", source)
            } else {
                out.put("ok", false)
            }
        } catch (e: Exception) {
            out.put("ok", false)
        } finally {
            pool.shutdownNow()
        }
        return out.toString()
    }
}
