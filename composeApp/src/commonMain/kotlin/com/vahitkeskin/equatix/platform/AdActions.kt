package com.vahitkeskin.equatix.platform

expect object AdActions {
    fun showInterstitial(onDismissed: () -> Unit)
    fun showRewarded(onRewardEarned: () -> Unit)
}
