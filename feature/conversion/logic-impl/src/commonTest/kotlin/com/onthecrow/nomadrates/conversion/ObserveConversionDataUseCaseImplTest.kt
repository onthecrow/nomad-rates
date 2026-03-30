package com.onthecrow.nomadrates.conversion

import com.onthecrow.nomadrates.conversion.domain.ConversionDataState
import com.onthecrow.nomadrates.conversion.domain.GetConversionPairUseCase
import com.onthecrow.nomadrates.conversion.domain.GetConversionPairsUseCase
import com.onthecrow.nomadrates.conversion.domain.GetSelectedConversionPairUseCase
import com.onthecrow.nomadrates.conversion.domain.model.ConversionPair
import com.onthecrow.nomadrates.conversion.domain.model.SelectedConversionPair
import com.onthecrow.nomadrates.currency.domain.CurrencyBootstrapState
import com.onthecrow.nomadrates.currency.domain.ObserveCurrencyBootstrapStateUseCase
import com.onthecrow.nomadrates.currency.model.Currency
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveConversionDataUseCaseImplTest {

    @Test
    fun `cached currencies emit content without loading`() = runTest {
        val pair = samplePair()
        val useCase = createUseCase(
            bootstrapStateFlow = MutableStateFlow(CurrencyBootstrapState.Cached),
            activePairFlow = MutableStateFlow(pair),
            conversionPairsFlow = MutableStateFlow(listOf(pair)),
        )

        val emissions = useCase().take(1).toList()

        assertEquals(
            listOf(ConversionDataState.Content(pair, listOf(pair))),
            emissions,
        )
    }

    @Test
    fun `cached currencies emit updated content when featured pairs arrive later`() = runTest {
        val pair = samplePair()
        val conversionPairsFlow = MutableStateFlow(emptyList<ConversionPair>())
        val useCase = createUseCase(
            bootstrapStateFlow = MutableStateFlow(CurrencyBootstrapState.Cached),
            activePairFlow = MutableStateFlow(pair),
            conversionPairsFlow = conversionPairsFlow,
        )

        backgroundScope.launch {
            delay(100)
            conversionPairsFlow.value = listOf(pair)
        }

        val emissions = useCase().take(2).toList()

        assertEquals(ConversionDataState.Content(pair, emptyList()), emissions[0])
        assertEquals(ConversionDataState.Content(pair, listOf(pair)), emissions[1])
    }

    @Test
    fun `empty cache emits loading then content`() = runTest {
        val pair = samplePair()
        val activePairFlow = MutableStateFlow<ConversionPair?>(null)
        val conversionPairsFlow = MutableStateFlow(emptyList<ConversionPair>())
        val useCase = createUseCase(
            bootstrapStateFlow = MutableStateFlow(CurrencyBootstrapState.Loading),
            activePairFlow = activePairFlow,
            conversionPairsFlow = conversionPairsFlow,
        )

        backgroundScope.launch {
            delay(100)
            conversionPairsFlow.value = listOf(pair)
            activePairFlow.value = pair
        }

        val emissions = useCase().take(2).toList()

        assertEquals(ConversionDataState.Loading, emissions[0])
        assertEquals(ConversionDataState.Content(pair, listOf(pair)), emissions[1])
    }

    @Test
    fun `empty cache emits loading then error on bootstrap error`() = runTest {
        val bootstrapStateFlow = MutableStateFlow<CurrencyBootstrapState>(CurrencyBootstrapState.Loading)
        val useCase = createUseCase(
            bootstrapStateFlow = bootstrapStateFlow,
        )

        backgroundScope.launch {
            delay(100)
            bootstrapStateFlow.value = CurrencyBootstrapState.Error
        }

        val emissions = useCase().take(2).toList()

        assertEquals(listOf(ConversionDataState.Loading, ConversionDataState.Error), emissions)
    }

    @Test
    fun `empty cache emits loading then error on timeout`() = runTest {
        val useCase = createUseCase(
            bootstrapStateFlow = MutableStateFlow(CurrencyBootstrapState.Loading),
            initialLoadTimeoutMs = 100L,
        )

        val emissions = useCase().take(2).toList()

        assertEquals(listOf(ConversionDataState.Loading, ConversionDataState.Error), emissions)
    }

    @Test
    fun `failure after first content is ignored`() = runTest {
        val pair = samplePair()
        val bootstrapStateFlow = MutableStateFlow<CurrencyBootstrapState>(CurrencyBootstrapState.Loading)
        val activePairFlow = MutableStateFlow<ConversionPair?>(null)
        val conversionPairsFlow = MutableStateFlow(emptyList<ConversionPair>())
        val useCase = createUseCase(
            bootstrapStateFlow = bootstrapStateFlow,
            activePairFlow = activePairFlow,
            conversionPairsFlow = conversionPairsFlow,
        )

        val emissions = mutableListOf<ConversionDataState>()
        val collectionJob = launch(start = CoroutineStart.UNDISPATCHED) {
            useCase().toList(emissions)
        }

        activePairFlow.value = pair
        conversionPairsFlow.value = listOf(pair)
        advanceUntilIdle()

        bootstrapStateFlow.value = CurrencyBootstrapState.Error
        advanceUntilIdle()

        assertEquals(
            listOf(
                ConversionDataState.Loading,
                ConversionDataState.Content(pair, listOf(pair)),
            ),
            emissions,
        )
        collectionJob.cancel()
    }

    private fun createUseCase(
        bootstrapStateFlow: MutableStateFlow<CurrencyBootstrapState> = MutableStateFlow(
            CurrencyBootstrapState.Loading,
        ),
        activePairFlow: MutableStateFlow<ConversionPair?> = MutableStateFlow(null),
        conversionPairsFlow: MutableStateFlow<List<ConversionPair>> = MutableStateFlow(emptyList()),
        initialLoadTimeoutMs: Long = 10_000L,
    ): ObserveConversionDataUseCaseImpl {
        return ObserveConversionDataUseCaseImpl(
            getSelectedConversionPairUseCase = FakeGetSelectedConversionPairUseCase(),
            getConversionPairUseCase = FakeGetConversionPairUseCase(activePairFlow),
            getConversionPairsUseCase = FakeGetConversionPairsUseCase(conversionPairsFlow),
            observeCurrencyBootstrapStateUseCase = FakeObserveCurrencyBootstrapStateUseCase(
                bootstrapStateFlow,
            ),
            initialLoadTimeoutMs = initialLoadTimeoutMs,
        )
    }

    private class FakeGetSelectedConversionPairUseCase : GetSelectedConversionPairUseCase {
        override fun invoke(): Flow<SelectedConversionPair> = MutableStateFlow(SelectedConversionPair.DEFAULT)
    }

    private class FakeGetConversionPairUseCase(
        private val flow: MutableStateFlow<ConversionPair?>,
    ) : GetConversionPairUseCase {
        override fun invoke(fromCurrencyCode: String, toCurrencyCode: String): Flow<ConversionPair?> = flow
    }

    private class FakeGetConversionPairsUseCase(
        private val flow: MutableStateFlow<List<ConversionPair>>,
    ) : GetConversionPairsUseCase {
        override fun invoke(): Flow<List<ConversionPair>> = flow
    }

    private class FakeObserveCurrencyBootstrapStateUseCase(
        private val flow: MutableStateFlow<CurrencyBootstrapState>,
    ) : ObserveCurrencyBootstrapStateUseCase {
        override fun invoke(): Flow<CurrencyBootstrapState> = flow
    }

    private fun samplePair(
        fromCode: String = "USD",
        toCode: String = "EUR",
    ): ConversionPair {
        return ConversionPair(
            fromCurrency = Currency(
                code = fromCode,
                conversionRate = 1.0,
                isFavourite = false,
                isFeatured = false,
                rates = listOf(1.0, 1.1),
            ),
            toCurrency = Currency(
                code = toCode,
                conversionRate = 0.9,
                isFavourite = false,
                isFeatured = false,
                rates = listOf(0.9, 1.0),
            ),
            conversionRate = 0.9,
            historicalRates = listOf(0.8, 0.9),
            isFeatured = true,
            isFavourite = false,
        )
    }
}
