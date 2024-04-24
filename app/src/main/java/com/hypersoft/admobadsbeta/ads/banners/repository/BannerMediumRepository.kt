package com.hypersoft.admobadsbeta.ads.banners.repository

import android.app.Activity
import android.hardware.display.DisplayManager
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.getSystemService
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.hypersoft.admobadsbeta.ads.banners.callbacks.BannerCallBack
import com.hypersoft.admobadsbeta.ads.banners.models.BannerResponse

/**
 * @Author: SOHAIB AHMED
 * @Date: 05/03/2024
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

class BannerMediumRepository {

    private var mActivity: Activity? = null
    private var mAdType: String = ""
    private var mBannerId: String = ""
    private var isAdEnable = true
    private var isAppPurchased = false
    private var isInternetConnected = false
    private var canRequestAdsConsent = false
    private var listener: BannerCallBack? = null

    private var mAdView: AdView? = null
    private var usingAdView: AdView? = null
    private var isBannerLoading = false

    private var requestList: MutableList<BannerResponse> = mutableListOf()
    private val impressionList: MutableList<BannerResponse> = mutableListOf()
    private val deleteList: MutableList<BannerResponse> = mutableListOf()

    /**
     * Loads a banner ad based on the provided parameters.
     *
     * @param activity The activity context.
     * @param adType The type of the ad.
     * @param bannerId The ID of the banner ad.
     * @param isAdEnable Indicates if the ad is enabled.
     * @param isAppPurchased Indicates if the app is purchased.
     * @param isInternetConnected Indicates if the device is connected to the internet.
     * @param canRequestAdsConsent Indicates if consent for ad requests is granted.
     * @param viewGroup The view group in which the ad will be displayed.
     * @param listener The banner ad callback listener.
     */
    fun loadBanner(
        activity: Activity?,
        adType: String,
        bannerId: String,
        isAdEnable: Boolean,
        isAppPurchased: Boolean,
        isInternetConnected: Boolean,
        canRequestAdsConsent: Boolean,
        viewGroup: ViewGroup?,
        listener: BannerCallBack?,
    ) {
        this.mActivity = activity
        this.mAdType = adType
        this.mBannerId = bannerId
        this.isAdEnable = isAdEnable
        this.isAppPurchased = isAppPurchased
        this.isInternetConnected = isInternetConnected
        this.canRequestAdsConsent = canRequestAdsConsent
        this.listener = listener

        // Check if app is purchased
        if (isAppPurchased) {
            Log.e("AdsInformation", "$adType -> loadBanner: Premium user")
            listener?.onResponse(false)
            return
        }

        // Check if ads are enabled
        if (isAdEnable.not()) {
            Log.e("AdsInformation", "$adType -> loadBanner: Remote config is off")
            listener?.onResponse(false)
            return
        }

        // Check internet connection
        if (isInternetConnected.not()) {
            Log.e("AdsInformation", "$adType -> loadBanner: Internet is not connected")
            listener?.onResponse(false)
            return
        }

        // Check if consent is permitted for ad calls
        if (canRequestAdsConsent.not()) {
            Log.e("AdsInformation", "$adType -> loadBanner: Consent not permitted for ad calls")
            listener?.onResponse(false)
            return
        }

        // Check activity validity
        if (activity == null) {
            Log.e("AdsInformation", "$adType -> loadBanner: Context is null")
            listener?.onResponse(false)
            return
        }

        // Check if the activity is finishing or destroyed
        if (activity.isFinishing || activity.isDestroyed) {
            Log.e("AdsInformation", "$adType -> loadBanner: activity is finishing or destroyed")
            listener?.onResponse(false)
            return
        }

        // Check if nativeId is empty
        if (bannerId.trim().isEmpty()) {
            Log.e("AdsInformation", "$adType -> loadBanner: Ad id is empty")
            listener?.onResponse(false)
            return
        }

        val shouldAdd = impressionList.indexOfFirst { it.adType == adType }

        // ReShowAd
        if (shouldAdd != -1) {
            Log.d("AdsInformation", "$adType -> loadBanner: Reshowing Ad")
            impressionList.find { it.adType == adType }?.let {
                usingAdView = it.adView
                it.viewGroup = viewGroup
                viewGroup?.addCleanView(it.adView)
            }
            return
        }

        val existingBannerResponse = requestList.find { it.adType == adType }
        val adView = existingBannerResponse?.adView
        requestList.remove(existingBannerResponse)
        existingBannerResponse?.let { deleteList.add(it) }

        if (adView == null) {
            // load ad for new Item
            val bannerResponse = BannerResponse(adType = adType, adView = null, isAdEnable = isAdEnable, viewGroup = viewGroup)
            requestList.add(bannerResponse)

            // check if already loading
            if (!isBannerLoading && mAdView == null) {
                Log.d("AdsInformation", "$adType -> loadBanner: Requesting admob server for ad...")

                // make a new call to load a ad
                viewGroup?.visibility = View.VISIBLE
                loadAd(activity, bannerId, adType, listener)
            } else {

                // check, maybe a preloaded is available
                mAdView?.let { ad ->
                    bannerResponse.adView = ad
                    showBanner(bannerResponse)
                }
            }
        } else {
            val bannerResponse = BannerResponse(adType = adType, adView = adView, isAdEnable = isAdEnable, viewGroup = viewGroup)
            requestList.add(bannerResponse)
            showBanner(bannerResponse)
        }
    }

    // Load Medium Banner Ad
    private fun loadAd(activity: Activity, bannerId: String, adType: String, listener: BannerCallBack?) {
        isBannerLoading = true

        val adRequest = AdRequest.Builder().build()
        val adSize = AdSize.MEDIUM_RECTANGLE
        val adView = AdView(activity).apply {
            adUnitId = bannerId
            setAdSize(adSize)
        }

        adView.adListener = getListener(adType, adView, listener)
        adView.loadAd(adRequest)
    }

    // Callback for ad loading events
    private fun getListener(adType: String, adView: AdView, listener: BannerCallBack?): AdListener {
        return object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.i("AdsInformation", "$adType -> loadBanner: onAdLoaded")

                mAdView = adView
                requestList.lastOrNull()?.let {
                    it.adView = adView
                    showBanner(it)
                }
                isBannerLoading = false
                listener?.onResponse(true)
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                super.onAdFailedToLoad(adError)
                Log.e("AdsInformation", "$adType -> loadBanner: onAdFailedToLoad: ${adError.message}")
                mAdView = null
                isBannerLoading = false
                checkIfThereIsAnymoreToLoad()
                listener?.onResponse(false)
            }

            override fun onAdImpression() {
                super.onAdImpression()
                Log.d("AdsInformation", "$adType -> loadBanner: onAdImpression")
                mAdView = null
                checkIfThereIsAnymoreToLoad()
            }

            override fun onAdOpened() {
                super.onAdOpened()
                Log.d("AdsInformation", "$adType -> loadBanner: onAdOpened")
                listener?.onAdOpened()
            }

            override fun onAdClosed() {
                super.onAdClosed()
                Log.d("AdsInformation", "$adType -> loadBanner: onAdClosed")
                listener?.onAdClosed()
            }
        }
    }

    // show medium banner ad
    protected fun showBanner(bannerResponse: BannerResponse) {
        if (isAppPurchased) {
            Log.e("AdsInformation", "${bannerResponse.adType} -> showBanner: Premium user")
            bannerResponse.viewGroup?.removeAllViews()
            bannerResponse.viewGroup?.visibility = View.GONE
            return
        }

        bannerResponse.viewGroup?.addCleanView(bannerResponse.adView)
        if (requestList.isNotEmpty()) {
            impressionList.add(requestList.removeLast())
        }
    }

    private fun checkIfThereIsAnymoreToLoad() {
        val bannerResponse = requestList.lastOrNull()
        bannerResponse?.let {
            // No need to load ad, if adType is same on top.
            if (mAdType == it.adType) return

            // loading ad for backstack
            loadBanner(
                activity = mActivity,
                adType = it.adType,
                bannerId = mBannerId,
                isAdEnable = isAdEnable,
                isAppPurchased = isAppPurchased,
                isInternetConnected = isInternetConnected,
                canRequestAdsConsent = canRequestAdsConsent,
                viewGroup = it.viewGroup,
                listener = listener
            )
        }
    }

    // Helper function to add a view to a view group and clean up existing views
    private fun ViewGroup.addCleanView(view: View?) {
        (view?.parent as? ViewGroup)?.removeView(view)
        this.removeAllViews()
        view?.let { this.addView(it) }
    }


    /**
     * Destroys the banner ad based on the provided ad type.
     *
     * @param adType The type of the ad.
     */
    fun onDestroy(adType: String) {
        impressionList.find { it.adType == adType }?.let { node ->
            if (usingAdView == node.adView) {
                usingAdView = null
                return
            }
            Log.d("AdsInformation", "$adType -> loadBanner: onDestroy")

            node.adView?.destroy()
            node.viewGroup?.removeAllViews()
            impressionList.remove(node)
        }
        requestList.find { it.adType == adType }?.let { node ->
            val existingResponse = deleteList.find { it.adType == adType }
            if (existingResponse != null) {
                deleteList.remove(existingResponse)
                return
            }
            Log.d("AdsInformation", "$adType -> loadBanner: onDestroy")

            node.adView?.destroy()
            node.viewGroup?.removeAllViews()
            requestList.remove(node)
        }
    }
}