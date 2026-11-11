package com.onthecrow.nomadrates.ui.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Title of a list section, such as "Featured" or "Favourites".
 */
@Composable
fun SectionHeaderView(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier.padding(16.dp),
        text = title,
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}
