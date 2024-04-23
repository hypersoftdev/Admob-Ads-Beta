@file:Suppress("KotlinConstantConditions")

package com.hypersoft.admobadsbeta.ads.rewarded

import android.app.Activity
import android.content.Context
import androidx.annotation.StringRes
import com.hypersoft.admobadsbeta.R
import com.hypersoft.admobadsbeta.ads.rewarded.callbacks.RewardedOnLoadCallBack
import com.hypersoft.admobadsbeta.ads.rewarded.callbacks.RewardedOnShowCallBack
import com.hypersoft.admobadsbeta.ads.rewarded.repository.RewardedRepository
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
 * Configurations and methods for managing rewarded ads.
 *
 * @param context The context, which can be an application class.
 */

class RewardedAdsConfig(private val context: Context?) : RewardedRepository() {

    // Lazy initialization of DI component
    private val diComponent by lazy { DiComponent() }

    /**
     * Loads a rewarded ad.
     *
     * @param adType The type of rewarded ad to load.
     * @param listener Callback to handle load events.
     */
    fun loadRewardedAd(adType: String, listener: RewardedOnLoadCallBack? = null) {
        var interAdId = ""
        var isRemoteEnable = false

        // Determine ad ID and remote enable status based on ad type
        when (adType) {
            REWARDED_FEATURE -> {
                interAdId = getResString(R.string.admob_rewarded_id)
                isRemoteEnable = diComponent.rcvRewardedFeature == 1
            }
        }

        // Load the rewarded ad
        loadRewarded(
            context = context,
            adType = adType,
            rewardedId = interAdId,
            adEnable = isRemoteEnable,
            isAppPurchased = diComponent.isAppPurchased,
            isInternetConnected = diComponent.isInternetConnected,
            canRequestAdsConsent = diComponent.canRequestAdsConsent,
            listener = listener
        )
    }

    /**
     * Shows a rewarded ad.
     *
     * @param activity The activity where the ad will be shown.
     * @param adType The type of rewarded ad to show.
     * @param listener Callback to handle show events.
     */
    fun showRewardedAd(activity: Activity?, adType: String, listener: RewardedOnShowCallBack? = null) {
        showRewarded(
            activity = activity,
            adType = adType,
            isAppPurchased = diComponent.isAppPurchased,
            listener
        )
    }

    /**
     * Retrieves string resource by ID.
     *
     * @param resId The resource ID.
     * @return The string value of the resource.
     */
    private fun getResString(@StringRes resId: Int): String {
        return context?.resources?.getString(resId) ?: ""
    }
}