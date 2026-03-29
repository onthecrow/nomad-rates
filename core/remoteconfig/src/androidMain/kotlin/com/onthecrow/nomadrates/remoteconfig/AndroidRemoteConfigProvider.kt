package com.onthecrow.nomadrates.remoteconfig

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.remoteConfig

internal class AndroidRemoteConfigProvider : RemoteConfigProviderImpl() {

    // TODO to di
    private val remoteConfig = Firebase.remoteConfig

    override fun startBackgroundSync() {
        Firebase.remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
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
        Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(
                    "NomadRatesFirebase",
                    "RemoteConfig initial fetch succeeded: lastFetchStatus=${remoteConfig.info.lastFetchStatus}"
                )
                onConfigUpdated()
            } else {
                Log.e(
                    "NomadRatesFirebase",
                    "RemoteConfig initial fetch failed: lastFetchStatus=${remoteConfig.info.lastFetchStatus}, error=${task.exception?.message}",
                    task.exception,
                )
            }
        }
    }

    override fun getString(key: String): String {
        return Firebase.remoteConfig.getString(key)
    }
}
