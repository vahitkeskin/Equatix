package com.vahitkeskin.equatix.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.vahitkeskin.equatix.domain.model.CellData
import com.vahitkeskin.equatix.domain.model.GameState
import com.vahitkeskin.equatix.domain.model.Operation
import com.vahitkeskin.equatix.ui.game.GameViewModel

@Composable
fun GameGrid(
    state: GameState,
    viewModel: GameViewModel,
    cellSize: Dp,
    opWidth: Dp,
    fontSize: TextUnit
) {
    val n = state.size
    // Dikey operatörler için boşluk hesabı (Hücre boyutunun yarısı kadar)
    val gapHeight = cellSize * 0.5f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // --- ANA IZGARA DÖNGÜSÜ ---
        for (i in 0 until n) {
            // 1. SATIR: Hücreler + Yatay Operatörler + Satır Sonucu
            Row(verticalAlignment = Alignment.CenterVertically) {
                for (j in 0 until n) {
                    val cell = state.grid[i * n + j]

                    // ViewModel'den seçim durumunu kontrol et
                    val isSelected = viewModel.selectedCellIndex == cell.id

                    GridCell(
                        data = cell,
                        isSelected = isSelected,
                        cellSize = cellSize,
                        fontSize = fontSize,
                        onClick = { viewModel.onCellSelected(cell.id) }
                    )

                    // Yatay Operatör (Son sütun hariç)
                    if (j < n - 1) {
                        OpSymbol(state.rowOps[i * (n - 1) + j], opWidth, fontSize)
                    }
                }
                // Satır sonu eşittir ve sonuç
                OpText("=", opWidth, fontSize)
                ResultCell(state.rowResults[i], cellSize, fontSize)
            }

            // 2. ARA SATIR: Dikey Operatörler (Son satır hariç)
            if (i < n - 1) {
                Row(
                    modifier = Modifier.height(gapHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (j in 0 until n) {
                        VerticalOp(state.colOps[j * (n - 1) + i], cellSize, fontSize)
                        if (j < n - 1) Spacer(modifier = Modifier.width(opWidth))
                    }
                    // Sağ taraftaki sonuç sütunu hizası için boşluk
                    Spacer(modifier = Modifier.width(opWidth + cellSize))
                }
            }
        }

        // --- GÖRSEL BOŞLUK (İsteğiniz üzerine eklendi) ---
        Spacer(modifier = Modifier.height(16.dp))

        // --- DİKEY EŞİTTİRLER ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            for (j in 0 until n) {
                VerticalEquals(cellSize, fontSize) // Genişlik olarak opWidth değil cellSize kullandık ki ortalansın
                if (j < n - 1) Spacer(modifier = Modifier.width(opWidth))
            }
            Spacer(modifier = Modifier.width(opWidth + cellSize))
        }

        // --- EN ALT SONUÇLAR ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            for (j in 0 until n) {
                ResultCell(state.colResults[j], cellSize, fontSize)
                if (j < n - 1) Spacer(modifier = Modifier.width(opWidth))
            }
            Spacer(modifier = Modifier.width(opWidth + cellSize))
        }
    }
}

// ----------------------------------------------------------------
// YARDIMCI BİLEŞENLER (COMPONENTS)
// ----------------------------------------------------------------

@Composable
fun GridCell(
    data: CellData,
    isSelected: Boolean,
    cellSize: Dp,
    fontSize: TextUnit,
    onClick: () -> Unit
) {
    // Animasyonlu renk geçişleri
    val backgroundColor by animateColorAsState(
        targetValue = when {
            data.isLocked -> Color.White.copy(alpha = 0.1f) // Kilitli hücre (sabit sayı)
            isSelected -> Color(0xFF38BDF8).copy(alpha = 0.3f) // Seçili
            else -> Color.White.copy(alpha = 0.05f) // Normal boş hücre
        },
        animationSpec = tween(200)
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF38BDF8) else Color.Transparent
    )

    Box(
        modifier = Modifier
            .size(cellSize)
            .padding(2.dp)
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .clickable(enabled = !data.isLocked) { onClick() }, // Kilitliyse tıklanamaz
        contentAlignment = Alignment.Center
    ) {
        val textColor = if (data.isLocked) Color.White else Color(0xFF38BDF8)

        // Ekranda gösterilecek metin: Kilitliyse doğru değer, değilse kullanıcının girdiği değer
        val displayText = if (data.isLocked) data.correctValue.toString() else data.userInput

        Text(
            text = displayText,
            color = textColor,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun VerticalEquals(width: Dp, fontSize: TextUnit) {
    Box(
        modifier = Modifier.width(width).height(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "=",
            modifier = Modifier.rotate(90f),
            color = Color.Gray,
            fontWeight = FontWeight.Light,
            fontSize = fontSize
        )
    }
}

@Composable
fun OpSymbol(op: Operation, width: Dp, fontSize: TextUnit) {
    Box(modifier = Modifier.width(width), contentAlignment = Alignment.Center) {
        val color = when(op) {
            Operation.ADD -> Color(0xFF0A84FF) // Mavi
            Operation.SUB -> Color(0xFFFF453A) // Kırmızı
            Operation.MUL -> Color(0xFFFF9F0A) // Turuncu
            Operation.DIV -> Color(0xFFAB47BC) // Mor
        }
        Text(text = op.symbol, color = color, fontWeight = FontWeight.Normal, fontSize = fontSize)
    }
}

@Composable
fun VerticalOp(op: Operation, width: Dp, fontSize: TextUnit) {
    // VerticalOp aslında OpSymbol ile aynı görseli kullanıyor ama boyutu farklı olabilir
    // Şimdilik aynı mantıkla kutu içine alıyoruz
    Box(modifier = Modifier.width(width), contentAlignment = Alignment.Center) {
        val color = when(op) {
            Operation.ADD -> Color(0xFF0A84FF)
            Operation.SUB -> Color(0xFFFF453A)
            Operation.MUL -> Color(0xFFFF9F0A)
            Operation.DIV -> Color(0xFFAB47BC)
        }
        Text(text = op.symbol, color = color, fontWeight = FontWeight.Normal, fontSize = fontSize)
    }
}

@Composable
fun OpText(text: String, width: Dp, fontSize: TextUnit) {
    Box(modifier = Modifier.width(width), contentAlignment = Alignment.Center) {
        Text(text = text, color = Color.Gray, fontWeight = FontWeight.Light, fontSize = fontSize)
    }
}