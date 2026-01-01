package com.vahitkeskin.equatix.ui.game.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.equatix.ui.common.GlassBox
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GamePauseOverlay(
    isVisible: Boolean,
    colors: EquatixDesignSystem.ThemeColors,
    isDark: Boolean,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onQuit: () -> Unit
) {
    // Light modda arka plan beyazımsı, Dark modda siyahımsı
    val overlayBg = if (isDark) Color.Black.copy(alpha = 0.8f) else Color.White.copy(0.7f)
    val textColor = if (isDark) Color.White else Color.Black

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(overlayBg)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.animateEnterExit(
                    enter = scaleIn(initialScale = 0.9f) + fadeIn(),
                    exit = scaleOut(targetScale = 0.9f) + fadeOut()
                )
            ) {
                PauseMenuContent(onResume, onRestart, onQuit, colors, textColor)
            }
        }
    }
}

@Composable
fun PauseMenuContent(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onQuit: () -> Unit,
    colors: EquatixDesignSystem.ThemeColors,
    textColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            PulseRadarEffect(colors.accent)
            Text(
                text = "DURAKLATILDI",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Light,
                    letterSpacing = 6.sp,
                    fontSize = 28.sp
                ),
                color = textColor.copy(alpha = 0.9f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        GlassBox(
            modifier = Modifier.width(280.dp),
            cornerRadius = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .background(colors.cardBackground)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ResumeButton(onClick = onResume, colors = colors)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MiniMenuButton(
                        icon = Icons.Rounded.Refresh,
                        text = "Yeniden",
                        color = colors.gold,
                        textColor = colors.textSecondary,
                        onClick = onRestart
                    )

                    MiniMenuButton(
                        icon = Icons.Rounded.Home,
                        text = "Çıkış",
                        color = Color(0xFFEF5350),
                        textColor = colors.textSecondary,
                        onClick = onQuit
                    )
                }
            }
        }
    }
}

@Composable
fun PulseRadarEffect(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "Radar")
    // ... (Animasyonlar aynı, sadece renk parametresi kullan)
    // Örnek: color = color.copy(alpha = alpha1)
}

@Composable
fun ResumeButton(onClick: () -> Unit, colors: EquatixDesignSystem.ThemeColors) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = colors.success),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().height(56.dp),
        elevation = ButtonDefaults.buttonElevation(8.dp)
    ) {
        Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = Color.White)
        Spacer(modifier = Modifier.width(8.dp))
        Text("DEVAM ET", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
fun MiniMenuButton(icon: ImageVector, text: String, color: Color, textColor: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(color.copy(alpha = 0.2f), CircleShape)
                .border(1.dp, color.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text, color = textColor, fontSize = 12.sp)
    }
}

@Preview
@Composable
fun PreviewGamePauseOverlayDark() {
    // 1. Tema Ayarları (Dark)
    val isDark = true
    val colors = EquatixDesignSystem.getColors(isDark)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background), // Temadan gelen arka plan
        contentAlignment = Alignment.Center
    ) {
        // Arkada "oyun varmış" gibi göstermek için içerik
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Oyun Arka Planı (Dark)", color = colors.textSecondary)
            Text("Burada Grid Var", color = colors.textSecondary)
        }

        // 2. Pause Overlay
        GamePauseOverlay(
            isVisible = true,
            colors = colors, // <--- YENİ PARAMETRE
            isDark = isDark, // <--- YENİ PARAMETRE
            onResume = {},
            onRestart = {},
            onQuit = {}
        )
    }
}

@Preview
@Composable
fun PreviewGamePauseOverlayLight() {
    // 1. Tema Ayarları (Light)
    val isDark = false
    val colors = EquatixDesignSystem.getColors(isDark)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background), // Temadan gelen arka plan
        contentAlignment = Alignment.Center
    ) {
        // Arkada "oyun varmış" gibi göstermek için içerik
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Oyun Arka Planı (Light)", color = colors.textSecondary)
            Text("Burada Grid Var", color = colors.textSecondary)
        }

        // 2. Pause Overlay
        GamePauseOverlay(
            isVisible = true,
            colors = colors, // <--- YENİ PARAMETRE
            isDark = isDark, // <--- YENİ PARAMETRE
            onResume = {},
            onRestart = {},
            onQuit = {}
        )
    }
}