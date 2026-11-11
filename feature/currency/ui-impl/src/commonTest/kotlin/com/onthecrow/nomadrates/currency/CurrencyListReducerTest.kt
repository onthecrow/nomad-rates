package com.onthecrow.nomadrates.currency

import com.onthecrow.nomadrates.currency.model.CurrencyListItem
import com.onthecrow.nomadrates.currency.model.ListGroup
import kotlin.test.Test
import kotlin.test.assertEquals

class CurrencyListReducerTest {
    private val reducer = CurrencyListReducer()

    private val ruble = currencyItem("RUB", "Russian Ruble", "Российский Рубль")
    private val dollar = currencyItem("USD", "US Dollar", "Доллар США")
    private val initialState = CurrencyListState(
        currencies = listOf(
            CurrencyListItem.Header("Featured"),
            dollar.copy(listGroup = ListGroup.FEATURED),
            CurrencyListItem.Header("All currencies"),
            ruble,
            dollar,
        ),
    )

    @Test
    fun onSearchValueChange_matchesDisplayedName() = kotlinx.coroutines.test.runTest {
        assertEquals(listOf<CurrencyListItem>(ruble), search("ruble"))
    }

    @Test
    fun onSearchValueChange_matchesAlternateNameIgnoringCase() = kotlinx.coroutines.test.runTest {
        assertEquals(listOf<CurrencyListItem>(ruble), search("рубль"))
        assertEquals(listOf<CurrencyListItem>(dollar), search("ДОЛЛАР"))
    }

    @Test
    fun onSearchValueChange_returnsNothingWithoutMatch() = kotlinx.coroutines.test.runTest {
        assertEquals(emptyList(), search("юань"))
    }

    private suspend fun search(query: String): List<CurrencyListItem> {
        return reducer.reduce(
            state = initialState,
            event = CurrencyListEvent.OnSearchValueChange(query),
        ).currenciesFiltered
    }

    private fun currencyItem(
        code: String,
        name: String,
        alternateName: String,
    ) = CurrencyListItem.Data(
        flagIcon = "",
        currencyCode = code,
        currencyName = name,
        alternateCurrencyName = alternateName,
        isFavourite = false,
        isFeatured = false,
        listGroup = ListGroup.ALL,
    )
}
