package com.vahitkeskin.equatix.ui.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.vahitkeskin.equatix.domain.model.Difficulty
import com.vahitkeskin.equatix.domain.model.GridSize
import com.vahitkeskin.equatix.ui.common.GlassBox
import com.vahitkeskin.equatix.ui.components.AnimatedCounter
import com.vahitkeskin.equatix.ui.components.GameGrid
import com.vahitkeskin.equatix.ui.components.TransparentNumpad
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

data class GameScreen(
    val difficulty: Difficulty,
    val gridSize: GridSize
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = rememberScreenModel { GameViewModel() }

        // --- STATE ---
        var elapsedTime by remember { mutableStateOf(0L) }
        var isTimerRunning by remember { mutableStateOf(true) }
        var showHintDialog by remember { mutableStateOf(false) }

        // --- PROGRESS CALCULATION ---
        // Oyunun ne kadarının bittiğini hesapla (0.0 -> 1.0)
        val gameState = viewModel.gameState
        val progress by remember(gameState) {
            derivedStateOf {
                if (gameState == null) 0f
                else {
                    val totalHidden = gameState.grid.count { it.isHidden }
                    val currentCorrect = gameState.grid.count {
                        it.isHidden && it.userInput == it.correctValue.toString()
                    }
                    if (totalHidden == 0) 1f else currentCorrect.toFloat() / totalHidden.toFloat()
                }
            }
        }

        // İlerleme değerini animasyonlu hale getir
        val animatedProgress by animateFloatAsState(
            targetValue = progress,
            animationSpec = tween(1000, easing = FastOutSlowInEasing)
        )

        // Timer Mantığı
        LaunchedEffect(isTimerRunning, viewModel.isSolved) {
            if (viewModel.isSolved) isTimerRunning = false
            while (isTimerRunning && !viewModel.isSolved && isActive) {
                delay(1000L)
                elapsedTime++
            }
        }

        LaunchedEffect(Unit) {
            if (viewModel.gameState == null) {
                viewModel.startGame(difficulty, gridSize)
                elapsedTime = 0
                isTimerRunning = true
            }
        }

        val state = viewModel.gameState ?: return
        val n = state.size

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F172A)) // Fallback color
        ) {
            // --- KATMAN 1: CANVAS ANIMASYONLARI (ARKA PLAN) ---
            CosmicBackground() // Yıldızlar ve ağ yapısı
            ProgressPath(progress = animatedProgress) // İlerleme yolu

            // --- KATMAN 2: SİSTEM BARLARI İÇİN PADDING ---
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // --- 1. TOOLBAR (Üst Bar) ---
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 8.dp)
                    ) {
                        // SOL: Geri Butonu
                        IconButton(
                            onClick = { navigator.pop() },
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .background(Color.White.copy(0.1f), RoundedCornerShape(12.dp))
                                .size(40.dp)
                        ) {
                            Icon(Icons.Default.ArrowBack, "Geri", tint = Color.White)
                        }

                        // ORTA: Başlık Bilgisi
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = difficulty.label.uppercase(),
                                color = difficulty.color,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp
                            )
                            Text(gridSize.label, color = Color.Gray, fontSize = 10.sp)
                        }

                        // SAĞ: Aksiyon Butonları
                        Row(
                            modifier = Modifier.align(Alignment.CenterEnd),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (!viewModel.isSolved) {
                                IconButton(onClick = {
                                    isTimerRunning = false
                                    viewModel.giveUpAndSolve()
                                }) {
                                    Icon(
                                        Icons.Default.AutoFixHigh,
                                        contentDescription = "Yerime Doldur",
                                        tint = Color(0xFFFF3B30)
                                    )
                                }
                            }

                            IconButton(onClick = {
                                viewModel.startGame(difficulty, gridSize)
                                elapsedTime = 0
                                isTimerRunning = true
                                showHintDialog = false
                            }) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "Yenile",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    // --- 2. KRONOMETRE & KONTROLLER ---
                    GlassBox(
                        modifier = Modifier.padding(vertical = 8.dp),
                        cornerRadius = 16.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // İpucu
                            if (!viewModel.isSolved) {
                                ControlButton(
                                    icon = Icons.Outlined.Lightbulb,
                                    color = Color(0xFFFFD54F),
                                    size = 42.dp,
                                    onClick = { showHintDialog = true }
                                )
                            } else {
                                Spacer(modifier = Modifier.size(42.dp))
                            }

                            // Sayaç
                            AnimatedCounter(
                                count = formatTime(elapsedTime),
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontWeight = FontWeight.Light,
                                    letterSpacing = 3.sp,
                                    fontSize = 40.sp
                                ),
                                color = if (isTimerRunning) Color.White else Color(0xFFFF9F0A)
                            )

                            // Play/Pause
                            if (!viewModel.isSolved) {
                                ControlButton(
                                    icon = if (isTimerRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                    color = if (isTimerRunning) Color(0xFF38BDF8) else Color(0xFF34C759),
                                    size = 42.dp,
                                    onClick = { isTimerRunning = !isTimerRunning }
                                )
                            } else {
                                Spacer(modifier = Modifier.size(42.dp))
                            }
                        }
                    }

                    // --- 3. OYUN ALANI (GRID) ---
                    // Dinamik boyut hesaplama ile ferah görünüm
                    BoxWithConstraints(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val minDimension = min(maxWidth.value, maxHeight.value)
                        val safeDimension = minDimension * 0.95f

                        // Ölçü Ayarları
                        val opWidthRatio = 0.65f
                        val totalUnitsInRow = n + (n * opWidthRatio) + 1.1f
                        val cellSizeValue = safeDimension / totalUnitsInRow
                        val cellSize = cellSizeValue.dp
                        val opWidth = (cellSizeValue * opWidthRatio).dp
                        val fontSize = (cellSizeValue * 0.42f).sp

                        Box {
                            GlassBox(
                                modifier = Modifier.wrapContentSize(),
                                cornerRadius = 24.dp
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    GameGrid(state, viewModel, cellSize, opWidth, fontSize)
                                }
                            }

                            // Pause Overlay
                            if (!isTimerRunning && !viewModel.isSolved) {
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .background(Color.Black.copy(0.85f), RoundedCornerShape(24.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            Icons.Rounded.Pause,
                                            null,
                                            tint = Color.White,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            "DURAKLATILDI",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 2.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // --- 4. ALT PANEL (KLAVYE/SONUÇ) ---
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            AnimatedVisibility(
                                visible = !viewModel.isSolved && isTimerRunning,
                                enter = slideInVertically { it } + fadeIn(),
                                exit = fadeOut()
                            ) {
                                TransparentNumpad(onInput = { viewModel.onInput(it) })
                            }

                            AnimatedVisibility(
                                visible = viewModel.isSolved,
                                enter = slideInVertically { it } + fadeIn()
                            ) {
                                ResultPanel(
                                    isSurrendered = viewModel.isSurrendered,
                                    elapsedTime = elapsedTime,
                                    onRestart = {
                                        viewModel.startGame(difficulty, gridSize)
                                        elapsedTime = 0
                                        isTimerRunning = true
                                        showHintDialog = false
                                    },
                                    onGiveUp = { }
                                )
                            }
                        }
                    }
                }
            }

            // --- İPUCU DIALOG ---
            if (showHintDialog) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(0.7f))
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    GlassBox(modifier = Modifier.wrapContentSize()) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Outlined.Lightbulb, null, tint = Color(0xFFFFD54F), modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("İPUCU", color = Color.White, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Önce 'x' ve '÷' işlemlerini içeren satırları çözmeye çalış. Bu hücreler genellikle tek bir ihtimale sahiptir.",
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { showHintDialog = false },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("DEVAM ET", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun formatTime(seconds: Long): String {
        val m = seconds / 60
        val s = seconds % 60
        return "${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
    }
}

// --------------------------------------------------------------------------------
// YENİ CANVAS BİLEŞENLERİ: OYUNUN ATMOSFERİNİ VE İLERLEMEYİ ÇİZEN KODLAR
// --------------------------------------------------------------------------------

@Composable
fun CosmicBackground() {
    val infiniteTransition = rememberInfiniteTransition()
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // Rastgele parçacıklar üret (Yıldızlar/Nodlar)
    val particles = remember {
        List(25) { // 25 adet nokta
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * 2f + 1f,
                speedX = (Random.nextFloat() - 0.5f) * 0.002f,
                speedY = (Random.nextFloat() - 0.5f) * 0.002f
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Koyu Uzay Gradyanı
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF000000))
            )
        )

        // Parçacıkları Çiz ve Hareket Ettir
        particles.forEachIndexed { index, p ->
            // Zamanla pozisyon güncelle (Animasyon yanılsaması için basit matematik)
            // Gerçek frame-by-frame state yönetimi yerine time parametresiyle kaydırıyoruz
            var currX = (p.x + p.speedX * (time * 10000f)) % 1f
            var currY = (p.y + p.speedY * (time * 10000f)) % 1f
            if (currX < 0) currX += 1f
            if (currY < 0) currY += 1f

            val screenX = currX * width
            val screenY = currY * height

            // Noktayı çiz
            drawCircle(
                color = Color.White.copy(alpha = 0.3f),
                radius = p.radius,
                center = Offset(screenX, screenY)
            )

            // Yakın noktalar arasına çizgi çek (Constellation Effect)
            for (j in index + 1 until particles.size) {
                val p2 = particles[j]
                var p2X = (p2.x + p2.speedX * (time * 10000f)) % 1f
                var p2Y = (p2.y + p2.speedY * (time * 10000f)) % 1f
                if (p2X < 0) p2X += 1f
                if (p2Y < 0) p2Y += 1f

                val screenX2 = p2X * width
                val screenY2 = p2Y * height

                val dist = sqrt((screenX - screenX2).pow(2) + (screenY - screenY2).pow(2))
                val maxDist = width * 0.2f // Bağlantı mesafesi

                if (dist < maxDist) {
                    val alpha = (1f - (dist / maxDist)) * 0.15f // Mesafe arttıkça silikleşsin
                    drawLine(
                        color = Color(0xFF38BDF8).copy(alpha = alpha),
                        start = Offset(screenX, screenY),
                        end = Offset(screenX2, screenY2),
                        strokeWidth = 1f
                    )
                }
            }
        }
    }
}

