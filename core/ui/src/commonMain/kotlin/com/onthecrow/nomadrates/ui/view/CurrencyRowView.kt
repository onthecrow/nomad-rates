package com.onthecrow.nomadrates.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

/**
 * Currency list row: flag, code and name, followed by [trailingContent]
 * (a like button on the currency list, a converted amount on favourites conversion).
 */
@Composable
fun CurrencyRowView(
    flagIcon: String,
    currencyCode: String,
    currencyName: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    trailingContent: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier = modifier
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            )
            .padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = flagIcon,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
        )
        Text(
            text = currencyCode,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            modifier = Modifier.weight(1f),
            text = currencyName,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onBackground,
        )
        trailingContent()
    }
}
