package com.onthecrow.nomadrates.settings

import com.onthecrow.nomadrates.remoteconfig.RemoteConfigProvider
import com.onthecrow.nomadrates.settings.domain.ObserveSettingsLinksUseCase
import com.onthecrow.nomadrates.settings.domain.SettingsLinks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

internal class ObserveSettingsLinksUseCaseImpl(
    private val remoteConfigProvider: RemoteConfigProvider,
) : ObserveSettingsLinksUseCase {
    override fun invoke(): Flow<SettingsLinks> {
        return remoteConfigProvider.getRemoteConfigFlow()
            .onStart {
                emit(
                    com.onthecrow.nomadrates.remoteconfig.RemoteConfig(
                        featuredCurrencies = emptyList(),
                        featuredConversions = emptyList(),
                    )
                )
            }
            .map { remoteConfig ->
                SettingsLinks(
                    privacyPolicyUrl = remoteConfig.privacyPolicyUrl,
                    dataSourceUrl = remoteConfig.dataSourceUrl,
                )
            }
    }
}
