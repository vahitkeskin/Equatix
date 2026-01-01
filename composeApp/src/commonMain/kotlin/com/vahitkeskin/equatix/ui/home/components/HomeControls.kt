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
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem

@Composable
fun HomeSelectionPanel(
    selectedDiff: Difficulty,
    onDiffSelect: (Difficulty) -> Unit,
    selectedSize: GridSize,
    onSizeSelect: (GridSize) -> Unit,
    isDark: Boolean,
    colors: EquatixDesignSystem.ThemeColors
) {
    GlassBox(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(if(isDark) Color.White.copy(0.03f) else Color.White.copy(0.6f))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                // Zorluk Seçimi
                SelectionRow(
                    label = "ZORLUK SEVİYESİ",
                    labelColor = colors.accent,
                    content = {
                        AnimatedSegmentedControl(
                            items = Difficulty.values().toList(),
                            selectedItem = selectedDiff,
                            onItemSelected = onDiffSelect,
                            modifier = Modifier.fillMaxWidth(),
                            itemLabel = { it.label.split(" ")[0].uppercase() }
                        )
                    }
                )

                Divider(color = colors.divider)

                // Boyut Seçimi
                SelectionRow(
                    label = "IZGARA BOYUTU",
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
fun CyberStartButton(isDark: Boolean, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "ButtonPulse")
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(1500, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val bgGradient = if (isDark) {
        Brush.horizontalGradient(listOf(Color(0xFF34C759).copy(0.15f), Color(0xFF32ADE6).copy(0.15f)))
    } else {
        Brush.horizontalGradient(listOf(Color(0xFF0F172A), Color(0xFF334155)))
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
                text = "OYUNU BAŞLAT",
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