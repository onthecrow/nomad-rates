package com.onthecrow.nomadrates.currency

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.onthecrow.nomadrates.currency.data.datastore.CurrencyRatesMetadataDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import okio.Path.Companion.toPath

class ObserveLastRatesTimestampUseCaseImplTest {

    @Test
    fun `emits saved timestamp`() = runTest {
        val dataSource = createMetadataDataSource()
        val useCase = ObserveLastRatesTimestampUseCaseImpl(dataSource)

        dataSource.saveLastRatesTimestampIfNewer(123L)

        assertEquals(123_000L, useCase().first())
    }

    @Test
    fun `emits manual refresh timestamp when it is more recent than backend timestamp`() = runTest {
        val dataSource = createMetadataDataSource()
        val useCase = ObserveLastRatesTimestampUseCaseImpl(dataSource)

        dataSource.saveLastRatesTimestampIfNewer(123L)
        dataSource.saveLastManualRefreshAt(456_000L)

        assertEquals(456_000L, useCase().first())
    }

    private fun createMetadataDataSource(): CurrencyRatesMetadataDataSource {
        return CurrencyRatesMetadataDataSource(
            PreferenceDataStoreFactory.createWithPath(
                scope = backgroundScope,
                produceFile = { "/tmp/observe_last_rates_${Random.nextInt()}.preferences_pb".toPath() },
            )
        )
    }
}
