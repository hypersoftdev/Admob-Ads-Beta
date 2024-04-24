package com.hypersoft.admobadsbeta.ads.natives.models

import android.view.ViewGroup
import com.google.android.gms.ads.nativead.NativeAd
import com.hypersoft.admobadsbeta.ads.natives.enums.NativeType

/**
 * @Author: SOHAIB AHMED
 * @Date: 11/03/2024
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

/**
 * Data class representing a response for a native ad.
 * @property adType Type of the ad.
 * @property nativeType Type of the native ad.
 * @property isAdEnable Boolean indicating whether the ad is enabled.
 * @property nativeAd Instance of the native ad.
 * @property viewGroup ViewGroup where the native ad will be displayed.
 */
data class NativeResponse(
    val adType: String,
    var nativeType: NativeType,
    var isAdEnable: Boolean,
    var nativeAd: NativeAd?,
    var viewGroup: ViewGroup?
)