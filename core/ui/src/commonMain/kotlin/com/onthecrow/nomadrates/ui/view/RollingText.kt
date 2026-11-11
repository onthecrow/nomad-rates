package com.onthecrow.nomadrates.ui.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.onthecrow.nomadrates.ui.NomadRatesTheme

private const val ROLL_DURATION_MILLIS = 220

/**
 * Text for numbers whose characters roll like an odometer when they change: a digit that grows
 * comes in from below, one that gets smaller comes in from above. Characters are matched from the
 * end, so when a number gets longer, the digits keep their places and the new leading ones appear.
 *
 * It is one line and does not ellipsize; give it room or clip its parent.
 */
@Composable
fun RollingText(
    text: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
) {
    // Characters that appear later than the first composition fade and widen in; the ones shown
    // from the start are just there.
    val firstComposition = remember { FirstCompositionFlag() }
    SideEffect { firstComposition.isDone = true }

    Row(
        modifier = modifier
            .clipToBounds()
            .clearAndSetSemantics { contentDescription = text },
    ) {
        text.forEachIndexed { index, char ->
            key(text.length - index) {
                val appearance = remember {
                    MutableTransitionState(initialState = !firstComposition.isDone).apply { targetState = true }
                }
                AnimatedVisibility(
                    visibleState = appearance,
                    enter = expandHorizontally(
                        animationSpec = tween(ROLL_DURATION_MILLIS),
                        expandFrom = Alignment.End,
                    ) + fadeIn(tween(ROLL_DURATION_MILLIS)),
                ) {
                    AnimatedContent(
                        targetState = char,
                        transitionSpec = { rollTransform() },
                        label = "rolling_char",
                    ) { targetChar ->
                        Text(
                            text = targetChar.toString(),
                            style = style,
                            color = color,
                            maxLines = 1,
                            softWrap = false,
                        )
                    }
                }
            }
        }
    }
}

private class FirstCompositionFlag {
    var isDone: Boolean = false
}

private fun AnimatedContentTransitionScope<Char>.rollTransform(): ContentTransform {
    val rollsUp = !initialState.isDigit() || !targetState.isDigit() || targetState > initialState
    val direction = if (rollsUp) 1 else -1
    return (
        slideInVertically(tween(ROLL_DURATION_MILLIS)) { height -> height * direction } +
            fadeIn(tween(ROLL_DURATION_MILLIS))
        ) togetherWith (
        slideOutVertically(tween(ROLL_DURATION_MILLIS)) { height -> -height * direction } +
            fadeOut(tween(ROLL_DURATION_MILLIS))
        ) using SizeTransform(clip = false) { _, _ -> tween(ROLL_DURATION_MILLIS) }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF1C1B1F,
)
@Composable
private fun RollingTextPreview() {
    NomadRatesTheme(darkTheme = true) {
        RollingText(
            text = "1 234,56",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}
