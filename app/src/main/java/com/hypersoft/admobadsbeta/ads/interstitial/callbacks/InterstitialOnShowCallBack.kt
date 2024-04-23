package com.hypersoft.admobadsbeta.ads.interstitial.callbacks

/**
 * @Author: SOHAIB AHMED
 * @Date: 05/03/2024
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

/**
 * Interface defining callback methods for Interstitial Ad show events.
 */
interface InterstitialOnShowCallBack {
    /**
     * Callback method invoked when the Interstitial Ad is dismissed.
     */
    fun onAdDismissedFullScreenContent() {}
    /**
     * Callback method invoked when the Interstitial Ad fails to show.
     */
    fun onAdFailedToShow()
    /**
     * Callback method invoked when the Interstitial Ad is shown.
     */
    fun onAdShowedFullScreenContent() {}
    /**
     * Callback method invoked when the Interstitial Ad impression is registered.
     */
    fun onAdImpression() {}
    /**
     * Callback method invoked when the Interstitial Ad impression with some 300 milli second delay.
     */
    fun onAdImpressionDelayed() {}
}