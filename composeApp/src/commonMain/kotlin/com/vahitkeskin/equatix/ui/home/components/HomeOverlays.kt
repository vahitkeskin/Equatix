package com.vahitkeskin.equatix.ui.home.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.MusicOff
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.vahitkeskin.equatix.domain.model.AppThemeConfig
import com.vahitkeskin.equatix.platform.getAppVersion
import com.vahitkeskin.equatix.ui.common.AnimatedSegmentedControl
import com.vahitkeskin.equatix.ui.common.GlassBox
import com.vahitkeskin.equatix.ui.home.HomeScreen
import com.vahitkeskin.equatix.ui.home.HomeViewModel
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem
import com.vahitkeskin.equatix.ui.utils.rememberNotificationPermissionControl

@Composable
fun HomeOverlayPanel(
    overlayType: HomeScreen.OverlayType?,
    onDismiss: () -> Unit,
    viewModel: HomeViewModel,
    isDark: Boolean,
    colors: EquatixDesignSystem.ThemeColors
) {
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
                .clickable(enabled = false) {}
                .clickable { onDismiss() }
        )
    }

    AnimatedVisibility(
        visible = overlayType != null,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut(),
    ) {
        val overlay = overlayType ?: return@AnimatedVisibility

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            GlassBox(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight()
                    .padding(16.dp)
                    .shadow(
                        elevation = if (isDark) 0.dp else 16.dp,
                        shape = RoundedCornerShape(24.dp)
                    ),
                cornerRadius = 24.dp
            ) {
                Column(
                    modifier = Modifier
                        .background(colors.cardBackground)
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
                                fontSize = 28.sp
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
private fun HistoryList(
    viewModel: HomeViewModel,
    colors: EquatixDesignSystem.ThemeColors,
    isDark: Boolean
) {
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
        LazyColumn(
            modifier = Modifier.heightIn(max = 400.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            items(items = scores, key = { it.id }) { score ->

                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = {
                        if (it == SwipeToDismissBoxValue.EndToStart) {
                            viewModel.deleteScore(score.id)
                            true
                        } else {
                            false
                        }
                    }
                )
                val itemBg = if (isDark) Color.White.copy(0.05f) else Color(0xFFF8FAFC)

                SwipeToDismissBox(
                    state = dismissState,
                    enableDismissFromStartToEnd = false,
                    backgroundContent = {
                        val alignment = Alignment.CenterEnd
                        val icon = Icons.Default.Delete

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(colors.error, RoundedCornerShape(16.dp))
                                .padding(horizontal = 20.dp),
                            contentAlignment = alignment
                        ) {
                            if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = "Delete",
                                    tint = Color.White
                                )
                            }
                        }
                    },
                    content = {
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
                                    color = score.difficulty.color,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
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
                )
            }
        }
    }
}

@Composable
private fun SettingsList(
    viewModel: HomeViewModel,
    isDark: Boolean,
    colors: EquatixDesignSystem.ThemeColors
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    // State Takipleri
    val isSoundOn by viewModel.isSoundOn.collectAsState()
    val isVibrationOn by viewModel.isVibrationOn.collectAsState()
    val themeConfig by viewModel.themeConfig.collectAsState()
    val isNotificationEnabled by viewModel.isNotificationEnabled.collectAsState()
    val notificationTime by viewModel.notificationTime.collectAsState()
    val isMusicOn by viewModel.isMusicOn.collectAsState()

    // 1. LIFECYCLE TAKİBİ (İzin durumu için)
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.refreshPermissionStatus()
        }
    }

    // 2. İZİN KONTROLCÜSÜ
    val permissionControl = rememberNotificationPermissionControl(
        onPermissionResult = { isGranted ->
            if (isGranted) {
                // İzin Verildi -> Planla
                viewModel.setNotificationSchedule(true)
            } else {
                // Reddedildi -> Ayarlara Gönder
                viewModel.openAppSettings()
            }
        }
    )

    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {

        // --- GÖRÜNÜM ---
        SectionTitle("GÖRÜNÜM", colors)

        AnimatedSegmentedControl(
            items = AppThemeConfig.values().toList(),
            selectedItem = themeConfig,
            onItemSelected = { viewModel.setTheme(it) },
            modifier = Modifier.fillMaxWidth().height(48.dp).background(Color.Transparent),
            itemLabel = { if (it == AppThemeConfig.FOLLOW_SYSTEM) "Sistem" else if (it == AppThemeConfig.LIGHT) "Açık" else "Koyu" }
        )

        Divider(color = colors.divider)

        // --- TERCİHLER ---
        SectionTitle("TERCİHLER", colors)

        // 1. YENİ: Arka Plan Müziği
        SettingItem(
            title = "Arka Plan Müziği",
            subtitle = "Rahatlatıcı Piyano", // Kullanıcı ne çalacağını bilsin
            icon = if (isMusicOn) Icons.Rounded.MusicNote else Icons.Rounded.MusicOff,
            isOn = isMusicOn,
            isDark = isDark,
            colors = colors,
            onToggle = { viewModel.toggleMusic(it) }
        )

        SettingItem(
            title = "Titreşim",
            icon = if (isVibrationOn) Icons.Rounded.Vibration else Icons.Rounded.Smartphone,
            isOn = isVibrationOn,
            isDark = isDark,
            colors = colors,
            onToggle = { viewModel.toggleVibration() }
        )

        // Dinamik Zaman Metni (Örn: "Her gece 22:00'de")
        val timeString = "${notificationTime.first.toString().padStart(2, '0')}:${notificationTime.second.toString().padStart(2, '0')}"
        val subtitleText = if (isNotificationEnabled) "Her gün $timeString'da" else "Kapalı"

        // 3. GÜNLÜK HATIRLATICI
        SettingItem(
            title = "Günlük Hatırlatıcı",
            subtitle = subtitleText, // Dinamik metin
            icon = if (isNotificationEnabled) Icons.Rounded.Notifications else Icons.Rounded.NotificationsOff,
            isOn = isNotificationEnabled,
            isDark = isDark,
            colors = colors,
            onToggle = { shouldEnable ->
                if (shouldEnable) {
                    if (permissionControl.hasPermission()) {
                        viewModel.setNotificationSchedule(true)
                    } else {
                        permissionControl.launchPermissionRequest()
                    }
                } else {
                    viewModel.setNotificationSchedule(false)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "EQUATIX v${getAppVersion()}", // Dinamik Sürüm
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
fun SettingItem(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    isOn: Boolean,
    isDark: Boolean,
    colors: EquatixDesignSystem.ThemeColors,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(!isOn) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(colors.textSecondary.copy(0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = colors.textPrimary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Switch(
            checked = isOn,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = colors.accent,
                uncheckedThumbColor = colors.textSecondary,
                uncheckedTrackColor = colors.cardBackground
            )
        )
    }
}