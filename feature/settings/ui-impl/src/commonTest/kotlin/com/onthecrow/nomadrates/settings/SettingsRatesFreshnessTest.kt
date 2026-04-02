package com.onthecrow.nomadrates.settings

import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsRatesFreshnessTest {

    @Test
    fun `null timestamp is unknown`() {
        assertEquals(
            SettingsRatesFreshness.Unknown,
            null.toSettingsRatesFreshness(nowMillis = 1_000L),
        )
    }

    @Test
    fun `recent timestamp is fresh`() {
        val now = 100_000L
        val timestamp = now - 1_000L

        assertEquals(
            SettingsRatesFreshness.Fresh,
            timestamp.toSettingsRatesFreshness(now),
        )
    }

    @Test
    fun `timestamp older than threshold is stale`() {
        val thresholdMillis = 27 * 60 * 60 * 1_000L
        val now = thresholdMillis + 1L

        assertEquals(
            SettingsRatesFreshness.Stale,
            0L.toSettingsRatesFreshness(now),
        )
    }
}
