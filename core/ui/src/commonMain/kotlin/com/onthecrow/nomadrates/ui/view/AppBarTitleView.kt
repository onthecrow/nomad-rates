package com.onthecrow.nomadrates.ui.view

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun AppBarTitleView(
    modifier: Modifier = Modifier,
    title: String,
    onBackPress: () -> Unit = {},
) {
    AppBarLayout(
        modifier = modifier,
        onBackPress = onBackPress,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
    }
}
