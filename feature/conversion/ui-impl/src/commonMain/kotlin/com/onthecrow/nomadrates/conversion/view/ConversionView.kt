package com.onthecrow.nomadrates.conversion.view

import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.animateBounds
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.onthecrow.nomadrates.conversion.ConversionEvent
import com.onthecrow.nomadrates.conversion.model.ConversionViewState
import com.onthecrow.nomadrates.favouritesconversion.FavouritesConversionSharedElements
import com.onthecrow.nomadrates.navigation.sharedNavElement
import com.onthecrow.nomadrates.navigation.shrinkDuringSharedTransition
import com.onthecrow.nomadrates.ui.NomadRatesTheme
import com.onthecrow.nomadrates.ui.model.CurrencyAmountViewState
import com.onthecrow.nomadrates.ui.view.CurrencyAmountView
import com.onthecrow.nomadrates.ui.view.LikeButtonView
import kotlin.math.PI
import kotlin.math.sin

private const val SWAP_DURATION_MILLIS = 450

/** How much the field moving up grows and the one moving down shrinks halfway through a swap. */
private const val SWAP_RISING_SCALE = 0.04f
private const val SWAP_SINKING_SCALE = 0.06f

/** Shadow the field moving up casts on the one moving down, halfway through a swap. */
private val SwapShadowElevation = 12.dp
private val FieldShape = RoundedCornerShape(100.dp)

private const val SLOT_FIRST = "first"
private const val SLOT_SECOND = "second"

