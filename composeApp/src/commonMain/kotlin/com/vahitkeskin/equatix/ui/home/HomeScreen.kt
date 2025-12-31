package com.vahitkeskin.equatix.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.vahitkeskin.equatix.ui.common.AnimatedSegmentedControl
import com.vahitkeskin.equatix.ui.common.GlassBox
import com.vahitkeskin.equatix.ui.game.GameScreen
import com.vahitkeskin.equatix.ui.game.visuals.CosmicBackground

class HomeScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = rememberScreenModel { HomeViewModel() }

        // --- UI State ---
        var selectedDiff by remember { mutableStateOf(Difficulty.EASY) }
        var selectedSize by remember { mutableStateOf(GridSize.SIZE_3x3) }
        var activeOverlay by remember { mutableStateOf<OverlayType?>(null) }

        // --- Giriş Animasyonları (Staggered Entry) ---
        var startAnimation by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            startAnimation = true
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F172A))
                .systemBarsPadding()
        ) {
            // KATMAN 1: Arka Plan Efektleri
            CosmicBackground() // Yıldızlar
            DigitalGridBackground() // Grid

            // KATMAN 2: Ana İçerik
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // 1. Header (0ms delay)
                AnimatedSlideIn(visible = startAnimation, delay = 0) {
                    HomeHeader(
                        onHistoryClick = { activeOverlay = HomeScreen.OverlayType.HISTORY },
                        onSettingsClick = { activeOverlay = HomeScreen.OverlayType.SETTINGS }
                    )
                }

                // GÜNCELLEME: Header ile Logo arasına az bir esneklik (Logo çok aşağı inmesin)
                Spacer(modifier = Modifier.weight(0.15f))

                // 2. Logo ve Motto (200ms delay)
                AnimatedSlideIn(visible = startAnimation, delay = 200) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "EQUATIX",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 8.sp,
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF38BDF8), Color.White)
                                )
                            ),
                            fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "MATRIX PUZZLE",
                            color = Color(0xFF38BDF8).copy(alpha = 0.7f),
                            fontSize = 14.sp,
                            letterSpacing = 4.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // GÜNCELLEME: Logo ile Kart arasına sabit ve daha kısa mesafe (Weight yerine Height)
                // Bu sayede kartlar Logoya yapışık gibi durur ve yukarı çıkar.
                Spacer(modifier = Modifier.height(48.dp))

                // 3. Seçim Paneli (400ms delay)
                AnimatedSlideIn(visible = startAnimation, delay = 400) {
                    GlassBox(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 24.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            SelectionRow(
                                label = "ZORLUK",
                                content = {
                                    AnimatedSegmentedControl(
                                        items = Difficulty.values().toList(),
                                        selectedItem = selectedDiff,
                                        onItemSelected = { selectedDiff = it },
                                        modifier = Modifier.fillMaxWidth(),
                                        itemLabel = { it.label.split(" ")[0].uppercase() }
                                    )
                                }
                            )
                            Divider(color = Color.White.copy(0.1f))
                            SelectionRow(
                                label = "BOYUT",
                                content = {
                                    AnimatedSegmentedControl(
                                        items = GridSize.values().toList(),
                                        selectedItem = selectedSize,
                                        onItemSelected = { selectedSize = it },
                                        modifier = Modifier.fillMaxWidth(),
                                        itemLabel = { it.label }
                                    )
                                }
                            )
                        }
                    }
                }

                // Kart ile Buton arası mesafe
                Spacer(modifier = Modifier.height(24.dp))

                // 4. Profesyonel Start Butonu (600ms delay)
                AnimatedSlideIn(visible = startAnimation, delay = 600) {
                    CyberStartButton(
                        onClick = { navigator.push(GameScreen(selectedDiff, selectedSize)) }
                    )
                }

                // GÜNCELLEME: En alta büyük bir ağırlık (Weight) koyuyoruz.
                // Bu "yay" görevi görerek üstteki her şeyi (Logo, Kart, Buton) yukarıya iter.
                Spacer(modifier = Modifier.weight(1f))
            }

            // KATMAN 3: Overlay (Ayarlar / Geçmiş)
            OverlayPanel(
                overlayType = activeOverlay,
                onDismiss = { activeOverlay = null },
                viewModel = viewModel
            )
        }
    }

    enum class OverlayType { HISTORY, SETTINGS }
}

// --- YENİ COMPONENTLER ---

/**
 * Canvas ile çizilen ve sürekli akan dijital bir ızgara efekti.
 * Derinlik hissini UI/UX açısından artırır.
 */
@Composable
fun DigitalGridBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "grid")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing)),
        label = "offset"
    )

    Canvas(modifier = Modifier.fillMaxSize().graphicsLayer { alpha = 0.15f }) {
        val gridSize = 50.dp.toPx()
        val width = size.width
        val height = size.height

        // Dikey Çizgiler
        for (x in 0..width.toInt() step gridSize.toInt()) {
            drawLine(
                color = Color(0xFF38BDF8),
                start = Offset(x.toFloat(), 0f),
                end = Offset(x.toFloat(), height),
                strokeWidth = 1f
            )
        }

        // Yatay Çizgiler (Hareketli)
        for (y in -100..height.toInt() step gridSize.toInt()) {
            val yPos = y + offsetY
            if (yPos < height) {
                drawLine(
                    color = Color(0xFF38BDF8),
                    start = Offset(0f, yPos),
                    end = Offset(width, yPos),
                    strokeWidth = 1f
                )
            }
        }

        // Alt kısımda karartma (Vignette)
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color(0xFF0F172A)),
                startY = height * 0.6f,
                endY = height
            )
        )
    }
}

