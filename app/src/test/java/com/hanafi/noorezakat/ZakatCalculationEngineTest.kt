package com.hanafi.noorezakat

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ZakatCalculationEngineTest {

    private fun calc(json: String) = JSONObject(ZakatCalculationEngine.calculateZakatJson(json))

    private fun gold(weight: Int) =
        """{"metal":"GOLD","unit":"GRAMS","weight":$weight,"carat":24,"stoneG":0}"""

    @Test fun nabaligh_hasNoZakat() {
        val r = calc("""{"ownerStatus":"NABALIGH","goldRate10g":100000,"silverRate10g":1000,"jewels":[${gold(500)}]}""")
        assertFalse(r.getBoolean("isFarz"))
        assertEquals(0L, r.getLong("zakatPayable"))
    }

    @Test fun goldBelowNisab_noZakat() {
        val r = calc("""{"goldRate10g":100000,"silverRate10g":1000,"jewels":[${gold(80)}]}""")
        assertFalse(r.getBoolean("isFarz"))
    }

    @Test fun goldAboveNisab_2point5percent() {
        val r = calc("""{"goldRate10g":100000,"silverRate10g":1000,"jewels":[${gold(100)}]}""")
        assertTrue(r.getBoolean("isFarz"))
        assertEquals(25000L, r.getLong("zakatPayable"))
    }

    @Test fun mixedWealth_usesSilverNisab() {
        val r = calc(
            """{"goldRate10g":100000,"silverRate10g":1000,
               "jewels":[${gold(50)}],
               "assets":[{"type":"CASH_BANK","amount":100000}]}"""
        )
        assertTrue(r.getBoolean("isFarz"))
        assertEquals(15000L, r.getLong("zakatPayable")) // (500000 + 100000) * 2.5%
    }

    @Test fun debtCancelsCash() {
        val r = calc(
            """{"goldRate10g":100000,"silverRate10g":1000,
               "assets":[{"type":"CASH_BANK","amount":100000}],
               "debts":[{"type":"SHORT_TERM","amount":100000}]}"""
        )
        assertFalse(r.getBoolean("isFarz"))
        assertEquals(0L, r.getLong("netZakatable"))
    }

    @Test fun debtPriority_cashThenGold() {
        val r = calc(
            """{"goldRate10g":100000,"silverRate10g":1000,
               "jewels":[${gold(100)}],
               "assets":[{"type":"CASH_BANK","amount":50000}],
               "debts":[{"type":"SHORT_TERM","amount":60000}]}"""
        )
        assertTrue(r.getBoolean("isFarz"))
        assertEquals(24750L, r.getLong("zakatPayable")) // 990000 * 2.5%
    }

    @Test fun missingRates_askUser() {
        val r = calc("""{"goldRate10g":0,"silverRate10g":0,"jewels":[${gold(100)}]}""")
        assertFalse(r.getBoolean("isFarz"))
        assertEquals("BHAV DARJ KAREIN", r.getString("statusTitle"))
    }


    @Test fun rawMaterial_isZakatableBusinessInventory() {
        val r = calc(
            """{"goldRate10g":100000,"silverRate10g":1000,
               "assets":[{"type":"RAW_MATERIAL","amount":100000}]}"""
        )
        assertTrue(r.getBoolean("isFarz"))
        assertEquals(2500L, r.getLong("zakatPayable"))
    }

    @Test fun ownershipPercentage_limitsSharedAssetToUserShare() {
        val r = calc(
            """{"goldRate10g":100000,"silverRate10g":1000,
               "assets":[{"type":"CASH_BANK","amount":150000,"ownershipPct":50}]}"""
        )
        assertTrue(r.getBoolean("isFarz"))
        assertEquals(75000L, r.getLong("netZakatable"))
        assertEquals(1875L, r.getLong("zakatPayable"))
    }

    @Test fun strongReceivable_isIncludedAsZakatable() {
        val r = calc(
            """{"goldRate10g":100000,"silverRate10g":1000,
               "assets":[{"type":"UDHAR_QAWI","amount":100000}]}"""
        )
        assertTrue(r.getBoolean("isFarz"))
        assertEquals(2500L, r.getLong("zakatPayable"))
    }

    @Test fun exemptAssets_notCounted() {
        val r = calc(
            """{"goldRate10g":100000,"silverRate10g":1000,
               "assets":[{"type":"HOME_USE","amount":9000000}]}"""
        )
        assertFalse(r.getBoolean("isFarz"))
        assertEquals(9000000L, r.getLong("totalExempt"))
    }
}
