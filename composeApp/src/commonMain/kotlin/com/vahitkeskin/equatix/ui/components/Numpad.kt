package com.vahitkeskin.equatix.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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

@Composable
fun TransparentNumpad(onInput: (String) -> Unit) {
    val rows = listOf(listOf("7", "8", "9"), listOf("4", "5", "6"), listOf("1", "2", "3"), listOf("", "0", "DEL"))
    Box(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            rows.forEach { row ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    row.forEach { key ->
                        if (key.isEmpty()) Spacer(modifier = Modifier.size(70.dp)) else GlassKeyButton(key, onInput)
                    }
                }
            }
        }
    }
}

@Composable
fun GlassKeyButton(key: String, onClick: (String) -> Unit) {
    val isDel = key == "DEL"
    val bgColor = if (isDel) Color(0xFFFF453A).copy(alpha = 0.1f) else Color.White.copy(alpha = 0.08f)
    val contentColor = if (isDel) Color(0xFFFF453A) else Color.White
    Box(modifier = Modifier.size(75.dp).clip(CircleShape).background(bgColor).clickable { onClick(key) }, contentAlignment = Alignment.Center) {
        Text(text = if (isDel) "⌫" else key, fontSize = 32.sp, color = contentColor, fontWeight = FontWeight.Thin)
    }
}