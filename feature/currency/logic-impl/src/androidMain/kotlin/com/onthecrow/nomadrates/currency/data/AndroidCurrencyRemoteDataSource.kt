package com.onthecrow.nomadrates.currency.data

import android.util.Log
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class AndroidCurrencyRemoteDataSource(
    private val remoteConfig: FirebaseRemoteConfig,
) : CurrencyRemoteDataSource() {

    init {
        start()
    }

    override fun startBackgroundSync(
        onActivated: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                remoteConfig.activate().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onActivated()
                    } else {
                        onError(task.exception ?: RuntimeException("activate() failed"))
                    }
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Log.e(javaClass.simpleName, error.message, error)
                onError(error)
            }
        })
    }

    override suspend fun fetchAndActivate(): Boolean {
        return suspendCoroutine { cont ->
            remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                cont.resume(task.isSuccessful)
            }
        }
    }

    override fun getString(key: String): String {
        return remoteConfig.getString(key)
    }

    override fun getKeysByPrefix(prefix: String): Set<String> {
        return remoteConfig.getKeysByPrefix(prefix)
    }
}
