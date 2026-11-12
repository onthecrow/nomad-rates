package com.onthecrow.nomadrates.favouritesconversion

import com.onthecrow.nomadrates.conversion.domain.GetSelectedConversionPairUseCase
import com.onthecrow.nomadrates.conversion.domain.SaveSelectedConversionPairUseCase
import com.onthecrow.nomadrates.conversion.domain.model.SelectedConversionPair
import com.onthecrow.nomadrates.favouritesconversion.domain.model.ConversionPairSide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SetFavouritesConversionBaseCurrencyUseCaseImplTest {

    @Test
    fun `changes the from currency and keeps the to currency`() = runTest {
        val savedPairs = mutableListOf<SelectedConversionPair>()
        val useCase = SetFavouritesConversionBaseCurrencyUseCaseImpl(
            getSelectedConversionPairUseCase = FakeGetSelectedConversionPairUseCase(
                SelectedConversionPair(fromCurrencyCode = "USD", toCurrencyCode = "EUR"),
            ),
            saveSelectedConversionPairUseCase = FakeSaveSelectedConversionPairUseCase(savedPairs),
        )

        useCase("GBP", ConversionPairSide.FROM)

        assertEquals(
            listOf(SelectedConversionPair(fromCurrencyCode = "GBP", toCurrencyCode = "EUR")),
            savedPairs,
        )
    }

    @Test
    fun `same from currency is not saved again`() = runTest {
        val savedPairs = mutableListOf<SelectedConversionPair>()
        val useCase = SetFavouritesConversionBaseCurrencyUseCaseImpl(
            getSelectedConversionPairUseCase = FakeGetSelectedConversionPairUseCase(
                SelectedConversionPair(fromCurrencyCode = "USD", toCurrencyCode = "EUR"),
            ),
            saveSelectedConversionPairUseCase = FakeSaveSelectedConversionPairUseCase(savedPairs),
        )

        useCase("USD", ConversionPairSide.FROM)

        assertEquals(emptyList(), savedPairs)
    }

    @Test
    fun `to side changes the to currency and keeps the from currency`() = runTest {
        val savedPairs = mutableListOf<SelectedConversionPair>()
        val useCase = SetFavouritesConversionBaseCurrencyUseCaseImpl(
            getSelectedConversionPairUseCase = FakeGetSelectedConversionPairUseCase(
                SelectedConversionPair(fromCurrencyCode = "USD", toCurrencyCode = "EUR"),
            ),
            saveSelectedConversionPairUseCase = FakeSaveSelectedConversionPairUseCase(savedPairs),
        )

        useCase("GBP", ConversionPairSide.TO)

        assertEquals(
            listOf(SelectedConversionPair(fromCurrencyCode = "USD", toCurrencyCode = "GBP")),
            savedPairs,
        )
    }

    private class FakeGetSelectedConversionPairUseCase(
        selectedConversionPair: SelectedConversionPair,
    ) : GetSelectedConversionPairUseCase {
        private val flow = MutableStateFlow(selectedConversionPair)
        override fun invoke(): Flow<SelectedConversionPair> = flow
    }

    private class FakeSaveSelectedConversionPairUseCase(
        private val savedPairs: MutableList<SelectedConversionPair>,
    ) : SaveSelectedConversionPairUseCase {
        override suspend fun invoke(selectedConversionPair: SelectedConversionPair) {
            savedPairs += selectedConversionPair
        }
    }
}
