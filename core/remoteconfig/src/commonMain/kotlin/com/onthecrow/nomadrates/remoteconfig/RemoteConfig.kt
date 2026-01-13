package com.onthecrow.nomadrates.remoteconfig

data class RemoteConfig(
    val featuredCurrencies: List<String>,
    val featuredConversions: List<Pair<String, String>>,
)
