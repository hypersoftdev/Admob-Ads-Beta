package com.hypersoft.admobadsbeta.ads.rewarded.callbacks

/**
 * @Author: SOHAIB AHMED
 * @Date: 05/03/2024
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

/**
 * Interface for handling callbacks when a rewarded ad is shown.
 */
interface RewardedOnShowCallBack {
    /**
     * Callback method invoked when the rewarded ad is dismissed.
     */
    fun onAdDismissedFullScreenContent() {}

    /**
     * Callback method invoked when the rewarded ad fails to show.
     */
    fun onAdFailedToShow()

    /**
     * Callback method invoked when the rewarded ad is shown.
     */
    fun onAdShowedFullScreenContent() {}

    /**
     * Callback method invoked when the ad impression occurs.
     */
    fun onAdImpression() {}

    /**
     * Callback method invoked when the user earns a reward from the ad.
     */
    fun onUserEarnedReward()
}