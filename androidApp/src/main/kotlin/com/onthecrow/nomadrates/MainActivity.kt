package com.onthecrow.nomadrates

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var isThemeLoaded = false
        // App draws nothing until the stored theme is read; the splash covers that instead of a blank window.
        installSplashScreen().setKeepOnScreenCondition { !isThemeLoaded }
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            App(onThemeLoaded = { isThemeLoaded = true })
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
