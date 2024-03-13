@file:Suppress("KotlinConstantConditions")

package com.hypersoft.admobadsbeta.ads.natives

import android.app.Activity
import android.view.ViewGroup
import androidx.annotation.StringRes
import com.hypersoft.admobadsbeta.R
import com.hypersoft.admobadsbeta.ads.natives.callbacks.NativeCallBack
import com.hypersoft.admobadsbeta.ads.natives.enums.NativeType
import com.hypersoft.admobadsbeta.ads.natives.repository.NativeRegularRepository
import com.hypersoft.admobadsbeta.ads.natives.repository.NativeRepository
import com.hypersoft.admobadsbeta.ads.utils.AdsType
import com.hypersoft.admobadsbeta.di.DiComponent

/**
 * @Author: SOHAIB AHMED
 * @Date: 11/03/2024
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

class NativeAdsConfig : NativeRepository() {

    private val diComponent by lazy { DiComponent() }
    private val nativeRegularRepository by lazy { NativeRegularRepository() }

    fun loadNativeAd(activity: Activity?, adType: String, viewGroup: ViewGroup?, listener: NativeCallBack? = null) {
        val nativeId = getNativeId(activity, adType)
        val isRemoteEnable = getRemoteEnable(adType)
        val nativeType = getNativeType(adType)

        loadNative(
            activity = activity,
            adType = adType,
            nativeId = nativeId,
            nativeType = nativeType,
            isAdEnable = isRemoteEnable,
            isAppPurchased = diComponent.isAppPurchased,
            isInternetConnected = diComponent.isInternetConnected,
            canRequestAdsConsent = diComponent.canRequestAdsConsent,
            viewGroup = viewGroup,
            listener = listener
        )
    }

    fun loadAndShowNativeAd(activity: Activity?, adType: String, viewGroup: ViewGroup, listener: NativeCallBack? = null) {
        val nativeId = getNativeId(activity, adType)
        val isRemoteEnable = getRemoteEnable(adType)
        val nativeType = getNativeType(adType)

        nativeRegularRepository.loadAndShowNative(
            activity = activity,
            adType = adType,
            nativeId = nativeId,
            nativeType = nativeType,
            isAdEnable = isRemoteEnable,
            isAppPurchased = diComponent.isAppPurchased,
            isInternetConnected = diComponent.isInternetConnected,
            canRequestAdsConsent = diComponent.canRequestAdsConsent,
            viewGroup = viewGroup,
            listener = listener
        )
    }

    private fun getNativeId(activity: Activity?, adType: String): String {
        return when (adType) {
            AdsType.NATIVE_ONE -> activity.getResString(R.string.admob_native_id)
            AdsType.NATIVE_TWO -> activity.getResString(R.string.admob_native_id)
            AdsType.NATIVE_THREE -> activity.getResString(R.string.admob_native_id)
            AdsType.NATIVE_FOUR -> activity.getResString(R.string.admob_native_id)
            AdsType.NATIVE_FIVE -> activity.getResString(R.string.admob_native_id)
            AdsType.NATIVE_SIX -> activity.getResString(R.string.admob_native_id)
            AdsType.NATIVE_SEVEN -> activity.getResString(R.string.admob_native_id)
            else -> ""
        }
    }

    private fun getRemoteEnable(adType: String): Boolean {
        return when (adType) {
            AdsType.NATIVE_ONE -> diComponent.rcvNativeOne == 1
            AdsType.NATIVE_TWO -> diComponent.rcvNativeTwo == 1
            AdsType.NATIVE_THREE -> diComponent.rcvNativeThree == 1
            AdsType.NATIVE_FOUR -> diComponent.rcvNativeFour == 1
            AdsType.NATIVE_FIVE -> diComponent.rcvNativeFive == 1
            AdsType.NATIVE_SIX -> diComponent.rcvNativeSix == 1
            AdsType.NATIVE_SEVEN -> diComponent.rcvNativeSeven == 1
            else -> false
        }
    }

    private fun getNativeType(adType: String): NativeType {
        return when (adType) {
            AdsType.NATIVE_ONE -> NativeType.NATIVE_BANNER_SMART
            AdsType.NATIVE_TWO -> NativeType.NATIVE_BANNER
            AdsType.NATIVE_THREE -> NativeType.NATIVE_MEDIUM_OLD_SMART
            AdsType.NATIVE_FOUR -> NativeType.NATIVE_MEDIUM_OLD
            AdsType.NATIVE_FIVE -> NativeType.NATIVE_MEDIUM_SMART
            AdsType.NATIVE_SIX -> NativeType.NATIVE_MEDIUM
            AdsType.NATIVE_SEVEN -> NativeType.NATIVE_LARGE
            else -> NativeType.NATIVE_LARGE
        }
    }

    private fun Activity?.getResString(@StringRes resId: Int): String {
        return this?.resources?.getString(resId) ?: ""
    }
}