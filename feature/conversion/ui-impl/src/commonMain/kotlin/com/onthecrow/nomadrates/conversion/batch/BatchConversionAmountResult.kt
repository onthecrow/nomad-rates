package com.onthecrow.nomadrates.conversion.batch

import com.onthecrow.nomadrates.navigation.ScreenResult

/**
 * Sent while the amount on the batch conversion screen changes, so the "from" field of the main
 * screen keeps the same amount.
 *
 * @param amount raw input like "1234.5".
 */
internal data class BatchConversionAmountResult(
    val amount: String,
) : ScreenResult
