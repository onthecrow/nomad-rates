package com.onthecrow.nomadrates.batch.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.onthecrow.nomadrates.ui.NomadRatesTheme
import com.onthecrow.nomadrates.ui.view.RoundIconButtonDefaults
import com.onthecrow.nomadrates.ui.view.rememberPopScaleState
import nomadrates.feature.batch.ui_impl.generated.resources.Res
import nomadrates.feature.batch.ui_impl.generated.resources.ic_add
import org.jetbrains.compose.resources.vectorResource

private val PillStartPadding = 16.dp
private val PillEndPadding = 20.dp
private val PillIconSize = 18.dp
private val PillIconTextGap = 8.dp

/** How far the label travels to the right while the button turns into a circle. */
private val LabelExitDistance = 24.dp

/**
 * The "Add currency" button. With [morphProgress] = 0 it is a pill with a plus and the label;
 * with [morphProgress] = 1 it is a round plus button that looks like
 * [com.onthecrow.nomadrates.ui.view.RoundIconButton]. In between, the plus moves to the center
 * and the label slides out to the right. Pressing it plays the same "pop" as the round button.
 *
 * @param outlineColor color of the surface the round button sits on; only shows in the round state.
 */
@Composable
internal fun AddCurrencyButtonView(
    text: String,
    morphProgress: Float,
    outlineColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val progress = morphProgress.coerceIn(0f, 1f)
    val popScale = rememberPopScaleState()
    val labelStyle = MaterialTheme.typography.labelLarge
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val labelWidth: Dp = remember(text, labelStyle, textMeasurer, density) {
        with(density) {
            textMeasurer.measure(
                text = AnnotatedString(text),
                style = labelStyle,
                maxLines = 1,
            ).size.width.toDp()
        }
    }

    val roundSize = RoundIconButtonDefaults.Size
    val pillWidth = PillStartPadding + PillIconSize + PillIconTextGap + labelWidth + PillEndPadding
    val outlineWidth = lerp(0.dp, RoundIconButtonDefaults.OutlineWidth, progress)
    val width = lerp(pillWidth, roundSize, progress)
    val innerWidth = width - outlineWidth * 2
    val iconSize = lerp(PillIconSize, RoundIconButtonDefaults.IconSize, progress)
    val iconStart = lerp(PillStartPadding, (innerWidth - iconSize) / 2, progress)
    val labelStart = PillStartPadding + PillIconSize + PillIconTextGap + LabelExitDistance * progress

    val colorScheme = MaterialTheme.colorScheme
    val containerColor = lerp(
        colorScheme.secondaryContainer,
        RoundIconButtonDefaults.containerColor(),
        progress,
    )
    val contentColor = lerp(
        colorScheme.onSecondaryContainer,
        RoundIconButtonDefaults.contentColor(),
        progress,
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = popScale.scale
                scaleY = popScale.scale
            }
            .size(width = width, height = roundSize)
            .background(outlineColor.copy(alpha = outlineColor.alpha * progress), CircleShape)
            .padding(outlineWidth)
            .clip(CircleShape)
            .background(containerColor)
            .clickable {
                popScale.pop()
                onClick()
            },
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            modifier = Modifier
                .offset(x = labelStart)
                .graphicsLayer { alpha = (1f - progress * 2f).coerceAtLeast(0f) },
            text = text,
            style = labelStyle,
            color = contentColor,
            maxLines = 1,
            softWrap = false,
        )
        Icon(
            modifier = Modifier
                .offset(x = iconStart)
                .size(iconSize),
            imageVector = vectorResource(Res.drawable.ic_add),
            contentDescription = if (progress > 0.5f) text else null,
            tint = contentColor,
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF1C1B1F,
)
@Composable
private fun AddCurrencyButtonPillPreview() {
    NomadRatesTheme(darkTheme = true) {
        AddCurrencyButtonView(
            text = "Add currency",
            morphProgress = 0f,
            outlineColor = MaterialTheme.colorScheme.background,
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF1C1B1F,
)
@Composable
private fun AddCurrencyButtonMorphPreview() {
    NomadRatesTheme(darkTheme = true) {
        AddCurrencyButtonView(
            text = "Add currency",
            morphProgress = 0.5f,
            outlineColor = MaterialTheme.colorScheme.background,
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF1C1B1F,
)
@Composable
private fun AddCurrencyButtonRoundPreview() {
    NomadRatesTheme(darkTheme = true) {
        AddCurrencyButtonView(
            text = "Add currency",
            morphProgress = 1f,
            outlineColor = MaterialTheme.colorScheme.background,
        )
    }
}
