package com.vahitkeskin.equatix.ui.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.vahitkeskin.equatix.domain.model.Difficulty
import com.vahitkeskin.equatix.domain.model.GameState
import com.vahitkeskin.equatix.domain.model.GridSize
import com.vahitkeskin.equatix.ui.common.GlassBox
import com.vahitkeskin.equatix.ui.components.AnimatedCounter
import com.vahitkeskin.equatix.ui.components.GameGrid
import com.vahitkeskin.equatix.ui.components.TransparentNumpad
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.*
import kotlin.random.Random

// --- ENTRY POINT ---
data class GameScreen(
    val difficulty: Difficulty,
    val gridSize: GridSize
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = rememberScreenModel { GameViewModel() }

        // Initialize game state if null, preventing unnecessary re-initialization on config changes.
        LaunchedEffect(Unit) {
            if (viewModel.gameState == null) viewModel.startGame(difficulty, gridSize)
        }

        val state = viewModel.gameState ?: return

        // Orchestrates the main game layout and overlays.
        GameContent(
            viewModel = viewModel,
            navigator = navigator,
            difficulty = difficulty,
            gridSize = gridSize,
            gridN = state.size
        )
    }
}

// --- MAIN LAYOUT COMPOSER ---
@Composable
private fun GameContent(
    viewModel: GameViewModel,
    navigator: cafe.adriel.voyager.navigator.Navigator,
    difficulty: Difficulty,
    gridSize: GridSize,
    gridN: Int
) {
    // Local state for timer and UI controls.
    var elapsedTime by remember { mutableStateOf(0L) }
    var isTimerRunning by remember { mutableStateOf(true) }
    var showHintDialog by remember { mutableStateOf(false) }

    // Calculates completion percentage (0.0 to 1.0) for the visual progress path.
    val progress by remember(viewModel.gameState) {
        derivedStateOf { calculateProgress(viewModel.gameState) }
    }

    // Smooths out the progress value for fluid visual updates.
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000, easing = FastOutSlowInEasing)
    )

    // Manage timer lifecycle: pauses on solution or explicit user action.
    LaunchedEffect(isTimerRunning, viewModel.isSolved) {
        if (viewModel.isSolved) isTimerRunning = false
        while (isTimerRunning && !viewModel.isSolved && isActive) {
            delay(1000L)
            elapsedTime++
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        // --- LAYER 1: AMBIENT VISUALS ---
        CosmicBackground()
        ProgressPath(progress = animatedProgress)

        // --- LAYER 2: GAME UI ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GameHeader(
                difficulty = difficulty,
                gridSize = gridSize,
                isSolved = viewModel.isSolved,
                onBack = { navigator.pop() },
                onAutoSolve = { isTimerRunning = false; viewModel.giveUpAndSolve() },
                onRefresh = {
                    viewModel.startGame(difficulty, gridSize)
                    elapsedTime = 0
                    isTimerRunning = true
                }
            )

            GameStatsBar(
                elapsedTime = elapsedTime,
                isTimerRunning = isTimerRunning,
                isSolved = viewModel.isSolved,
                onHintClick = { showHintDialog = true },
                onPauseToggle = { isTimerRunning = !isTimerRunning }
            )

            GamePlayArea(
                viewModel = viewModel,
                n = gridN,
                isTimerRunning = isTimerRunning,
                isSolved = viewModel.isSolved,
                modifier = Modifier.weight(1f)
            )

            GameBottomPanel(
                viewModel = viewModel,
                isTimerRunning = isTimerRunning,
                elapsedTime = elapsedTime,
                onInput = { viewModel.onInput(it) },
                onRestart = {
                    viewModel.startGame(difficulty, gridSize)
                    elapsedTime = 0
                    isTimerRunning = true
                }
            )
        }

        // --- LAYER 3: OVERLAYS ---
        if (showHintDialog) {
            HintDialog(onDismiss = { showHintDialog = false })
        }

        // Triggers physics-based particle system upon successful completion.
        if (viewModel.isSolved && !viewModel.isSurrendered) {
            FireworkOverlay()
        }
    }
}

