package com.vahitkeskin.equatix.ui.game.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.equatix.ui.common.GlassBox

@Composable
fun HintDialog(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.7f))
            .padding(32.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        GlassBox(modifier = Modifier.wrapContentSize()) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Outlined.Lightbulb,
                    null,
                    tint = Color(0xFFFFD54F),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text("HINT", color = Color.White, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Try solving rows containing 'x' and '÷' first. These cells usually have fewer possibilities.",
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("CONTINUE", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}