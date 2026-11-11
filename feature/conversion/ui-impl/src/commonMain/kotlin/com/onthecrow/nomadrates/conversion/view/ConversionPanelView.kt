package com.onthecrow.nomadrates.conversion.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp

private val ConversionPanelShape = RoundedCornerShape(32.dp)

/**
 * Raised card that holds the amount inputs on the conversion and batch conversion screens.
 */
@Composable
internal fun ConversionPanelView(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(
                elevation = 6.dp,
                shape = ConversionPanelShape,
            )
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = ConversionPanelShape,
            )
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        content = content,
    )
}
