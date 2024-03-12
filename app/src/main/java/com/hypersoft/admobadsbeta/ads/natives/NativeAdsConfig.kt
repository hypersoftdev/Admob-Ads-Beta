@file:Suppress("KotlinConstantConditions")

package com.hypersoft.admobadsbeta.ads.natives

import android.app.Activity
import android.view.ViewGroup
import androidx.annotation.StringRes
import com.hypersoft.admobadsbeta.R
import com.hypersoft.admobadsbeta.ads.natives.callbacks.NativeCallBack
import com.hypersoft.admobadsbeta.ads.natives.enums.NativeType
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

    fun loadNativeAd(activity: Activity?, adType: String, viewGroup: ViewGroup, listener: NativeCallBack? = null) {
        var nativeId = ""
        var nativeType = NativeType.NATIVE_MEDIUM_SMART
        var isRemoteEnable = false

        when (adType) {
            AdsType.NATIVE_LANGUAGE -> {
                nativeId = activity.getResString(R.string.admob_native_id)
                isRemoteEnable = diComponent.rcvNativeLanguage == 1
                nativeType = NativeType.NATIVE_BANNER
            }

            AdsType.NATIVE_HOME -> {
                nativeId = activity.getResString(R.string.admob_native_id)
                isRemoteEnable = diComponent.rcvNativeHome == 1
                nativeType = NativeType.NATIVE_BANNER_SMART
            }
        }

        loadNative(
            activity = activity,
            adType = adType,
            nativeId = nativeId,
            nativeType = nativeType,
            isAdEnable = isRemoteEnable,
            isAppPurchased = diComponent.isAppPurchased,
            isInternetConnected = diComponent.isInternetConnected,
            viewGroup = viewGroup,
            listener = listener
        )
    }

    private fun Activity?.getResString(@StringRes resId: Int): String {
        return this?.resources?.getString(resId) ?: ""
    }
}