@Composable
internal fun ConversionView(
    state: ConversionViewState,
    modifier: Modifier = Modifier,
    /** Scale of the swap button, so the screen can hide it before leaving for the favourites conversion screen. */
    swapButtonScale: () -> Float = { 1f },
    /** The bottom field, not the top one, flies to the favourites conversion screen and back. */
    sharedFieldIsBottom: Boolean = false,
    /** Whether the user is typing in the bottom field. */
    onBottomFieldFocusChange: (Boolean) -> Unit = {},
    onEvent: (ConversionEvent) -> Unit = {},
) {
    var focusedSlot by remember { mutableStateOf<String?>(null) }
    val swapCount = (state as? ConversionViewState.Content)?.swapCount ?: 0
    val swapProgress = rememberSwapProgress(swapCount)
    val swapButtonRotation by animateFloatAsState(
        targetValue = swapCount * 180f,
        animationSpec = tween(SWAP_DURATION_MILLIS, easing = FastOutSlowInEasing),
        label = "swap_button_rotation",
    )

    Box(
        modifier = modifier,
    ) {
        LookaheadScope {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(32.dp),
                    )
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(32.dp),
                    )
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                when (state) {
                    ConversionViewState.Loading -> {
                        CurrencyAmountView(
                            state = CurrencyAmountViewState.Loading,
                        )
                        CurrencyAmountView(
                            state = CurrencyAmountViewState.Loading,
                        )
                    }

                    is ConversionViewState.Content -> {
                        // Each field is one element that keeps its currency and amount; a swap
                        // changes which of them is on top, and they move to their new places.
                        val topSlot = if (state.swapCount % 2 == 0) SLOT_FIRST else SLOT_SECOND
                        val bottomSlot = if (topSlot == SLOT_FIRST) SLOT_SECOND else SLOT_FIRST
                        val bottomFieldFocused = focusedSlot == bottomSlot
                        SideEffect { onBottomFieldFocusChange(bottomFieldFocused) }
                        listOf(topSlot, bottomSlot).forEach { slot ->
                            key(slot) {
                                val isTop = slot == topSlot
                                val isShared = isTop != sharedFieldIsBottom
                                CurrencyAmountView(
                                    modifier = Modifier
                                        .animateBounds(
                                            lookaheadScope = this@LookaheadScope,
                                            boundsTransform = SwapBoundsTransform,
                                        )
                                        // The field moving up passes over the one moving down.
                                        .zIndex(if (isTop) 1f else 0f)
                                        .graphicsLayer {
                                            val swapPeak = sin(PI.toFloat() * swapProgress())
                                            val depth = swapPeak *
                                                if (isTop) SWAP_RISING_SCALE else -SWAP_SINKING_SCALE
                                            scaleX = 1f + depth
                                            scaleY = 1f + depth
                                            if (isTop) {
                                                shadowElevation = swapPeak * SwapShadowElevation.toPx()
                                                shape = FieldShape
                                            }
                                        }
                                        .onFocusChanged { focusState ->
                                            if (focusState.hasFocus) {
                                                focusedSlot = slot
                                            } else if (focusedSlot == slot) {
                                                focusedSlot = null
                                            }
                                        }
                                        .then(
                                            if (isShared) {
                                                Modifier.sharedNavElement(FavouritesConversionSharedElements.AMOUNT_FIELD_KEY)
                                            } else {
                                                Modifier
                                            }
                                        ),
                                    state = if (isTop) state.from else state.to,
                                    onCurrencyClick = {
                                        onEvent(
                                            if (isTop) ConversionEvent.OnFromCurrencyClick else ConversionEvent.OnToCurrencyClick
                                        )
                                    },
                                    onValueChange = {
                                        onEvent(
                                            if (isTop) ConversionEvent.OnFromValueChange(it) else ConversionEvent.OnToValueChange(it)
                                        )
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
        ConversionSwapView(
            modifier = Modifier
                .align(Alignment.Center)
                // The "from" field flies over this button between this and the favourites conversion screen.
                .shrinkDuringSharedTransition()
                .graphicsLayer {
                    scaleX = swapButtonScale()
                    scaleY = swapButtonScale()
                    rotationZ = swapButtonRotation
                },
            enabled = state is ConversionViewState.Content,
            onClick = { onEvent(ConversionEvent.OnSwitchButtonPress) },
        )
        LikeButtonView(
            modifier = Modifier.align(Alignment.TopEnd)
                .background(MaterialTheme.colorScheme.surfaceContainer, shape = CircleShape)
                .padding(4.dp)
                .background(MaterialTheme.colorScheme.background, shape = CircleShape),
            isFavourite = (state as? ConversionViewState.Content)?.isFavourite == true,
            enabled = state is ConversionViewState.Content,
            onClick = { onEvent(ConversionEvent.OnActiveConversionPairFavouritesClick) },
        )
    }
}

private val SwapBoundsTransform = BoundsTransform { _, _ ->
    tween(SWAP_DURATION_MILLIS, easing = FastOutSlowInEasing)
}

/**
 * Progress of the latest swap, from 0 to 1, going in step with the fields moving. It is 1 when
 * no swap is running, including when the screen is first shown.
 */
@Composable
private fun rememberSwapProgress(swapCount: Int): () -> Float {
    val progress = remember { Animatable(1f) }
    val lastSwapCount = remember { mutableIntStateOf(swapCount) }
    LaunchedEffect(swapCount) {
        if (swapCount != lastSwapCount.intValue) {
            lastSwapCount.intValue = swapCount
            progress.snapTo(0f)
            progress.animateTo(1f, tween(SWAP_DURATION_MILLIS, easing = FastOutSlowInEasing))
        }
    }
    return { progress.value }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF1C1B1F,
)
@Composable
private fun ConversionViewPreview() {
    NomadRatesTheme(darkTheme = true) {
        ConversionView(
            state = ConversionViewState.Content(
                from = CurrencyAmountViewState.Content("", "EUR", ""),
                to = CurrencyAmountViewState.Content("", "USD", ""),
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF1C1B1F,
)
@Composable
private fun ConversionViewLoadingPreview() {
    NomadRatesTheme(darkTheme = true) {
        ConversionView(
            state = ConversionViewState.Loading,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
