package com.vahitkeskin.equatix.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem

@Composable
fun TransparentNumpad(
    colors: EquatixDesignSystem.ThemeColors, // <--- TEMA EKLENDİ
    onInput: (String) -> Unit
) {
    val rows = listOf(
        listOf("7", "8", "9"),
        listOf("4", "5", "6"),
        listOf("1", "2", "3"),
        listOf("", "0", "DEL")
    )
    Box(modifier = Modifier.fillMaxWidth().padding(bottom = 0.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            rows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    row.forEach { key ->
                        if (key.isEmpty()) {
                            Spacer(modifier = Modifier.size(50.dp))
                        } else {
                            GlassKeyButton(
                                key = key,
                                colors = colors, // Rengi iletiyoruz
                                onClick = onInput
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GlassKeyButton(
    key: String,
    colors: EquatixDesignSystem.ThemeColors,
    onClick: (String) -> Unit
) {
    val isDel = key == "DEL"

    // DEL tuşu için 'error' rengi, Rakamlar için 'numpadText' rengi (Dark:Beyaz, Light:Lacivert)
    val baseColor = if (isDel) colors.error else colors.numpadText

    // Arka plan rengi baseColor'un çok saydam hali (%8)
    // Dark modda: White %8 -> Buzlu cam
    // Light modda: Lacivert %8 -> Hafif gri/mavi cam (Görünür)
    val bgColor = baseColor.copy(alpha = if(isDel) 0.1f else 0.08f)

    Box(
        modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(bgColor)
            .clickable { onClick(key) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isDel) "⌫" else key,
            fontSize = 22.sp,
            color = baseColor, // İkon/Yazı rengi
            fontWeight = FontWeight.Thin
        )
    }
}