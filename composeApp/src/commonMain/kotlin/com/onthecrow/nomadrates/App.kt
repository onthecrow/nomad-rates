package com.onthecrow.nomadrates

import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.onthecrow.nomadrates.currency.CurrencyListRoute
import com.onthecrow.nomadrates.navigation.FeatureEntry
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.getKoin

@Composable
@Preview
fun App() {
    MaterialTheme {
        val koin = getKoin()
        val entries = remember(koin) { koin.getAll<FeatureEntry<*>>() }
        val entryMap = remember(entries) { entries.associateBy { it.keyClass } }
        val config = remember(entries) {
            SavedStateConfiguration {
                serializersModule = SerializersModule {
                    polymorphic(NavKey::class) {
                        entries.forEach { entry ->
                            @Suppress("UNCHECKED_CAST")
                            val entry = entry as FeatureEntry<NavKey>
                            subclass(entry.keyClass, entry.serializer)
                        }
                    }
                }
            }
        }
        val backStack = rememberNavBackStack(config, CurrencyListRoute)

        NavDisplay(
            modifier = Modifier.systemBarsPadding()
                .imePadding(),
            backStack = backStack,
        ) { key ->
            NavEntry(
                key = key,
                content = @Composable {
                    val entry = entryMap[key::class]

                    @Suppress("UNCHECKED_CAST")
                    val typedEntry = entry as? FeatureEntry<NavKey>
                    typedEntry?.content?.invoke(key, Modifier)
                },
            )
        }
    }
}
