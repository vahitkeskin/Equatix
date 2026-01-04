package com.vahitkeskin.equatix.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedCounter(
    count: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color // <--- Varsayılan Color.White kaldırıldı
) {
    Row(modifier = modifier) {
        count.forEach { char ->
            if (char.isDigit()) {
                AnimatedContent(
                    targetState = char,
                    transitionSpec = {
                        slideInVertically { height -> height } + fadeIn() with
                                slideOutVertically { height -> -height } + fadeOut()
                    }
                ) { targetChar ->
                    Text(
                        text = targetChar.toString(),
                        style = style,
                        color = color, // Temadan gelen renk
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Text(
                    text = char.toString(),
                    style = style,
                    color = color, // Temadan gelen renk
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}