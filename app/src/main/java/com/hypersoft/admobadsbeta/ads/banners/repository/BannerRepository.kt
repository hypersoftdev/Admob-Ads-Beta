package com.hypersoft.admobadsbeta.ads.banners.repository

import android.app.Activity
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.content.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.hypersoft.admobadsbeta.ads.banners.callbacks.BannerOnLoadCallBack
import com.hypersoft.admobadsbeta.ads.banners.enums.BannerType
import com.hypersoft.admobadsbeta.ads.banners.models.BannerResponse

/**
 * @Author: SOHAIB AHMED
 * @Date: 05/03/2024
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

abstract class BannerRepository {

    private var mAdview: AdView? = null
    private var isBannerLoading = false
    private var mAdType = ""

    private val _bannerObserver = MutableLiveData<BannerResponse?>()
    val bannerObserver: LiveData<BannerResponse?> get() = _bannerObserver

    protected fun loadBanner(
        activity: Activity?,
        adType: String,
        bannerType: BannerType,
        bannerId: String,
        adEnable: Boolean,
        isAppPurchased: Boolean,
        isInternetConnected: Boolean,
        listener: BannerOnLoadCallBack?,
    ) {
        this.mAdType = adType
        hashmap.putIfAbsent(adType, BannerResponse(adType = adType, adView = null, loadState = -1, viewGroup = FrameLayout(activity!!)))

        hashmap[adType]?.let { bannerResponse ->
            if (bannerResponse.loadState == 1) {
                // already available
                //_bannerObserver.postValue(bannerResponse)
                return
            }
        }

        /*if (isBannerLoaded()) {
            Log.i("AdsInformation", "$adType -> loadBanner: Already loaded")
            listener?.onPreloaded()
            return
        }*/

        if (isBannerLoading) {
            Log.d("AdsInformation", "$adType -> loadBanner: Ad is already loading...")
            // No need to invoke callback, in some cases (e.g. activity recreation) it interrupts our response, as we are waiting for response in Splash
            // listener?.onResponse()  // Uncomment if u still need to listen this case
            return
        }

        if (isAppPurchased) {
            Log.e("AdsInformation", "$adType -> loadBanner: Premium user")
            listener?.onResponse(false)
            hashmap[adType]?.loadState = 0
            _bannerObserver.postValue(hashmap[adType])
            return
        }

        if (adEnable.not()) {
            Log.e("AdsInformation", "$adType -> loadBanner: Remote config is off")
            listener?.onResponse(false)
            hashmap[adType]?.loadState = 0
            _bannerObserver.postValue(hashmap[adType])
            return
        }

        if (isInternetConnected.not()) {
            Log.e("AdsInformation", "$adType -> loadBanner: Internet is not connected")
            listener?.onResponse(false)
            hashmap[adType]?.loadState = 0
            _bannerObserver.postValue(hashmap[adType])
            return
        }

        if (activity == null) {
            Log.e("AdsInformation", "$adType -> loadBanner: Context is null")
            listener?.onResponse(false)
            hashmap[adType]?.loadState = 0
            _bannerObserver.postValue(hashmap[adType])
            return
        }

        if (activity.isFinishing || activity.isDestroyed) {
            Log.e("AdsInformation", "$adType -> loadBanner: activity is finishing or destroyed")
            listener?.onResponse(false)
            hashmap[adType]?.loadState = 0
            _bannerObserver.postValue(hashmap[adType])
            return
        }

        if (bannerId.trim().isEmpty()) {
            Log.e("AdsInformation", "$adType -> loadBanner: Ad id is empty")
            listener?.onResponse(false)
            hashmap[adType]?.loadState = 0
            _bannerObserver.postValue(hashmap[adType])
            return
        }

        Log.d("AdsInformation", "$adType -> loadBanner: Requesting admob server for ad...")
        isBannerLoading = true
        hashmap[adType]?.loadState = 2

        val adRequest = AdRequest.Builder()

        val adSize = when (bannerType) {
            BannerType.ADAPTIVE -> getAdSize(activity) ?: AdSize.BANNER

            BannerType.COLLAPSIBLE_TOP -> {
                adRequest.addNetworkExtrasBundle(AdMobAdapter::class.java, Bundle().apply {
                    putString("collapsible", "top")
                })
                getAdSize(activity) ?: AdSize.BANNER
            }

            BannerType.COLLAPSIBLE_BOTTOM -> {
                adRequest.addNetworkExtrasBundle(AdMobAdapter::class.java, Bundle().apply {
                    putString("collapsible", "bottom")
                })
                getAdSize(activity) ?: AdSize.BANNER
            }
        }

        val adView = AdView(activity).apply {
            adUnitId = bannerId
            setAdSize(adSize)
        }

        adView.adListener = getListener(adType, adView, listener)
        adView.loadAd(adRequest.build())
    }

    private val hashmap = HashMap<String, BannerResponse>()

    private fun getListener(adType: String, adView: AdView, listener: BannerOnLoadCallBack?): AdListener {
        return object : AdListener() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                super.onAdFailedToLoad(adError)
                Log.e("AdsInformation", "$adType -> loadBanner: onAdFailedToLoad: ${adError.message}")
                isBannerLoading = false
                mAdview = null
                listener?.onResponse(false)
                hashmap[adType]?.loadState = 0
                _bannerObserver.postValue(hashmap[adType])
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.i("AdsInformation", "$adType -> loadBanner: onAdLoaded")
                isBannerLoading = false
                mAdview = adView
                listener?.onResponse(true)

                if (adType == mAdType) {
                    hashmap[adType]?.loadState = 1
                    hashmap[adType]?.adView = adView
                    _bannerObserver.postValue(hashmap[adType])
                } else {
                    hashmap[adType]?.loadState = -1
                    hashmap[adType]?.adView = null

                    hashmap[mAdType]?.loadState = 1
                    hashmap[mAdType]?.adView = adView
                    _bannerObserver.postValue(hashmap[mAdType])
                }
            }

            override fun onAdImpression() {
                super.onAdImpression()
                Log.d("AdsInformation", "$adType -> loadBanner: onAdImpression")
                // _bannerObserver.postValue(null)

                hashmap[mAdType]?.loadState = 3
                _bannerObserver.postValue(hashmap[mAdType])
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

    protected fun showBanner(
        adType: String,
        adView: AdView?,
        viewGroup: ViewGroup,
        isAppPurchased: Boolean
    ) {

        /*if (adView == null) {
            Log.e("AdsInformation", "$adType -> showBanner: Banner is not loaded yet")
            listener?.onAdFailedToShow()
            return
        }*/

        if (isAppPurchased) {
            Log.e("AdsInformation", "$adType -> showBanner: Premium user")
            viewGroup.removeAllViews()
            viewGroup.visibility = View.GONE

            /*if (isBannerLoaded()) {
                Log.d("AdsInformation", "$adType -> Destroying loaded banner ad due to Premium user")
                mAdview = null
            }
            listener?.onAdFailedToShow()*/
            return
        }

        if (adView == null) {
            Log.e("AdsInformation", "$adType -> showBanner: Banner failed to show, ad is null")
            viewGroup.removeAllViews()
            viewGroup.visibility = View.GONE
        } else {
            Log.e("AdsInformation", "$adType -> showBanner: Banner showing admob server ad")
            viewGroup.addCleanView(adView)
            viewGroup.visibility = View.VISIBLE
        }


        /*mAdview?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d("AdsInformation", "admob Banner onAdDismissedFullScreenContent")
                listener?.onAdDismissedFullScreenContent()
                mAdview = null
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e("AdsInformation", "admob Banner onAdFailedToShowFullScreenContent: ${adError.message}")
                listener?.onAdFailedToShow()
                mAdview = null
            }

            override fun onAdShowedFullScreenContent() {
                Log.d("AdsInformation", "admob Banner onAdShowedFullScreenContent")
                listener?.onAdShowedFullScreenContent()
                mAdview = null
            }

            override fun onAdImpression() {
                Log.d("AdsInformation", "admob Banner onAdImpression")
                listener?.onAdImpression()
                Handler(Looper.getMainLooper()).postDelayed({ listener?.onAdImpressionDelayed() }, 300)
            }
        }
        mAdview?.show(activity)*/
    }

    @Suppress("DEPRECATION")
    private fun getAdSize(activity: Activity): AdSize? {
        val density = activity.resources.displayMetrics.density

        val adWidthPixels = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowManager = activity.getSystemService<WindowManager>()
            val bounds = windowManager?.currentWindowMetrics?.bounds
            bounds?.width()?.toFloat()
        } else {
            val display: Display? = activity.getSystemService<DisplayManager>()?.getDisplay(Display.DEFAULT_DISPLAY)
            val outMetrics = DisplayMetrics()
            display?.getMetrics(outMetrics)
            outMetrics.widthPixels.toFloat()
        }
        if (adWidthPixels == null) {
            return null
        }
        val adWidth = (adWidthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
    }

    private fun ViewGroup.addCleanView(view: View?) {
        (view?.parent as? ViewGroup)?.removeView(view)
        this.removeAllViews()
        view?.let { this.addView(it) }
    }

    fun isBannerLoaded(): Boolean {
        return mAdview != null
    }
}