package com.vahitkeskin.equatix.ui.game.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.equatix.ui.common.GlassBox
import com.vahitkeskin.equatix.ui.game.utils.formatTime

@Composable
fun ResultPanel(
    isSurrendered: Boolean,
    elapsedTime: Long,
    onRestart: () -> Unit,
    onGiveUp: () -> Unit
) {
    GlassBox(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 30.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (isSurrendered) Icons.Default.Info else Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = if (isSurrendered) Color(0xFFFF9F0A) else Color(0xFF34C759),
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isSurrendered) "SOLUTION SHOWN" else "PERFECT!",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                letterSpacing = 2.sp
            )

            if (!isSurrendered) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Completion Time: ${formatTime(elapsedTime)}",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onRestart,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    "NEW GAME",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}