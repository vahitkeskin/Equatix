package com.vahitkeskin.equatix.ui.home.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.equatix.domain.model.AppThemeConfig
import com.vahitkeskin.equatix.ui.common.AnimatedSegmentedControl
import com.vahitkeskin.equatix.ui.common.GlassBox
import com.vahitkeskin.equatix.ui.home.HomeScreen
import com.vahitkeskin.equatix.ui.home.HomeViewModel
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem

@Composable
fun HomeOverlayPanel(
    overlayType: HomeScreen.OverlayType?,
    onDismiss: () -> Unit,
    viewModel: HomeViewModel,
    isDark: Boolean,
    colors: EquatixDesignSystem.ThemeColors
) {
    // 1. Karartma Katmanı (Dimmer)
    // Dark modda arkası neredeyse tamamen kararsın ki öndeki cam efekti parlasın
    val overlayDimColor = if (isDark) Color.Black.copy(0.85f) else Color.Black.copy(0.4f)

    AnimatedVisibility(
        visible = overlayType != null,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(overlayDimColor)
                .clickable(enabled = false) {} // Tıklamayı yut
                .clickable { onDismiss() } // Boşluğa tıklayınca kapat
        )
    }

    AnimatedVisibility(
        visible = overlayType != null,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut(),
    ) {
        val overlay = overlayType ?: return@AnimatedVisibility

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            // 2. Ana Kart (Glass veya Solid)
            // GlassBox dış çerçeveyi ve bluru sağlar.
            GlassBox(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight()
                    .padding(16.dp)
                    // Gölge: Light modda yumuşak gölge, Dark modda glow yok (cam efekti yeterli)
                    .shadow(
                        elevation = if (isDark) 0.dp else 16.dp,
                        shape = RoundedCornerShape(24.dp)
                    ),
                cornerRadius = 24.dp
            ) {
                // 3. İçerik Konteynerı
                // İşte "Beyaz Kutu" sorununu çözen yer burası.
                // GlassBox'ın içine kendi background rengimizi (Yarı saydam siyah veya Solid beyaz) basıyoruz.
                Column(
                    modifier = Modifier
                        .background(colors.cardBackground) // <-- KRİTİK NOKTA
                        .padding(24.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (overlay == HomeScreen.OverlayType.HISTORY) "SKORLAR" else "AYARLAR",
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.Black,
                                color = colors.textPrimary,
                                fontSize = 28.sp // Büyük Başlık
                            )
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = colors.textSecondary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    Divider(
                        color = colors.divider,
                        modifier = Modifier.padding(vertical = 20.dp)
                    )

                    // İçerik
                    when (overlay) {
                        HomeScreen.OverlayType.HISTORY -> HistoryList(viewModel, colors, isDark)
                        HomeScreen.OverlayType.SETTINGS -> SettingsList(viewModel, isDark, colors)
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryList(viewModel: HomeViewModel, colors: EquatixDesignSystem.ThemeColors, isDark: Boolean) {
    val scores by viewModel.scores.collectAsState()

    if (scores.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Henüz oyun oynanmadı.",
                color = colors.textSecondary,
                fontSize = 18.sp
            )
        }
    } else {
        // Listeyi sınırlı bir yükseklikte tutabiliriz veya ekrana sığdırabiliriz
        LazyColumn(
            modifier = Modifier.heightIn(max = 400.dp), // Çok uzamasın
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            items(scores) { score ->
                // Satır Arka Planı
                // Dark modda çok hafif beyazlık (%5), Light modda hafif gri
                val itemBg = if (isDark) Color.White.copy(0.05f) else Color(0xFFF8FAFC)
                val borderColor = if (isDark) Color.Transparent else Color(0xFFE2E8F0)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(itemBg, RoundedCornerShape(16.dp))
                        .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Sol Taraf (Tarih ve Zorluk)
                    Column {
                        Text(
                            text = score.date,
                            color = colors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${score.difficulty.label} • ${score.gridSize.label}",
                            color = score.difficulty.color, // Enum içinden gelen renk
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Sağ Taraf (Puan ve Süre)
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${score.score} P",
                            color = colors.success,
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = colors.textSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = score.time,
                                color = colors.textSecondary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsList(viewModel: HomeViewModel, isDark: Boolean, colors: EquatixDesignSystem.ThemeColors) {
    val isSoundOn by viewModel.isSoundOn.collectAsState()
    val isVibrationOn by viewModel.isVibrationOn.collectAsState()
    val themeConfig by viewModel.themeConfig.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) { // Elemanlar arası boşluk

        // --- GÖRÜNÜM BÖLÜMÜ ---
        SectionTitle("GÖRÜNÜM", colors)

        // Tema Seçici
        AnimatedSegmentedControl(
            items = AppThemeConfig.values().toList(),
            selectedItem = themeConfig,
            onItemSelected = { viewModel.setTheme(it) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                // Segmented control arkası şeffaf olsun ki kart rengi görünsün
                .background(Color.Transparent),
            itemLabel = { config ->
                when(config) {
                    AppThemeConfig.FOLLOW_SYSTEM -> "Sistem"
                    AppThemeConfig.LIGHT -> "Açık"
                    AppThemeConfig.DARK -> "Koyu"
                }
            }
        )

        Divider(color = colors.divider)

        // --- TERCİHLER BÖLÜMÜ ---
        SectionTitle("TERCİHLER", colors)

        SettingItem(
            title = "Oyun Sesleri",
            icon = if (isSoundOn) Icons.Rounded.VolumeUp else Icons.Rounded.VolumeOff,
            isOn = isSoundOn,
            isDark = isDark,
            colors = colors,
            onToggle = { viewModel.toggleSound() }
        )

        SettingItem(
            title = "Titreşim",
            icon = if (isVibrationOn) Icons.Rounded.Vibration else Icons.Rounded.Smartphone,
            isOn = isVibrationOn,
            isDark = isDark,
            colors = colors,
            onToggle = { viewModel.toggleVibration() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Versiyon Bilgisi
        Text(
            text = "EQUATIX v1.0",
            color = colors.textSecondary.copy(0.5f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SectionTitle(text: String, colors: EquatixDesignSystem.ThemeColors) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = colors.accent,
        letterSpacing = 1.5.sp
    )
}

@Composable
private fun SettingItem(
    title: String,
    icon: ImageVector,
    isOn: Boolean,
    isDark: Boolean,
    colors: EquatixDesignSystem.ThemeColors,
    onToggle: () -> Unit
) {
    val activeColor = colors.success
    val inactiveColor = colors.textPrimary

    // Butonun Arka Planı
    // Aktif değilse ŞEFFAF olsun (Burası önemli, beyaz kalmasın)
    val bgColor = if (isOn) activeColor.copy(0.1f) else Color.Transparent

    // Kenarlık
    val borderColor = if (isOn) activeColor.copy(0.5f) else colors.textPrimary.copy(0.15f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onToggle() }
            .background(bgColor) // Arka plan burada uygulanıyor
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(20.dp), // İri butonlar
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isOn) activeColor else inactiveColor,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = title,
                color = colors.textPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Switch(
            checked = isOn,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = activeColor,
                uncheckedThumbColor = if(isDark) Color.Gray else Color.White,
                uncheckedTrackColor = if(isDark) Color.Black else Color.Gray,
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}