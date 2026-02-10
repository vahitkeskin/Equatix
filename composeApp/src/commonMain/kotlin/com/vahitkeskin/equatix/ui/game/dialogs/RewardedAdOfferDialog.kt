package com.vahitkeskin.equatix.ui.game.dialogs

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import com.vahitkeskin.equatix.ui.components.EquatixIcons_Answer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vahitkeskin.equatix.ui.utils.PreviewContainer
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.vahitkeskin.equatix.domain.model.AppStrings
import com.vahitkeskin.equatix.platform.AdBlockerManager
import com.vahitkeskin.equatix.ui.common.GlassBox
import com.vahitkeskin.equatix.ui.theme.EquatixDesignSystem

@Composable
fun RewardedAdOfferDialog(
    isDnsActive: Boolean,
    appStrings: AppStrings,
    colors: EquatixDesignSystem.ThemeColors,
    onDismiss: () -> Unit,
    onWatchAd: () -> Unit,
    onOpenDnsSettings: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

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
                // Professional Animated Icon
                Icon(
                    imageVector = EquatixIcons_Answer,
                    contentDescription = null,
                    tint = colors.error,
                    modifier = Modifier
                        .size(64.dp)
                        .scale(scale)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isDnsActive) appStrings.dnsBlockTitle else appStrings.rewardAdTitle,
                    color = colors.textPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isDnsActive) appStrings.dnsBlockDescription else appStrings.rewardAdDescription,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Action Button
                Button(
                    onClick = {
                        if (isDnsActive) {
                            onOpenDnsSettings()
                        } else {
                            onWatchAd()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.error,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Text(
                        if (isDnsActive) appStrings.dnsBlockButton else appStrings.rewardAdWatchButton,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Cancel Button
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        appStrings.rewardAdCancelButton,
                        color = colors.textSecondary.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewRewardedAdOfferDialog() {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Dark Mode / EN / Active DNS
        com.vahitkeskin.equatix.ui.utils.PreviewContainer(isDark = true, language = com.vahitkeskin.equatix.domain.model.AppLanguage.ENGLISH) { colors, strings ->
            Box(modifier = Modifier.height(400.dp)) {
                RewardedAdOfferDialog(
                    isDnsActive = true,
                    appStrings = strings,
                    colors = colors,
                    onDismiss = {},
                    onWatchAd = {},
                    onOpenDnsSettings = {}
                )
            }
        }

        // Light Mode / TR / No DNS
        com.vahitkeskin.equatix.ui.utils.PreviewContainer(isDark = false, language = com.vahitkeskin.equatix.domain.model.AppLanguage.TURKISH) { colors, strings ->
            Box(modifier = Modifier.height(400.dp)) {
                RewardedAdOfferDialog(
                    isDnsActive = false,
                    appStrings = strings,
                    colors = colors,
                    onDismiss = {},
                    onWatchAd = {},
                    onOpenDnsSettings = {}
                )
            }
        }
    }
}
