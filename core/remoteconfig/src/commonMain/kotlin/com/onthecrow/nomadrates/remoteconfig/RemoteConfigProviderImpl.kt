package com.onthecrow.nomadrates.remoteconfig

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent

internal abstract class RemoteConfigProviderImpl : KoinComponent, RemoteConfigProvider {

    private val _configDataFlow = MutableStateFlow<RemoteConfig?>(null)
    private var isBackgroundSyncStarted = false
    protected val configFields = listOf(
        KEY_FEATURED_CONVERSIONS,
        KEY_FEATURED_CURRENCIES,
        KEY_PRIVACY_POLICY_URL,
        KEY_DATA_SOURCE_URL,
    )

    override fun getRemoteConfigFlow(): Flow<RemoteConfig> {
        return _configDataFlow.filterNotNull()
            .distinctUntilChanged()
    }

    override fun refresh() {
        refreshRemoteConfig()
    }

    protected abstract fun getString(key: String): String
    protected abstract fun startBackgroundSync()
    protected abstract fun refreshRemoteConfig()

    protected fun initializeBackgroundSync() {
        if (isBackgroundSyncStarted) return
        isBackgroundSyncStarted = true
        startBackgroundSync()
    }

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

            KEY_PRIVACY_POLICY_URL -> _configDataFlow.update { configData ->
                configData?.copy(
                    privacyPolicyUrl = getUrlOrDefault(
                        key = KEY_PRIVACY_POLICY_URL,
                        defaultValue = RemoteConfig.DEFAULT_PRIVACY_POLICY_URL,
                    )
                )
            }

            KEY_DATA_SOURCE_URL -> _configDataFlow.update { configData ->
                configData?.copy(
                    dataSourceUrl = getUrlOrDefault(
                        key = KEY_DATA_SOURCE_URL,
                        defaultValue = RemoteConfig.DEFAULT_DATA_SOURCE_URL,
                    )
                )
            }

            else -> updateWholeConfig()
        }
    }

    private fun updateWholeConfig() {
        val featuredCurrencies = getString(key = KEY_FEATURED_CURRENCIES)
        val featuredConversions = getString(key = KEY_FEATURED_CONVERSIONS)
        _configDataFlow.update {
            RemoteConfig(
                featuredCurrencies = featuredCurrencies.toFeaturedCurrencies(),
                featuredConversions = featuredConversions.toFeaturedConversions(),
                privacyPolicyUrl = getUrlOrDefault(
                    key = KEY_PRIVACY_POLICY_URL,
                    defaultValue = RemoteConfig.DEFAULT_PRIVACY_POLICY_URL,
                ),
                dataSourceUrl = getUrlOrDefault(
                    key = KEY_DATA_SOURCE_URL,
                    defaultValue = RemoteConfig.DEFAULT_DATA_SOURCE_URL,
                ),
            )
        }
    }

    private fun String.toFeaturedConversions(): List<Pair<String, String>> {
        if (isBlank()) return emptyList()
        return this.split(DELIMITER_SEMICOLON)
            .mapNotNull {
                val rawConversions = it.split(DELIMITER_SLASH)
                    .take(2)
                    .filter { value -> value.isNotBlank() }
                if (rawConversions.size < 2) return@mapNotNull null
                rawConversions.first() to rawConversions.last()
            }
    }

    private fun String.toFeaturedCurrencies(): List<String> {
        if (isBlank()) return emptyList()
        return this.split(DELIMITER_SEMICOLON)
            .filter { it.isNotBlank() }
    }

    private fun getUrlOrDefault(
        key: String,
        defaultValue: String,
    ): String {
        return getString(key = key).ifBlank { defaultValue }
    }

    companion object Companion {
        protected const val KEY_FEATURED_CONVERSIONS = "featured_conversions"
        protected const val KEY_FEATURED_CURRENCIES = "featured_currencies"
        protected const val KEY_PRIVACY_POLICY_URL = "privacy_policy_url"
        protected const val KEY_DATA_SOURCE_URL = "data_source_url"
        private const val DELIMITER_SEMICOLON = ";"
        private const val DELIMITER_SLASH = "/"
    }
}
