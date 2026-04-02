package com.onthecrow.nomadrates.remoteconfig

import android.util.Log
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import org.koin.core.component.inject

internal class AndroidRemoteConfigProvider : RemoteConfigProviderImpl() {

    private val remoteConfig: FirebaseRemoteConfig by inject()

    init {
        remoteConfig.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(MIN_REMOTE_CONFIG_FETCH_WINDOW.inWholeSeconds)
                .build()
        )
        initializeBackgroundSync()
    }

    override fun startBackgroundSync() {
        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                remoteConfig.activate().addOnCompleteListener {
                    Log.d(
                        "NomadRatesFirebase",
                        "RemoteConfig update received: keys=${configUpdate.updatedKeys}, activateSuccess=${it.isSuccessful}, error=${it.exception?.message}"
                    )
                    configFields.forEach { configField ->
                        if (configUpdate.updatedKeys.contains(configField)) {
                            onConfigUpdated(configField)
                        }
                    }
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Log.e(javaClass.simpleName, error.message, error)
            }
        })

        refresh()
    }

    override fun refreshRemoteConfig() {
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(
                    "NomadRatesFirebase",
                    "RemoteConfig fetch succeeded: lastFetchStatus=${remoteConfig.info.lastFetchStatus}"
                )
                onConfigUpdated()
            } else {
                Log.e(
                    "NomadRatesFirebase",
                    "RemoteConfig fetch failed: lastFetchStatus=${remoteConfig.info.lastFetchStatus}, error=${task.exception?.message}",
                    task.exception,
                )
            }
        }
    }

    override fun getString(key: String): String {
        return remoteConfig.getString(key)
    }
}
