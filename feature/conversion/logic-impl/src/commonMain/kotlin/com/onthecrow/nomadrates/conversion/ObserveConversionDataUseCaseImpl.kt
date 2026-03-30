package com.onthecrow.nomadrates.conversion

import com.onthecrow.nomadrates.conversion.domain.ConversionDataState
import com.onthecrow.nomadrates.conversion.domain.GetConversionPairUseCase
import com.onthecrow.nomadrates.conversion.domain.GetConversionPairsUseCase
import com.onthecrow.nomadrates.conversion.domain.GetSelectedConversionPairUseCase
import com.onthecrow.nomadrates.conversion.domain.ObserveConversionDataUseCase
import com.onthecrow.nomadrates.currency.domain.CurrencyBootstrapState
import com.onthecrow.nomadrates.currency.domain.ObserveCurrencyBootstrapStateUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.take

internal class ObserveConversionDataUseCaseImpl(
    private val getSelectedConversionPairUseCase: GetSelectedConversionPairUseCase,
    private val getConversionPairUseCase: GetConversionPairUseCase,
    private val getConversionPairsUseCase: GetConversionPairsUseCase,
    private val observeCurrencyBootstrapStateUseCase: ObserveCurrencyBootstrapStateUseCase,
    private val initialLoadTimeoutMs: Long = INITIAL_LOAD_TIMEOUT_MS,
) : ObserveConversionDataUseCase {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(): Flow<ConversionDataState> = flow {
        coroutineScope {
            val activePairFlow = getSelectedConversionPairUseCase()
                .flatMapLatest { selectedConversionPair ->
                    getConversionPairUseCase(
                        selectedConversionPair.fromCurrencyCode,
                        selectedConversionPair.toCurrencyCode,
                    )
                }
                .distinctUntilChanged()
            val conversionPairsFlow = getConversionPairsUseCase().distinctUntilChanged()
            val contentFlow = combine(
                activePairFlow.filterNotNull(),
                conversionPairsFlow,
            ) { activePair, conversionPairs ->
                ConversionDataState.Content(
                    activePair = activePair,
                    conversionPairs = conversionPairs,
                )
            }
                .distinctUntilChanged()
                .shareIn(
                    scope = this,
                    started = SharingStarted.Eagerly,
                    replay = 1,
                )
            val bootstrapStateFlow = observeCurrencyBootstrapStateUseCase()
                .distinctUntilChanged()
                .shareIn(
                    scope = this,
                    started = SharingStarted.Eagerly,
                    replay = 1,
                )

            when (bootstrapStateFlow.first()) {
                CurrencyBootstrapState.Cached -> {
                    emitAll(contentFlow)
                }

                CurrencyBootstrapState.Error -> {
                    emit(ConversionDataState.Error)
                }

                CurrencyBootstrapState.Loading -> {
                    emit(ConversionDataState.Loading)

                    when (
                        val initialResult = merge(
                            contentFlow.take(1).map { InitialResult.Content(it) },
                            bootstrapStateFlow
                                .dropWhile { it != CurrencyBootstrapState.Error }
                                .take(1)
                                .map { InitialResult.Error },
                            flow {
                                delay(initialLoadTimeoutMs)
                                emit(InitialResult.Error)
                            },
                        ).first()
                    ) {
                        is InitialResult.Content -> {
                            emit(initialResult.content)
                            emitAll(contentFlow.dropWhile { it == initialResult.content })
                        }

                        InitialResult.Error -> emit(ConversionDataState.Error)
                    }
                }
            }
        }
    }

    private sealed interface InitialResult {
        data class Content(val content: ConversionDataState.Content) : InitialResult
        data object Error : InitialResult
    }

    private companion object {
        private const val INITIAL_LOAD_TIMEOUT_MS = 10_000L
    }
}
