package com.noorazakat.app.core

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Decision-support engine for clearly bounded, uncomplicated cases.
 *
 * IMPORTANT: This is not a fatwa. Complex mixed-metal, debt, receivable, committee,
 * property-intention and ownership cases deliberately return MUFTI_REVIEW_REQUIRED.
 * The engine uses source parameters selected by the user/reviewer rather than silently
 * resolving differences between the supplied project specification and later publications.
 */
enum class ZakatHawlBasis(val rate: BigDecimal) {
    QAMRI_354(BigDecimal("0.025")),
    // Exact day-ratio from the supplied research: (365 / 354) x 2.5%.
    SHAMSI_365_DOCUMENT_VALUE(
        BigDecimal("0.025").multiply(BigDecimal("365"))
            .divide(BigDecimal("354"), 12, RoundingMode.HALF_UP)
    )
}

enum class SilverNisabSource(val grams: BigDecimal, val displayName: String) {
    PROJECT_RESEARCH(BigDecimal("612.36"), "Supplied Noor-A-Zakat research (612.36 g)"),
    FAIZAN_E_ZAKAT_APPROX(BigDecimal("612.41"), "Faizan-e-Zakat approximate measure (612.41 g)")
}

enum class ZakatDecisionStatus {
    NOT_DUE,
    NEED_MARKET_RATES,
    INDICATIVE_ESTIMATE,
    MUFTI_REVIEW_REQUIRED
}

data class ZakatCalculationInput(
    val adultAndSane: Boolean = true,
    val hawlComplete: Boolean = true,
    val hawlBasis: ZakatHawlBasis = ZakatHawlBasis.QAMRI_354,
    val silverNisabSource: SilverNisabSource = SilverNisabSource.FAIZAN_E_ZAKAT_APPROX,
    val goldPureGrams: BigDecimal = BigDecimal.ZERO,
    val goldRatePerGram: BigDecimal = BigDecimal.ZERO,
    val silverPureGrams: BigDecimal = BigDecimal.ZERO,
    val silverRatePerGram: BigDecimal = BigDecimal.ZERO,
    val cash: BigDecimal = BigDecimal.ZERO,
    val bankBalance: BigDecimal = BigDecimal.ZERO,
    val savedCommittee: BigDecimal = BigDecimal.ZERO,
    val tradeStock: BigDecimal = BigDecimal.ZERO,
    val rawMaterial: BigDecimal = BigDecimal.ZERO,
    val tradeProperty: BigDecimal = BigDecimal.ZERO,
    /** Non-zero debt is not automatically deducted: its type, due date and time matter. */
    val debtForReview: BigDecimal = BigDecimal.ZERO,
    /** Receivables are not automatically included: strong/medium/weak dayn differ. */
    val receivablesForReview: BigDecimal = BigDecimal.ZERO,
    /** Enable for committee received, rent, investment, inherited property, crops, etc. */
    val hasOtherComplexAssets: Boolean = false
) {
    init {
        listOf(
            goldPureGrams, goldRatePerGram, silverPureGrams, silverRatePerGram,
            cash, bankBalance, savedCommittee, tradeStock, rawMaterial, tradeProperty,
            debtForReview, receivablesForReview
        ).forEach { require(it >= BigDecimal.ZERO) { "Amounts and weights cannot be negative" } }
    }
}

data class ZakatCalculationResult(
    val status: ZakatDecisionStatus,
    val category: String,
    val grossAssetValue: BigDecimal,
    val nisabValue: BigDecimal?,
    val nisabBasis: String,
    val zakatableBase: BigDecimal?,
    val zakatRate: BigDecimal,
    val zakatAmount: BigDecimal?,
    val explanation: String,
    val reviewReasons: List<String>,
    val sourceNotes: List<String>
)

object FiqhZakatEngine {
    val GOLD_NISAB_GRAMS = BigDecimal("87.48")
    private val ONE_FIFTH = BigDecimal("0.2")
    private val MONEY_SCALE = 2
    private val MC = java.math.MathContext.DECIMAL128

