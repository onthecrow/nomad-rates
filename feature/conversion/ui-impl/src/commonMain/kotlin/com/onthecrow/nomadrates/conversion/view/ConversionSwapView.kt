package com.onthecrow.nomadrates.conversion.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.onthecrow.nomadrates.ui.NomadRatesTheme
import com.onthecrow.nomadrates.ui.view.RoundIconButton
import nomadrates.feature.conversion.ui_impl.generated.resources.Res
import nomadrates.feature.conversion.ui_impl.generated.resources.ic_swap
import org.jetbrains.compose.resources.vectorResource

@Composable
internal fun ConversionSwapView(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
) {
    RoundIconButton(
        modifier = modifier,
        imageVector = vectorResource(Res.drawable.ic_swap),
        outlineColor = MaterialTheme.colorScheme.surfaceContainer,
        enabled = enabled,
        onClick = onClick,
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF1C1B1F,
)
@Composable
private fun ConversionViewPreview() {
    NomadRatesTheme(darkTheme = true) {
        ConversionSwapView(modifier = Modifier.padding(16.dp))
    }
}
