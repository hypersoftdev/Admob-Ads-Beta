package com.hypersoft.admobadsbeta.ads.interstitial.callbacks

/**
 * @Author: SOHAIB AHMED
 * @Date: 05/03/2024
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

/**
 * Interface defining callback methods for Interstitial Ad load events.
 */
interface InterstitialOnLoadCallBack {
    /**
     * Callback method invoked when the Interstitial Ad loading is completed,
     * either load, failed or any other interruption, like internet, premium etc.
     */
    fun onResponse(){}
}