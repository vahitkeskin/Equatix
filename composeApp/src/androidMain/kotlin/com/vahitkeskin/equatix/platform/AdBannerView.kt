package com.vahitkeskin.equatix.platform

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.vahitkeskin.equatix.BuildConfig

@Composable
fun AdBannerView(
    modifier: Modifier = Modifier,
    adUnitId: String = BuildConfig.ADMOB_BANNER_ID
) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        factory = { ctx ->
            AdView(ctx).apply {
                setAdUnitId(adUnitId)
                setAdSize(AdSize.BANNER)
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}


