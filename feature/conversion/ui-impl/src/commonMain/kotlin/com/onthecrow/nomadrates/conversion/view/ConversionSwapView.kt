package com.onthecrow.nomadrates.conversion.view

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.onthecrow.nomadrates.ui.NomadRatesTheme
import kotlinx.coroutines.launch
import nomadrates.feature.conversion.ui_impl.generated.resources.Res
import nomadrates.feature.conversion.ui_impl.generated.resources.ic_swap
import org.jetbrains.compose.resources.vectorResource

@Composable
internal fun ConversionSwapView(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
) {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    val clickWithPop = {
        scope.launch {
            scale.snapTo(1f)
            scale.animateTo(0.7f, tween(90))
            scale.animateTo(1f, tween(140))
        }
        onClick()
    }

    Icon(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .background(MaterialTheme.colorScheme.surfaceContainer, shape = CircleShape)
            .padding(4.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondary)
            .clickable(enabled = enabled, onClick = clickWithPop)
            .padding(8.dp),
        imageVector = vectorResource(Res.drawable.ic_swap),
        contentDescription = null,
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF1C1B1F,
)
@Composable
private fun ConversionViewPreview() {
    NomadRatesTheme(darkTheme = true) {
        ConversionSwapView(modifier = Modifier.padding(16.dp))
    }
}