    fun calculate(input: ZakatCalculationInput): ZakatCalculationResult {
        val rate = input.hawlBasis.rate
        val goldValue = input.goldPureGrams.multiply(input.goldRatePerGram, MC)
        val silverValue = input.silverPureGrams.multiply(input.silverRatePerGram, MC)
        val moneyAndTrade = listOf(
            input.cash, input.bankBalance, input.savedCommittee,
            input.tradeStock, input.rawMaterial, input.tradeProperty
        ).fold(BigDecimal.ZERO, BigDecimal::add)
        val gross = goldValue.add(silverValue).add(moneyAndTrade)

        fun result(
            status: ZakatDecisionStatus,
            category: String,
            nisab: BigDecimal? = null,
            basis: String = "",
            base: BigDecimal? = null,
            amount: BigDecimal? = null,
            explanation: String,
            reasons: List<String> = emptyList(),
            notes: List<String> = emptyList()
        ) = ZakatCalculationResult(
            status, category, money(gross), nisab?.let(::money), basis,
            base?.let(::money), rate, amount?.let(::money), explanation, reasons, notes
        )

        if (!input.adultAndSane) {
            return result(
                ZakatDecisionStatus.NOT_DUE, "OWNER_CONDITION",
                explanation = "Supplied project specification says Zakat is not due on the wealth of a minor or a person without legal capacity (R01).",
                notes = listOf("Noor-A-Zakat supplied research, section 4; Bahar-e-Shariat, Hissa 5 — verify the precise personal case.")
            )
        }
        if (!input.hawlComplete) {
            return result(
                ZakatDecisionStatus.NOT_DUE, "HAWL_INCOMPLETE",
                explanation = "The selected Hawl has not completed. Do not treat this screen as a final personal ruling; track the Hijri anniversary and local moon-sighting adjustment.",
                notes = listOf("Supplied Noor-A-Zakat research, section 4; Hawl is tracked by the lunar anniversary.")
            )
        }
        if (input.goldPureGrams > BigDecimal.ZERO && input.goldRatePerGram <= BigDecimal.ZERO ||
            input.silverPureGrams > BigDecimal.ZERO && input.silverRatePerGram <= BigDecimal.ZERO ||
            (moneyAndTrade > BigDecimal.ZERO && (input.goldRatePerGram <= BigDecimal.ZERO || input.silverRatePerGram <= BigDecimal.ZERO))) {
            return result(
                ZakatDecisionStatus.NEED_MARKET_RATES, "MARKET_RATES",
                explanation = "Enter current 24K gold and silver market rates per gram. Use a local verified rate; international spot estimates can differ from local shop prices."
            )
        }

        val reviewReasons = buildList {
            if (input.debtForReview > BigDecimal.ZERO) add("Debt entered: determine whether it is an immediately demandable human liability, when it arose, and how much is actually deductible.")
            if (input.receivablesForReview > BigDecimal.ZERO) add("Receivable entered: strong, medium and weak debts have different collection and payment rules.")
            if (input.hasOtherComplexAssets) add("A property, committee/BC, rent, crop, loan, interest or other complex asset has been marked for review.")
        }
        if (reviewReasons.isNotEmpty()) {
            return result(
                ZakatDecisionStatus.MUFTI_REVIEW_REQUIRED,
                "COMPLEX_CASE",
                explanation = "The app has recorded your figures but will not issue a possibly incorrect final Zakat amount for this combination. The items below need a qualified Sunni Hanafi Mufti's review.",
                reasons = reviewReasons,
                notes = listOf(
                    "Faizan-e-Zakat, pp. 27–28: khums and combining different wealth categories.",
                    "Faizan-e-Zakat, pp. 53–54: strong, medium and weak receivables have different rules.",
                    "Supplied Noor-A-Zakat research, sections 6–7: debt and modern assets need precise classification."
                )
            )
        }

        val hasGold = input.goldPureGrams > BigDecimal.ZERO
        val hasSilver = input.silverPureGrams > BigDecimal.ZERO
        val hasMoney = moneyAndTrade > BigDecimal.ZERO

        if (!hasGold && !hasSilver && !hasMoney) {
            return result(ZakatDecisionStatus.NOT_DUE, "NO_ZAKATABLE_ASSETS", explanation = "No Zakatable asset amount was entered.")
        }

        if (hasGold && !hasSilver && !hasMoney) {
            val nisabGrams = GOLD_NISAB_GRAMS
            val baseGrams = khumsEligibleBase(input.goldPureGrams, nisabGrams)
            if (input.goldPureGrams < nisabGrams) {
                return result(
                    ZakatDecisionStatus.NOT_DUE, "GOLD_ONLY", nisab = nisabGrams.multiply(input.goldRatePerGram),
                    basis = "7.5 Shar'i tola = 87.48 g pure gold",
                    explanation = "Pure gold is below the 7.5 Shar'i tola threshold.",
                    notes = listOf("Faizan-e-Zakat, pp. 27–28; Fatawa Razawiyyah, vol. 10, pp. 85.")
                )
            }
            val baseValue = baseGrams.multiply(input.goldRatePerGram, MC)
            return result(
                ZakatDecisionStatus.INDICATIVE_ESTIMATE, "GOLD_ONLY", nisab = nisabGrams.multiply(input.goldRatePerGram),
                basis = "7.5 Shar'i tola = 87.48 g; complete one-fifth surplus steps only",
                base = baseValue, amount = baseValue.multiply(rate, MC),
                explanation = "Gold-only estimate applies the published khums threshold to surplus above nisab. Confirm jewelry classification, purity and exact case before payment.",
                notes = listOf("Dawat-e-Islami, Faizan-e-Zakat, pp. 27–28; derived from Fatawa Razawiyyah, vol. 10, p. 85.")
            )
        }

        if (hasSilver && !hasGold && !hasMoney) {
            val nisabGrams = input.silverNisabSource.grams
            val baseGrams = khumsEligibleBase(input.silverPureGrams, nisabGrams)
            if (input.silverPureGrams < nisabGrams) {
                return result(
                    ZakatDecisionStatus.NOT_DUE, "SILVER_ONLY", nisab = nisabGrams.multiply(input.silverRatePerGram),
                    basis = "52.5 Shar'i tola = ${nisabGrams.stripTrailingZeros().toPlainString()} g (${input.silverNisabSource.displayName})",
                    explanation = "Pure silver is below the selected 52.5 Shar'i tola threshold.",
                    notes = listOf("Supplied research specifies 612.36 g; Faizan-e-Zakat gives approximately 612.41 g. Select and confirm the adopted measure with the reviewing scholar.")
                )
            }
            val baseValue = baseGrams.multiply(input.silverRatePerGram, MC)
            return result(
                ZakatDecisionStatus.INDICATIVE_ESTIMATE, "SILVER_ONLY",
                nisab = nisabGrams.multiply(input.silverRatePerGram),
                basis = "52.5 Shar'i tola = ${nisabGrams.stripTrailingZeros().toPlainString()} g; complete one-fifth surplus steps",
                base = baseValue, amount = baseValue.multiply(rate, MC),
                explanation = "Silver-only estimate uses the selected silver nisab measure and khums threshold. Confirm the exact adopted measure and purity with the reviewing scholar.",
                notes = listOf("Supplied Noor-A-Zakat research, section 3; Faizan-e-Zakat, pp. 26–28 gives approximately 612.41 g.")
            )
        }

        // Values used for the category-combination rule in Faizan-e-Zakat.
        val goldNisabValue = GOLD_NISAB_GRAMS.multiply(input.goldRatePerGram, MC)
        val silverNisabValue = input.silverNisabSource.grams.multiply(input.silverRatePerGram, MC)
        val moneyNisabValue = minOf(goldNisabValue, silverNisabValue)
        val chosenNisab = moneyNisabValue
        val chosenBasis = if (goldNisabValue <= silverNisabValue) {
            "Gold valuation (lower nisab value; compare source rules for equal outcomes)"
        } else {
            "Silver valuation (${input.silverNisabSource.displayName}; lower nisab value)"
        }

        if (!hasGold && !hasSilver) {
            if (moneyAndTrade < chosenNisab) {
                return result(
                    ZakatDecisionStatus.NOT_DUE, "CURRENCY_AND_TRADE_ONLY", nisab = chosenNisab,
                    basis = chosenBasis,
                    explanation = "The entered currency and trading assets are below the selected nisab value.",
                    notes = listOf("Faizan-e-Zakat, pp. 26–28: combine zakatable currency/trade wealth and apply the more beneficial nisab valuation.")
                )
            }
            val eligibleValue = khumsEligibleBase(moneyAndTrade, chosenNisab)
            return result(
                ZakatDecisionStatus.INDICATIVE_ESTIMATE, "CURRENCY_AND_TRADE_ONLY", nisab = chosenNisab,
                basis = chosenBasis + "; complete one-fifth surplus increments applied",
                base = eligibleValue, amount = eligibleValue.multiply(rate, MC),
                explanation = "Indicative estimate for currency and trade assets only. Confirm ownership, hawl, liabilities and local valuation before payment.",
                notes = listOf("Faizan-e-Zakat, pp. 26–28; 1/40 (2.5%) and khums threshold; Fatawa Amjadiyyah, vol. 1, p. 378.")
            )
        }

        // When each category is individually below its own nisab, combine the total
        // value and compare it with the nisab valuation that benefits the Fuqara more.
        val goldCategoryBelow = !hasGold || input.goldPureGrams < GOLD_NISAB_GRAMS
        val silverCategoryBelow = !hasSilver || input.silverPureGrams < input.silverNisabSource.grams
        val moneyCategoryBelow = !hasMoney || moneyAndTrade < moneyNisabValue
        if (goldCategoryBelow && silverCategoryBelow && moneyCategoryBelow) {
            if (gross < chosenNisab) {
                return result(
                    ZakatDecisionStatus.NOT_DUE, "MIXED_WEALTH_BELOW_NISAB", nisab = chosenNisab,
                    basis = chosenBasis,
                    explanation = "Each entered category is below its individual nisab and the combined value is also below the selected nisab.",
                    notes = listOf("Faizan-e-Zakat, p. 28; Rad-dul-Muhtar, Kitab-uz-Zakah, vol. 3, p. 271 (summarised).")
                )
            }
            val eligibleValue = khumsEligibleBase(gross, chosenNisab)
            return result(
                ZakatDecisionStatus.INDICATIVE_ESTIMATE, "MIXED_WEALTH_BELOW_INDIVIDUAL_NISAB", nisab = chosenNisab,
                basis = chosenBasis + "; all individually-below-nisab categories combined; khums applied",
                base = eligibleValue, amount = eligibleValue.multiply(rate, MC),
                explanation = "Indicative combined-wealth estimate. Each category was below its own nisab; their values were combined and compared using the valuation more beneficial to the Fuqara. Confirm the precise asset classification and hawl with a qualified Sunni Hanafi Mufti.",
                notes = listOf("Faizan-e-Zakat, p. 28; Rad-dul-Muhtar, Kitab-uz-Zakah, vol. 3, p. 271 (summarised).")
            )
        }

        // If one or more categories already individually reach nisab, the source
        // describes additional case-specific combinations of exempted khums portions.
        // Refuse to flatten those cases into a generic 2.5% total.
        return result(
            ZakatDecisionStatus.MUFTI_REVIEW_REQUIRED,
            "MIXED_WEALTH_ABOVE_CATEGORY_NISAB",
            nisab = chosenNisab,
            basis = chosenBasis,
            explanation = "At least one wealth category individually reaches its nisab while other categories are also present. The published Hanafi examples apply additional khums and combination steps; this case needs the exact category-wise calculation reviewed before a final amount is issued.",
            reasons = listOf("Review the gold, silver and currency/trade categories separately, then combine only the relevant exempted portions according to the cited case."),
            notes = listOf("Faizan-e-Zakat, pp. 28–29; Rad-dul-Muhtar, Kitab-uz-Zakah, vol. 3, p. 271; Al-Fatawa Al-Hindiyyah, Kitab-uz-Zakah, vol. 1, p. 179; Fatawa Razawiyyah, vol. 10, p. 116.")
        )
    }

    /** Returns nisab plus only complete one-fifth-of-nisab surplus increments. */
    fun khumsEligibleBase(total: BigDecimal, nisab: BigDecimal): BigDecimal {
        require(total >= BigDecimal.ZERO) { "Total cannot be negative" }
        require(nisab > BigDecimal.ZERO) { "Nisab must be greater than zero" }
        if (total < nisab) return BigDecimal.ZERO
        val step = nisab.multiply(ONE_FIFTH, MC)
        val surplus = total.subtract(nisab)
        val fullSteps = surplus.divideToIntegralValue(step)
        return nisab.add(step.multiply(fullSteps, MC), MC)
    }

    private fun money(value: BigDecimal): BigDecimal = value.setScale(MONEY_SCALE, RoundingMode.HALF_UP)
}
