package com.vahitkeskin.equatix.ui.common

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * GIF'teki gibi kayan animasyonlu seçim bileşeni.
 */
@Composable
fun <T> AnimatedSegmentedControl(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 12.dp,
    itemLabel: (T) -> String
) {
    val selectedIndex = items.indexOf(selectedItem)

    // Dış Kapsayıcı (Koyu Arka Plan)
    BoxWithConstraints(
        modifier = modifier
            .height(50.dp)
            .clip(RoundedCornerShape(cornerRadius))
            .background(Color.Black.copy(alpha = 0.3f)) // Hafif transparan siyah zemin
            .padding(4.dp) // Gösterge ile dış kenar arasındaki boşluk (Padding)
    ) {
        val maxWidth = this.maxWidth
        val itemWidth = maxWidth / items.size

        // 1. Hareket Eden Gösterge (Beyaz Pill)
        // Seçili index'e göre X ekseninde kayma hesaplanır
        val indicatorOffset by animateDpAsState(
            targetValue = itemWidth * selectedIndex,
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
            label = "indicator"
        )

        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(itemWidth)
                .fillMaxHeight()
                .shadow(4.dp, RoundedCornerShape(cornerRadius)) // Hafif gölge
                .background(Color.White, RoundedCornerShape(cornerRadius))
        )

        // 2. Metin Katmanı (Üstte durur ve tıklanabilir)
        Row(modifier = Modifier.fillMaxSize()) {
            items.forEach { item ->
                val isSelected = item == selectedItem
                Box(
                    modifier = Modifier
                        .width(itemWidth)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null // Tıklama efektini (ripple) kapatıyoruz, temiz dursun
                        ) { onItemSelected(item) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = itemLabel(item),
                        color = if (isSelected) Color.Black else Color.Gray, // Seçiliyse Siyah, değilse Gri
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}