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
 * @param context: Can be of application class
 */

class RewardedInterAdsConfig(private val context: Context?) : RewardedInterRepository() {

    private val diComponent by lazy { DiComponent() }

    fun loadRewardedInterAd(adType: String, listener: RewardedOnLoadCallBack? = null) {
        var interAdId = ""
        var isRemoteEnable = false

        when (adType) {
            REWARDED_FEATURE -> {
                interAdId = getResString(R.string.admob_rewarded_id)
                isRemoteEnable = diComponent.rcvRewardedFeature == 1
            }
        }

        loadRewardedInter(
            context = context,
            adType = adType,
            rewardedInterId = interAdId,
            adEnable = isRemoteEnable,
            isAppPurchased = diComponent.isAppPurchased,
            isInternetConnected = diComponent.isInternetConnected,
            listener = listener
        )
    }

    fun showRewardedInterAd(activity: Activity?, adType: String, listener: RewardedOnShowCallBack? = null) {
        showRewardedInter(
            activity = activity,
            adType = adType,
            isAppPurchased = diComponent.isAppPurchased,
            listener
        )
    }

    private fun getResString(@StringRes resId: Int): String {
        return context?.resources?.getString(resId) ?: ""
    }
}