package com.onthecrow.nomadrates.conversion

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.onthecrow.nomadrates.conversion.data.ConversionSelectionRepository
import com.onthecrow.nomadrates.conversion.data.datastore.ConversionPreferencesDataSource
import com.onthecrow.nomadrates.conversion.domain.model.SelectedConversionPair
import com.onthecrow.nomadrates.settings.domain.LaunchPairMode
import com.onthecrow.nomadrates.settings.domain.ObserveDefaultPairUseCase
import com.onthecrow.nomadrates.settings.domain.ObserveLaunchPairModeUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import okio.Path.Companion.toPath

@OptIn(ExperimentalCoroutinesApi::class)
class GetSelectedConversionPairUseCaseImplTest {

    @Test
    fun `use default pair applies only on cold start`() = runTest {
        val dataSource = createPreferencesDataSource(backgroundScope)
        val repository = ConversionSelectionRepository(dataSource)
        val previouslySavedPair = SelectedConversionPair("HUF", "EUR")
        val currentSessionPair = SelectedConversionPair("GBP", "JPY")
        val defaultPair = SelectedConversionPair("USD", "EUR")

        dataSource.saveSelectedConversionPair(previouslySavedPair)

        val useCase = GetSelectedConversionPairUseCaseImpl(
            conversionSelectionRepository = repository,
            observeDefaultPairUseCase = FakeObserveDefaultPairUseCase(
                MutableStateFlow(defaultPair),
            ),
            observeLaunchPairModeUseCase = FakeObserveLaunchPairModeUseCase(
                MutableStateFlow(LaunchPairMode.USE_DEFAULT_PAIR),
            ),
        )

        assertEquals(defaultPair, useCase().first())

        repository.saveSelectedConversionPair(currentSessionPair)

        assertEquals(currentSessionPair, useCase().first())
    }

    @Test
    fun `launch mode changes do not affect active session pair`() = runTest {
        val dataSource = createPreferencesDataSource(backgroundScope)
        val repository = ConversionSelectionRepository(dataSource)
        val launchModeFlow = MutableStateFlow(LaunchPairMode.REMEMBER_LAST_PAIR)
        val defaultPairFlow = MutableStateFlow(SelectedConversionPair.DEFAULT)
        val savedPair = SelectedConversionPair("HUF", "EUR")
        val updatedPair = SelectedConversionPair("GBP", "USD")

        dataSource.saveSelectedConversionPair(savedPair)

        val useCase = GetSelectedConversionPairUseCaseImpl(
            conversionSelectionRepository = repository,
            observeDefaultPairUseCase = FakeObserveDefaultPairUseCase(defaultPairFlow),
            observeLaunchPairModeUseCase = FakeObserveLaunchPairModeUseCase(launchModeFlow),
        )

        val emissions = collectEmissions(useCase)
        assertEquals(savedPair, emissions.receive())

        launchModeFlow.value = LaunchPairMode.USE_DEFAULT_PAIR
        advanceUntilIdle()
        assertTrue(emissions.isEmpty)

        repository.saveSelectedConversionPair(updatedPair)
        assertEquals(updatedPair, emissions.receive())
    }

    @Test
    fun `remember last pair falls back to configured default pair when nothing is saved`() = runTest {
        val dataSource = createPreferencesDataSource(backgroundScope)
        val repository = ConversionSelectionRepository(dataSource)
        val configuredDefaultPair = SelectedConversionPair("CAD", "CHF")

        val useCase = GetSelectedConversionPairUseCaseImpl(
            conversionSelectionRepository = repository,
            observeDefaultPairUseCase = FakeObserveDefaultPairUseCase(
                MutableStateFlow(configuredDefaultPair),
            ),
            observeLaunchPairModeUseCase = FakeObserveLaunchPairModeUseCase(
                MutableStateFlow(LaunchPairMode.REMEMBER_LAST_PAIR),
            ),
        )

        assertEquals(configuredDefaultPair, useCase().first())
    }

