package com.vahitkeskin.equatix.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.vahitkeskin.equatix.di.AppModule
import com.vahitkeskin.equatix.domain.model.AppThemeConfig
import com.vahitkeskin.equatix.platform.getAppVersion
import com.vahitkeskin.equatix.ui.home.HomeScreen
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem
import kotlinx.coroutines.delay
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.random.Random

class SplashScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        // 1. Mantık ve Veri Katmanı (Preview'da çalışmaz, sadece burada çalışır)
        val settingsRepo = AppModule.settingsRepository
        val themeConfig by settingsRepo.themeConfig.collectAsState(initial = AppThemeConfig.FOLLOW_SYSTEM)
        val isSystemDark = isSystemInDarkTheme()

        val isDark = when (themeConfig) {
            AppThemeConfig.FOLLOW_SYSTEM -> isSystemDark
            AppThemeConfig.DARK -> true
            AppThemeConfig.LIGHT -> false
        }

        // Navigasyon Mantığı
        LaunchedEffect(Unit) {
            delay(2500)
            navigator.replace(HomeScreen())
        }

        // 2. Saf UI Katmanı (Aşağıdaki fonksiyonu çağırıyoruz)
        SplashUI(isDark = isDark)
    }
}

/**
 * STATELESS UI COMPONENT
 * Navigator veya DataStore bilmez. Sadece renk ve çizim yapar.
 * Bu sayede Preview edilebilir.
 */
@Composable
fun SplashUI(isDark: Boolean) {
    val colors = EquatixDesignSystem.getColors(isDark)

    // Arka Plan Gradyanı
    val bgGradient = Brush.verticalGradient(
        colors = if (isDark) {
            listOf(colors.background, Color.Black)
        } else {
            listOf(colors.background, Color.White)
        }
    )

    val rainColor = if (isDark) colors.accent else colors.textPrimary

    // Animasyon State'leri (UI içinde yönetilebilir)
    var startAnimation by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1500)
    )

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient),
        contentAlignment = Alignment.Center
    ) {
        // 1. KATMAN: Matrix Yağmuru
        MatrixRainBackground(dropColor = rainColor, isDark = isDark)

        // 2. KATMAN: Logo ve Başlık
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(scale)
                .alpha(alpha)
        ) {
            // Logo Kutusu
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        color = colors.textPrimary.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "∑",
                    fontSize = 50.sp,
                    color = colors.accent
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "EQUATIX",
                style = MaterialTheme.typography.displayMedium,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 8.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "MATRIX PUZZLE",
                color = colors.textSecondary,
                fontSize = 12.sp,
                letterSpacing = 4.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // 3. KATMAN: Alt Telif
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 32.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Text(
                text = "v${getAppVersion()}", // Dinamik Sürüm
                color = colors.textSecondary.copy(alpha = 0.5f),
                fontSize = 10.sp
            )
        }
    }
}

// --- MATRIX YAĞMURU ---
@Composable
fun MatrixRainBackground(dropColor: Color, isDark: Boolean) {
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

    val drops = remember {
        List(40) {
            MatrixDrop(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                speed = Random.nextFloat() * 0.5f + 0.2f,
                length = Random.nextFloat() * 0.15f + 0.05f
            )
        }
    }

    val canvasAlpha = if (isDark) 0.3f else 0.15f

    Canvas(modifier = Modifier.fillMaxSize().alpha(canvasAlpha)) {
        val width = size.width
        val height = size.height

        drops.forEach { drop ->
            val currentY = (drop.y + time * drop.speed * 5f) % 1.2f
            if (currentY - drop.length < 1f) {
                val startY = (currentY - drop.length) * height
                val endY = currentY * height
                val xPos = drop.x * width

                drawLine(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, dropColor),
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

// ==========================================
//              PREVIEW BÖLÜMÜ
// ==========================================

@Preview
@Composable
fun PreviewSplashDark() {
    // Navigator ve DataStore olmadan doğrudan UI çiziyoruz
    SplashUI(isDark = true)
}

@Preview
@Composable
fun PreviewSplashLight() {
    // Navigator ve DataStore olmadan doğrudan UI çiziyoruz
    SplashUI(isDark = false)
}