// --- SUB-COMPONENT: HEADER ---
@Composable
private fun GameHeader(
    difficulty: Difficulty,
    gridSize: GridSize,
    isSolved: Boolean,
    onBack: () -> Unit,
    onAutoSolve: () -> Unit,
    onRefresh: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp)
    ) {
        // Back Navigation
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .background(Color.White.copy(0.1f), RoundedCornerShape(12.dp))
                .size(40.dp)
        ) {
            Icon(Icons.Default.ArrowBack, "Geri", tint = Color.White)
        }

        // Title Info
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

        // Actions
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!isSolved) {
                IconButton(onClick = onAutoSolve) {
                    Icon(Icons.Default.AutoFixHigh, "Yerime Doldur", tint = Color(0xFFFF3B30))
                }
            }
            IconButton(onClick = onRefresh) {
                Icon(Icons.Default.Refresh, "Yenile", tint = Color.White)
            }
        }
    }
}

// --- SUB-COMPONENT: STATS BAR (Timer & Controls) ---
@Composable
private fun GameStatsBar(
    elapsedTime: Long,
    isTimerRunning: Boolean,
    isSolved: Boolean,
    onHintClick: () -> Unit,
    onPauseToggle: () -> Unit
) {
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
            if (!isSolved) {
                ControlButton(
                    icon = Icons.Outlined.Lightbulb,
                    color = Color(0xFFFFD54F),
                    size = 42.dp,
                    onClick = onHintClick
                )
            } else {
                Spacer(modifier = Modifier.size(42.dp))
            }

            AnimatedCounter(
                count = formatTime(elapsedTime),
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Light,
                    letterSpacing = 3.sp,
                    fontSize = 40.sp
                ),
                color = if (isTimerRunning) Color.White else Color(0xFFFF9F0A)
            )

            if (!isSolved) {
                ControlButton(
                    icon = if (isTimerRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    color = if (isTimerRunning) Color(0xFF38BDF8) else Color(0xFF34C759),
                    size = 42.dp,
                    onClick = onPauseToggle
                )
            } else {
                Spacer(modifier = Modifier.size(42.dp))
            }
        }
    }
}

// --- SUB-COMPONENT: PLAY AREA (Grid) ---
@Composable
private fun GamePlayArea(
    modifier: Modifier,
    viewModel: GameViewModel,
    n: Int,
    isTimerRunning: Boolean,
    isSolved: Boolean
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Dynamically calculates cell size based on screen width to ensure responsiveness.
        val minDimension = min(maxWidth.value, maxHeight.value)
        val safeDimension = minDimension * 0.95f
        val opWidthRatio = 0.65f
        val totalUnitsInRow = n + (n * opWidthRatio) + 1.1f
        val cellSizeValue = safeDimension / totalUnitsInRow
        val cellSize = cellSizeValue.dp
        val opWidth = (cellSizeValue * opWidthRatio).dp
        val fontSize = (cellSizeValue * 0.42f).sp

        Box {
            GlassBox(modifier = Modifier.wrapContentSize(), cornerRadius = 24.dp) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GameGrid(
                        state = viewModel.gameState!!,
                        viewModel = viewModel,
                        cellSize = cellSize,
                        opWidth = opWidth,
                        fontSize = fontSize
                    )
                }
            }

            // Pause Overlay
            if (!isTimerRunning && !isSolved) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(0.85f), RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Rounded.Pause, null, tint = Color.White, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("DURAKLATILDI", color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    }
                }
            }
        }
    }
}

// --- SUB-COMPONENT: BOTTOM PANEL ---
@Composable
private fun GameBottomPanel(
    viewModel: GameViewModel,
    isTimerRunning: Boolean,
    elapsedTime: Long,
    onInput: (String) -> Unit,
    onRestart: () -> Unit
) {
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
                TransparentNumpad(onInput = onInput)
            }

            AnimatedVisibility(
                visible = viewModel.isSolved,
                enter = slideInVertically { it } + fadeIn()
            ) {
                ResultPanel(
                    isSurrendered = viewModel.isSurrendered,
                    elapsedTime = elapsedTime,
                    onRestart = onRestart,
                    onGiveUp = { }
                )
            }
        }
    }
}

// --- SABİTLER (Fizik Ayarları) ---
private const val GRAVITY = 0.5f         // Yerçekimi kuvveti
private const val DRAG = 0.96f           // Hava sürtünmesi (Yavaşlama)
private const val SPAWN_CHANCE = 0.03f   // Her karede patlama olma ihtimali (%3)
private const val PARTICLE_COUNT = 50    // Patlama başına parçacık sayısı

