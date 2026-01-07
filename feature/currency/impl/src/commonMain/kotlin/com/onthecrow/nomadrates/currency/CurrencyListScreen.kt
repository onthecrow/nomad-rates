package com.onthecrow.nomadrates.currency

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun CurrencyListScreen(
    viewModel: CurrencyListViewModel,
    modifier: Modifier = Modifier,
) {
    Text("Hello world!")
}

@Preview
@Composable
private fun CurrencyListScreenPreview() {
    CurrencyListScreen(CurrencyListViewModel())
}
