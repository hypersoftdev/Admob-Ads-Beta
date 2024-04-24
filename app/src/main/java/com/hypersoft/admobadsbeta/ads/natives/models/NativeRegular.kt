package com.hypersoft.admobadsbeta.ads.natives.models

import android.view.ViewGroup
import com.google.android.gms.ads.nativead.NativeAd
import com.hypersoft.admobadsbeta.ads.natives.enums.NativeType

/**
 * @Author: SOHAIB AHMED
 * @Date: 12/03/2024
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

/**
 * Data class representing a regular native ad.
 * @property adType Type of the ad.
 * @property nativeType Type of the native ad.
 * @property nativeId ID for the native ad.
 * @property nativeAd Instance of the loaded native ad.
 * @property viewGroup ViewGroup where the native ad will be displayed.
 */
data class NativeRegular(
    val adType: String,
    val nativeType: NativeType,
    val nativeId: String,
    var nativeAd: NativeAd?,
    val viewGroup: ViewGroup
)