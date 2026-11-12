package com.onthecrow.nomadrates.favouritesconversion

import androidx.lifecycle.viewModelScope
import com.onthecrow.nomadrates.favouritesconversion.domain.ObserveFavouritesConversionUseCase
import com.onthecrow.nomadrates.favouritesconversion.domain.SetFavouritesConversionBaseCurrencyUseCase
import com.onthecrow.nomadrates.favouritesconversion.domain.model.ConversionPairSide
import com.onthecrow.nomadrates.favouritesconversion.domain.model.FavouritesConversion
import com.onthecrow.nomadrates.currency.CurrencyListDestination
import com.onthecrow.nomadrates.currency.CurrencyListScreenResult
import com.onthecrow.nomadrates.currency.CurrencySelectionSource
import com.onthecrow.nomadrates.currency.domain.SetCurrencyFavoriteUseCase
import com.onthecrow.nomadrates.navigation.Navigator
import com.onthecrow.nomadrates.navigation.ScreenResultDispatcher
import com.onthecrow.nomadrates.settings.domain.ObserveUseEnglishCurrencyNamesUseCase
import com.onthecrow.nomadrates.uicore.BaseViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

internal class FavouritesConversionViewModel(
    private val navigator: Navigator,
    private val screenResultDispatcher: ScreenResultDispatcher,
    private val observeFavouritesConversionUseCase: ObserveFavouritesConversionUseCase,
    private val observeUseEnglishCurrencyNamesUseCase: ObserveUseEnglishCurrencyNamesUseCase,
    private val setFavouritesConversionBaseCurrencyUseCase: SetFavouritesConversionBaseCurrencyUseCase,
    private val setCurrencyFavoriteUseCase: SetCurrencyFavoriteUseCase,
    reducer: FavouritesConversionReducer,
    private val destination: FavouritesConversionDestination,
) : BaseViewModel<FavouritesConversionEvent, FavouritesConversionState, FavouritesConversionReducer>(reducer) {

    private val side: ConversionPairSide = when (destination.side) {
        FavouritesConversionSide.FROM -> ConversionPairSide.FROM
        FavouritesConversionSide.TO -> ConversionPairSide.TO
    }

    init {
        bindEventHandlers()
        observeFavouritesConversion()
        observeScreenResults()
        syncAmountWithConversionScreen()
    }

    override fun getInitialState(): FavouritesConversionState = FavouritesConversionState.Loading()

    private fun bindEventHandlers() {
        eventFlow.onEach(::handleEvent).launchIn(viewModelScope)
    }

    private fun handleEvent(event: FavouritesConversionEvent) {
        when (event) {
            FavouritesConversionEvent.OnBackPress -> navigator.navigateBack()
            FavouritesConversionEvent.OnBaseCurrencyClick -> openCurrencySelector(
                CurrencySelectionSource.FavouritesConversionBase,
            )
            FavouritesConversionEvent.OnAddCurrencyClick -> openCurrencySelector(
                CurrencySelectionSource.FavouritesConversionAddFavourite,
            )
            is FavouritesConversionEvent.OnRemoveCurrency -> viewModelScope.launch {
                setCurrencyFavoriteUseCase(event.currencyCode, isFavourite = false)
            }
            // The amount is sent to the conversion screen from the state, see syncAmountWithConversionScreen.
            is FavouritesConversionEvent.OnAmountChange,
            is FavouritesConversionEvent.OnOpened,
            is FavouritesConversionEvent.OnFavouritesConversionChanged -> Unit
        }
    }

    private fun observeFavouritesConversion() {
        combine<FavouritesConversion, Boolean, FavouritesConversionEvent>(
            observeFavouritesConversionUseCase(side),
            observeUseEnglishCurrencyNamesUseCase(),
        ) { favouritesConversion, useEnglishCurrencyNames ->
            FavouritesConversionEvent.OnFavouritesConversionChanged(
                favouritesConversion = favouritesConversion,
                useEnglishCurrencyNames = useEnglishCurrencyNames,
            )
        }
            // What the screen was opened with has to be in place before the first data arrives.
            .onStart {
                emit(
                    FavouritesConversionEvent.OnOpened(
                        baseCurrencyCode = destination.baseCurrencyCode,
                        amount = destination.amount,
                    )
                )
            }
            .onEach(::onEvent)
            .launchIn(viewModelScope)
    }

    /**
     * Keeps the "from" field of the conversion screen at the same amount as this screen, whatever
     * changed it (typing, the input rules, the amount the screen was opened with).
     */
    private fun syncAmountWithConversionScreen() {
        state
            .map { state -> state.amount }
            .distinctUntilChanged()
            .drop(1)
            .onEach { amount ->
                screenResultDispatcher.dispatch(FavouritesConversionAmountResult(amount, destination.side))
            }
            .launchIn(viewModelScope)
    }

    private fun observeScreenResults() {
        screenResultDispatcher.resultFlow
            .onEach { screenResult ->
                when (screenResult) {
                    is CurrencyListScreenResult -> onCurrencySelected(screenResult)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun openCurrencySelector(source: CurrencySelectionSource) {
        navigator.navigate(CurrencyListDestination(source))
    }

    private fun onCurrencySelected(result: CurrencyListScreenResult) {
        when (result.source) {
            CurrencySelectionSource.FavouritesConversionBase -> viewModelScope.launch {
                setFavouritesConversionBaseCurrencyUseCase(result.selectedCurrencyCode, side)
            }

            CurrencySelectionSource.FavouritesConversionAddFavourite -> viewModelScope.launch {
                setCurrencyFavoriteUseCase(result.selectedCurrencyCode, isFavourite = true)
            }

            CurrencySelectionSource.ConversionFrom,
            CurrencySelectionSource.ConversionTo,
            CurrencySelectionSource.SettingsDefaultFrom,
            CurrencySelectionSource.SettingsDefaultTo -> Unit
        }
    }
}
