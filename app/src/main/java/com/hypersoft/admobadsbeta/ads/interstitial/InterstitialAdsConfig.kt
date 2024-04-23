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
 * Class responsible for configuring and managing Interstitial Ads.
 *
 * @param context Context instance, can be Application class.
 */

class InterstitialAdsConfig(private val context: Context?) : InterstitialRepository() {

    // Lazy initialization of DI component
    private val diComponent by lazy { DiComponent() }

    // Map to store counters for each ad type
    private val counterMap by lazy { HashMap<String, Int>() }

    /**
     * Loads an Interstitial Ad.
     *
     * @param adType The type of Interstitial Ad to load.
     * @param listener Callback to handle load events.
     */
    fun loadInterstitialAd(adType: String, listener: InterstitialOnLoadCallBack? = null) {
        var interAdId = ""
        var isRemoteEnable = false

        // Determine Interstitial Ad ID and remote enable status based on ad type
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

        // Load Interstitial Ad
        loadInterstitial(
            context = context,
            adType = adType,
            interId = interAdId,
            adEnable = isRemoteEnable,
            isAppPurchased = diComponent.isAppPurchased,
            isInternetConnected = diComponent.isInternetConnected,
            canRequestAdsConsent = diComponent.canRequestAdsConsent,
            listener = listener
        )
    }

    /**
     * Shows an Interstitial Ad.
     *
     * @param activity The activity where the ad should be displayed.
     * @param adType The type of Interstitial Ad to show.
     * @param listener Callback to handle show events.
     */
    fun showInterstitialAd(activity: Activity?, adType: String, listener: InterstitialOnShowCallBack? = null) {
        showInterstitial(
            activity = activity,
            adType = adType,
            isAppPurchased = diComponent.isAppPurchased,
            listener
        )
    }

    /**
     * Loads an Interstitial Ad based on counter value and load conditions.
     *
     * @param adType Key of the Ad, it should be a unique id and case-sensitive.
     * @param remoteCounter Pass remote counter value. If the value is n, it will load on "n-1". In case of <= 2, it will load every time.
     * @param loadOnStart Determine whether the ad should be loaded on the very first time or not.
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

    /**
     * Retrieves string resource from the context.
     *
     * @param resId Resource ID of the string.
     * @return String value of the resource.
     */
    private fun getResString(@StringRes resId: Int): String {
        return context?.resources?.getString(resId) ?: ""
    }
}