// İlerleme Yolu (Bezire Curve)
@Composable
fun ProgressPath(progress: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Yolun geçtiği koordinatlar (Ekranın alt/orta kısmında S şekli)
        val pathStart = Offset(0f, height * 0.85f)
        val pathEnd = Offset(width, height * 0.85f)

        // Bezier Kontrol Noktaları
        val cp1 = Offset(width * 0.25f, height * 0.75f) // Yukarı kıvrım
        val cp2 = Offset(width * 0.75f, height * 0.95f) // Aşağı kıvrım

        val path = Path().apply {
            moveTo(pathStart.x, pathStart.y)
            cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, pathEnd.x, pathEnd.y)
        }

        // 1. Silik Yol (Arka plan izi)
        drawPath(
            path = path,
            color = Color.White.copy(alpha = 0.05f),
            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
        )

        // 2. Dolu Yol (İlerleme kadar çizilecek kısım)
        // PathMeasure kullanarak yolun sadece belirli bir yüzdesini kesiyoruz
        val pathMeasure = PathMeasure()
        pathMeasure.setPath(path, false)
        val pathLength = pathMeasure.length

        val progressPath = Path()
        // Progress 0 ise segment oluşturma hata verebilir, minik bir değer verelim
        pathMeasure.getSegment(0f, pathLength * progress.coerceIn(0.001f, 1f), progressPath, true)

        if (progress > 0) {
            // Neon Glow Efekti (Altına kalın, blurumsu çizgi)
            drawPath(
                path = progressPath,
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF38BDF8).copy(alpha = 0f), Color(0xFF38BDF8).copy(alpha = 0.5f))
                ),
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            )

            // Ana Çizgi
            drawPath(
                path = progressPath,
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF38BDF8), Color(0xFF34C759)) // Mavi -> Yeşil geçişi
                ),
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )

            // Yolun ucundaki parlayan top
            val currentPosition = pathMeasure.getPosition(pathLength * progress)
            if (currentPosition != Offset.Unspecified) {
                // Beyaz iç daire
                drawCircle(
                    color = Color.White,
                    radius = 6.dp.toPx(),
                    center = currentPosition // Offset'i doğrudan buraya veriyoruz
                )

                // Yeşil halka efekti
                drawCircle(
                    color = Color(0xFF34C759).copy(alpha = 0.4f),
                    radius = 12.dp.toPx(),
                    center = currentPosition
                )
            }
        }
    }
}

