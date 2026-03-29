package com.onthecrow.nomadrates.conversion

import androidx.lifecycle.viewModelScope
import com.onthecrow.nomadrates.conversion.domain.ConvertCurrenciesUseCase
import com.onthecrow.nomadrates.conversion.domain.GetConversionPairUseCase
import com.onthecrow.nomadrates.conversion.domain.GetConversionPairsUseCase
import com.onthecrow.nomadrates.conversion.domain.GetHistoricalRatesUseCase
import com.onthecrow.nomadrates.conversion.domain.GetSelectedConversionPairUseCase
import com.onthecrow.nomadrates.conversion.domain.SaveSelectedConversionPairUseCase
import com.onthecrow.nomadrates.conversion.domain.ToggleConversionPairFavouriteUseCase
import com.onthecrow.nomadrates.conversion.domain.model.ConversionPair
import com.onthecrow.nomadrates.conversion.domain.model.SelectedConversionPair
import com.onthecrow.nomadrates.currency.CurrencyListDestination
import com.onthecrow.nomadrates.currency.CurrencyListScreenResult
import com.onthecrow.nomadrates.currency.domain.GetCurrencyUseCase
import com.onthecrow.nomadrates.navigation.Navigator
import com.onthecrow.nomadrates.navigation.ScreenResultDispatcher
import com.onthecrow.nomadrates.uicore.BaseViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.runningReduce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
internal class ConversionViewModel(
    private val navigator: Navigator,
    private val getCurrencyUseCase: GetCurrencyUseCase,
    private val convertCurrenciesUseCase: ConvertCurrenciesUseCase,
    private val getHistoricalRatesUseCase: GetHistoricalRatesUseCase,
    private val getConversionPairUseCase: GetConversionPairUseCase,
    private val getConversionPairsUseCase: GetConversionPairsUseCase,
    private val getSelectedConversionPairUseCase: GetSelectedConversionPairUseCase,
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

    private var conversionCurrencySource: ConversionCurrencySource = ConversionCurrencySource.FROM

    init {
        eventFlow.onEach { event ->
            when (event) {
                is ConversionEvent.OnBackPress -> onBackPress()
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
                    is CurrencyListScreenResult -> onCurrencySelected(screenResult.selectedCurrencyCode)
                }
            }
            .launchIn(viewModelScope)

        getConversionPairsUseCase().runningReduce { prevValue, newValue ->
            // Check if the pair was added to favourites, to highlite it to the user
            val diff = (newValue - prevValue.toSet())
            if (diff.size == 1 && diff.first().isFavourite) {
                scrollToItemAndHighlight(diff.first())
            }
            newValue
        }
            .onEach { conversionPairs ->
                onEvent(ConversionEvent.OnConversionPairsReceived(conversionPairs))
            }
            .launchIn(viewModelScope)

        selectedConversionPairStateFlow
            .flatMapLatest { selectedConversionPair ->
                getConversionPairUseCase(
                    selectedConversionPair.fromCurrencyCode,
                    selectedConversionPair.toCurrencyCode,
                )
            }
            .filterNotNull()
            .distinctUntilChanged()
            .onEach {
                onEvent(ConversionEvent.OnActiveConversionPairChanged(it))
                val fromValue = state.value.conversionViewState.from?.conversionValue
                if (!fromValue.isNullOrBlank()) {
                    onEvent(ConversionEvent.OnFromValueChange(fromValue))
                }
            }
            .launchIn(viewModelScope)
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

    override fun getInitialState(): ConversionState = ConversionState()

    private fun onCurrencySelected(selectedCurrencyCode: String) {
        val selectedConversionPair = selectedConversionPairStateFlow.value
        val updatedPair = when (conversionCurrencySource) {
            ConversionCurrencySource.FROM -> selectedConversionPair.copy(
                fromCurrencyCode = selectedCurrencyCode,
            )

            ConversionCurrencySource.TO -> selectedConversionPair.copy(
                toCurrencyCode = selectedCurrencyCode,
            )
        }
        updateSelectedConversionPair(updatedPair)
        // todo maybe move it to reducer
//        onFromCurrencyValueChange(state.value.conversionViewState.from?.conversionValue ?: return)
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
        conversionCurrencySource = ConversionCurrencySource.FROM
        navigator.navigate(CurrencyListDestination)
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
        conversionCurrencySource = ConversionCurrencySource.TO
        navigator.navigate(CurrencyListDestination)
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
}

enum class ConversionCurrencySource {
    FROM, TO
}
