package com.onthecrow.nomadrates.conversion.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.onthecrow.nomadrates.ui.NomadRatesTheme
import nomadrates.feature.conversion.ui_impl.generated.resources.Res
import nomadrates.feature.conversion.ui_impl.generated.resources.ic_swap
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun ConversionSwapView(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Icon(
        modifier = modifier.clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .clickable(enabled = true, onClick = onClick)
            .padding(8.dp),
        imageVector = vectorResource(Res.drawable.ic_swap),
        contentDescription = null,
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
