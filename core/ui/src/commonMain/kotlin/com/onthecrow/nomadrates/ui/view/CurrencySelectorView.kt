package com.onthecrow.nomadrates.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import nomadrates.core.ui.generated.resources.Res
import nomadrates.core.ui.generated.resources.ic_drop_down
import org.jetbrains.compose.resources.vectorResource

private val CurrencySelectorShape = RoundedCornerShape(100.dp)

@Composable
fun CurrencySelectorView(
    currencyCode: String,
    currencyIcon: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    flagSize: androidx.compose.ui.unit.Dp? = null,
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
) {
    Row(
        modifier = modifier
            .padding(8.dp)
            .defaultMinSize(minHeight = 40.dp)
            .clip(CurrencySelectorShape)
            .clickable(enabled = enabled, onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            modifier = flagSize?.let { size ->
                Modifier.size(size)
            } ?: Modifier
                .fillMaxHeight()
                .aspectRatio(1f),
            model = currencyIcon,
            contentDescription = null,
        )
        Text(
            text = currencyCode,
            color = contentColor,
        )
        Icon(
            modifier = Modifier.size(12.dp),
            imageVector = vectorResource(Res.drawable.ic_drop_down),
            contentDescription = null,
            tint = contentColor,
        )
        Spacer(modifier = Modifier.size(4.dp))
    }
}
