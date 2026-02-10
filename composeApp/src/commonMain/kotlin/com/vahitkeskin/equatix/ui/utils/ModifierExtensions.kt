package com.vahitkeskin.equatix.ui.utils

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

/**
 * Sistem navigation bar yüksekliğini hesaplayarak
 * bileşene alttan padding ekler.
 */
fun Modifier.systemNavBarPadding(): Modifier = composed {
    val density = LocalDensity.current
    val bottomPadding = WindowInsets.systemBars.asPaddingValues(density).calculateBottomPadding()
    this.padding(bottom = bottomPadding)
}
