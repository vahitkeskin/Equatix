package com.vahitkeskin.equatix.ui.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckBoxOutlineBlank
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.vahitkeskin.equatix.domain.model.AppLanguage
import com.vahitkeskin.equatix.ui.home.HomeViewModel
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun LanguageSelectionCarousel(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    colors: EquatixDesignSystem.ThemeColors
) {
    val items = AppLanguage.values().toList()
    val infinitePageCount = Int.MAX_VALUE
    val midPoint = infinitePageCount / 2
    val startIndex = midPoint - (midPoint % items.size) + items.indexOf(currentLanguage)

    val pagerState = rememberPagerState(
        initialPage = startIndex,
        pageCount = { infinitePageCount }
    )

    val scope = rememberCoroutineScope()

    // Renk Tanımları
    val selectedColor = Color(0xFF4CAF50) // Yeşil (Onay)
    val unselectedIconColor = colors.textSecondary.copy(alpha = 0.6f) // Gri (Boş kutu)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // --- CAROUSEL ALANI ---
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp), // Kart yüksekliği + Animasyon payı
            contentAlignment = Alignment.Center
        ) {
            val cardWidth = maxWidth / 3.2f
            val cardHeight = 72.dp
            val padding = (maxWidth - cardWidth) / 2

            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(horizontal = padding),
                pageSpacing = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) { globalIndex ->
                val actualIndex = globalIndex % items.size
                val language = items[actualIndex]
                val isSelected = language == currentLanguage

                val appStrings by HomeViewModel().strings.collectAsState()
                val labelText = if (language == AppLanguage.SYSTEM) {
                    appStrings.langSystem
                } else {
                    language.label
                }

                // --- Animasyon Hesaplamaları ---
                val pageOffset = (
                        (pagerState.currentPage - globalIndex) + pagerState.currentPageOffsetFraction
                        ).absoluteValue

                // Ortadaki 1f, kenardakiler 0.85f
                val scale = lerp(0.85f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
                val alpha = lerp(0.5f, 1f, 1f - pageOffset.coerceIn(0f, 1f))

                // --- Stil Değişkenleri ---
                val backgroundColor by animateColorAsState(
                    targetValue = if (isSelected) selectedColor.copy(alpha = 0.1f) else colors.cardBackground,
                    label = "bgColor"
                )
                val borderColor = if (isSelected) selectedColor else colors.divider
                val contentColor by animateColorAsState(
                    targetValue = if (isSelected) selectedColor else colors.textPrimary,
                    label = "contentColor"
                )

                Box(
                    modifier = Modifier
                        .width(cardWidth)
                        .height(cardHeight)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            this.alpha = alpha
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                width = 1.dp,
                                color = borderColor,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                // Tıklama Mantığı:
                                // Eğer yandaki karta tıklandıysa -> Oraya kaydır
                                // Eğer ortadaki karta tıklandıysa -> Seçimi yap
                                if (globalIndex != pagerState.currentPage) {
                                    scope.launch { pagerState.animateScrollToPage(globalIndex) }
                                } else {
                                    onLanguageSelected(language)
                                }
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = backgroundColor),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        // KART İÇERİĞİ (Box kullanarak üst üste bindirme)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        ) {
                            // 1. ORTA İÇERİK: Bayrak ve İsim
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = language.flagEmoji,
                                    fontSize = 24.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = labelText,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = contentColor,
                                    maxLines = 1,
                                    textAlign = TextAlign.Center
                                )
                            }

                            // 2. SAĞ ÜST KÖŞE: Checkbox / Tik
                            // Seçiliyse dolu tik, değilse boş kutucuk
                            Icon(
                                imageVector = if (isSelected) Icons.Rounded.CheckCircle else Icons.Rounded.CheckBoxOutlineBlank,
                                contentDescription = if (isSelected) "Selected" else "Select",
                                tint = if (isSelected) selectedColor else unselectedIconColor,
                                modifier = Modifier
                                    .size(20.dp)
                                    .align(Alignment.TopEnd)
                            )
                        }
                    }
                }
            }
        }
    }
}