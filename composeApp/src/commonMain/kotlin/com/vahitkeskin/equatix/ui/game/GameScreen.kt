package com.vahitkeskin.equatix.ui.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import kotlin.math.min

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

        // Timer Mantığı
        LaunchedEffect(isTimerRunning, viewModel.isSolved) {
            if (viewModel.isSolved) isTimerRunning = false
            while (isTimerRunning && !viewModel.isSolved) {
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
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0F172A), Color(0xFF000000))
                    )
                )
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
                // Box kullanarak başlığı tam ortalarken butonları kenarlara yaslıyoruz.
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

                    // SAĞ: Aksiyon Butonları (Yerime Doldur & Yenile)
                    Row(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Yerime Doldur (Sadece oyun bitmemişse göster)
                        if (!viewModel.isSolved) {
                            IconButton(onClick = {
                                isTimerRunning = false
                                viewModel.giveUpAndSolve()
                            }) {
                                // Sihirli değnek ikonu (AutoFixHigh) "Çöz" hissi verir
                                Icon(
                                    Icons.Default.AutoFixHigh,
                                    contentDescription = "Yerime Doldur",
                                    tint = Color(0xFFFF3B30) // Dikkat çekici kırmızı/turuncu
                                )
                            }
                        }

                        // Yenile Butonu
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
                    cornerRadius = 16.dp,
                    //backgroundColor = Color.Black.copy(0.4f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Sol: İpucu Butonu
                        if (!viewModel.isSolved) {
                            ControlButton(
                                icon = Icons.Outlined.Lightbulb,
                                color = Color(0xFFFFD54F),
                                size = 42.dp,
                                onClick = { showHintDialog = true }
                            )
                        } else {
                            Spacer(modifier = Modifier.size(42.dp)) // Boşluk koruyucu
                        }

                        // Orta: Animasyonlu Sayaç
                        AnimatedCounter(
                            count = formatTime(elapsedTime),
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.Light,
                                letterSpacing = 3.sp,
                                fontSize = 40.sp
                            ),
                            color = if (isTimerRunning) Color.White else Color(0xFFFF9F0A)
                        )

                        // Sağ: Play/Pause Butonu
                        if (!viewModel.isSolved) {
                            ControlButton(
                                icon = if (isTimerRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                color = if (isTimerRunning) Color(0xFF38BDF8) else Color(0xFF34C759),
                                size = 42.dp,
                                onClick = { isTimerRunning = !isTimerRunning }
                            )
                        } else {
                            Spacer(modifier = Modifier.size(42.dp)) // Boşluk koruyucu
                        }
                    }
                }

                // --- 3. OYUN ALANI (GRID) ---
                BoxWithConstraints(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val minDimension = min(maxWidth.value, maxHeight.value)
                    val safeDimension = minDimension * 0.95f
                    val cellSize = (safeDimension / (1.4f * n + 1.2f)).dp
                    val opWidth = (cellSize.value * 0.4f).dp
                    val fontSize = (cellSize.value * 0.45f).sp

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

// --- YENİ BİLEŞEN: SONUÇ PANELİ ---
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