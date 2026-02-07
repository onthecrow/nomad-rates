package com.onthecrow.nomadrates.entity

import java.math.BigDecimal
import java.math.MathContext
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import java.util.Currency

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class MoneyAmount : Comparable<MoneyAmount> {
    private val value: BigDecimal

    actual constructor(value: String) {
        this.value = BigDecimal(value)
    }

    actual constructor(value: Double) {
        this.value = BigDecimal.valueOf(value)
    }

    actual constructor(value: Int) {
        this.value = BigDecimal(value)
    }

    actual constructor(value: Long) {
        this.value = BigDecimal.valueOf(value)
    }

    private constructor(bd: BigDecimal) {
        this.value = bd
    }

    actual operator fun plus(other: MoneyAmount): MoneyAmount = MoneyAmount(value.add(other.value))
    actual operator fun minus(other: MoneyAmount): MoneyAmount = MoneyAmount(value.subtract(other.value))
    actual operator fun times(other: MoneyAmount): MoneyAmount = MoneyAmount(value.multiply(other.value))

    actual operator fun div(other: MoneyAmount): MoneyAmount {
        return MoneyAmount(value.divide(other.value, MathContext.DECIMAL128))
    }

    actual override operator fun compareTo(other: MoneyAmount): Int = value.compareTo(other.value)

    actual fun abs(): MoneyAmount = MoneyAmount(value.abs())
    actual fun isZero(): Boolean = value.signum() == 0
    actual fun isNegative(): Boolean = value.signum() == -1

    actual fun toDouble(): Double = value.toDouble()
    actual fun toPlainString(): String = value.toPlainString()

    fun getNative(): BigDecimal = value
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object PlatformFormatter {
    actual fun format(amount: MoneyAmount, pattern: String, currencyCode: String): String {
        val symbols = DecimalFormatSymbols(Locale.getDefault())
        val formatter = DecimalFormat(pattern, symbols)

        try {
            formatter.currency = Currency.getInstance(currencyCode)
        } catch (e: Exception) {
            println(e.stackTrace)
        }

        return formatter.format(amount.getNative())
    }
}
