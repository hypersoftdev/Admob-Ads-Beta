package com.hypersoft.admobadsbeta.ads.banners.callbacks

/**
 * @Author: SOHAIB AHMED
 * @Date: 06/03/2024
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

/**
 * Interface for handling banner ad callbacks.
 */
interface BannerCallBack {

    /**
     * Called when the banner ad loading is completed successfully or unsuccessfully.
     * @param successfullyLoaded Indicates whether the banner ad was loaded successfully.
     */
    fun onResponse(successfullyLoaded: Boolean) {}

    /**
     * Called when the banner ad is closed.
     */
    fun onAdClosed() {}

    /**
     * Called when the banner ad is opened.
     */
    fun onAdOpened() {}
}