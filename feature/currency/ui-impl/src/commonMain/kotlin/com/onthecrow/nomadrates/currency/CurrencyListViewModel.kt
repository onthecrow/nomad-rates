package com.onthecrow.nomadrates.currency

import androidx.lifecycle.viewModelScope
import com.onthecrow.nomadrates.currency.domain.GetCurrencyListUseCase
import com.onthecrow.nomadrates.currency.domain.ToggleCurrencyFavoriteUseCase
import com.onthecrow.nomadrates.navigation.Navigator
import com.onthecrow.nomadrates.navigation.ScreenResultDispatcher
import com.onthecrow.nomadrates.settings.domain.ObserveShowFeaturedCurrenciesUseCase
import com.onthecrow.nomadrates.uicore.BaseViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class CurrencyListViewModel(
    private val navigator: Navigator,
    private val screenResultDispatcher: ScreenResultDispatcher,
    private val toggleCurrencyFavoriteUseCase: ToggleCurrencyFavoriteUseCase,
    observeShowFeaturedCurrenciesUseCase: ObserveShowFeaturedCurrenciesUseCase,
    getCurrencyListUseCase: GetCurrencyListUseCase,
    reducer: CurrencyListReducer,
    private val selectionSource: CurrencySelectionSource,
) : BaseViewModel<CurrencyListEvent, CurrencyListState, CurrencyListReducer>(reducer) {

    init {
        getCurrencyListUseCase()
            .filterNotNull()
            .combine(observeShowFeaturedCurrenciesUseCase()) { currencies, showFeaturedCurrencies ->
                currencies to showFeaturedCurrencies
            }
            .onEach { (currencies, showFeaturedCurrencies) ->
                onEvent(
                    CurrencyListEvent.OnCurrencyListUpdate(
                        currencies = currencies,
                        showFeaturedCurrencies = showFeaturedCurrencies,
                    )
                )
            }
            .launchIn(viewModelScope)
        eventFlow.onEach { event ->
            when (event) {
                is CurrencyListEvent.OnBackPress -> onBackPress()
                is CurrencyListEvent.OnCurrencyClick -> onCurrencyClick(event.currencyCode)
                is CurrencyListEvent.OnAddToFavouriteClick -> onAddToFavouriteClick(event.currencyCode)
                is CurrencyListEvent.OnSearchValueChange -> onSearchValueChange(event.value)
                else -> {}
            }
        }
            .launchIn(viewModelScope)
    }

    override fun getInitialState(): CurrencyListState = CurrencyListState()

    private fun onBackPress() {
        navigator.navigateBack()
    }

    private fun onAddToFavouriteClick(currencyCode: String) {
        viewModelScope.launch {
            toggleCurrencyFavoriteUseCase(currencyCode)
        }
    }

    private fun onCurrencyClick(currencyCode: String) {
        screenResultDispatcher.dispatch(
            CurrencyListScreenResult(
                selectedCurrencyCode = currencyCode,
                source = selectionSource,
            )
        )
        navigator.navigateBack()
    }

    private fun onSearchValueChange(value: String) {
        if (value.isNotEmpty()) {
            onEvent(CurrencyListEvent.OnScrollToTop)
        }
    }
}
