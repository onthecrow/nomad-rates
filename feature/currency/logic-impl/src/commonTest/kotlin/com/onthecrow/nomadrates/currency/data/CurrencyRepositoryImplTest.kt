package com.onthecrow.nomadrates.currency.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.onthecrow.nomadrates.currency.data.database.CurrencyDao
import com.onthecrow.nomadrates.currency.data.database.CurrencyDatabaseDataSource
import com.onthecrow.nomadrates.currency.data.database.CurrencyEntity
import com.onthecrow.nomadrates.currency.data.datastore.CurrencyRatesMetadataDataSource
import com.onthecrow.nomadrates.currency.model.CurrenciesResponse
import com.onthecrow.nomadrates.currency.model.Currency
import com.onthecrow.nomadrates.remoteconfig.RemoteConfig
import com.onthecrow.nomadrates.remoteconfig.RemoteConfigProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import okio.Path.Companion.toPath
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CurrencyRepositoryImplTest {

    @Test
    fun `currencies are saved even when remote config has not emitted yet`() = runTest {
        startTestKoin()

        try {
            val repository = CurrencyRepositoryImpl(
                currencyRemoteDataSource = SuccessfulCurrencyRemoteDataSource(samplePayload()),
                currencyDatabaseDataSource = CurrencyDatabaseDataSource(FakeCurrencyDao()),
                currencyRatesMetadataDataSource = createMetadataDataSource(),
                remoteConfigProvider = TestRemoteConfigProvider(),
            )

            val currencies = withTimeout(5_000) {
                repository.getCurrencyList()
                    .map { it.orEmpty() }
                    .first { it.isNotEmpty() }
            }

            assertEquals(listOf("EUR", "USD"), currencies.map { it.code }.sorted())
            assertFalse(currencies.any { it.isFeatured })
        } finally {
            stopKoin()
        }
    }

    @Test
    fun `featured flags are updated when remote config emits later`() = runTest {
        startTestKoin()

        try {
            val remoteConfigFlow = MutableStateFlow(RemoteConfig(emptyList(), emptyList()))
            val repository = CurrencyRepositoryImpl(
                currencyRemoteDataSource = SuccessfulCurrencyRemoteDataSource(samplePayload()),
                currencyDatabaseDataSource = CurrencyDatabaseDataSource(FakeCurrencyDao()),
                currencyRatesMetadataDataSource = createMetadataDataSource(),
                remoteConfigProvider = TestRemoteConfigProvider(remoteConfigFlow),
            )

            withTimeout(5_000) {
                repository.getCurrencyList()
                    .map { it.orEmpty() }
                    .first { it.isNotEmpty() }
            }

            remoteConfigFlow.value = RemoteConfig(
                featuredCurrencies = listOf("EUR"),
                featuredConversions = emptyList(),
            )

            val updatedCurrencies = withTimeout(5_000) {
                repository.getCurrencyList()
                    .map { it.orEmpty() }
                    .first { currencies ->
                        currencies.firstOrNull { it.code == "EUR" }?.isFeatured == true
                    }
            }

            assertTrue(updatedCurrencies.first { it.code == "EUR" }.isFeatured)
        } finally {
            stopKoin()
        }
    }

    @Test
    fun `newer backend timestamp is saved`() = runTest {
        startTestKoin()

        try {
            val metadataDataSource = createMetadataDataSource()
            val repository = CurrencyRepositoryImpl(
                currencyRemoteDataSource = SuccessfulCurrencyRemoteDataSource(samplePayload()),
                currencyDatabaseDataSource = CurrencyDatabaseDataSource(FakeCurrencyDao()),
                currencyRatesMetadataDataSource = metadataDataSource,
                remoteConfigProvider = TestRemoteConfigProvider(),
            )

            withTimeout(5_000) {
                repository.getCurrencyList()
                    .map { it.orEmpty() }
                    .first { it.isNotEmpty() }
            }

            assertEquals(1_000L, metadataDataSource.observeLastRatesTimestamp().first())
        } finally {
            stopKoin()
        }
    }

    private fun startTestKoin() {
        stopKoin()
        startKoin {
            modules(
                module {
                    single {
                        Json {
                            ignoreUnknownKeys = true
                        }
                    }
                },
            )
        }
    }

    private fun createMetadataDataSource(): CurrencyRatesMetadataDataSource {
        return CurrencyRatesMetadataDataSource(
            PreferenceDataStoreFactory.createWithPath(
                scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.SupervisorJob() + Dispatchers.Default),
                produceFile = { "/tmp/currency_repo_${Random.nextInt()}.preferences_pb".toPath() },
            )
        )
    }

    private class TestRemoteConfigProvider(
        private val flow: Flow<RemoteConfig> = emptyFlow(),
    ) : RemoteConfigProvider {
        override fun getRemoteConfigFlow(): Flow<RemoteConfig> = flow
        override fun refresh() = Unit
    }

    private class SuccessfulCurrencyRemoteDataSource(
        private val payload: CurrencyConfigPayload,
    ) : CurrencyRemoteDataSource() {
        private val json = Json { ignoreUnknownKeys = true }

        init {
            start()
        }

        override fun getString(key: String): String = when (key) {
            KEY_DATA -> json.encodeToString(payload.config)
            else -> payload.historical[key.removePrefix(PREFIX_CURRENCY)]?.joinToString(";").orEmpty()
        }

        override fun getKeysByPrefix(prefix: String): Set<String> {
            return payload.historical.keys.map { "$prefix$it" }.toSet()
        }

        override fun startBackgroundSync(onActivated: () -> Unit, onError: (Throwable) -> Unit) = Unit

        override suspend fun fetchAndActivate(): Boolean = true
    }

    private class FailingCurrencyRemoteDataSource : CurrencyRemoteDataSource() {

        init {
            start()
        }

        override fun getString(key: String): String = ""

        override fun getKeysByPrefix(prefix: String): Set<String> = emptySet()

        override fun startBackgroundSync(onActivated: () -> Unit, onError: (Throwable) -> Unit) = Unit

        override suspend fun fetchAndActivate(): Boolean = false
    }

    private class FakeCurrencyDao(
        initialCurrencies: List<CurrencyEntity> = emptyList(),
    ) : CurrencyDao {
        private val currenciesFlow = MutableStateFlow(initialCurrencies)

        override suspend fun insert(currency: CurrencyEntity) {
            upsert(currency)
        }

        override suspend fun insertAll(currencies: List<CurrencyEntity>) {
            val updatedCurrencies = currenciesFlow.value.associateBy { it.id }.toMutableMap()
            currencies.forEach { currency ->
                updatedCurrencies[currency.id] = currency
            }
            currenciesFlow.value = updatedCurrencies.values.sortedBy { it.id }
        }

        override suspend fun getCurrencyById(id: String): CurrencyEntity? {
            return currenciesFlow.value.find { it.id == id }
        }

        override fun getCurrencyByIdFlow(id: String): Flow<CurrencyEntity?> {
            return currenciesFlow.map { currencies ->
                currencies.find { it.id == id }
            }
        }

        override fun getAllCurrenciesFlow(): Flow<List<CurrencyEntity>> = currenciesFlow

        override suspend fun update(currency: CurrencyEntity) {
            upsert(currency)
        }

        override suspend fun delete(currency: CurrencyEntity) {
            currenciesFlow.value = currenciesFlow.value.filterNot { it.id == currency.id }
        }

        override suspend fun deleteById(id: String) {
            currenciesFlow.value = currenciesFlow.value.filterNot { it.id == id }
        }

        private fun upsert(currency: CurrencyEntity) {
            val updatedCurrencies = currenciesFlow.value.associateBy { it.id }.toMutableMap()
            updatedCurrencies[currency.id] = currency
            currenciesFlow.value = updatedCurrencies.values.sortedBy { it.id }
        }
    }

    private fun samplePayload(): CurrencyConfigPayload {
        return CurrencyConfigPayload(
            config = CurrenciesResponse(
                disclaimer = "test",
                license = "test",
                timestamp = 1L,
                base = "USD",
                rates = mapOf(
                    "USD" to 1.0,
                    "EUR" to 0.9,
                ),
            ),
            historical = mapOf(
                "USD" to listOf(1.0, 1.1),
                "EUR" to listOf(0.9, 1.0),
            ),
        )
    }
}
