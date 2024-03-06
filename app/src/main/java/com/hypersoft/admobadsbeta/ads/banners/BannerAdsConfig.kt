@file:Suppress("KotlinConstantConditions")

package com.hypersoft.admobadsbeta.ads.banners

import android.app.Activity
import android.view.ViewGroup
import androidx.annotation.StringRes
import com.google.android.gms.ads.AdView
import com.hypersoft.admobadsbeta.R
import com.hypersoft.admobadsbeta.ads.banners.callbacks.BannerOnLoadCallBack
import com.hypersoft.admobadsbeta.ads.banners.enums.BannerType
import com.hypersoft.admobadsbeta.ads.banners.repository.BannerRepository
import com.hypersoft.admobadsbeta.ads.utils.AdsType
import com.hypersoft.admobadsbeta.di.DiComponent

/**
 * @Author: SOHAIB AHMED
 * @Date: 05/03/2024
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

class BannerAdsConfig : BannerRepository() {

    private val diComponent by lazy { DiComponent() }

    fun loadBannerAd(activity: Activity?, adType: String, bannerType: BannerType, listener: BannerOnLoadCallBack? = null) {
        var bannerId = ""
        var isRemoteEnable = false

        when (adType) {
            AdsType.BANNER_HOME -> {
                bannerId = activity.getResString(R.string.admob_banner_id)
                isRemoteEnable = diComponent.rcvBannerHome == 1
            }

            AdsType.BANNER_GALLERY -> {
                bannerId = activity.getResString(R.string.admob_banner_id)
                isRemoteEnable = diComponent.rcvBannerGallery == 1
            }
        }

        loadBanner(
            activity = activity,
            adType = adType,
            bannerType = bannerType,
            bannerId = bannerId,
            adEnable = isRemoteEnable,
            isAppPurchased = diComponent.isAppPurchased,
            isInternetConnected = diComponent.isInternetConnected,
            listener = listener
        )
    }

    fun showBannerAd(adType: String, adView: AdView?, viewGroup: ViewGroup) {
        showBanner(
            adType = adType,
            adView = adView,
            viewGroup = viewGroup,
            isAppPurchased = diComponent.isAppPurchased,
        )
    }

    private fun Activity?.getResString(@StringRes resId: Int): String {
        return this?.resources?.getString(resId) ?: ""
    }
}