data class Particle(
    val x: Float,
    val y: Float,
    val radius: Float,
    val speedX: Float,
    val speedY: Float
)

// --------------------------------------------------------------------------------
// YARDIMCI BİLEŞENLER (MEVCUT)
// --------------------------------------------------------------------------------

@Composable
fun ControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    size: androidx.compose.ui.unit.Dp = 48.dp,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = color.copy(alpha = 0.15f),
        modifier = Modifier.size(size),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(size * 0.55f)
            )
        }
    }
}

@Composable
fun ResultPanel(
    isSurrendered: Boolean,
    elapsedTime: Long,
    onRestart: () -> Unit,
    onGiveUp: () -> Unit
) {
    GlassBox(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 30.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (isSurrendered) Icons.Default.Info else Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = if (isSurrendered) Color(0xFFFF9F0A) else Color(0xFF34C759),
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isSurrendered) "ÇÖZÜM GÖSTERİLDİ" else "MÜKEMMEL!",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                letterSpacing = 2.sp
            )

            if (!isSurrendered) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tamamlama Süresi: ${formatTime(elapsedTime)}",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onRestart,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    "YENİ OYUN BAŞLAT",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

// Yardımcı (Result panel içinde kullanılıyor)
private fun formatTime(seconds: Long): String {
    val m = seconds / 60
    val s = seconds % 60
    return "${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
}