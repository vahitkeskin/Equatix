package com.vahitkeskin.equatix.platform

actual object AdActions {
    actual fun showInterstitial(onDismissed: () -> Unit) {
        onDismissed()
    }

    actual fun showRewarded(onRewardEarned: () -> Unit) {
        // Not implemented for iOS
    }
}
