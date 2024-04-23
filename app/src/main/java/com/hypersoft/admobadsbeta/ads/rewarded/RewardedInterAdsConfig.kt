@file:Suppress("KotlinConstantConditions")

package com.hypersoft.admobadsbeta.ads.rewarded

import android.app.Activity
import android.content.Context
import androidx.annotation.StringRes
import com.hypersoft.admobadsbeta.R
import com.hypersoft.admobadsbeta.ads.rewarded.repository.RewardedInterRepository
import com.hypersoft.admobadsbeta.ads.rewarded.callbacks.RewardedOnLoadCallBack
import com.hypersoft.admobadsbeta.ads.rewarded.callbacks.RewardedOnShowCallBack
import com.hypersoft.admobadsbeta.ads.utils.AdsType.REWARDED_FEATURE
import com.hypersoft.admobadsbeta.di.DiComponent

/**
 * @Author: SOHAIB AHMED
 * @Date: 05/03/2024
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

/**
 * Configurations and operations for rewarded interstitial ads.
 *
 * @param context The context, which can be of the application class.
 */

class RewardedInterAdsConfig(private val context: Context?) : RewardedInterRepository() {

    // Lazy initialization of DI component
    private val diComponent by lazy { DiComponent() }

    /**
     * Loads a rewarded interstitial ad.
     *
     * @param adType The type of the rewarded interstitial ad.
     * @param listener Callback to handle load events.
     */
    fun loadRewardedInterAd(adType: String, listener: RewardedOnLoadCallBack? = null) {
        var interAdId = ""
        var isRemoteEnable = false

        when (adType) {
            REWARDED_FEATURE -> {
                interAdId = getResString(R.string.admob_rewarded_inter_id)
                isRemoteEnable = diComponent.rcvRewardedInterFeature == 1
            }
        }

        // Load the rewarded interstitial ad
        loadRewardedInter(
            context = context,
            adType = adType,
            rewardedInterId = interAdId,
            adEnable = isRemoteEnable,
            isAppPurchased = diComponent.isAppPurchased,
            isInternetConnected = diComponent.isInternetConnected,
            canRequestAdsConsent = diComponent.canRequestAdsConsent,
            listener = listener
        )
    }

    /**
     * Shows a rewarded interstitial ad.
     *
     * @param activity The activity.
     * @param adType The type of the rewarded interstitial ad.
     * @param listener Callback to handle show events.
     */
    fun showRewardedInterAd(activity: Activity?, adType: String, listener: RewardedOnShowCallBack? = null) {
        showRewardedInter(
            activity = activity,
            adType = adType,
            isAppPurchased = diComponent.isAppPurchased,
            listener
        )
    }

    /**
     * Retrieves a string resource by its resource ID.
     *
     * @param resId The resource ID of the string.
     * @return The string value, or an empty string if the resource is not found or if the context is null.
     */
    private fun getResString(@StringRes resId: Int): String {
        return context?.resources?.getString(resId) ?: ""
    }
}