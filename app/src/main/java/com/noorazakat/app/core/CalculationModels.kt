package com.noorazakat.app.core

import java.math.BigDecimal
import java.math.RoundingMode

/** Fitrah food measures reproduced from the cited Ahl-e-Sunnat material. */
enum class FitrahFood(val kilogramsPerPerson: BigDecimal) {
    WHEAT(BigDecimal("1.920")),
    BARLEY(BigDecimal("3.840")),
    DATES(BigDecimal("3.840")),
    RAISINS(BigDecimal("3.840"))
}

data class FitrahResult(
    val kilogramsPerPerson: BigDecimal,
    val valuePerPerson: BigDecimal,
    val peopleCount: Int,
    val totalValue: BigDecimal
)

object FitrahCalculator {
    fun calculate(
        food: FitrahFood,
        localPricePerKg: BigDecimal,
        peopleCount: Int
    ): FitrahResult {
        require(localPricePerKg >= BigDecimal.ZERO) { "Price cannot be negative" }
        require(peopleCount in 1..500) { "People count must be between 1 and 500" }
        val unroundedPerPerson = food.kilogramsPerPerson.multiply(localPricePerKg)
        val perPerson = unroundedPerPerson.setScale(2, RoundingMode.HALF_UP)
        // Calculate the household total from the unrounded amount to avoid rounding drift.
        val total = unroundedPerPerson.multiply(BigDecimal(peopleCount))
            .setScale(2, RoundingMode.HALF_UP)
        return FitrahResult(food.kilogramsPerPerson, perPerson, peopleCount, total)
    }
}

/** User-visible weight units. Market tola and Shar'i tola are intentionally distinct. */
enum class JewelleryWeightUnit(val gramsPerUnit: BigDecimal) {
    GRAMS(BigDecimal.ONE),
    MARKET_TOLA(BigDecimal("10.0")),
    SHARI_TOLA(BigDecimal("11.664"));

    fun toGrams(value: BigDecimal): BigDecimal {
        require(value >= BigDecimal.ZERO) { "Weight cannot be negative" }
        return value.multiply(gramsPerUnit)
    }
}

object AssetOwnershipCalculator {
    fun ownedValue(fullValue: BigDecimal, ownershipPercent: BigDecimal): BigDecimal {
        require(fullValue >= BigDecimal.ZERO) { "Asset value cannot be negative" }
        require(ownershipPercent >= BigDecimal.ZERO && ownershipPercent <= BigDecimal("100")) {
            "Ownership percentage must be between 0 and 100"
        }
        return fullValue.multiply(ownershipPercent)
            .divide(BigDecimal("100"), 2, RoundingMode.HALF_UP)
    }
}

data class GoldPurityResult(
    val netMetalGrams: BigDecimal,
    val pureGoldGrams: BigDecimal,
    val estimatedValue: BigDecimal
)

object GoldPurityCalculator {
    fun calculate(
        grossWeightGrams: BigDecimal,
        stonesAndNonMetalGrams: BigDecimal,
        carat: Int,
        price24kPerGram: BigDecimal
    ): GoldPurityResult {
        require(grossWeightGrams >= BigDecimal.ZERO) { "Gross weight cannot be negative" }
        require(stonesAndNonMetalGrams >= BigDecimal.ZERO) { "Non-metal weight cannot be negative" }
        require(stonesAndNonMetalGrams <= grossWeightGrams) { "Non-metal weight cannot exceed gross weight" }
        require(carat in 1..24) { "Carat must be between 1 and 24" }
        require(price24kPerGram >= BigDecimal.ZERO) { "Gold rate cannot be negative" }
        val net = grossWeightGrams.subtract(stonesAndNonMetalGrams)
        val pure = net.multiply(BigDecimal(carat))
            .divide(BigDecimal(24), 6, RoundingMode.HALF_UP)
        val value = pure.multiply(price24kPerGram).setScale(2, RoundingMode.HALF_UP)
        return GoldPurityResult(net, pure, value)
    }
}

/**
 * A 2.5% arithmetic helper only. It intentionally refuses to infer whether a user's
 * assets are fiqh-eligible, whether nisab is met, or which debts may be deducted.
 * The caller must first attest that a qualified Hanafi Mufti has confirmed the base.
 */
object VerifiedZakatEstimate {
    private val RATE = BigDecimal("0.025")

    fun calculate(verifiedNetZakatableWealth: BigDecimal, scholarConfirmed: Boolean): BigDecimal? {
        require(verifiedNetZakatableWealth >= BigDecimal.ZERO) { "Amount cannot be negative" }
        if (!scholarConfirmed) return null
        return verifiedNetZakatableWealth.multiply(RATE).setScale(2, RoundingMode.HALF_UP)
    }
}

/**
 * Parses plain, western-grouped (1,234,567.89) and Indian-grouped
 * (12,34,567.89) numbers. Invalid comma grouping is rejected rather than
 * silently changing the entered amount (for example, "1,25" is not 125).
 * Decimal separator is always a dot to keep financial input unambiguous.
 */
fun parseNonNegativeDecimal(value: String): BigDecimal? {
    val raw = value.trim()
    if (raw.isEmpty()) return BigDecimal.ZERO

    val plain = "(?:[0-9]+(?:\\.[0-9]+)?|\\.[0-9]+)"
    val westernGrouped = "[0-9]{1,3}(?:,[0-9]{3})+(?:\\.[0-9]+)?"
    val indianGrouped = "[0-9]{1,2}(?:,[0-9]{2})*,[0-9]{3}(?:\\.[0-9]+)?"
    if (!Regex("^(?:$plain|$westernGrouped|$indianGrouped)$").matches(raw)) return null

    val normalized = raw.replace(",", "")
    return runCatching { BigDecimal(normalized).takeIf { it >= BigDecimal.ZERO } }.getOrNull()
}
