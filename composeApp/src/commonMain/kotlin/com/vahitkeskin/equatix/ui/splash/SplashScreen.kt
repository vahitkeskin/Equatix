package com.vahitkeskin.equatix.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.vahitkeskin.equatix.ui.home.HomeScreen
import kotlinx.coroutines.delay
import kotlin.random.Random

class SplashScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        // Animasyon State'leri
        var startAnimation by remember { mutableStateOf(false) }

        // Logo Ölçek Animasyonu (Spring Efekti ile)
        val scale by animateFloatAsState(
            targetValue = if (startAnimation) 1f else 0.5f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )

        // Opaklık Animasyonu
        val alpha by animateFloatAsState(
            targetValue = if (startAnimation) 1f else 0f,
            animationSpec = tween(1500)
        )

        LaunchedEffect(Unit) {
            startAnimation = true
            delay(2500) // 2.5 saniye bekle
            navigator.replace(HomeScreen()) // Home'a git ve Splash'i geçmişten sil
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0F172A), Color(0xFF000000))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // 1. KATMAN: Canvas Matrix Efekti
            MatrixRainBackground()

            // 2. KATMAN: Logo ve Başlık
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .scale(scale)
                    .alpha(alpha)
            ) {
                // Logo Kutusu (Basit bir geometrik şekil veya ikon)
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.White.copy(0.1f), androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "∑", // Matematik Sembolü
                        fontSize = 50.sp,
                        color = Color(0xFF38BDF8)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "EQUATIX",
                    style = MaterialTheme.typography.displayMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 8.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "MATRIX PUZZLE",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    letterSpacing = 4.sp
                )
            }

            // 3. KATMAN: Alt Telif Bilgisi
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    text = "v1.0",
                    color = Color.White.copy(0.3f),
                    fontSize = 10.sp
                )
            }
        }
    }
}

// --- CANVAS EFEKTİ: DİJİTAL YAĞMUR ---
@Composable
fun MatrixRainBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "rain")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    // Rastgele noktalar oluştur (Sadece bir kez)
    val drops = remember {
        List(40) { // 40 adet düşen çizgi
            MatrixDrop(
                x = Random.nextFloat(),
                y = Random.nextFloat(), // Başlangıç Y
                speed = Random.nextFloat() * 0.5f + 0.2f, // Hız
                length = Random.nextFloat() * 0.15f + 0.05f // Çizgi uzunluğu
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize().alpha(0.3f)) {
        val width = size.width
        val height = size.height

        drops.forEach { drop ->
            // Zamanla aşağı inme mantığı
            val currentY = (drop.y + time * drop.speed * 5f) % 1.2f
            // 1.2f yapıyoruz ki ekranın biraz altına inip sonra yukarıdan başlasın

            // Eğer ekranın içindeyse çiz
            if (currentY - drop.length < 1f) {
                val startY = (currentY - drop.length) * height
                val endY = currentY * height
                val xPos = drop.x * width

                drawLine(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xFF34C759)), // Matrix Yeşili
                        startY = startY,
                        endY = endY
                    ),
                    start = Offset(xPos, startY),
                    end = Offset(xPos, endY),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

private data class MatrixDrop(
    val x: Float,
    val y: Float,
    val speed: Float,
    val length: Float
)