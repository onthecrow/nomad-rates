package com.onthecrow.nomadrates.remoteconfig

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent

internal abstract class RemoteConfigProviderImpl : KoinComponent, RemoteConfigProvider {

    private val _configDataFlow = MutableStateFlow<RemoteConfig?>(null)
    protected val configFields = listOf(KEY_FEATURED_CONVERSIONS, KEY_FEATURED_CURRENCIES)

    init {
        startBackgroundSync()
    }

    override fun getRemoteConfigFlow(): Flow<RemoteConfig> {
        return _configDataFlow.filterNotNull()
            .distinctUntilChanged()
    }

    protected abstract fun getString(key: String): String
    protected abstract fun startBackgroundSync()

    protected fun onConfigUpdated(configField: String? = null) {
        when (configField) {
            KEY_FEATURED_CONVERSIONS -> _configDataFlow.update { configData ->
                configData?.copy(
                    featuredConversions = getString(
                        key = KEY_FEATURED_CONVERSIONS
                    ).toFeaturedConversions()
                )
            }

            KEY_FEATURED_CURRENCIES -> _configDataFlow.update { configData ->
                configData?.copy(
                    featuredCurrencies = getString(
                        key = KEY_FEATURED_CURRENCIES
                    ).toFeaturedCurrencies()
                )
            }

            else -> updateWholeConfig()
        }
    }

    private fun updateWholeConfig() {
        val featuredCurrencies = getString(key = KEY_FEATURED_CURRENCIES)
        val featuredConversions = getString(key = KEY_FEATURED_CONVERSIONS)
        if (featuredCurrencies.isBlank() || featuredConversions.isBlank()) return
        _configDataFlow.update {
            RemoteConfig(
                featuredCurrencies = featuredCurrencies.toFeaturedCurrencies(),
                featuredConversions = featuredConversions.toFeaturedConversions(),
            )
        }
    }

    private fun String.toFeaturedConversions(): List<Pair<String, String>> {
        return this.split(DELIMITER_SEMICOLON)
            .map {
                val rawConversions = it.split(DELIMITER_SLASH)
                    .take(2)
                rawConversions.first() to rawConversions.last()
            }
    }

    private fun String.toFeaturedCurrencies(): List<String> {
        return this.split(DELIMITER_SEMICOLON)
    }

    companion object Companion {
        protected const val KEY_FEATURED_CONVERSIONS = "featured_conversions"
        protected const val KEY_FEATURED_CURRENCIES = "featured_currencies"
        private const val DELIMITER_SEMICOLON = ";"
        private const val DELIMITER_SLASH = "/"
    }
}
