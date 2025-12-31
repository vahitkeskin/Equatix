package com.vahitkeskin.equatix.ui.game.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.equatix.ui.common.GlassBox
import androidx.compose.foundation.border // Border import'u eklendi
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalAnimationApi::class) // animateEnterExit için gerekli olabilir
@Composable
fun GamePauseOverlay(
    isVisible: Boolean,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onQuit: () -> Unit
) {
    // 1. DIŞ KATMAN: Sadece Opaklık (Fade) Animasyonu
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300))
    ) {
        // Tam Ekran Koyu Arka Plan
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* Arkaya tıklamayı engelle */ },
            contentAlignment = Alignment.Center
        ) {

            // 2. İÇ KATMAN: Menü Kartı (Ölçeklenme Animasyonu Burada)
            // AnimatedVisibility Scope içinde olduğumuz için Modifier.animateEnterExit kullanabiliriz.
            Box(
                modifier = Modifier.animateEnterExit(
                    enter = scaleIn(initialScale = 0.9f) + fadeIn(),
                    exit = scaleOut(targetScale = 0.9f) + fadeOut()
                )
            ) {
                PauseMenuContent(onResume, onRestart, onQuit)
            }
        }
    }
}

@Composable
fun PauseMenuContent(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onQuit: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // --- BAŞLIK ve CANVAS EFEKTİ ---
        Box(contentAlignment = Alignment.Center) {
            // Arkadaki Radar Animasyonu
            PulseRadarEffect()

            // "PAUSED" Yazısı
            Text(
                text = "DURAKLATILDI",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Light,
                    letterSpacing = 6.sp,
                    fontSize = 28.sp
                ),
                color = Color.White.copy(alpha = 0.9f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- BUTONLAR ---
        GlassBox(
            modifier = Modifier.width(280.dp),
            cornerRadius = 24.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Büyük DEVAM ET Butonu
                ResumeButton(onClick = onResume)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // 2. Yeniden Başlat
                    MiniMenuButton(
                        icon = Icons.Rounded.Refresh,
                        text = "Yeniden",
                        color = Color(0xFFFFD54F),
                        onClick = onRestart
                    )

                    // 3. Çıkış
                    MiniMenuButton(
                        icon = Icons.Rounded.Home,
                        text = "Çıkış",
                        color = Color(0xFFEF5350),
                        onClick = onQuit
                    )
                }
            }
        }
    }
}

// --- CANVAS ANİMASYONU: RADAR ---
@Composable
fun PulseRadarEffect() {
    val infiniteTransition = rememberInfiniteTransition(label = "Radar")

    val radius1 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 150f,
        animationSpec = infiniteRepeatable(
            tween(2000, easing = LinearOutSlowInEasing),
            RepeatMode.Restart
        ), label = "r1"
    )
    val alpha1 by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 0f,
        animationSpec = infiniteRepeatable(
            tween(2000, easing = LinearOutSlowInEasing),
            RepeatMode.Restart
        ), label = "a1"
    )

    val radius2 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 150f,
        animationSpec = infiniteRepeatable(
            tween(
                2000,
                delayMillis = 1000,
                easing = LinearOutSlowInEasing
            ), RepeatMode.Restart
        ), label = "r2"
    )
    val alpha2 by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 0f,
        animationSpec = infiniteRepeatable(
            tween(
                2000,
                delayMillis = 1000,
                easing = LinearOutSlowInEasing
            ), RepeatMode.Restart
        ), label = "a2"
    )

    Canvas(modifier = Modifier.size(200.dp)) {
        drawCircle(
            color = Color(0xFF38BDF8).copy(alpha = alpha1),
            radius = radius1,
            style = Stroke(width = 2.dp.toPx())
        )
        drawCircle(
            color = Color(0xFF38BDF8).copy(alpha = alpha2),
            radius = radius2,
            style = Stroke(width = 1.dp.toPx())
        )
    }
}

// --- ÖZEL BUTONLAR ---
@Composable
fun ResumeButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34C759)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().height(56.dp),
        elevation = ButtonDefaults.buttonElevation(8.dp)
    ) {
        Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = Color.White)
        Spacer(modifier = Modifier.width(8.dp))
        Text("DEVAM ET", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun MiniMenuButton(icon: ImageVector, text: String, color: Color, onClick: () -> Unit) {
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
        Text(text, color = Color.Gray, fontSize = 12.sp)
    }
}

@Preview
@Composable
fun PreviewGamePauseOverlay() {
    // 1. Arka Plan (Oyun Ekranı Simülasyonu)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)), // Uygulama ana rengi
        contentAlignment = Alignment.Center
    ) {
        // Arkada "oyun varmış" gibi göstermek için rastgele içerik
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Oyun Arka Planı", color = Color.Gray)
            Text("Burada Grid Var", color = Color.Gray)
        }

        // 2. Pause Overlay (Görünür Modda)
        GamePauseOverlay(
            isVisible = true, // Önizlemede görmek için true yapıyoruz
            onResume = {},
            onRestart = {},
            onQuit = {}
        )
    }
}