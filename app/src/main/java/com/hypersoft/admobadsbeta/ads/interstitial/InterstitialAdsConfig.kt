@file:Suppress("KotlinConstantConditions")

package com.hypersoft.admobadsbeta.ads.interstitial

import android.app.Activity
import android.content.Context
import androidx.annotation.StringRes
import com.hypersoft.admobadsbeta.R
import com.hypersoft.admobadsbeta.ads.interstitial.repository.InterstitialRepository
import com.hypersoft.admobadsbeta.ads.interstitial.callbacks.InterstitialOnLoadCallBack
import com.hypersoft.admobadsbeta.ads.interstitial.callbacks.InterstitialOnShowCallBack
import com.hypersoft.admobadsbeta.ads.utils.AdsType.INTER_HOME
import com.hypersoft.admobadsbeta.ads.utils.AdsType.INTER_SAVE
import com.hypersoft.admobadsbeta.ads.utils.AdsType.INTER_SPLASH
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

class InterstitialAdsConfig(private val context: Context?) : InterstitialRepository() {

    private val diComponent by lazy { DiComponent() }
    private val counterMap by lazy { HashMap<String, Int>() }

    fun loadInterstitialAd(adType: String, listener: InterstitialOnLoadCallBack? = null) {
        var interAdId = ""
        var isRemoteEnable = false

        when (adType) {
            INTER_SPLASH -> {
                interAdId = getResString(R.string.admob_inter_id)
                isRemoteEnable = diComponent.rcvInterSplash == 1
            }

            INTER_HOME -> {
                interAdId = getResString(R.string.admob_inter_id)
                isRemoteEnable = diComponent.rcvInterHome == 1
            }

            INTER_SAVE -> {
                interAdId = getResString(R.string.admob_inter_id)
                isRemoteEnable = diComponent.rcvInterSave == 1
            }
        }

        loadInterstitial(
            context = context,
            adType = adType,
            interId = interAdId,
            adEnable = isRemoteEnable,
            isAppPurchased = diComponent.isAppPurchased,
            isInternetConnected = diComponent.isInternetConnected,
            listener = listener
        )
    }

    fun showInterstitialAd(activity: Activity?, adType: String, listener: InterstitialOnShowCallBack? = null) {
        showInterstitial(
            activity = activity,
            adType = adType,
            isAppPurchased = diComponent.isAppPurchased,
            listener
        )
    }

    /**
     * @param adType   Key of the Ad, it should be unique id and should be case-sensitive
     * @param remoteCounter   Pass remote counter value, if the value is n, it will load on "n-1". In case of <= 2, it will load everytime
     * @param loadOnStart  Determine whether ad should be load on the very first time or not?
     *
     *  e.g. remoteCounter = 4, ad will  load on "n-1" = 3
     *      if (loadOnStart) {
     *          // 1, 0, 0, 1, 0, 0, 1, 0, 0 ... so on
     *      } else {
     *          // 0, 0, 1, 0, 0, 1, 0, 0, 1 ... so on
     *      }
     */

    fun loadInterstitialAd(adType: String, remoteCounter: Int, loadOnStart: Boolean, listener: InterstitialOnLoadCallBack? = null) {
        when (loadOnStart) {
            true -> counterMap.putIfAbsent(adType, remoteCounter - 1)
            false -> counterMap.putIfAbsent(adType, 0)
        }

        if (counterMap.containsKey(adType)) {
            val counter = counterMap[adType] ?: 0
            counterMap[adType] = counter + 1
            counterMap[adType]?.let { currentCounter ->
                if (currentCounter >= remoteCounter - 1) {
                    counterMap[adType] = 0
                    loadInterstitialAd(adType = adType, listener = listener)
                }
            }
        }
    }

    private fun getResString(@StringRes resId: Int): String {
        return context?.resources?.getString(resId) ?: ""
    }
}