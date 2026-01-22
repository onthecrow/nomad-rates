package com.onthecrow.nomadrates.entity

import platform.Foundation.*

actual class MoneyAmount : Comparable<MoneyAmount> {
    private val value: NSDecimalNumber

    actual constructor(value: String) {
        this.value = NSDecimalNumber.decimalNumberWithString(value)
    }

    actual constructor(value: Double) {
        this.value = NSDecimalNumber(double = value)
    }

    actual constructor(value: Int) {
        this.value = NSDecimalNumber(int = value)
    }

    actual constructor(value: Long) {
        this.value = NSDecimalNumber(longLong = value)
    }

    private constructor(nsd: NSDecimalNumber) {
        this.value = nsd
    }

    private val mathBehavior = NSDecimalNumberHandler.decimalNumberHandlerWithRoundingMode(
        roundingMode = NSRoundingMode.NSRoundPlain,
        scale = 34,
        raiseOnExactness = false,
        raiseOnOverflow = false,
        raiseOnUnderflow = false,
        raiseOnDivideByZero = true
    )

    actual operator fun plus(other: MoneyAmount): MoneyAmount =
        MoneyAmount(value.decimalNumberByAdding(other.value))

    actual operator fun minus(other: MoneyAmount): MoneyAmount =
        MoneyAmount(value.decimalNumberBySubtracting(other.value))

    actual operator fun times(other: MoneyAmount): MoneyAmount =
        MoneyAmount(value.decimalNumberByMultiplyingBy(other.value))

    actual operator fun div(other: MoneyAmount): MoneyAmount =
        MoneyAmount(value.decimalNumberByDividingBy(other.value, withBehavior = mathBehavior))

    actual override operator fun compareTo(other: MoneyAmount): Int =
        value.compare(other.value).toInt()

    actual fun abs(): MoneyAmount {
        val minusOne = NSDecimalNumber(string = "-1")
        return if (isNegative()) MoneyAmount(value.decimalNumberByMultiplyingBy(minusOne)) else this
    }

    actual fun isZero(): Boolean = value.compare(NSDecimalNumber.zero) == NSOrderedSame

    actual fun isNegative(): Boolean = value.compare(NSDecimalNumber.zero) == NSOrderedAscending

    actual fun toDouble(): Double = value.doubleValue
    actual fun toPlainString(): String = value.stringValue

    fun getNative(): NSDecimalNumber = value
}

actual object PlatformFormatter {
    actual fun format(amount: MoneyAmount, pattern: String, currencyCode: String): String {
        val formatter = NSNumberFormatter()
        formatter.numberStyle = NSNumberFormatterDecimalStyle
        formatter.currencyCode = currencyCode

        val decimals = pattern.substringAfter(".", "").length.toULong()

        val isHardLimit = pattern.contains("00")

        formatter.maximumFractionDigits = decimals
        formatter.minimumFractionDigits = if (isHardLimit) decimals else 0u

        return formatter.stringFromNumber(amount.getNative()) ?: ""
    }
}
