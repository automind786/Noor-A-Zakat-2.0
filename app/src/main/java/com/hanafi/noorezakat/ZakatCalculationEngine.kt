package com.hanafi.noorezakat

import org.json.JSONArray
import org.json.JSONObject
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

object ZakatCalculationEngine {

    private val MC = MathContext.DECIMAL128
    val SHARI_TOLA_G = BigDecimal("11.664")
    val MARKET_TOLA_G = BigDecimal("10.0")
    val GOLD_NISAB_G = BigDecimal("87.48")
    val SILVER_NISAB_G = BigDecimal("612.36")

    val RATE_LUNAR = BigDecimal("0.025") // 2.5% (354 days)
    val RATE_SOLAR_DISPLAY = BigDecimal("0.025775") // 2.5775% (Documented display)
    val RATE_SOLAR_EXACT = BigDecimal("0.025")
        .multiply(BigDecimal("365"), MC)
        .divide(BigDecimal("354"), 10, RoundingMode.HALF_UP) // ~0.0257768362

    private fun bd(v: Any?): BigDecimal {
        return try {
            val d = BigDecimal(v?.toString()?.trim() ?: "0")
            if (d < BigDecimal.ZERO) BigDecimal.ZERO else d
        } catch (e: Exception) {
            BigDecimal.ZERO
        }
    }

