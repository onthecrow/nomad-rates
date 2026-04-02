package com.onthecrow.nomadrates.conversion

import androidx.lifecycle.viewModelScope
import com.onthecrow.nomadrates.conversion.domain.ConversionDataState
import com.onthecrow.nomadrates.conversion.domain.ConvertCurrenciesUseCase
import com.onthecrow.nomadrates.conversion.domain.GetSelectedConversionPairUseCase
import com.onthecrow.nomadrates.conversion.domain.ObserveConversionDataUseCase
import com.onthecrow.nomadrates.conversion.domain.SaveSelectedConversionPairUseCase
import com.onthecrow.nomadrates.conversion.domain.ToggleConversionPairFavouriteUseCase
import com.onthecrow.nomadrates.conversion.domain.model.ConversionPair
import com.onthecrow.nomadrates.conversion.domain.model.SelectedConversionPair
import com.onthecrow.nomadrates.currency.CurrencySelectionSource
import com.onthecrow.nomadrates.currency.CurrencyListDestination
import com.onthecrow.nomadrates.currency.CurrencyListScreenResult
import com.onthecrow.nomadrates.currency.domain.RefreshCurrenciesUseCase
import com.onthecrow.nomadrates.navigation.Navigator
import com.onthecrow.nomadrates.navigation.ScreenResultDispatcher
import com.onthecrow.nomadrates.settings.SettingsDestination
import com.onthecrow.nomadrates.settings.domain.ObserveShowFeaturedPairsUseCase
import com.onthecrow.nomadrates.uicore.BaseViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
internal class ConversionViewModel(
    private val navigator: Navigator,
    private val convertCurrenciesUseCase: ConvertCurrenciesUseCase,
    private val observeConversionDataUseCase: ObserveConversionDataUseCase,
    private val observeShowFeaturedPairsUseCase: ObserveShowFeaturedPairsUseCase,
    private val getSelectedConversionPairUseCase: GetSelectedConversionPairUseCase,
    private val refreshCurrenciesUseCase: RefreshCurrenciesUseCase,
    private val saveSelectedConversionPairUseCase: SaveSelectedConversionPairUseCase,
    private val toggleConversionPairFavouriteUseCase: ToggleConversionPairFavouriteUseCase,
    reducer: ConversionReducer,
    screenResultDispatcher: ScreenResultDispatcher,
) : BaseViewModel<ConversionEvent, ConversionState, ConversionReducer>(reducer) {
    private val selectedConversionPairStateFlow: StateFlow<SelectedConversionPair> =
        getSelectedConversionPairUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = SelectedConversionPair.DEFAULT,
            )
    internal val showFeaturedPairsStateFlow: StateFlow<Boolean> =
        observeShowFeaturedPairsUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = true,
            )

    private val retryTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    init {
        eventFlow.onEach { event ->
            when (event) {
                is ConversionEvent.OnBackPress -> onBackPress()
                is ConversionEvent.OnRetryClick -> retryTrigger.tryEmit(Unit)
                is ConversionEvent.OnSettingsClick -> navigator.navigate(SettingsDestination)
                is ConversionEvent.OnToCurrencyClick -> onToCurrencyChangeClick()
                is ConversionEvent.OnFromCurrencyClick -> onFromCurrencyChangeClick()
                is ConversionEvent.OnSwitchButtonPress -> onSwitchButtonClick()
                is ConversionEvent.OnFromValueChange -> onFromCurrencyValueChange(event.value)
                is ConversionEvent.OnToValueChange -> onToCurrencyValueChange(event.value)
                is ConversionEvent.OnActiveConversionPairFavouritesClick -> toggleFavourites()
                is ConversionEvent.OnConversionPairFavouritesClick -> toggleFavourites(event.currencyPair)
                is ConversionEvent.OnConversionViewClick -> onListItemClick(event.conversionPair)
                else -> {}
            }
        }
            .launchIn(viewModelScope)

        screenResultDispatcher.resultFlow
            .onEach { screenResult ->
                when (screenResult) {
                    is CurrencyListScreenResult -> onCurrencySelected(screenResult)
                }
            }
            .launchIn(viewModelScope)

        merge(
            flowOf(false),
            retryTrigger.map { true },
        )
            .onEach { shouldRefresh ->
                if (shouldRefresh) {
                    refreshCurrenciesUseCase()
                }
            }
            .flatMapLatest {
                observeConversionDataUseCase()
                    .combine(showFeaturedPairsStateFlow) { dataState, showFeaturedPairs ->
                        dataState.filtered(showFeaturedPairs)
                    }
                    .runningFold(ConversionDataTransition()) { transition, current ->
                        ConversionDataTransition(
                            previous = transition.current,
                            current = current,
                        )
                    }
                    .drop(1)
            }
            .onEach(::handleConversionDataTransition)
            .launchIn(viewModelScope)
    }

    override fun getInitialState(): ConversionState = ConversionState.Content()

    private fun handleConversionDataTransition(transition: ConversionDataTransition) {
        val currentState = transition.current ?: return
        val fromValue = (state.value as? ConversionState.Content)
            ?.conversionViewState
            ?.from
            ?.conversionValue

        onEvent(
            ConversionEvent.OnConversionDataChanged(
                dataState = currentState,
            )
        )

        val previousContentState = transition.previous as? ConversionDataState.Content ?: return
        val currentContentState = currentState as? ConversionDataState.Content ?: return

        maybeHighlightNewFavourite(previousContentState, currentContentState)

        if (previousContentState.activePair.identity() != currentContentState.activePair.identity() &&
            !fromValue.isNullOrBlank()
        ) {
            onEvent(ConversionEvent.OnFromValueChange(fromValue))
        }
    }

    private fun maybeHighlightNewFavourite(
        previousContentState: ConversionDataState.Content,
        newContentState: ConversionDataState.Content,
    ) {
        val diff = (newContentState.conversionPairs - previousContentState.conversionPairs.toSet())
        if (diff.size == 1 && diff.first().isFavourite) {
            scrollToItemAndHighlight(diff.first())
        }
    }

    // todo refactor and maybe move this logic to compose
    private fun scrollToItemAndHighlight(item: ConversionPair) {
        viewModelScope.launch {
            val conversionPair = item.fromCurrency.code to item.toCurrency.code
            delay(100)
            onEvent(
                ConversionEvent.OnItemAddToFavourite(conversionPair)
            )
            delay(150)

            repeat(2) {
                delay(150)
                onEvent(
                    ConversionEvent.HighlightConversionPair(
                        conversionPair,
                        shouldHighlight = true,
                    )
                )
                delay(150)
                onEvent(
                    ConversionEvent.HighlightConversionPair(
                        conversionPair,
                        shouldHighlight = false,
                    )
                )
            }
        }
    }

    private fun onListItemClick(conversionPair: Pair<String, String>) {
        updateSelectedConversionPair(
            SelectedConversionPair(
                fromCurrencyCode = conversionPair.first,
                toCurrencyCode = conversionPair.second,
            )
        )
    }

    private fun toggleFavourites(currencyPair: Pair<String, String>? = null) {
        val selectedConversionPair = selectedConversionPairStateFlow.value
        val conversionPair = currencyPair
            ?: (selectedConversionPair.fromCurrencyCode to selectedConversionPair.toCurrencyCode)
        viewModelScope.launch {
            toggleConversionPairFavouriteUseCase(
                conversionPair.first,
                conversionPair.second,
            )
        }
    }

    private fun onCurrencySelected(result: CurrencyListScreenResult) {
        val selectedConversionPair = selectedConversionPairStateFlow.value
        val updatedPair = when (result.source) {
            CurrencySelectionSource.ConversionFrom -> selectedConversionPair.copy(
                fromCurrencyCode = result.selectedCurrencyCode,
            )

            CurrencySelectionSource.ConversionTo -> selectedConversionPair.copy(
                toCurrencyCode = result.selectedCurrencyCode,
            )

            CurrencySelectionSource.SettingsDefaultFrom,
            CurrencySelectionSource.SettingsDefaultTo -> return
        }
        updateSelectedConversionPair(updatedPair)
    }

    private fun onFromCurrencyValueChange(value: String) {
        viewModelScope.launch {
            val selectedConversionPair = selectedConversionPairStateFlow.value
            runCatching {
                convertCurrenciesUseCase(
                    fromCurrencyCode = selectedConversionPair.fromCurrencyCode,
                    toCurrencyCode = selectedConversionPair.toCurrencyCode,
                    amount = value.toDouble(),
                )
            }
                .onSuccess { onEvent(ConversionEvent.OnToValueConverted(it.toString())) }
                .onFailure {
                    // TODO add error handling
                }
        }
    }

    private fun onFromCurrencyChangeClick() {
        navigator.navigate(CurrencyListDestination(CurrencySelectionSource.ConversionFrom))
    }

    private fun onSwitchButtonClick() {
        val selectedConversionPair = selectedConversionPairStateFlow.value
        updateSelectedConversionPair(
            SelectedConversionPair(
                fromCurrencyCode = selectedConversionPair.toCurrencyCode,
                toCurrencyCode = selectedConversionPair.fromCurrencyCode,
            )
        )
    }

    private fun onToCurrencyValueChange(value: String) {
        viewModelScope.launch {
            val selectedConversionPair = selectedConversionPairStateFlow.value
            runCatching {
                convertCurrenciesUseCase(
                    fromCurrencyCode = selectedConversionPair.toCurrencyCode,
                    toCurrencyCode = selectedConversionPair.fromCurrencyCode,
                    amount = value.toDouble(),
                )
            }
                .onSuccess { onEvent(ConversionEvent.OnFromValueConverted(it.toString())) }
                .onFailure {
                    // TODO add error handling
                }
        }
    }

    private fun onToCurrencyChangeClick() {
        navigator.navigate(CurrencyListDestination(CurrencySelectionSource.ConversionTo))
    }

    private fun onBackPress() {
        navigator.navigateBack()
    }

    private fun updateSelectedConversionPair(selectedConversionPair: SelectedConversionPair) {
        if (selectedConversionPair == selectedConversionPairStateFlow.value) return

        viewModelScope.launch {
            saveSelectedConversionPairUseCase(selectedConversionPair)
        }
    }

    private fun ConversionPair.identity(): Pair<String, String> {
        return fromCurrency.code to toCurrency.code
    }

    private fun ConversionDataState.filtered(showFeaturedPairs: Boolean): ConversionDataState {
        return when (this) {
            is ConversionDataState.Content -> copy(
                conversionPairs = if (showFeaturedPairs) {
                    conversionPairs
                } else {
                    conversionPairs.filterNot { conversionPair -> conversionPair.isFeatured }
                }
            )

            ConversionDataState.Error,
            ConversionDataState.Loading -> this
        }
    }

    private data class ConversionDataTransition(
        val previous: ConversionDataState? = null,
        val current: ConversionDataState? = null,
    )
}