@Composable
fun FireworkOverlay() {
    // Performans için StateList yerine düz List kullanıyoruz.
    // Canvas'ı tetiklemek için sadece zamanı (timeMillis) state olarak tutuyoruz.
    val particles = remember { mutableListOf<FireworkParticle>() }
    var timeMillis by remember { mutableStateOf(0L) }

    val colors = remember {
        listOf(
            Color(0xFFFF3B30), Color(0xFFFF9500), Color(0xFFFFCC00),
            Color(0xFF4CD964), Color(0xFF5AC8FA), Color(0xFF007AFF),
            Color(0xFF5856D6), Color(0xFFFF2D55)
        )
    }

    // Animasyon Döngüsü
    LaunchedEffect(Unit) {
        var lastFrameTime = withFrameNanos { it }

        while (true) {
            withFrameNanos { currentFrameTime ->
                // Nanositeyi saniyeye çevir (Delta Time)
                val delta = (currentFrameTime - lastFrameTime) / 1_000_000_000f
                lastFrameTime = currentFrameTime

                // State'i güncelle ki Canvas yeniden çizilsin
                timeMillis = currentFrameTime / 1_000_000

                // 1. Yeni Havai Fişek Oluşturma Mantığı
                if (Random.nextFloat() < SPAWN_CHANCE) {
                    val centerX = Random.nextFloat()
                    val centerY = Random.nextFloat() * 0.4f // Ekranın üst %40'ında patlasın
                    val color = colors.random()

                    repeat(PARTICLE_COUNT) {
                        val angle = Random.nextDouble() * 2 * PI
                        // Hız varyasyonu ekleyerek tam daire yerine patlama efekti veriyoruz
                        val speed = Random.nextFloat() * 0.3f + 0.1f

                        particles.add(
                            FireworkParticle(
                                x = centerX,
                                y = centerY,
                                // Ekran oranına göre hızı ölçekle
                                vx = (cos(angle) * speed).toFloat(),
                                vy = (sin(angle) * speed).toFloat(),
                                color = color,
                                alpha = 1f,
                                size = Random.nextFloat() * 4f + 2f
                            )
                        )
                    }
                }

                // 2. Fizik Güncellemesi (Iterator ile güvenli silme)
                val iterator = particles.listIterator()
                while (iterator.hasNext()) {
                    val p = iterator.next()

                    // Pozisyon güncelleme (Hız * Zaman)
                    p.x += p.vx * delta
                    p.y += p.vy * delta

                    // Yerçekimi ve Sürtünme
                    p.vy += GRAVITY * delta
                    p.vx *= DRAG
                    p.vy *= DRAG

                    // Sönümlenme (Alpha azalması)
                    p.alpha -= 0.5f * delta

                    // Görünmez olanı sil
                    if (p.alpha <= 0f) {
                        iterator.remove()
                    }
                }
            }
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        // timeMillis'i burada okuyarak recomposition scope'u Canvas ile sınırlıyoruz
        val trigger = timeMillis

        val width = size.width
        val height = size.height

        particles.forEach { p ->
            // Aspect Ratio düzeltmesi:
            // X ve Y hızları 0-1 aralığında normalize olduğu için,
            // ekran dikdörtgense patlamalar oval görünür. Bunu engellemek için:
            val screenX = p.x * width
            // Y eksenindeki hareketi ekran oranına (aspect ratio) göre dengeleyebiliriz
            // Ancak basitlik adına doğrudan çarpıyoruz, yukarıdaki hız ayarı bunu telafi eder.
            val screenY = p.y * height

            if (p.alpha > 0f) {
                drawCircle(
                    color = p.color.copy(alpha = p.alpha),
                    radius = p.size,
                    center = Offset(screenX, screenY)
                )
            }
        }
    }
}

// Data Class (Ayrı dosyada veya aynı dosyanın altında durabilir)
data class FireworkParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val color: Color,
    var alpha: Float,
    val size: Float
)

// --- UTILS ---

@Composable
fun ControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    size: Dp = 48.dp,
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
            Icon(icon, null, tint = color, modifier = Modifier.size(size * 0.55f))
        }
    }
}

