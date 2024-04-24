package com.hypersoft.admobadsbeta.ads.natives.callbacks

/**
 * @Author: SOHAIB AHMED
 * @Date: 11/03/2024
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

/**
 * Callback interface for handling native ad loading events.
 */
interface NativeCallBack {
    /**
     * Called when a native ad is successfully loaded or failed to load.
     *
     * @param successfullyLoaded Indicates whether the native ad was successfully loaded (`true`) or not (`false`).
     */
    fun onResponse(successfullyLoaded: Boolean){}
}