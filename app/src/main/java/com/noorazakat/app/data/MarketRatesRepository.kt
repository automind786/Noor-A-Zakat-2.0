package com.noorazakat.app.data

import org.json.JSONObject
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.HttpURLConnection
import java.net.URL
import java.time.Instant

/**
 * International spot-price estimate converted to INR per gram.
 * It is NOT a local jeweller quotation: local premium, purity, tax and regional market
 * differences are not represented. Users must verify the market value at their Hawl date.
 */
data class PreciousMetalSpotRates(
    val gold24kInrPerGram: BigDecimal,
    val silverInrPerGram: BigDecimal,
    val currency: String,
    val updatedAt: Instant,
    val sourceNote: String
)

object MarketRatesRepository {
    private const val TROY_OUNCE_GRAMS = 31.1034768

    fun fetchIndiaSpotEstimate(): PreciousMetalSpotRates {
        val goldUsdPerOunce = fetchMetalUsd("XAU")
        val silverUsdPerOunce = fetchMetalUsd("XAG")
        val usdInr = fetchUsdInr()
        val divisor = BigDecimal.valueOf(TROY_OUNCE_GRAMS)
        val fx = BigDecimal.valueOf(usdInr)

        val gold = BigDecimal.valueOf(goldUsdPerOunce).multiply(fx)
            .divide(divisor, 4, RoundingMode.HALF_UP)
        val silver = BigDecimal.valueOf(silverUsdPerOunce).multiply(fx)
            .divide(divisor, 4, RoundingMode.HALF_UP)
        require(gold > BigDecimal.ZERO && silver > BigDecimal.ZERO) { "Market service returned an invalid rate" }

        return PreciousMetalSpotRates(
            gold24kInrPerGram = gold,
            silverInrPerGram = silver,
            currency = "INR",
            updatedAt = Instant.now(),
            sourceNote = "International spot estimate converted using USD/INR; not a local retail quote."
        )
    }

    private fun fetchMetalUsd(symbol: String): Double {
        val json = JSONObject(httpGet("https://api.gold-api.com/price/$symbol"))
        return json.optDouble("price", 0.0).also { require(it.isFinite() && it > 0) { "Invalid $symbol spot price" } }
    }

    private fun fetchUsdInr(): Double {
        val json = JSONObject(httpGet("https://open.er-api.com/v6/latest/USD"))
        val rates = json.optJSONObject("rates") ?: error("Currency service did not return rates")
        return rates.optDouble("INR", 0.0).also { require(it.isFinite() && it > 0) { "Invalid USD/INR exchange rate" } }
    }

    private fun httpGet(address: String): String {
        val connection = (URL(address).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 8_000
            readTimeout = 8_000
            setRequestProperty("Accept", "application/json")
            setRequestProperty("User-Agent", "Noor-A-Zakat-Android/0.2")
        }
        try {
            val status = connection.responseCode
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            val body = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            require(status in 200..299) { "Market service returned HTTP $status" }
            require(body.isNotBlank()) { "Market service returned an empty response" }
            return body
        } finally {
            connection.disconnect()
        }
    }
}
