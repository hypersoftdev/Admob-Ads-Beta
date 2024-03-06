package com.hypersoft.admobadsbeta.ads.banners.callbacks

/**
 * @Author: SOHAIB AHMED
 * @Date: 06/03/2024
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

interface BannerOnLoadCallBack {
    fun onResponse(successfullyLoaded: Boolean) {}
    fun onAdClosed() {}
    fun onAdOpened() {}
}