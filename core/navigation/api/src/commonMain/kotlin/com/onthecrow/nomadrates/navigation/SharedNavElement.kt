package com.onthecrow.nomadrates.navigation

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
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
