package com.onthecrow.nomadrates.entity

expect class MoneyAmount {
    constructor(value: String)
    constructor(value: Double)
    constructor(value: Int)
    constructor(value: Long)

    operator fun plus(other: MoneyAmount): MoneyAmount
    operator fun minus(other: MoneyAmount): MoneyAmount
    operator fun times(other: MoneyAmount): MoneyAmount
    operator fun div(other: MoneyAmount): MoneyAmount

    operator fun compareTo(other: MoneyAmount): Int

    fun abs(): MoneyAmount
    fun isZero(): Boolean
    fun isNegative(): Boolean

    fun toDouble(): Double
    fun toPlainString(): String
}

expect object PlatformFormatter {
    fun format(amount: MoneyAmount, pattern: String, currencyCode: String): String
}

fun MoneyAmount.formatAdaptive(currencyCode: String): String {
    val absValue = this.abs()

    val zero = MoneyAmount("0")
    val oneCent = MoneyAmount("0.01")
    val oneUnit = MoneyAmount("1")

    val pattern = when {
        this.compareTo(zero) == 0 -> "###0.00"
        absValue.compareTo(oneCent) < 0 -> "###0.########"
        absValue.compareTo(oneUnit) < 0 -> "###0.####"
        else -> "###0.00"
    }

    return PlatformFormatter.format(this, pattern, currencyCode)
}
