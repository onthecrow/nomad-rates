package com.onthecrow.nomadrates.ui.view

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.onthecrow.nomadrates.ui.NomadRatesTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import nomadrates.core.ui.generated.resources.Res
import nomadrates.core.ui.generated.resources.ic_swap
import org.jetbrains.compose.resources.vectorResource

/**
 * Small round icon button with an outline, made to sit on top of other elements (e.g. between two
 * amount fields). The outline has the color of whatever is behind the button, so the button looks
 * cut into the elements it overlaps. Pressing it plays a short "pop" scale animation.
 *
 * @param outlineColor color of the surface the button is placed on.
 */
@Composable
fun RoundIconButton(
    imageVector: ImageVector,
    outlineColor: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String? = null,
    onClick: () -> Unit = {},
) {
    val popScale = rememberPopScaleState()

    Icon(
        modifier = modifier
            .graphicsLayer {
                scaleX = popScale.scale
                scaleY = popScale.scale
            }
            .background(outlineColor, shape = CircleShape)
            .padding(RoundIconButtonDefaults.OutlineWidth)
            .clip(CircleShape)
            .background(RoundIconButtonDefaults.containerColor())
            .clickable(
                enabled = enabled,
                onClick = {
                    popScale.pop()
                    onClick()
                },
            )
            .padding(RoundIconButtonDefaults.ContentPadding),
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = RoundIconButtonDefaults.contentColor(),
    )
}

object RoundIconButtonDefaults {
    val OutlineWidth: Dp = 4.dp
    val ContentPadding: Dp = 8.dp
    val IconSize: Dp = 24.dp

    /** Full size of the button including its outline. */
    val Size: Dp = IconSize + ContentPadding * 2 + OutlineWidth * 2

    @Composable
    fun containerColor(): Color {
        val colorScheme = MaterialTheme.colorScheme
        return if (colorScheme.background.luminance() < 0.5f) {
            colorScheme.secondary
        } else {
            colorScheme.surfaceContainerHighest
        }
    }

    @Composable
    fun contentColor(): Color {
        val colorScheme = MaterialTheme.colorScheme
        return if (colorScheme.background.luminance() < 0.5f) {
            colorScheme.onSecondary
        } else {
            colorScheme.onSecondaryContainer
        }
    }
}

/**
 * The press reaction of [RoundIconButton]: the scale quickly drops and springs back.
 * Apply [scale] with `graphicsLayer` and call [pop] on click.
 */
@Stable
class PopScaleState internal constructor(
    private val animatable: Animatable<Float, *>,
    private val scope: CoroutineScope,
) {
    val scale: Float
        get() = animatable.value

    fun pop() {
        scope.launch {
            animatable.snapTo(1f)
            animatable.animateTo(POP_SCALE, tween(POP_DOWN_DURATION_MILLIS))
            animatable.animateTo(1f, tween(POP_UP_DURATION_MILLIS))
        }
    }

    private companion object {
        const val POP_SCALE = 0.7f
        const val POP_DOWN_DURATION_MILLIS = 90
        const val POP_UP_DURATION_MILLIS = 140
    }
}

@Composable
fun rememberPopScaleState(): PopScaleState {
    val animatable = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    return remember(animatable, scope) { PopScaleState(animatable, scope) }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF1C1B1F,
)
@Composable
private fun RoundIconButtonPreview() {
    NomadRatesTheme(darkTheme = true) {
        RoundIconButton(
            imageVector = vectorResource(Res.drawable.ic_swap),
            outlineColor = MaterialTheme.colorScheme.surfaceContainer,
        )
    }
}
