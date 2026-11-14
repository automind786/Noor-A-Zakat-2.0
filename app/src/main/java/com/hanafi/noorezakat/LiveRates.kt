package com.hanafi.noorezakat

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * International spot rate (gold/silver, USD per troy ounce) + currency exchange rate se
 * 10 gram ka bhav nikalta hai. Ye LOCAL bazar bhav nahi hai (duty/premium shamil nahi).
 */
object LiveRates {

    private const val TROY_OUNCE_G = 31.1034768

    private fun httpGet(url: String): String? {
        var conn: HttpURLConnection? = null
        return try {
            conn = (URL(url).openConnection() as HttpURLConnection).apply {
                connectTimeout = 7000
                readTimeout = 7000
                setRequestProperty("User-Agent", "NoorEZakat/2.1.0")
                setRequestProperty("Accept", "application/json")
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

    fun fetch(currency: String): String {
        val out = JSONObject()
        val fx = usdTo(currency)
        val xau = spotUsdPerOunce("XAU")
        val xag = spotUsdPerOunce("XAG")
        if (fx > 0 && xau > 0 && xag > 0) {
            out.put("ok", true)
            out.put("gold10g", xau / TROY_OUNCE_G * 10.0 * fx)
            out.put("silver10g", xag / TROY_OUNCE_G * 10.0 * fx)
            out.put("currency", currency)
        } else {
            out.put("ok", false)
        }
        return out.toString()
    }
}