    fun calculateZakatJson(payloadJson: String): String {
        val req = JSONObject(payloadJson)
        val ownerStatus = req.optString("ownerStatus", "BALIGH")
        val hawlStatus = req.optString("hawlStatus", "QAMRI_354")
        val solarPolicy = req.optString("solarPolicy", "DISPLAY_2_5775")

        val goldRate10g = bd(req.opt("goldRate10g"))
        val silverRate10g = bd(req.opt("silverRate10g"))
        val goldRate1g = goldRate10g.divide(BigDecimal.TEN, 6, RoundingMode.HALF_UP)
        val silverRate1g = silverRate10g.divide(BigDecimal.TEN, 6, RoundingMode.HALF_UP)

        val goldNisabVal = GOLD_NISAB_G.multiply(goldRate1g, MC)
        val silverNisabVal = SILVER_NISAB_G.multiply(silverRate1g, MC)

        var totalGoldPureG = BigDecimal.ZERO
        var totalGoldVal = BigDecimal.ZERO
        var totalSilverVal = BigDecimal.ZERO
        var totalOtherZakatable = BigDecimal.ZERO
        var totalExemptVal = BigDecimal.ZERO
        var totalDebtVal = BigDecimal.ZERO

        val jewelsArr = req.optJSONArray("jewels") ?: JSONArray()
        val processedJewels = JSONArray()

        for (i in 0 until jewelsArr.length()) {
            val j = jewelsArr.getJSONObject(i)
            val metal = j.optString("metal", "GOLD")
            val unit = j.optString("unit", "GRAMS")
            val rawW = bd(j.opt("weight"))
            val grossG = when (unit) {
                "MARKET_TOLA" -> rawW.multiply(MARKET_TOLA_G, MC)
                "SHARI_TOLA" -> rawW.multiply(SHARI_TOLA_G, MC)
                else -> rawW
            }
            // Prevent stoneG > grossG exploit
            val rawStoneG = bd(j.opt("stoneG"))
            val validStoneG = if (rawStoneG > grossG) grossG else rawStoneG
            val netG = grossG.subtract(validStoneG).max(BigDecimal.ZERO)

            val carat = bd(j.opt("carat")).let { if (it <= BigDecimal.ZERO || it > BigDecimal("24")) BigDecimal("24") else it }
            val pureG = if (metal == "DIAMOND_EXEMPT") BigDecimal.ZERO
            else netG.multiply(carat, MC).divide(BigDecimal("24"), 6, RoundingMode.HALF_UP)

            val itemVal = when (metal) {
                "GOLD" -> pureG.multiply(goldRate1g, MC).also {
                    totalGoldPureG = totalGoldPureG.add(pureG)
                    totalGoldVal = totalGoldVal.add(it)
                }
                "SILVER" -> pureG.multiply(silverRate1g, MC).also {
                    totalSilverVal = totalSilverVal.add(it)
                }
                else -> BigDecimal.ZERO
            }

            processedJewels.put(JSONObject().apply {
                put("id", j.optLong("id"))
                put("name", j.optString("name", "Zewar"))
                put("metal", metal)
                put("grossG", grossG.setScale(2, RoundingMode.HALF_UP).toDouble())
                put("stoneG", validStoneG.setScale(2, RoundingMode.HALF_UP).toDouble())
                put("pureG", pureG.setScale(3, RoundingMode.HALF_UP).toDouble())
                put("carat", carat.toInt())
                put("value", itemVal.setScale(2, RoundingMode.HALF_UP).toDouble())
            })
        }

        val zakatableTypes = setOf("CASH_BANK", "BC_SAVED", "SHOP_STOCK", "RAW_MATERIAL", "PLOT_TRADING", "UDHAR_QAWI")
        val assetsArr = req.optJSONArray("assets") ?: JSONArray()
        for (i in 0 until assetsArr.length()) {
            val a = assetsArr.getJSONObject(i)
            val amt = bd(a.opt("amount"))
            val type = a.optString("type", "CASH_BANK")
            val sharePct = bd(a.opt("ownershipPct")).let { if (it <= BigDecimal.ZERO || it > BigDecimal("100")) BigDecimal("100") else it }
            val effectiveAmt = amt.multiply(sharePct, MC).divide(BigDecimal("100"), 2, RoundingMode.HALF_UP)

            if (zakatableTypes.contains(type)) {
                totalOtherZakatable = totalOtherZakatable.add(effectiveAmt)
            } else {
                totalExemptVal = totalExemptVal.add(effectiveAmt)
            }
        }

        val debtsArr = req.optJSONArray("debts") ?: JSONArray()
        for (i in 0 until debtsArr.length()) {
            val d = debtsArr.getJSONObject(i)
            val amt = bd(d.opt("amount"))
            val type = d.optString("type", "SHORT_TERM")
            val deduct = when (type) {
                "LONG_EMI" -> amt.multiply(BigDecimal("12"), MC)
                "EXEMPT_PROPERTY_LOAN" -> BigDecimal.ZERO
                else -> amt
            }
            totalDebtVal = totalDebtVal.add(deduct)
        }

        // Istighraq Debt Priority: 1. Cash/Business -> 2. Silver -> 3. Gold
        var remDebt = totalDebtVal
        val netOther = totalOtherZakatable.subtract(remDebt).max(BigDecimal.ZERO)
        remDebt = remDebt.subtract(totalOtherZakatable).max(BigDecimal.ZERO)

        val netSilver = totalSilverVal.subtract(remDebt).max(BigDecimal.ZERO)
        remDebt = remDebt.subtract(totalSilverVal).max(BigDecimal.ZERO)

        val netGold = totalGoldVal.subtract(remDebt).max(BigDecimal.ZERO)
        val netGoldPureG = if (goldRate1g > BigDecimal.ZERO) {
            netGold.divide(goldRate1g, 6, RoundingMode.HALF_UP)
        } else BigDecimal.ZERO

        val grossZakatable = totalGoldVal.add(totalSilverVal).add(totalOtherZakatable)
        val netZakatable = netGold.add(netSilver).add(netOther)

        val activeRate = when {
            hawlStatus == "SHAMSI_365" && solarPolicy == "EXACT_FORMULA" -> RATE_SOLAR_EXACT
            hawlStatus == "SHAMSI_365" -> RATE_SOLAR_DISPLAY
            else -> RATE_LUNAR
        }

        var isFarz = false
        var zakatPayable = BigDecimal.ZERO
        var statusTitle = "ZAKAT FARZ NAHI (0)"
        var statusReason = ""

        val ratesMissing = goldRate1g <= BigDecimal.ZERO || silverRate1g <= BigDecimal.ZERO

        if (ratesMissing) {
            statusTitle = "BHAV DARJ KAREIN"
            statusReason = "Sahi hisab ke liye Sone aur Chandi ka aaj ka bazaru bhav darj karein."
        } else if (ownerStatus == "NABALIGH") {
            statusReason = "Fiqh-e-Hanafi mein Nabaligh bache ke maal par Zakat farz nahi hoti (Rule R01)."
        } else if (hawlStatus == "INCOMPLETE") {
            statusTitle = "ABHI 1 SAAL POORA NAHI HUA"
            statusReason = "Nisab ke maal par 1 Qamri saal (354 din) guzarna shart hai (Rule R02)."
        } else if (netZakatable <= BigDecimal.ZERO) {
            statusReason = "Wajib qarz ghatane ke baad koi Zakatable bachat nahi bachti."
        } else if (netGoldPureG > BigDecimal.ZERO && netSilver == BigDecimal.ZERO && netOther == BigDecimal.ZERO) {
            if (netGoldPureG >= GOLD_NISAB_G) {
                isFarz = true
                zakatPayable = netGold.multiply(activeRate, MC)
                statusTitle = "ZAKAT FARZ HAI"
                statusReason = "Khalis Sona (${netGoldPureG.setScale(2, RoundingMode.HALF_UP)}g) 7.5 Shar'i Tola (87.48g) Nisab se zyada hai."
            } else {
                statusReason = "Sirf Sona hone par 87.48g shart hai; aapka khalis sona ${netGoldPureG.setScale(2, RoundingMode.HALF_UP)}g hai."
            }
        } else {
            if (netZakatable >= silverNisabVal) {
                isFarz = true
                zakatPayable = netZakatable.multiply(activeRate, MC)
                statusTitle = "ZAKAT FARZ HAI"
                statusReason = "Mila-jula khalis maal 52.5 Shar'i Tola Chandi ke Nisab se zyada hai (Rule R04)."
            } else {
                statusReason = "Aapka khalis maal Chandi ke Nisab se kam hai."
            }
        }

        return JSONObject().apply {
            put("isFarz", isFarz)
            put("statusTitle", statusTitle)
            put("statusReason", statusReason)
            put("goldNisabVal", goldNisabVal.setScale(0, RoundingMode.HALF_UP).toLong())
            put("silverNisabVal", silverNisabVal.setScale(0, RoundingMode.HALF_UP).toLong())
            put("grossZakatable", grossZakatable.setScale(0, RoundingMode.HALF_UP).toLong())
            put("totalExempt", totalExemptVal.setScale(0, RoundingMode.HALF_UP).toLong())
            put("totalDebt", totalDebtVal.setScale(0, RoundingMode.HALF_UP).toLong())
            put("netZakatable", netZakatable.setScale(0, RoundingMode.HALF_UP).toLong())
            put("zakatPayable", zakatPayable.setScale(0, RoundingMode.HALF_UP).toLong())
            put("zakatExact", zakatPayable.setScale(2, RoundingMode.HALF_UP).toDouble())
            put("ratePct", activeRate.multiply(BigDecimal("100"), MC).setScale(4, RoundingMode.HALF_UP).toPlainString())
            put("processedJewels", processedJewels)
        }.toString()
    }
}