    @Test
    fun `changing default pair updates current session when active pair equals old default`() = runTest {
        val dataSource = createPreferencesDataSource(backgroundScope)
        val repository = ConversionSelectionRepository(dataSource)
        val defaultPairFlow = MutableStateFlow(SelectedConversionPair.DEFAULT)
        val newDefaultPair = SelectedConversionPair("GBP", "JPY")

        val useCase = GetSelectedConversionPairUseCaseImpl(
            conversionSelectionRepository = repository,
            observeDefaultPairUseCase = FakeObserveDefaultPairUseCase(defaultPairFlow),
            observeLaunchPairModeUseCase = FakeObserveLaunchPairModeUseCase(
                MutableStateFlow(LaunchPairMode.USE_DEFAULT_PAIR),
            ),
        )

        val emissions = collectEmissions(useCase)
        assertEquals(SelectedConversionPair.DEFAULT, emissions.receive())

        defaultPairFlow.value = newDefaultPair

        assertEquals(newDefaultPair, emissions.receive())
        assertEquals(newDefaultPair, repository.getSavedSelectedConversionPair())
    }

    @Test
    fun `changing default pair does not update current session when active pair differs from old default`() = runTest {
        val dataSource = createPreferencesDataSource(backgroundScope)
        val repository = ConversionSelectionRepository(dataSource)
        val defaultPairFlow = MutableStateFlow(SelectedConversionPair.DEFAULT)
        val currentSessionPair = SelectedConversionPair("AUD", "NZD")
        val newDefaultPair = SelectedConversionPair("GBP", "JPY")

        val useCase = GetSelectedConversionPairUseCaseImpl(
            conversionSelectionRepository = repository,
            observeDefaultPairUseCase = FakeObserveDefaultPairUseCase(defaultPairFlow),
            observeLaunchPairModeUseCase = FakeObserveLaunchPairModeUseCase(
                MutableStateFlow(LaunchPairMode.USE_DEFAULT_PAIR),
            ),
        )

        val emissions = collectEmissions(useCase)
        assertEquals(SelectedConversionPair.DEFAULT, emissions.receive())

        repository.saveSelectedConversionPair(currentSessionPair)
        assertEquals(currentSessionPair, emissions.receive())

        defaultPairFlow.value = newDefaultPair
        advanceUntilIdle()

        assertTrue(emissions.isEmpty)
        assertEquals(currentSessionPair, repository.getSavedSelectedConversionPair())
    }

    /**
     * Everything [useCase] emits. DataStore reads and writes files on its own threads, outside the
     * test's virtual time, so emissions are awaited with [Channel.receive] rather than expected right
     * after [advanceUntilIdle].
     */
    private fun TestScope.collectEmissions(
        useCase: GetSelectedConversionPairUseCaseImpl,
    ): ReceiveChannel<SelectedConversionPair> {
        val emissions = Channel<SelectedConversionPair>(Channel.UNLIMITED)
        backgroundScope.launch {
            useCase().collect { emissions.send(it) }
        }
        return emissions
    }

    private fun createPreferencesDataSource(
        scope: CoroutineScope,
    ): ConversionPreferencesDataSource {
        return ConversionPreferencesDataSource(
            PreferenceDataStoreFactory.createWithPath(
                scope = scope,
                produceFile = {
                    "/tmp/get_selected_pair_${Random.nextInt()}.preferences_pb".toPath()
                },
            ),
        )
    }

    private class FakeObserveLaunchPairModeUseCase(
        private val flow: MutableStateFlow<LaunchPairMode>,
    ) : ObserveLaunchPairModeUseCase {
        override fun invoke(): Flow<LaunchPairMode> = flow
    }

    private class FakeObserveDefaultPairUseCase(
        private val flow: MutableStateFlow<SelectedConversionPair>,
    ) : ObserveDefaultPairUseCase {
        override fun invoke(): Flow<SelectedConversionPair> = flow
    }
}
