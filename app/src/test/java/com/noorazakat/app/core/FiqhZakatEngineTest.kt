package com.noorazakat.app.core

import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FiqhZakatEngineTest {
    private val goldRate = BigDecimal("7000")

    @Test
    fun eightTolaGoldOnlyUsesOnlyNisabBecauseSurplusIsBelowOneFifth() {
        val eightTola = BigDecimal("11.664").multiply(BigDecimal("8"))
        val result = FiqhZakatEngine.calculate(
            ZakatCalculationInput(
                hawlComplete = true,
                goldPureGrams = eightTola,
                goldRatePerGram = goldRate
            )
        )
        assertEquals(ZakatDecisionStatus.INDICATIVE_ESTIMATE, result.status)
        assertEquals(BigDecimal("612360.00"), result.zakatableBase)
        assertEquals(BigDecimal("15309.00"), result.zakatAmount)
    }

    @Test
    fun nineTolaGoldOnlyIncludesOneCompleteKhumsIncrement() {
        val nineTola = BigDecimal("11.664").multiply(BigDecimal("9"))
        val result = FiqhZakatEngine.calculate(
            ZakatCalculationInput(goldPureGrams = nineTola, goldRatePerGram = goldRate)
        )
        assertEquals(BigDecimal("734832.00"), result.zakatableBase)
        assertEquals(BigDecimal("18370.80"), result.zakatAmount)
    }

    @Test
    fun tenTolaGoldOnlyStillIncludesNineTola() {
        val tenTola = BigDecimal("11.664").multiply(BigDecimal("10"))
        val result = FiqhZakatEngine.calculate(
            ZakatCalculationInput(goldPureGrams = tenTola, goldRatePerGram = goldRate)
        )
        assertEquals(BigDecimal("734832.00"), result.zakatableBase)
    }

    @Test
    fun tenAndHalfTolaGoldOnlyIncludesFullTenAndHalf() {
        val weight = BigDecimal("11.664").multiply(BigDecimal("10.5"))
        val result = FiqhZakatEngine.calculate(
            ZakatCalculationInput(goldPureGrams = weight, goldRatePerGram = goldRate)
        )
        assertEquals(BigDecimal("857304.00"), result.zakatableBase)
    }

    @Test
    fun mixedGoldAndCashRequiresMuftiReviewInsteadOfGuessing() {
        val result = FiqhZakatEngine.calculate(
            ZakatCalculationInput(
                goldPureGrams = BigDecimal("50"), goldRatePerGram = goldRate,
                cash = BigDecimal("100000"), silverRatePerGram = BigDecimal("100")
            )
        )
        assertEquals(ZakatDecisionStatus.MUFTI_REVIEW_REQUIRED, result.status)
        assertNull(result.zakatAmount)
    }

    @Test
    fun categoriesIndividuallyBelowNisabCanBeCombinedForIndicativeEstimate() {
        val result = FiqhZakatEngine.calculate(
            ZakatCalculationInput(
                goldPureGrams = BigDecimal("50"), goldRatePerGram = goldRate,
                silverPureGrams = BigDecimal("100"), silverRatePerGram = BigDecimal("100"),
                cash = BigDecimal("50000")
            )
        )
        assertEquals(ZakatDecisionStatus.INDICATIVE_ESTIMATE, result.status)
        assertEquals("MIXED_WEALTH_BELOW_INDIVIDUAL_NISAB", result.category)
        assertNotNull(result.zakatAmount)
    }

    @Test
    fun solarYearRateUsesExactDayRatioFromSuppliedResearch() {
        assertEquals(BigDecimal("0.025776836158"), ZakatHawlBasis.SHAMSI_365_DOCUMENT_VALUE.rate)
    }

    @Test
    fun anyDebtRequiresCaseSpecificReview() {
        val result = FiqhZakatEngine.calculate(
            ZakatCalculationInput(
                cash = BigDecimal("100000"), goldRatePerGram = goldRate,
                silverRatePerGram = BigDecimal("100"), debtForReview = BigDecimal("10000")
            )
        )
        assertEquals(ZakatDecisionStatus.MUFTI_REVIEW_REQUIRED, result.status)
        assertTrue(result.reviewReasons.isNotEmpty())
    }

    @Test
    fun minorOrIncompetentOwnerIsNotDueAccordingToSuppliedSpecification() {
        val result = FiqhZakatEngine.calculate(
            ZakatCalculationInput(adultAndSane = false, goldPureGrams = BigDecimal("100"), goldRatePerGram = goldRate)
        )
        assertEquals(ZakatDecisionStatus.NOT_DUE, result.status)
    }

    @Test
    fun incompleteHawlDoesNotReturnPaymentAmount() {
        val result = FiqhZakatEngine.calculate(
            ZakatCalculationInput(hawlComplete = false, goldPureGrams = BigDecimal("100"), goldRatePerGram = goldRate)
        )
        assertEquals(ZakatDecisionStatus.NOT_DUE, result.status)
        assertNull(result.zakatAmount)
    }

    @Test
    fun cashAndTradeSelectsLowerNisabAndUsesKhumsSteps() {
        val result = FiqhZakatEngine.calculate(
            ZakatCalculationInput(
                cash = BigDecimal("1000000"),
                goldRatePerGram = BigDecimal("7000"),
                silverRatePerGram = BigDecimal("100")
            )
        )
        assertEquals(ZakatDecisionStatus.INDICATIVE_ESTIMATE, result.status)
        assertNotNull(result.nisabValue)
        assertNotNull(result.zakatAmount)
        assertEquals("CURRENCY_AND_TRADE_ONLY", result.category)
    }
    @Test
    fun defaultSilverNisabUsesPublished612Point41GramConversion() {
        assertEquals(SilverNisabSource.FAIZAN_E_ZAKAT_APPROX, ZakatCalculationInput().silverNisabSource)
        assertEquals(BigDecimal("612.41"), SilverNisabSource.FAIZAN_E_ZAKAT_APPROX.grams)
        assertEquals(BigDecimal("612.36"), SilverNisabSource.PROJECT_RESEARCH.grams)
    }

    @Test
    fun goldOnlyDoesNotRequireAnUnusedSilverMarketRate() {
        val result = FiqhZakatEngine.calculate(
            ZakatCalculationInput(
                goldPureGrams = BigDecimal("87.48"),
                goldRatePerGram = goldRate,
                hawlComplete = true
            )
        )
        assertEquals(ZakatDecisionStatus.INDICATIVE_ESTIMATE, result.status)
        assertNotNull(result.zakatAmount)
    }

    @Test
    fun currencyCalculationRequiresBothMetalRatesToCompareNisab() {
        val result = FiqhZakatEngine.calculate(
            ZakatCalculationInput(cash = BigDecimal("100000"), goldRatePerGram = goldRate)
        )
        assertEquals(ZakatDecisionStatus.NEED_MARKET_RATES, result.status)
        assertNull(result.zakatAmount)
    }

    @Test
    fun mixedCategoryAtItsOwnNisabIsGuardedForMuftiReview() {
        val result = FiqhZakatEngine.calculate(
            ZakatCalculationInput(
                goldPureGrams = BigDecimal("90"), goldRatePerGram = goldRate,
                cash = BigDecimal("1000"), silverRatePerGram = BigDecimal("100")
            )
        )
        assertEquals(ZakatDecisionStatus.MUFTI_REVIEW_REQUIRED, result.status)
        assertNull(result.zakatAmount)
    }

    @Test
    fun khumsThresholdOnlyCountsCompleteFifthSteps() {
        val nisab = BigDecimal("87.48")
        assertEquals(BigDecimal("87.48"), FiqhZakatEngine.khumsEligibleBase(nisab, nisab))
        assertEquals(BigDecimal("87.480"), FiqhZakatEngine.khumsEligibleBase(BigDecimal("104.975"), nisab))
        assertEquals(BigDecimal("104.976"), FiqhZakatEngine.khumsEligibleBase(BigDecimal("104.976"), nisab))
    }

    @Test
    fun negativeZakatInputsAreRejectedAtModelBoundary() {
        org.junit.Assert.assertThrows(IllegalArgumentException::class.java) {
            ZakatCalculationInput(cash = BigDecimal("-1"))
        }
    }

}
