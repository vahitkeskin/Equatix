package com.vahitkeskin.equatix.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.vahitkeskin.equatix.domain.model.CellData
import com.vahitkeskin.equatix.ui.game.GameViewModel
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem

@Composable
fun GridCell(
    data: CellData,
    viewModel: GameViewModel,
    size: Dp,
    fontSize: TextUnit,
    colors: EquatixDesignSystem.ThemeColors // <--- TEMA
) {
    val isSelected = viewModel.selectedCellIndex == data.id

    // RENK MANTIĞI:
    // 1. Kilitli: Kart rengi (Dark: Şeffaf Siyah, Light: Beyaz)
    // 2. Seçili: Accent Rengi (Saydam)
    // 3. Normal: Grid çizgisi rengi (Saydam)
    val targetBgColor = when {
        data.isLocked -> colors.cardBackground
        isSelected -> colors.accent.copy(alpha = 0.2f)
        else -> colors.gridLines.copy(alpha = 0.1f)
    }

    val backgroundColor by animateColorAsState(targetValue = targetBgColor, animationSpec = tween(200))

    val borderColor = if (isSelected) colors.accent else colors.gridLines.copy(alpha = 0.3f)

    // TEXT RENGİ:
    // 1. Kilitli: Ana Metin Rengi (Dark: Beyaz, Light: Siyah)
    // 2. Sistem Açtı (İpucu): Gold rengi
    // 3. Kullanıcı Girdi: Accent rengi
    val textColor = when {
        data.isRevealedBySystem -> colors.gold
        data.isLocked -> colors.textPrimary
        else -> colors.accent
    }

    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(enabled = !data.isLocked && !viewModel.isSolved) {
                viewModel.onCellSelected(data.id)
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (data.isHidden) data.userInput else data.correctValue.toString(),
            fontSize = fontSize,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}