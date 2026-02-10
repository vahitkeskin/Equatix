package com.vahitkeskin.equatix.ui.game.dialogs

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vahitkeskin.equatix.domain.model.AppStrings
import com.vahitkeskin.equatix.ui.common.GlassBox
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem

@Composable
fun HintDialog(
    appStrings: AppStrings,
    colors: EquatixDesignSystem.ThemeColors,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.75f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        GlassBox(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .wrapContentSize(),
            cornerRadius = 24.dp,
            backgroundColor = colors.cardBackground
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Outlined.Lightbulb,
                    null,
                    tint = Color(0xFFFFD54F),
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = appStrings.hintDialogTitle,
                    color = colors.textPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // --- ANIMATED VISUALIZATION ---
                HintAnimation(colors)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = appStrings.hintDialogDescription,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.textPrimary,
                        contentColor = colors.cardBackground
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Text(
                        appStrings.hintDialogButton,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun HintAnimation(colors: EquatixDesignSystem.ThemeColors) {
    // Popup her açıldığında farklı işlemler ve sayılar üretilmesi için remember kullanıyoruz
    val rowData = remember {
        val ops = listOf("+", "-", "x")
        val op = ops.random()
        when (op) {
            "+" -> {
                val n1 = (1..9).random()
                val n2 = (1..9).random()
                Triple(n1.toString(), "+", (n1 + n2).toString()) to n2.toString()
            }
            "-" -> {
                val n1 = (5..15).random()
                val n2 = (1 until n1).random()
                Triple(n1.toString(), "-", (n1 - n2).toString()) to n2.toString()
            }
            else -> { // "x"
                val n1 = (2..5).random()
                val n2 = (2..5).random()
                Triple(n1.toString(), "x", (n1 * n2).toString()) to n2.toString()
            }
        }
    }
    
    val colData = remember {
        val ops = listOf("x", "÷", "+")
        val op = ops.random()
        when (op) {
            "x" -> {
                val n1 = (2..4).random()
                val n2 = (2..5).random()
                Triple(n1.toString(), "x", (n1 * n2).toString()) to n2.toString()
            }
            "÷" -> {
                val n2 = (2..4).random()
                val res = (2..5).random()
                val n1 = n2 * res
                Triple(n1.toString(), "÷", res.toString()) to n2.toString()
            }
            else -> { // "+"
                val n1 = (1..9).random()
                val n2 = (1..9).random()
                Triple(n1.toString(), "+", (n1 + n2).toString()) to n2.toString()
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition()
    val rawStage by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val stage = rawStage.toInt()
    val isSolvingX = stage in 0..2
    
    // Animasyon alfa değerleri
    val alphaRow by animateFloatAsState(if (isSolvingX) 1f else 0f)
    val alphaCol by animateFloatAsState(if (!isSolvingX) 1f else 0f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        // ROW ANIMATION
        if (alphaRow > 0f) {
            Row(
                modifier = Modifier.graphicsLayer(alpha = alphaRow),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HintAnimateCell(rowData.first.first, colors)
                Text(rowData.first.second, color = colors.textSecondary, fontWeight = FontWeight.Bold)
                HintAnimateCell(if (stage >= 1) rowData.second else "?", colors, isGoal = stage >= 1)
                Text("=", color = colors.textSecondary, fontWeight = FontWeight.Bold)
                HintAnimateCell(rowData.first.third, colors, isResult = true, highlight = stage >= 1)
            }
        }

        // COLUMN ANIMATION
        if (alphaCol > 0f) {
            Column(
                modifier = Modifier.graphicsLayer(alpha = alphaCol),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                HintAnimateCell(colData.first.first, colors)
                Text(colData.first.second, color = colors.textSecondary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                HintAnimateCell(if (stage >= 4) colData.second else "?", colors, isGoal = stage >= 4)
                Text("=", color = colors.textSecondary, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.rotate(90f))
                HintAnimateCell(colData.first.third, colors, isResult = true, highlight = stage >= 4)
            }
        }
    }
}

@Composable
private fun HintAnimateCell(
    text: String,
    colors: EquatixDesignSystem.ThemeColors,
    isResult: Boolean = false,
    isGoal: Boolean = false,
    highlight: Boolean = false
) {
    val backgroundColor = when {
        isResult -> if (highlight) Color(0xFF34C759) else Color(0xFF34C759).copy(alpha = 0.2f)
        isGoal -> colors.textPrimary
        else -> colors.cardBackground
    }
    
    val textColor = when {
        isResult -> if (highlight) Color.White else Color(0xFF34C759)
        isGoal -> colors.cardBackground
        else -> colors.textPrimary
    }

    val scale by animateFloatAsState(if (isGoal || highlight) 1.2f else 1f)

    Box(
        modifier = Modifier
            .size(34.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .background(backgroundColor, CircleShape)
            .then(if (!isResult && !isGoal) Modifier.border(1.dp, colors.textSecondary.copy(alpha = 0.3f), CircleShape) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}