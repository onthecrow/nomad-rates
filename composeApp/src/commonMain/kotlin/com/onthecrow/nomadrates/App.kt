package com.onthecrow.nomadrates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.onthecrow.nomadrates.currency.CurrencyListRoute
import com.onthecrow.nomadrates.navigation.FeatureEntry
import com.onthecrow.nomadrates.ui.NomadRatesTheme
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.getKoin

@Composable
@Preview
fun App() {
    NomadRatesTheme(
        // TODO take it from settings later
        dynamicColor = false,
        darkTheme = true,
    ) {
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

        Box(
            modifier = Modifier.fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            NavDisplay(
                modifier = Modifier.fillMaxSize(),
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

            // TODO move these shades to ui-kit
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(WindowInsets.systemBars.asPaddingValues().calculateTopPadding() * 2)
                    .align(Alignment.TopCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
                                Color.Transparent,
                            )
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() * 2)
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
                            )
                        )
                    )
            )
        }
    }
}
