package com.onthecrow.nomadrates.ui.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.onthecrow.nomadrates.ui.StrongPastelRed
import nomadrates.core.ui.generated.resources.Res
import nomadrates.core.ui.generated.resources.ic_like
import nomadrates.core.ui.generated.resources.ic_like_filled
import org.jetbrains.compose.resources.vectorResource

@Composable
fun LikeButtonView(
    isFavourite: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
) {
    AnimatedContent(
        modifier = modifier,
        targetState = vectorResource(
            when {
                isFavourite -> Res.drawable.ic_like_filled
                else -> Res.drawable.ic_like
            }
        ),
        label = "icon",
        transitionSpec = {
            (scaleIn(initialScale = 0.5f) + fadeIn()) togetherWith
                    (scaleOut(targetScale = 0.5f) + fadeOut())
        }
    ) { vector ->
        Icon(
            modifier = Modifier.clip(CircleShape)
                .clickable(enabled = enabled, onClick = onClick)
                .padding(12.dp),
            imageVector = vector,
            contentDescription = null,
            tint = when {
                // todo use custom theme instead eg. NomadRatesTheme.colorScheme.StrongPastelRed
                isFavourite -> StrongPastelRed
                else -> MaterialTheme.colorScheme.onBackground.copy(alpha = .5f)
            },
        )
    }
}
