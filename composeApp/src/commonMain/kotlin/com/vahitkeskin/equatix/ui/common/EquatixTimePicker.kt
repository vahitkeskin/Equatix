package com.vahitkeskin.equatix.ui.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem

@Composable
fun EquatixTimePicker(
    initialHour: Int,
    initialMinute: Int,
    colors: EquatixDesignSystem.ThemeColors,
    onTimeChanged: (Int, Int) -> Unit
) {
    var currentHour by remember { mutableStateOf(initialHour) }
    var currentMinute by remember { mutableStateOf(initialMinute) }

    // Görseldeki gibi geniş ve ferah bir alan
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp), // Yüksekliği artırdık
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- SAAT ---
            WheelPicker(
                count = 24,
                initialItem = initialHour,
                visibleItemCount = 3,
                itemHeight = 50.dp, // Satır yüksekliği arttı
                onItemSelected = {
                    currentHour = it
                    onTimeChanged(currentHour, currentMinute)
                }
            )

            // --- İKİ NOKTA (:) ---
            Text(
                text = ":",
                fontSize = 40.sp, // Görseldeki gibi büyük
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2), // Mavi renk (Görseldeki ton)
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .offset(y = (-4).dp)
            )

            // --- DAKİKA ---
            WheelPicker(
                count = 60,
                initialItem = initialMinute,
                visibleItemCount = 3,
                itemHeight = 50.dp,
                format = { it.toString().padStart(2, '0') },
                onItemSelected = {
                    currentMinute = it
                    onTimeChanged(currentHour, currentMinute)
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WheelPicker(
    count: Int,
    initialItem: Int,
    visibleItemCount: Int,
    itemHeight: Dp,
    format: (Int) -> String = { it.toString() },
    onItemSelected: (Int) -> Unit
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialItem)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val centerIndex = listState.firstVisibleItemIndex
            onItemSelected(centerIndex % count)
        }
    }

    val contentPadding = itemHeight * (visibleItemCount / 2)

    LazyColumn(
        state = listState,
        flingBehavior = flingBehavior,
        contentPadding = PaddingValues(vertical = contentPadding),
        modifier = Modifier
            .width(80.dp) // Genişlik artırıldı
            .height(itemHeight * visibleItemCount),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(count) { index ->
            // Seçili olma durumu
            val isSelected by remember {
                derivedStateOf { listState.firstVisibleItemIndex == index }
            }

            Box(
                modifier = Modifier
                    .height(itemHeight)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = format(index),
                    // Görseldeki gibi: Seçiliyse ÇOK BÜYÜK (40sp), değilse KÜÇÜK (24sp)
                    fontSize = if (isSelected) 40.sp else 24.sp,

                    // Seçiliyse Kalın, değilse Normal
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,

                    // Görseldeki Mavi Ton (0xFF1976D2) ve Gri Ton
                    color = if (isSelected) Color(0xFF1976D2) else Color.LightGray,

                    // Seçili değilse daha silik
                    modifier = Modifier.alpha(if (isSelected) 1f else 0.4f),

                    textAlign = TextAlign.Center
                )
            }
        }
    }
}