package com.onthecrow.nomadrates.remoteconfig

import kotlinx.coroutines.flow.Flow

interface RemoteConfigProvider {
    fun getRemoteConfigFlow(): Flow<RemoteConfig>
    fun refresh()
}
