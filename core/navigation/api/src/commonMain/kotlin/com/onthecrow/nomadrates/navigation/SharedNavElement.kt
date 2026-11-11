package com.onthecrow.nomadrates.navigation

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.navigation3.ui.LocalNavAnimatedContentScope

/**
 * Shared transition scope around the navigation host, or `null` outside of it (e.g. in previews).
 */
val LocalSharedTransitionScope: ProvidableCompositionLocal<SharedTransitionScope?> =
    staticCompositionLocalOf { null }

/**
 * Marks this element as shared between screens: when two screens that both contain an element with
 * the same [key] replace each other, the element moves and resizes between their positions.
 * Does nothing outside of the navigation host.
 */
@Composable
fun Modifier.sharedNavElement(key: Any): Modifier {
    val sharedTransitionScope = LocalSharedTransitionScope.current ?: return this
    val animatedContentScope = LocalNavAnimatedContentScope.current
    return with(sharedTransitionScope) {
        this@sharedNavElement.sharedElement(
            sharedContentState = rememberSharedContentState(key),
            animatedVisibilityScope = animatedContentScope,
        )
    }
}

/**
 * For an element that a shared element would fly over (e.g. a button on top of a shared field):
 * while a shared transition runs, the element is drawn above shared elements and shrinks into a
 * point, then grows back once the transition is over. Does nothing outside of the navigation host
 * and during ordinary transitions.
 */
@Composable
fun Modifier.shrinkDuringSharedTransition(): Modifier {
    val sharedTransitionScope = LocalSharedTransitionScope.current ?: return this
    val scale by animateFloatAsState(
        targetValue = if (sharedTransitionScope.isTransitionActive) 0f else 1f,
        label = "shrinkDuringSharedTransition",
    )
    return with(sharedTransitionScope) {
        this@shrinkDuringSharedTransition
            .renderInSharedTransitionScopeOverlay(zIndexInOverlay = 1f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    }
}
