package com.vahitkeskin.equatix.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Composable
fun ResultCell(value: Int, size: Dp, fontSize: TextUnit) {
    val textStr = value.toString()
    val dynamicFontSize = if (textStr.length > 2) fontSize * 0.7f else fontSize
    Box(modifier = Modifier.size(size).aspectRatio(1f).padding(2.dp), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.fillMaxSize().border(1.dp, Color(0xFF34C759).copy(alpha = 0.5f), CircleShape), contentAlignment = Alignment.Center) {
            Text(text = textStr, color = Color(0xFF34C759), fontWeight = FontWeight.SemiBold, fontSize = dynamicFontSize, textAlign = TextAlign.Center, maxLines = 1)
        }
    }
}