/**
 * Özel Tasarım Başlat Butonu
 * Neon, Glow efektli ve Canvas animasyonlu.
 */
@Composable
fun CyberStartButton(onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "ButtonPulse")

    // Kenar Glow Animasyonu
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(1500, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFF34C759).copy(0.1f), Color(0xFF32ADE6).copy(0.1f))
                )
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // 1. Kenarlık (Canvas ile çizim)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 2.dp.toPx()
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF34C759), Color(0xFF32ADE6))
                ),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(24.dp.toPx()),
                style = Stroke(width = strokeWidth),
                alpha = borderAlpha
            )
        }

        // 2. İçerik
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "OYUNU BAŞLAT",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = Icons.Rounded.PlayArrow,
                contentDescription = null,
                tint = Color(0xFF34C759),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun HomeHeader(onHistoryClick: () -> Unit, onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        HeaderButton(
            icon = Icons.Default.EmojiEvents,
            color = Color(0xFFFFD54F),
            onClick = onHistoryClick
        )
        HeaderButton(icon = Icons.Default.Settings, color = Color.White, onClick = onSettingsClick)
    }
}

@Composable
fun HeaderButton(icon: ImageVector, color: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.White.copy(0.05f),
        modifier = Modifier.size(48.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = color)
        }
    }
}

@Composable
fun SelectionRow(label: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = label,
            color = Color(0xFF38BDF8),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

// --- ANIMATION HELPER ---
@Composable
fun AnimatedSlideIn(
    visible: Boolean,
    delay: Int,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { 50 },
            animationSpec = tween(500, delayMillis = delay, easing = FastOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(500, delayMillis = delay)),
        content = { content() }
    )
}

// --- OVERLAY HELPER ---
@Composable
fun OverlayPanel(
    overlayType: HomeScreen.OverlayType?,
    onDismiss: () -> Unit,
    viewModel: HomeViewModel
) {
    AnimatedVisibility(
        visible = overlayType != null,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(0.8f))
                .clickable { onDismiss() }
        )
    }

    AnimatedVisibility(
        visible = overlayType != null,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut(),
        modifier = Modifier
    ) {
        val overlay = overlayType ?: return@AnimatedVisibility

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            GlassBox(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.75f)
                    .padding(16.dp),
                cornerRadius = 24.dp
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (overlay == HomeScreen.OverlayType.HISTORY) "SKORLAR" else "AYARLAR",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, null, tint = Color.Gray)
                        }
                    }
                    Divider(
                        color = Color.White.copy(0.1f),
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    when (overlay) {
                        HomeScreen.OverlayType.HISTORY -> HistoryList(viewModel)
                        HomeScreen.OverlayType.SETTINGS -> SettingsList(viewModel)
                    }
                }
            }
        }
    }
}

// (HistoryList ve SettingsList fonksiyonları mevcut kodundaki gibi kalabilir,
// sadece Box yerine GlassBox içinde çağrıldıklarından emin ol.)
// Aşağıda referans için SettingsList'in güncellenmiş hali:

@Composable
fun SettingsList(viewModel: HomeViewModel) {
    val isSoundOn by viewModel.isSoundOn.collectAsState()
    val isVibrationOn by viewModel.isVibrationOn.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SettingItem(
            title = "Oyun Sesleri",
            icon = if (isSoundOn) Icons.Rounded.VolumeUp else Icons.Rounded.VolumeOff,
            isOn = isSoundOn,
            onToggle = { viewModel.toggleSound() }
        )

        SettingItem(
            title = "Titreşim",
            icon = if (isVibrationOn) Icons.Rounded.Vibration else Icons.Rounded.Smartphone,
            isOn = isVibrationOn,
            onToggle = { viewModel.toggleVibration() }
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            "EQUATIX v1.0",
            color = Color.Gray,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// HistoryList fonksiyonu da aynı mantıkla kullanılabilir.

// --- HISTORY LIST COMPONENT ---
@Composable
fun HistoryList(viewModel: HomeViewModel) {
    val scores by viewModel.scores.collectAsState()

    if (scores.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Henüz oyun oynanmadı.", color = Color.Gray)
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(scores) { score ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(0.05f), RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Sol: Tarih ve Zorluk
                    Column {
                        Text(
                            score.date,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "${score.difficulty.label} • ${score.gridSize.label}",
                            color = score.difficulty.color,
                            fontSize = 12.sp
                        )
                    }

                    // Sağ: Puan ve Süre
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "${score.score} P",
                            color = Color(0xFF34C759), // Yeşil
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.History,
                                null,
                                tint = Color.Gray,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(score.time, color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingItem(
    title: String,
    icon: ImageVector,
    isOn: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .background(
                if (isOn) Color.White.copy(0.1f) else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .border(1.dp, Color.White.copy(0.1f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Color.White)
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, color = Color.White, fontSize = 16.sp)
        }

        Switch(
            checked = isOn,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF34C759),
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color.Black
            )
        )
    }
}