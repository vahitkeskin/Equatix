package com.vahitkeskin.equatix.ui.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.equatix.ui.common.GlassBox
import com.vahitkeskin.equatix.ui.home.HomeScreen
import com.vahitkeskin.equatix.ui.home.HomeViewModel
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Composable
fun HomeOverlayPanel(
    overlayType: HomeScreen.OverlayType?,
    onDismiss: () -> Unit,
    viewModel: HomeViewModel,
    isDark: Boolean,
    colors: EquatixDesignSystem.ThemeColors
) {
    val overlayDimColor = if (isDark) Color.Black.copy(0.85f) else Color.Black.copy(0.4f)
    val strings by viewModel.strings.collectAsState()

    // 1. Kapanış animasyonu için son görünen tipi hafızada tutuyoruz.
    // Çünkü 'overlayType' null olduğunda (kapanırken) yönü hatırlamamız lazım.
    var lastActiveOverlay by remember { mutableStateOf(overlayType) }

    if (overlayType != null) {
        lastActiveOverlay = overlayType
    }

    // 2. Yön Belirleme Mantığı
    val isSettings = lastActiveOverlay == HomeScreen.OverlayType.SETTINGS

    // Settings ise Sağdan (+it), History ise Soldan (-it)
    val enterTransition = if (isSettings) {
        slideInHorizontally(initialOffsetX = { it }) + fadeIn()
    } else {
        slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
    }

    val exitTransition = if (isSettings) {
        slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
    } else {
        slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
    }

    // 3. Kapsayıcı Box
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        // Katman 1: Karartma Arka Planı
        AnimatedVisibility(
            visible = overlayType != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.matchParentSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(overlayDimColor)
                    .clickable(enabled = false) {}
                    .clickable { onDismiss() }
            )
        }

        // Katman 2: Panel İçeriği (Yönlü Animasyon Burada)
        AnimatedVisibility(
            visible = overlayType != null,
            enter = enterTransition,
            exit = exitTransition,
            modifier = Modifier.align(Alignment.Center)
        ) {
            val overlay = overlayType ?: return@AnimatedVisibility

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
                    // --- HEADER ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val title = if (overlay == HomeScreen.OverlayType.HISTORY)
                            strings.scoresTitle
                        else
                            strings.settingsTitle

                        Text(
                            text = title,
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.Black,
                                color = colors.textPrimary,
                                fontSize = 28.sp
                            )
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = strings.close,
                                tint = colors.textSecondary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    HorizontalDivider(
                        color = colors.divider,
                        modifier = Modifier.padding(vertical = 20.dp)
                    )

                    // --- İÇERİK ---
                    when (overlay) {
                        HomeScreen.OverlayType.HISTORY -> HistoryList(
                            viewModel,
                            colors,
                            isDark,
                            strings
                        )

                        HomeScreen.OverlayType.SETTINGS -> SettingsList(
                            viewModel,
                            isDark,
                            colors
                        )
                    }
                }
            }
        }
    }
}