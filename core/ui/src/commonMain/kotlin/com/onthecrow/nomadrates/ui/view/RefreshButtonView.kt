package com.onthecrow.nomadrates.ui.view

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp

@Composable
fun RefreshButtonView(
    text: String,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val labelStyle: TextStyle = MaterialTheme.typography.labelLarge
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val textLayout = remember(text, labelStyle, textMeasurer) {
        textMeasurer.measure(
            text = AnnotatedString(text),
            style = labelStyle,
            maxLines = 1,
        )
    }

    val horizontalPadding = 20.dp
    val verticalPadding = 12.dp
    val minTouchTargetHeight = 48.dp

    val naturalWidth = remember(textLayout, horizontalPadding, density) {
        with(density) {
            textLayout.size.width.toDp() + horizontalPadding * 2
        }
    }
    val naturalHeight = remember(textLayout, verticalPadding, minTouchTargetHeight, density) {
        with(density) {
            maxOf(
                textLayout.size.height.toDp() + verticalPadding * 2,
                minTouchTargetHeight,
            )
        }
    }

    val transition = updateTransition(targetState = isLoading, label = "refresh_button")
    val containerWidth = transition.animateDp(label = "refresh_button_width") { loading ->
        if (loading) naturalHeight else naturalWidth
    }
    val textAlpha = transition.animateFloat(label = "refresh_button_text_alpha") { loading ->
        if (loading) 0f else 1f
    }
    val progressAlpha = transition.animateFloat(label = "refresh_button_progress_alpha") { loading ->
        if (loading) 1f else 0f
    }

    Box(
        modifier = modifier
            .requiredWidth(naturalWidth)
            .height(naturalHeight),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .requiredWidth(containerWidth.value)
                .height(naturalHeight)
                .clip(RoundedCornerShape(percent = 50))
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(percent = 50),
                )
                .clickable(
                    enabled = !isLoading,
                    onClick = onClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(textAlpha.value),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = text,
                    style = labelStyle,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    maxLines = 1,
                    softWrap = false,
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(progressAlpha.value),
                contentAlignment = Alignment.Center,
            ) {
                val progressSize = with(density) {
                    maxOf(18.dp, (naturalHeight.toPx() * 0.42f).toDp())
                }
                CircularProgressIndicator(
                    modifier = Modifier.size(progressSize),
                    strokeWidth = with(density) {
                        maxOf(2.dp, (naturalHeight.toPx() * 0.06f).toDp())
                    },
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        }
    }
}
