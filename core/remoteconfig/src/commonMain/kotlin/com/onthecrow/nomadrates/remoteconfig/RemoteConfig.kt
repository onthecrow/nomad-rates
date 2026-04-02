package com.onthecrow.nomadrates.remoteconfig

data class RemoteConfig(
    val featuredCurrencies: List<String>,
    val featuredConversions: List<Pair<String, String>>,
    val privacyPolicyUrl: String = DEFAULT_PRIVACY_POLICY_URL,
    val dataSourceUrl: String = DEFAULT_DATA_SOURCE_URL,
) {
    companion object {
        const val DEFAULT_PRIVACY_POLICY_URL = "https://onthecrow.github.io/nomad-rates/privacy-policy/"
        const val DEFAULT_DATA_SOURCE_URL = "https://openexchangerates.org/"
    }
}
