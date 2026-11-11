package com.onthecrow.nomadrates.batch

import com.onthecrow.nomadrates.conversion.domain.GetSelectedConversionPairUseCase
import com.onthecrow.nomadrates.conversion.domain.SaveSelectedConversionPairUseCase
import com.onthecrow.nomadrates.conversion.domain.model.SelectedConversionPair
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SetBatchConversionBaseCurrencyUseCaseImplTest {

    @Test
    fun `changes the from currency and keeps the to currency`() = runTest {
        val savedPairs = mutableListOf<SelectedConversionPair>()
        val useCase = SetBatchConversionBaseCurrencyUseCaseImpl(
            getSelectedConversionPairUseCase = FakeGetSelectedConversionPairUseCase(
                SelectedConversionPair(fromCurrencyCode = "USD", toCurrencyCode = "EUR"),
            ),
            saveSelectedConversionPairUseCase = FakeSaveSelectedConversionPairUseCase(savedPairs),
        )

        useCase("GBP")

        assertEquals(
            listOf(SelectedConversionPair(fromCurrencyCode = "GBP", toCurrencyCode = "EUR")),
            savedPairs,
        )
    }

    @Test
    fun `same from currency is not saved again`() = runTest {
        val savedPairs = mutableListOf<SelectedConversionPair>()
        val useCase = SetBatchConversionBaseCurrencyUseCaseImpl(
            getSelectedConversionPairUseCase = FakeGetSelectedConversionPairUseCase(
                SelectedConversionPair(fromCurrencyCode = "USD", toCurrencyCode = "EUR"),
            ),
            saveSelectedConversionPairUseCase = FakeSaveSelectedConversionPairUseCase(savedPairs),
        )

        useCase("USD")

        assertEquals(emptyList(), savedPairs)
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
