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

data class NativeResponse(
    val adType: String,
    var nativeType: NativeType,
    var isAdEnable: Boolean,
    var nativeAd: NativeAd?,
    var viewGroup: ViewGroup?
)