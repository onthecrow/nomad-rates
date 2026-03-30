package com.onthecrow.nomadrates.currency.data

import com.onthecrow.nomadrates.currency.model.CurrenciesResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal data class CurrencyConfigPayload(
    val config: CurrenciesResponse,
    val historical: Map<String, List<Double>>
)

internal abstract class CurrencyRemoteDataSource : KoinComponent {

    private val json: Json by inject()

    // todo inject
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _state = MutableStateFlow<Result<CurrencyConfigPayload>?>(null)
    val state: StateFlow<Result<CurrencyConfigPayload>?> = _state.asStateFlow()

    protected fun emitState(value: Result<CurrencyConfigPayload>?) {
        _state.value = value
    }

    protected fun start() {
        startBackgroundSync(
            onActivated = { scope.launch { loadFromLocalAndEmit() } },
            onError = { t -> emitState(Result.failure(t)) }
        )

        refresh()
    }

    fun refresh() {
        emitState(null)
        scope.launch { refreshInternal() }
    }

    private suspend fun refreshInternal() {
        runCatching {
            withTimeout(REMOTE_CONFIG_TIMEOUT_MS) {
                val ok = fetchAndActivate()
                if (!ok) error(RemoteConfigFetchFailedException("fetchAndActivate() returned false"))
            }
            loadFromLocalAndEmit()
        }.onFailure { t ->
            emitState(Result.failure(t))
        }
    }

    private suspend fun loadFromLocalAndEmit() {
        runCatching {
            val payload = readAndParsePayload()
            emitState(Result.success(payload))
        }.onFailure { t ->
            emitState(Result.failure(t))
        }
    }


    private fun readAndParsePayload(): CurrencyConfigPayload {
        val raw = getString(key = KEY_DATA).trim()
        if (raw.isBlank()) throw MissingRemoteConfigKeyException(KEY_DATA)

        val config = try {
            json.decodeFromString<CurrenciesResponse>(raw)
        } catch (e: Throwable) {
            throw RemoteConfigParseException("Failed to parse $KEY_DATA", e)
        }

        val keys = getKeysByPrefix(prefix = PREFIX_CURRENCY)
        val historical = keys.associate { key ->
            val code = key.removePrefix(PREFIX_CURRENCY)
            val valuesRaw = getString(key).trim()
            if (valuesRaw.isBlank()) {
                throw MissingRemoteConfigKeyException(key)
            }

            val list = try {
                valuesRaw
                    .split(DELIMITER_HISTORICAL_VALUE)
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .map { it.toDouble() }
            } catch (e: Throwable) {
                throw RemoteConfigParseException("Failed to parse historical values for $key", e)
            }

            code to list
        }

        return CurrencyConfigPayload(
            config = config,
            historical = historical
        )
    }

    protected abstract fun getString(key: String): String
    protected abstract fun getKeysByPrefix(prefix: String): Set<String>
    protected abstract fun startBackgroundSync(
        onActivated: () -> Unit,
        onError: (Throwable) -> Unit
    )

    protected abstract suspend fun fetchAndActivate(): Boolean

    companion object Companion {
        protected const val PREFIX_CURRENCY = "currency_"
        protected const val KEY_DATA = "exchange_rates"
        private const val REMOTE_CONFIG_TIMEOUT_MS = 10_000L
        private const val DELIMITER_HISTORICAL_VALUE = ";"
    }
}

internal class MissingRemoteConfigKeyException(val key: String) :
    IllegalStateException("Remote Config key is missing or blank: $key")

internal class RemoteConfigParseException(message: String, cause: Throwable) :
    IllegalStateException(message, cause)

internal class RemoteConfigFetchFailedException(message: String) :
    IllegalStateException(message)
