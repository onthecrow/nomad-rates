package com.onthecrow.nomadrates.conversion

import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun ConversionScreen(
    modifier: Modifier = Modifier,
    onEvent: (ConversionEvent) -> Unit = {},
) {
    Button(onClick = { onEvent(ConversionEvent.OnButtonPress) }) {
        Text("Press me!")
    }
}

@Preview
@Composable
private fun ConversionScreenPreview() {
    MaterialTheme {
        ConversionScreen(/*state = ConversionState()*/)
    }
}
