package com.onthecrow.nomadrates.conversion

import androidx.lifecycle.viewModelScope
import com.onthecrow.nomadrates.favouritesconversion.FavouritesConversionAmountResult
import com.onthecrow.nomadrates.favouritesconversion.FavouritesConversionDestination
import com.onthecrow.nomadrates.favouritesconversion.FavouritesConversionSide
import com.onthecrow.nomadrates.conversion.domain.ConversionDataState
import com.onthecrow.nomadrates.conversion.domain.ConvertCurrenciesUseCase
import com.onthecrow.nomadrates.conversion.domain.GetSelectedConversionPairUseCase
import com.onthecrow.nomadrates.conversion.domain.ObserveConversionDataUseCase
import com.onthecrow.nomadrates.conversion.domain.SaveSelectedConversionPairUseCase
import com.onthecrow.nomadrates.conversion.domain.ToggleConversionPairFavouriteUseCase
import com.onthecrow.nomadrates.conversion.domain.model.ConversionPair
import com.onthecrow.nomadrates.conversion.domain.model.SelectedConversionPair
import com.onthecrow.nomadrates.conversion.mapper.visiblePairs
import com.onthecrow.nomadrates.util.normalizeFormattedAmount
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
import kotlinx.coroutines.flow.MutableStateFlow
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

    /**
     * The field the user changed last. When the pair changes, the other field is converted from it,
     * so what the user typed stays as it is.
     */
    private var lastEditedSide = EditedSide.FROM

    /**
     * Set by the swap button until the swapped pair comes back from the data. The fields already
     * changed places together with their amounts, so that pair change needs no conversion. Any
     * other change to the reversed pair (e.g. picking it in the list) does.
     */
    private var isSwapPending = false

    init {
        eventFlow.onEach { event ->
            when (event) {
                is ConversionEvent.OnBackPress -> onBackPress()
                is ConversionEvent.OnRetryClick -> onRetryClick()
                is ConversionEvent.OnSettingsClick -> navigator.navigate(SettingsDestination)
                is ConversionEvent.OnFavouritesConversionClick -> onFavouritesConversionClick(event.fromBottomField)
                is ConversionEvent.OnToCurrencyClick -> onToCurrencyChangeClick()
                is ConversionEvent.OnFromCurrencyClick -> onFromCurrencyChangeClick()
                is ConversionEvent.OnSwitchButtonPress -> onSwitchButtonClick()
                is ConversionEvent.OnFromValueChange -> {
                    lastEditedSide = EditedSide.FROM
                    onFromCurrencyValueChange(event.value)
                }
                is ConversionEvent.OnToValueChange -> {
                    lastEditedSide = EditedSide.TO
                    onToCurrencyValueChange(event.value)
                }
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
                    is FavouritesConversionAmountResult -> onEvent(
                        when (screenResult.side) {
                            FavouritesConversionSide.FROM -> ConversionEvent.OnFromValueChange(screenResult.amount)
                            FavouritesConversionSide.TO -> ConversionEvent.OnToValueChange(screenResult.amount)
                        }
                    )
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

    private val isRetryingStateFlow = MutableStateFlow(false)

    private fun handleConversionDataTransition(transition: ConversionDataTransition) {
        val currentState = transition.current ?: return
        resetRetryStateIfNeeded(currentState)
        val conversionViewState = (state.value as? ConversionState.Content)?.conversionViewState

        onEvent(
            ConversionEvent.OnConversionDataChanged(
                dataState = currentState,
            )
        )

        val previousContentState = transition.previous as? ConversionDataState.Content ?: return
        val currentContentState = currentState as? ConversionDataState.Content ?: return

        maybeHighlightNewFavourite(previousContentState, currentContentState)

        if (conversionViewState == null) return
        val previousPair = previousContentState.activePair.identity()
        val currentPair = currentContentState.activePair.identity()
        if (previousPair == currentPair) {
            // Same pair with new rates (e.g. after a refresh): the converted field follows them.
            if (previousContentState.activePair.conversionRate == currentContentState.activePair.conversionRate) return
        } else {
            val isSwap = isSwapPending && currentPair == previousPair.second to previousPair.first
            isSwapPending = false
            if (isSwap) return
        }

        when (lastEditedSide) {
            EditedSide.FROM -> onEvent(ConversionEvent.OnFromValueChange(conversionViewState.from.conversionValue))
            EditedSide.TO -> onEvent(ConversionEvent.OnToValueChange(conversionViewState.to.conversionValue))
        }
    }

    private fun resetRetryStateIfNeeded(currentState: ConversionDataState) {
        if (!isRetryingStateFlow.value) return

        when (currentState) {
            is ConversionDataState.Content -> {
                isRetryingStateFlow.value = false
            }

            ConversionDataState.Error -> {
                isRetryingStateFlow.value = false
                onEvent(ConversionEvent.OnRetryStateChanged(isRetrying = false))
            }

            ConversionDataState.Loading -> Unit
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
            CurrencySelectionSource.SettingsDefaultTo,
            CurrencySelectionSource.FavouritesConversionBase,
            CurrencySelectionSource.FavouritesConversionAddFavourite -> return
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
                .onSuccess { converted ->
                    converted?.let { onEvent(ConversionEvent.OnToValueConverted(it.toString())) }
                }
                .onFailure {
                    // TODO add error handling
                }
        }
    }

    private fun onFavouritesConversionClick(fromBottomField: Boolean) {
        val conversionViewState = (state.value as? ConversionState.Content)?.conversionViewState
        val field = if (fromBottomField) conversionViewState?.to else conversionViewState?.from
        navigator.navigate(
            FavouritesConversionDestination(
                baseCurrencyCode = field?.currencyCode.orEmpty(),
                amount = field?.conversionValue.orEmpty().normalizeFormattedAmount(),
                side = if (fromBottomField) FavouritesConversionSide.TO else FavouritesConversionSide.FROM,
            )
        )
    }

    private fun onFromCurrencyChangeClick() {
        navigator.navigate(CurrencyListDestination(CurrencySelectionSource.ConversionFrom))
    }

    private fun onSwitchButtonClick() {
        // The edited amount moves to the other field together with its currency.
        lastEditedSide = when (lastEditedSide) {
            EditedSide.FROM -> EditedSide.TO
            EditedSide.TO -> EditedSide.FROM
        }
        val selectedConversionPair = selectedConversionPairStateFlow.value
        isSwapPending = selectedConversionPair.fromCurrencyCode != selectedConversionPair.toCurrencyCode
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
                .onSuccess { converted ->
                    converted?.let { onEvent(ConversionEvent.OnFromValueConverted(it.toString())) }
                }
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

    private fun onRetryClick() {
        if (isRetryingStateFlow.value) return

        isRetryingStateFlow.value = true
        onEvent(ConversionEvent.OnRetryStateChanged(isRetrying = true))
        retryTrigger.tryEmit(Unit)
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
                conversionPairs = conversionPairs.visiblePairs(showFeaturedPairs),
            )

            ConversionDataState.Error,
            ConversionDataState.Loading -> this
        }
    }

    private enum class EditedSide { FROM, TO }

    private data class ConversionDataTransition(
        val previous: ConversionDataState? = null,
        val current: ConversionDataState? = null,
    )
}
