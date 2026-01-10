package com.onthecrow.nomadrates.conversion

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.onthecrow.nomadrates.conversion.view.ConversionView
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun ConversionScreen(
    state: ConversionState,
    modifier: Modifier = Modifier,
    onEvent: (ConversionEvent) -> Unit = {},
) {
    Column(
        modifier = modifier
            .systemBarsPadding()
            .imePadding()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        if (state.from != null && state.to != null) {
            ConversionView(
                from = state.from,
                to = state.to,
                onEvent = onEvent,
            )
        }
    }
}

@Preview
@Composable
private fun ConversionScreenPreview() {
    MaterialTheme {
        ConversionScreen(state = ConversionState())
    }
}
