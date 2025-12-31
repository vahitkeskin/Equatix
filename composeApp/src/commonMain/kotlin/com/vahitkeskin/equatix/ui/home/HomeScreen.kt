package com.vahitkeskin.equatix.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
import com.vahitkeskin.equatix.ui.common.AnimatedSegmentedControl
import com.vahitkeskin.equatix.ui.common.GlassBox
import com.vahitkeskin.equatix.ui.game.GameScreen

class HomeScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = rememberScreenModel { HomeViewModel() }

        // --- UI State ---
        var selectedDiff by remember { mutableStateOf(Difficulty.EASY) }
        var selectedSize by remember { mutableStateOf(GridSize.SIZE_3x3) }

        // Overlay Kontrolü (Hangi panel açık?)
        var activeOverlay by remember { mutableStateOf<OverlayType?>(null) }

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
            // --- ANA İÇERİK ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // 1. HEADER (Profil, Başlık, Skorlar, Ayarlar)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Skor Geçmişi Butonu
                    IconButton(
                        onClick = { activeOverlay = OverlayType.HISTORY },
                        modifier = Modifier.background(Color.White.copy(0.05f), CircleShape)
                    ) {
                        Icon(Icons.Default.EmojiEvents, "Skorlar", tint = Color(0xFFFFD54F))
                    }

                    // Ayarlar Butonu
                    IconButton(
                        onClick = { activeOverlay = OverlayType.SETTINGS },
                        modifier = Modifier.background(Color.White.copy(0.05f), CircleShape)
                    ) {
                        Icon(Icons.Default.Settings, "Ayarlar", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 2. LOGO / BAŞLIK
                Text(
                    "MATRIX\nPUZZLE",
                    style = MaterialTheme.typography.displayMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Thin,
                    letterSpacing = 6.sp
                )

                // Günün Sözü (Extra Touch)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "\"Matematik zihnin müziğidir.\"",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )

                Spacer(modifier = Modifier.height(48.dp))

                // 3. OYUN AYARLARI (Zorluk & Boyut)
                Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    // Zorluk
                    GlassBox(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            LabelText("ZORLUK SEVİYESİ")
                            Spacer(modifier = Modifier.height(12.dp))
                            AnimatedSegmentedControl(
                                items = Difficulty.values().toList(),
                                selectedItem = selectedDiff,
                                onItemSelected = { selectedDiff = it },
                                modifier = Modifier.fillMaxWidth(),
                                itemLabel = { it.label.split(" ")[0].uppercase() }
                            )
                        }
                    }

                    // Boyut
                    GlassBox(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            LabelText("MATRİS BOYUTU")
                            Spacer(modifier = Modifier.height(12.dp))
                            AnimatedSegmentedControl(
                                items = GridSize.values().toList(),
                                selectedItem = selectedSize,
                                onItemSelected = { selectedSize = it },
                                modifier = Modifier.fillMaxWidth(),
                                itemLabel = { it.label }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // 4. BAŞLAT BUTONU (Pulse Efekti verilebilir)
                Button(
                    onClick = { navigator.push(GameScreen(selectedDiff, selectedSize)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .shadow(16.dp, RoundedCornerShape(32.dp), spotColor = Color.White),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(32.dp)
                ) {
                    Text(
                        "OYUNU BAŞLAT",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        letterSpacing = 2.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // --- OVERLAY PANELS (History & Settings) ---
            // Arka planı karartma
            AnimatedVisibility(
                visible = activeOverlay != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(0.8f))
                        .clickable { activeOverlay = null } // Dışarı tıklayınca kapat
                )
            }

            // Panel İçeriği
            AnimatedVisibility(
                visible = activeOverlay != null,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                val overlay = activeOverlay ?: return@AnimatedVisibility

                GlassBox(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .fillMaxHeight(0.7f) // Ekranın %70'ini kapla
                        .padding(16.dp),
                    cornerRadius = 24.dp
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        // Panel Başlığı ve Kapat Butonu
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (overlay == OverlayType.HISTORY) "SKOR GEÇMİŞİ" else "AYARLAR",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                letterSpacing = 1.sp
                            )
                            IconButton(onClick = { activeOverlay = null }) {
                                Icon(Icons.Default.Close, null, tint = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = Color.White.copy(0.1f))
                        Spacer(modifier = Modifier.height(16.dp))

                        // İçerik
                        when (overlay) {
                            OverlayType.HISTORY -> HistoryList(viewModel)
                            OverlayType.SETTINGS -> SettingsList(viewModel)
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun LabelText(text: String) {
        Text(
            text = text,
            color = Color.Gray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }

    enum class OverlayType { HISTORY, SETTINGS }
}

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

// --- SETTINGS LIST COMPONENT ---
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

        Spacer(modifier = Modifier.height(24.dp))

        // Ekstra Bilgi
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF38BDF8).copy(0.1f), RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFF38BDF8).copy(0.3f), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text(
                "Equatix v1.0\nDeveloped by Vahit Keskin",
                color = Color(0xFF38BDF8),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SettingItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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