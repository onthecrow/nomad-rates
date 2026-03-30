package com.onthecrow.nomadrates.currency

import com.onthecrow.nomadrates.currency.data.CurrencyRemoteDataSource
import com.onthecrow.nomadrates.currency.domain.CurrencyBootstrapState
import com.onthecrow.nomadrates.currency.domain.GetCurrencyListUseCase
import com.onthecrow.nomadrates.currency.model.Currency
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ObserveCurrencyBootstrapStateUseCaseImplTest {

    @Test
    fun `empty cache and null remote state emit loading`() = runTest {
        val useCase = createUseCase()

        val state = useCase().take(1).toList().single()

        assertEquals(CurrencyBootstrapState.Loading, state)
    }

    @Test
    fun `empty cache and failed remote state emit error`() = runTest {
        val remoteDataSource = TestCurrencyRemoteDataSource().apply {
            emitFailure()
        }
        val useCase = createUseCase(
            currencyRemoteDataSource = remoteDataSource,
        )

        val state = useCase().take(1).toList().single()

        assertEquals(CurrencyBootstrapState.Error, state)
    }

    @Test
    fun `non empty cache emits cached regardless of remote state`() = runTest {
        val remoteDataSource = TestCurrencyRemoteDataSource().apply {
            emitFailure()
        }
        val useCase = createUseCase(
            currencyListFlow = MutableStateFlow(listOf(sampleCurrency("USD"))),
            currencyRemoteDataSource = remoteDataSource,
        )

        val state = useCase().take(1).toList().single()

        assertEquals(CurrencyBootstrapState.Cached, state)
    }

    @Test
    fun `empty cache returns to loading after refresh reset`() = runTest {
        val remoteDataSource = TestCurrencyRemoteDataSource().apply {
            emitFailure()
        }
        val useCase = createUseCase(
            currencyRemoteDataSource = remoteDataSource,
        )

        backgroundScope.launch {
            delay(100)
            remoteDataSource.emitLoading()
        }

        val states = useCase().take(2).toList()

        assertEquals(
            listOf(
                CurrencyBootstrapState.Error,
                CurrencyBootstrapState.Loading,
            ),
            states,
        )
    }

    private fun createUseCase(
        currencyListFlow: MutableStateFlow<List<Currency>?> = MutableStateFlow(emptyList()),
        currencyRemoteDataSource: TestCurrencyRemoteDataSource = TestCurrencyRemoteDataSource(),
    ): ObserveCurrencyBootstrapStateUseCaseImpl {
        return ObserveCurrencyBootstrapStateUseCaseImpl(
            getCurrencyListUseCase = FakeGetCurrencyListUseCase(currencyListFlow),
            currencyRemoteDataSource = currencyRemoteDataSource,
        )
    }

    private class FakeGetCurrencyListUseCase(
        private val flow: MutableStateFlow<List<Currency>?>,
    ) : GetCurrencyListUseCase {
        override fun invoke(): Flow<List<Currency>?> = flow
    }

    private class TestCurrencyRemoteDataSource : CurrencyRemoteDataSource() {
        fun emitFailure() {
            emitState(Result.failure(IllegalStateException("boom")))
        }

        fun emitLoading() {
            emitState(null)
        }

        override fun getString(key: String): String = ""

        override fun getKeysByPrefix(prefix: String): Set<String> = emptySet()

        override fun startBackgroundSync(onActivated: () -> Unit, onError: (Throwable) -> Unit) = Unit

        override suspend fun fetchAndActivate(): Boolean = false
    }

    private fun sampleCurrency(code: String): Currency {
        return Currency(
            code = code,
            conversionRate = 1.0,
            isFavourite = false,
            isFeatured = false,
            rates = listOf(1.0, 1.1),
        )
    }
}
