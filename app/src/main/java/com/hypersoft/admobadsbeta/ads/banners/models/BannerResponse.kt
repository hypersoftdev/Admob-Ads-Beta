package com.hypersoft.admobadsbeta.ads.banners.models

import android.view.ViewGroup
import com.google.android.gms.ads.AdView

/**
 * @Author: SOHAIB AHMED
 * @Date: 06/03/2024
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

/**
 * @param loadState
 *      -1: Default State
 *      0: Can't call (ad failed / Premium)
 *      1: Ad available
 *      2: Ad is loading
 *      3: Impression
 */

data class BannerResponse(
    val adType: String,
    var adView: AdView?,
    var loadState: Int = -1,
    var viewGroup: ViewGroup
)
