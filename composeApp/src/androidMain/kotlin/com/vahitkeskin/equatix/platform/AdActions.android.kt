package com.vahitkeskin.equatix.platform

import android.app.Activity

actual object AdActions {
    actual fun showInterstitial(onDismissed: () -> Unit) {
        val activity = appContext as? Activity ?: return
        AdManager.showInterstitial(activity, onDismissed)
    }

    actual fun showRewarded(onRewardEarned: () -> Unit) {
        val activity = appContext as? Activity ?: return
        AdManager.showRewarded(activity, onRewardEarned)
    }
}
