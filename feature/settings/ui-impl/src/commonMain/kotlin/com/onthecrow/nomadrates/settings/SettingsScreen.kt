package com.onthecrow.nomadrates.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.onthecrow.nomadrates.ui.view.BackButtonView

@Composable
internal fun SettingsScreen(
    state: SettingsState,
    modifier: Modifier = Modifier,
    onEvent: (SettingsEvent) -> Unit = {},
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        BackButtonView(
            modifier = Modifier
                .align(Alignment.TopStart)
                .systemBarsPadding()
                .padding(16.dp),
            onClick = { onEvent(SettingsEvent.OnBackPress) },
        )
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    SettingsScreen(state = SettingsState)
}
