package com.vahitkeskin.equatix.ui.home.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.equatix.domain.model.Difficulty
import com.vahitkeskin.equatix.domain.model.GridSize
import com.vahitkeskin.equatix.ui.common.AnimatedSegmentedControl
import com.vahitkeskin.equatix.ui.common.GlassBox
import com.vahitkeskin.equatix.ui.home.HomeViewModel
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem

@Composable
fun HomeSelectionPanel(
    viewModel: HomeViewModel, // YENİ: Stringlere erişim için eklendi
    selectedDiff: Difficulty,
    onDiffSelect: (Difficulty) -> Unit,
    selectedSize: GridSize,
    onSizeSelect: (GridSize) -> Unit,
    isDark: Boolean,
    colors: EquatixDesignSystem.ThemeColors
) {
    // ViewModel'den güncel dil paketini çekiyoruz
    val strings by viewModel.strings.collectAsState()

    val borderColor = if (isDark) Color.Transparent else colors.gridLines.copy(alpha = 0.5f)
    val containerBg = if (isDark) Color.White.copy(0.03f) else colors.cardBackground

    GlassBox(
        modifier = Modifier
            .fillMaxWidth()
            // 1. GÖLGE (Light Mod için derinlik)
            .shadow(
                elevation = 0.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color.Black.copy(alpha = 0.1f),
                ambientColor = Color.Black.copy(alpha = 0.05f)
            )
            // 2. KENARLIK (Light Mod için netlik)
            .border(
                width = if (isDark) 0.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(24.dp)
            ),
        cornerRadius = 24.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(containerBg)
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                // Zorluk Seçimi
                SelectionRow(
                    label = strings.difficultyLevel, // "ZORLUK SEVİYESİ" -> Dinamik
                    labelColor = colors.accent,
                    content = {
                        AnimatedSegmentedControl(
                            items = Difficulty.values().toList(),
                            selectedItem = selectedDiff,
                            onItemSelected = onDiffSelect,
                            modifier = Modifier.fillMaxWidth(),
                            itemLabel = { strings.getDifficultyLabel(it).uppercase() }
                        )
                    }
                )

                // Divider -> HorizontalDivider Güncellemesi
                HorizontalDivider(color = colors.divider)

                // Boyut Seçimi
                SelectionRow(
                    label = strings.gridSize, // "IZGARA BOYUTU" -> Dinamik
                    labelColor = colors.accent,
                    content = {
                        AnimatedSegmentedControl(
                            items = GridSize.values().toList(),
                            selectedItem = selectedSize,
                            onItemSelected = onSizeSelect,
                            modifier = Modifier.fillMaxWidth(),
                            itemLabel = { it.label }
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun CyberStartButton(
    homeViewModel: HomeViewModel,
    isDark: Boolean,
    onClick: () -> Unit
) {
    // ViewModel'den güncel dil paketini çekiyoruz
    val strings by homeViewModel.strings.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "ButtonPulse")
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(1500, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "alpha"
    )

    // Light Mod için butona da hafif bir gölge ekleyelim
    val buttonShadow = if (isDark) 0.dp else 8.dp

    val bgGradient = if (isDark) {
        Brush.horizontalGradient(listOf(Color(0xFF34C759).copy(0.15f), Color(0xFF32ADE6).copy(0.15f)))
    } else {
        Brush.horizontalGradient(listOf(Color(0xFF0F172A), Color(0xFF1E293B)))
    }

    val borderBrush = if (isDark) {
        Brush.linearGradient(listOf(Color(0xFF34C759), Color(0xFF32ADE6)))
    } else {
        Brush.linearGradient(listOf(Color(0xFF0F172A), Color(0xFF0F172A)))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .shadow(
                elevation = buttonShadow,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color(0xFF0F172A).copy(0.25f)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(bgGradient)
            .clickable { onClick() }
            .border(
                width = if(isDark) 2.dp else 0.dp,
                brush = borderBrush,
                shape = RoundedCornerShape(24.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isDark) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRoundRect(
                    brush = borderBrush,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(24.dp.toPx()),
                    style = Stroke(width = 2.dp.toPx()),
                    alpha = borderAlpha
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = strings.startGame, // "OYUNU BAŞLAT" -> Dinamik
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = Icons.Rounded.PlayArrow,
                contentDescription = null,
                tint = if(isDark) Color(0xFF34C759) else Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun SelectionRow(label: String, labelColor: Color, content: @Composable () -> Unit) {
    Column {
        Text(
            text = label,
            color = labelColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}