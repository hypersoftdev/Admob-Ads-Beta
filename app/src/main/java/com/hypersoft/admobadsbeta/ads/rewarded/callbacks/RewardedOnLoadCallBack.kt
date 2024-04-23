package com.hypersoft.admobadsbeta.ads.rewarded.callbacks

/**
 * @Author: SOHAIB AHMED
 * @Date: 05/03/2024
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

/**
 * Interface for handling callbacks when a rewarded ad is loaded.
 */
interface RewardedOnLoadCallBack {
    /**
     * Callback method invoked when the rewarded ad loading process is completed.
     *
     * @param isSuccess True if the rewarded ad is successfully loaded, false otherwise.
     */
    fun onResponse(isSuccess: Boolean) {}
}