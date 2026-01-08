package com.onthecrow.nomadrates.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun CurrencyView(
    modifier: Modifier = Modifier,
    currencyIcon: String,
    currencyCode: String,
    currencyName: String,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = currencyIcon,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
        )
        Text(
            text = currencyCode,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = currencyName,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}
