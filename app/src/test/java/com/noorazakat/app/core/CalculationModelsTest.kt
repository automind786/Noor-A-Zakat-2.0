package com.noorazakat.app.core

import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class CalculationModelsTest {
    @Test
    fun fitrahWheatUsesHalfSaSourceQuantity() {
        val result = FitrahCalculator.calculate(FitrahFood.WHEAT, BigDecimal("50"), 2)
        assertEquals(BigDecimal("1.920"), result.kilogramsPerPerson)
        assertEquals(BigDecimal("96.00"), result.valuePerPerson)
        assertEquals(BigDecimal("192.00"), result.totalValue)
    }

    @Test
    fun fitrahOtherListedFoodsUseFullSaSourceQuantity() {
        listOf(FitrahFood.BARLEY, FitrahFood.DATES, FitrahFood.RAISINS).forEach { food ->
            val result = FitrahCalculator.calculate(food, BigDecimal("100"), 1)
            assertEquals(BigDecimal("3.840"), result.kilogramsPerPerson)
            assertEquals(BigDecimal("384.00"), result.totalValue)
        }
    }

    @Test
    fun fitrahHouseholdTotalDoesNotMultiplyRoundedPerPersonValue() {
        val result = FitrahCalculator.calculate(FitrahFood.WHEAT, BigDecimal("0.3373"), 3)
        assertEquals(BigDecimal("0.65"), result.valuePerPerson)
        assertEquals(BigDecimal("1.94"), result.totalValue)
    }

    @Test
    fun fitrahRejectsInvalidPeopleCountAndNegativeRate() {
        assertThrows(IllegalArgumentException::class.java) {
            FitrahCalculator.calculate(FitrahFood.WHEAT, BigDecimal("10"), 0)
        }
        assertThrows(IllegalArgumentException::class.java) {
            FitrahCalculator.calculate(FitrahFood.WHEAT, BigDecimal("-1"), 1)
        }
    }

    @Test
    fun jewelleryPuritySubtractsNonMetalThenAppliesCarat() {
        val result = GoldPurityCalculator.calculate(
            grossWeightGrams = BigDecimal("10"),
            stonesAndNonMetalGrams = BigDecimal("1"),
            carat = 22,
            price24kPerGram = BigDecimal("7000")
        )
        assertEquals(BigDecimal("9"), result.netMetalGrams)
        assertEquals(BigDecimal("8.250000"), result.pureGoldGrams)
        assertEquals(BigDecimal("57750.00"), result.estimatedValue)
    }

    @Test
    fun jewelleryRejectsStoneWeightAboveGrossWeight() {
        assertThrows(IllegalArgumentException::class.java) {
            GoldPurityCalculator.calculate(
                BigDecimal("5"), BigDecimal("6"), 22, BigDecimal("7000")
            )
        }
    }

    @Test
    fun zakatPercentageHelperRequiresScholarConfirmation() {
        assertNull(VerifiedZakatEstimate.calculate(BigDecimal("100000"), scholarConfirmed = false))
        assertEquals(
            BigDecimal("2500.00"),
            VerifiedZakatEstimate.calculate(BigDecimal("100000"), scholarConfirmed = true)
        )
    }

    @Test
    fun decimalParserAcceptsIndianAndWesternGrouping() {
        assertEquals(BigDecimal("1234.50"), parseNonNegativeDecimal("1,234.50"))
        assertEquals(BigDecimal("1234567.89"), parseNonNegativeDecimal("12,34,567.89"))
        assertEquals(BigDecimal("1234567"), parseNonNegativeDecimal("1,234,567"))
        assertEquals(BigDecimal("1250"), parseNonNegativeDecimal("1250"))
    }

    @Test
    fun decimalParserRejectsAmbiguousOrMalformedCommaGrouping() {
        assertNull(parseNonNegativeDecimal("-10"))
        assertNull(parseNonNegativeDecimal("12x"))
        assertNull(parseNonNegativeDecimal("1,25"))
        assertNull(parseNonNegativeDecimal("12,3456"))
        assertNull(parseNonNegativeDecimal("1.234,50"))
    }

    @Test
    fun tolaUnitsConvertSeparatelyAndPrecisely() {
        assertEquals(BigDecimal("10.0"), JewelleryWeightUnit.MARKET_TOLA.toGrams(BigDecimal.ONE))
        assertEquals(BigDecimal("11.664"), JewelleryWeightUnit.SHARI_TOLA.toGrams(BigDecimal.ONE))
        assertEquals(BigDecimal("23.328"), JewelleryWeightUnit.SHARI_TOLA.toGrams(BigDecimal("2")))
    }

    @Test
    fun jewelleryCalculatorAcceptsConvertedShariTolaWeight() {
        val grams = JewelleryWeightUnit.SHARI_TOLA.toGrams(BigDecimal("1"))
        val result = GoldPurityCalculator.calculate(grams, BigDecimal.ZERO, 24, BigDecimal("7000"))
        assertEquals(BigDecimal("11.664000"), result.pureGoldGrams)
        assertEquals(BigDecimal("81648.00"), result.estimatedValue)
    }
    @Test
    fun assetOwnershipShareIsAppliedPerLineAndRoundedToPaise() {
        assertEquals(
            BigDecimal("30000.00"),
            AssetOwnershipCalculator.ownedValue(BigDecimal("120000"), BigDecimal("25"))
        )
        assertEquals(
            BigDecimal("1000.00"),
            AssetOwnershipCalculator.ownedValue(BigDecimal("1234.57"), BigDecimal("81"))
        )
    }

    @Test
    fun assetOwnershipRejectsPercentAboveOneHundred() {
        assertThrows(IllegalArgumentException::class.java) {
            AssetOwnershipCalculator.ownedValue(BigDecimal("1000"), BigDecimal("101"))
        }
    }

}
