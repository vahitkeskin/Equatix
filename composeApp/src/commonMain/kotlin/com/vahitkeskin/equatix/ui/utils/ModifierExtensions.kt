package com.vahitkeskin.equatix.ui.utils

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.dp

/**
 * AdBanner için her cihaza özel sistem navigation bar yüksekliğini hesaplayarak 
 * banner'ın en altta ama sistem navigasyonunun üstünde kalmasını sağlar.
 */
fun Modifier.bannerSystemPadding(): Modifier = composed {
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    // Eğer navigation bar yüksekliği 0 ise (bazı gesture modları veya desktop) 
    // minimum estetik bir boşluk bırakmak profesyonel bir dokunuştur.
    val finalPadding = if (bottomPadding > 0.dp) bottomPadding else 0.dp
    this.padding(bottom = finalPadding)
}
