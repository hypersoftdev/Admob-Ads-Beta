@file:Suppress("KotlinConstantConditions")

package com.hypersoft.admobadsbeta.ads.banners

import android.app.Activity
import android.view.ViewGroup
import androidx.annotation.StringRes
import com.hypersoft.admobadsbeta.R
import com.hypersoft.admobadsbeta.ads.banners.callbacks.BannerCallBack
import com.hypersoft.admobadsbeta.ads.banners.enums.BannerType
import com.hypersoft.admobadsbeta.ads.banners.repository.BannerCollapsibleBottomRepository
import com.hypersoft.admobadsbeta.ads.banners.repository.BannerCollapsibleTopRepository
import com.hypersoft.admobadsbeta.ads.banners.repository.BannerMediumRepository
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
    private val bannerMediumRepository by lazy { BannerMediumRepository() }
    private val bannerCollapsibleTopRepository by lazy { BannerCollapsibleTopRepository() }
    private val bannerCollapsibleBottomRepository by lazy { BannerCollapsibleBottomRepository() }

    fun loadBannerAd(activity: Activity?, adType: String, viewGroup: ViewGroup?, listener: BannerCallBack? = null) {
        var bannerId = ""
        var isRemoteEnable = false
        var bannerType = BannerType.ADAPTIVE

        when (adType) {
            AdsType.BANNER_ONE -> {
                bannerId = activity.getResString(R.string.admob_banner_adaptive_id)
                isRemoteEnable = diComponent.rcvBannerOne == 1
                bannerType = BannerType.ADAPTIVE
            }

            AdsType.BANNER_TWO -> {
                bannerId = activity.getResString(R.string.admob_banner_medium_rectance_id)
                isRemoteEnable = diComponent.rcvBannerTwo == 1
                bannerType = BannerType.MEDIUM_RECTANGLE
            }

            AdsType.BANNER_THREE -> {
                bannerId = activity.getResString(R.string.admob_banner_collapsible_id)
                isRemoteEnable = diComponent.rcvBannerThree == 1
                bannerType = BannerType.COLLAPSIBLE_BOTTOM
            }

            AdsType.BANNER_FOUR -> {
                bannerId = activity.getResString(R.string.admob_banner_collapsible_id)
                isRemoteEnable = diComponent.rcvBannerFour == 1
                bannerType = BannerType.COLLAPSIBLE_TOP
            }
        }

        when (bannerType) {
            BannerType.ADAPTIVE -> {
                loadBanner(
                    activity = activity,
                    adType = adType,
                    bannerId = bannerId,
                    isAdEnable = isRemoteEnable,
                    isAppPurchased = diComponent.isAppPurchased,
                    isInternetConnected = diComponent.isInternetConnected,
                    canRequestAdsConsent = diComponent.canRequestAdsConsent,
                    viewGroup = viewGroup,
                    listener = listener
                )
            }

            BannerType.MEDIUM_RECTANGLE -> {
                bannerMediumRepository.loadBanner(
                    activity = activity,
                    adType = adType,
                    bannerId = bannerId,
                    isAdEnable = isRemoteEnable,
                    isAppPurchased = diComponent.isAppPurchased,
                    isInternetConnected = diComponent.isInternetConnected,
                    canRequestAdsConsent = diComponent.canRequestAdsConsent,
                    viewGroup = viewGroup,
                    listener = listener
                )
            }

            BannerType.COLLAPSIBLE_TOP -> {
                bannerCollapsibleTopRepository.loadBanner(
                    activity = activity,
                    adType = adType,
                    bannerId = bannerId,
                    isAdEnable = isRemoteEnable,
                    isAppPurchased = diComponent.isAppPurchased,
                    isInternetConnected = diComponent.isInternetConnected,
                    canRequestAdsConsent = diComponent.canRequestAdsConsent,
                    viewGroup = viewGroup,
                    listener = listener
                )
            }

            BannerType.COLLAPSIBLE_BOTTOM -> {
                bannerCollapsibleBottomRepository.loadBanner(
                    activity = activity,
                    adType = adType,
                    bannerId = bannerId,
                    isAdEnable = isRemoteEnable,
                    isAppPurchased = diComponent.isAppPurchased,
                    isInternetConnected = diComponent.isInternetConnected,
                    canRequestAdsConsent = diComponent.canRequestAdsConsent,
                    viewGroup = viewGroup,
                    listener = listener
                )
            }
        }
    }

    fun destroyBanner(adType: String) {
        var bannerType = BannerType.ADAPTIVE
        when (adType) {
            AdsType.BANNER_ONE -> bannerType = BannerType.ADAPTIVE
            AdsType.BANNER_TWO -> bannerType = BannerType.MEDIUM_RECTANGLE
            AdsType.BANNER_THREE -> bannerType = BannerType.COLLAPSIBLE_BOTTOM
            AdsType.BANNER_FOUR -> bannerType = BannerType.COLLAPSIBLE_TOP
        }

        when (bannerType) {
            BannerType.ADAPTIVE -> onDestroy(adType)
            BannerType.MEDIUM_RECTANGLE -> bannerMediumRepository.onDestroy(adType)
            BannerType.COLLAPSIBLE_TOP -> bannerCollapsibleTopRepository.onDestroy(adType)
            BannerType.COLLAPSIBLE_BOTTOM -> bannerCollapsibleBottomRepository.onDestroy(adType)
        }
    }

    private fun Activity?.getResString(@StringRes resId: Int): String {
        return this?.resources?.getString(resId) ?: ""
    }
}