@Composable
fun HintDialog(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.7f))
            .padding(32.dp)
            .clickable(interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }, indication = null) { onDismiss() },
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
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("DEVAM ET", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun calculateProgress(gameState: GameState?): Float {
    if (gameState == null) return 0f

    // Artık 'gameState' gerçek bir obje olduğu için 'grid' ve 'it' tanınır.
    val totalHidden = gameState.grid.count { it.isHidden }

    val currentCorrect = gameState.grid.count {
        it.isHidden && it.userInput == it.correctValue.toString()
    }

    return if (totalHidden == 0) 1f else currentCorrect.toFloat() / totalHidden.toFloat()
}

// --- VISUAL COMPONENT: COSMIC BACKGROUND ---
/**
 * Renders a dynamic space background with moving particles and constellation-like connections.
 * Uses an infinite animation loop to drive particle movement without recomposing the entire UI.
 */
@Composable
fun CosmicBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "cosmic_anim")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    // Generate random particles once and remember them.
    val particles = remember {
        List(30) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * 2f + 1f,
                speedX = (Random.nextFloat() - 0.5f) * 0.05f,
                speedY = (Random.nextFloat() - 0.5f) * 0.05f
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // 1. Draw Deep Space Gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF000000))
            )
        )

        // 2. Update and Draw Particles
        particles.forEachIndexed { index, p ->
            // Calculate wrapping position based on time factor
            val currX = (p.x + p.speedX * (time * 10f)) % 1f
            val currY = (p.y + p.speedY * (time * 10f)) % 1f

            // Handle negative wrap-around
            val finalX = (if (currX < 0) currX + 1f else currX) * width
            val finalY = (if (currY < 0) currY + 1f else currY) * height

            drawCircle(
                color = Color.White.copy(alpha = 0.3f),
                radius = p.radius,
                center = Offset(finalX, finalY)
            )

            // 3. Draw Constellation Lines (Connect nearby particles)
            for (j in index + 1 until particles.size) {
                val p2 = particles[j]
                val p2XRaw = (p2.x + p2.speedX * (time * 10f)) % 1f
                val p2YRaw = (p2.y + p2.speedY * (time * 10f)) % 1f

                val finalP2X = (if (p2XRaw < 0) p2XRaw + 1f else p2XRaw) * width
                val finalP2Y = (if (p2YRaw < 0) p2YRaw + 1f else p2YRaw) * height

                val dist = sqrt((finalX - finalP2X).pow(2) + (finalY - finalP2Y).pow(2))
                val maxDist = width * 0.25f // Connection threshold

                if (dist < maxDist) {
                    val alpha = (1f - (dist / maxDist)) * 0.15f
                    drawLine(
                        color = Color(0xFF38BDF8).copy(alpha = alpha),
                        start = Offset(finalX, finalY),
                        end = Offset(finalP2X, finalP2Y),
                        strokeWidth = 1f
                    )
                }
            }
        }
    }
}

// --- VISUAL COMPONENT: PROGRESS PATH ---
/**
 * Draws a Bezier curve progress bar with a neon glow effect.
 * Uses PathMeasure to extract and draw only the completed segment of the path.
 */
@Composable
fun ProgressPath(progress: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Define the S-shaped path at the bottom of the screen
        val pathStart = Offset(0f, height * 0.85f)
        val pathEnd = Offset(width, height * 0.85f)
        val cp1 = Offset(width * 0.25f, height * 0.75f)
        val cp2 = Offset(width * 0.75f, height * 0.95f)

        val fullPath = Path().apply {
            moveTo(pathStart.x, pathStart.y)
            cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, pathEnd.x, pathEnd.y)
        }

        // 1. Draw Background Track (Faint)
        drawPath(
            path = fullPath,
            color = Color.White.copy(alpha = 0.05f),
            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
        )

        // 2. Measure and Cut Path
        val pathMeasure = PathMeasure()
        pathMeasure.setPath(fullPath, false)
        val pathLength = pathMeasure.length

        val activePath = Path()
        // Ensure strictly positive length to avoid artifacts
        val drawLength = pathLength * progress.coerceIn(0f, 1f)
        pathMeasure.getSegment(0f, drawLength, activePath, true)

        if (progress > 0) {
            // Neon Glow (Wide, semi-transparent stroke)
            drawPath(
                path = activePath,
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF38BDF8).copy(alpha = 0f), Color(0xFF38BDF8).copy(alpha = 0.5f))
                ),
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            )

            // Core Line (Sharp, bright gradient)
            drawPath(
                path = activePath,
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF38BDF8), Color(0xFF34C759))
                ),
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )

            // 3. Draw Indicator Thumb
            val currentPos = pathMeasure.getPosition(drawLength)
            if (currentPos != Offset.Unspecified) {
                // Outer Glow Ring
                drawCircle(
                    color = Color(0xFF34C759).copy(alpha = 0.4f),
                    radius = 12.dp.toPx(),
                    center = currentPos
                )
                // Inner White Dot
                drawCircle(
                    color = Color.White,
                    radius = 6.dp.toPx(),
                    center = currentPos
                )
            }
        }
    }
}

// Data class for CosmicBackground particles
private data class Particle(
    val x: Float, val y: Float,
    val radius: Float,
    val speedX: Float, val speedY: Float
)

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