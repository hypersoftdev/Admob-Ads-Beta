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
 * Data class representing a response for banner ads.
 *
 * @property adType The type of the ad.
 * @property adView The banner ad view.
 * @property isAdEnable Indicates whether the ad is enabled.
 * @property viewGroup The view group in which the ad will be displayed.
 */
data class BannerResponse(
    val adType: String,
    var adView: AdView?,
    var isAdEnable: Boolean,
    var viewGroup: ViewGroup?
)
