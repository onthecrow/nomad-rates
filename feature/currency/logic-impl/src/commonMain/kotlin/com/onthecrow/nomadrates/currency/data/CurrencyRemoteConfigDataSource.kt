package com.onthecrow.nomadrates.currency.data

import com.onthecrow.nomadrates.currency.model.CurrenciesResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class CurrencyRemoteConfigDataSource : KoinComponent {

    private val json: Json by inject()
    private val _configDataFlow = MutableStateFlow<CurrenciesResponse?>(null)
    private val _historicalDataFlow = MutableStateFlow<Map<String, List<Float>>?>(null)

    val configDataFlow = _configDataFlow.asStateFlow()
    val historicalDataFlow = _historicalDataFlow.asStateFlow()

    init {
        startBackgroundSync()
    }

    protected abstract fun getString(key: String): String
    protected abstract fun getKeysByPrefix(prefix: String): Set<String>
    protected abstract fun startBackgroundSync()

    protected fun updateData() {
        val data = getString(key = KEY_DATA)
        if (data.isBlank()) return
        _configDataFlow.update { json.decodeFromString<CurrenciesResponse>(data) }
    }

    protected fun updateHistoricalData() {
        val keys = getKeysByPrefix(prefix = PREFIX_CURRENCY)
        _historicalDataFlow.update {
            keys.associateWith { key ->
                getString(key).split(DELIMITER_HISTORICAL_VALUE)
                    .map(String::toFloat)
            }
        }
    }

    companion object {
        protected const val PREFIX_CURRENCY = "currency_"
        protected const val KEY_DATA = "exchange_rates"
        private const val DELIMITER_HISTORICAL_VALUE = ";"
    }
}
