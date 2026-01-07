package com.vahitkeskin.equatix

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.vahitkeskin.equatix.ui.splash.SplashScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme(
        colorScheme = darkColorScheme() // Senin tema ayarların
    ) {
        Navigator(SplashScreen()) { navigator ->
            SlideTransition(navigator)
        }
    }
}