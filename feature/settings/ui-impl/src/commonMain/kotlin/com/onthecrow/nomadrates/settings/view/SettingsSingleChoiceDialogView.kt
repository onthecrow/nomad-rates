package com.onthecrow.nomadrates.settings.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toOffset

internal data class SettingsDialogOption<T>(
    val value: T,
    val label: String,
)

/**
 * @param onOptionSelected receives the chosen value and where it was pressed, in screen coordinates
 * ([Offset.Unspecified] if unknown).
 */
@Composable
internal fun <T> SettingsSingleChoiceDialogView(
    title: String,
    selectedOption: T,
    options: List<SettingsDialogOption<T>>,
    onOptionSelected: (value: T, pressOnScreen: Offset) -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {},
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                options.forEach { option ->
                    val press = remember { PressTracker() }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { press.coordinates = it }
                            .pointerInput(Unit) {
                                awaitEachGesture {
                                    press.position = awaitFirstDown(
                                        requireUnconsumed = false,
                                        pass = PointerEventPass.Initial,
                                    ).position
                                }
                            }
                            .clickable { onOptionSelected(option.value, press.positionOnScreen()) }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        RadioButton(
                            selected = selectedOption == option.value,
                            onClick = null,
                        )
                        Text(
                            text = option.label,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }
        },
    )
}

/** Remembers the last press on a row, so a click can report where it happened on screen. */
private class PressTracker {
    var coordinates: LayoutCoordinates? = null
    var position: Offset = Offset.Unspecified

    fun positionOnScreen(): Offset {
        val coordinates = coordinates?.takeIf { it.isAttached } ?: return Offset.Unspecified
        // A click without a touch (keyboard, accessibility) starts from the row's centre.
        val local = if (position.isSpecified) position else coordinates.size.center.toOffset()
        return coordinates.localToScreen(local)
    }
}
