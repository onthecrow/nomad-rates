package com.onthecrow.nomadrates.ui.view

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
    onClick: () -> Unit = {},
) {
    Icon(
        modifier = modifier.clip(CircleShape)
            .clickable(enabled = true, onClick = onClick)
            .padding(12.dp),
        imageVector = vectorResource(
            when {
                isFavourite -> Res.drawable.ic_like_filled
                else -> Res.drawable.ic_like
            }
        ),
        contentDescription = null,
        tint = when {
            // todo use custom theme instead eg. NomadRatesTheme.colorScheme.StrongPastelRed
            isFavourite -> StrongPastelRed
            else -> MaterialTheme.colorScheme.onBackground.copy(alpha = .5f)
        },
    )
}