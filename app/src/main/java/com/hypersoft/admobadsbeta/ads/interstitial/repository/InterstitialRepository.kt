package com.hypersoft.admobadsbeta.ads.interstitial.repository

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.hypersoft.admobadsbeta.ads.interstitial.callbacks.InterstitialOnLoadCallBack
import com.hypersoft.admobadsbeta.ads.interstitial.callbacks.InterstitialOnShowCallBack

/**
 * @Author: SOHAIB AHMED
 * @Date: 05/03/2024
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

abstract class InterstitialRepository {

    private var mInterstitialAd: InterstitialAd? = null
    private var isInterLoading = false

    protected fun loadInterstitial(
        context: Context?,
        adType: String,
        interId: String,
        adEnable: Boolean,
        isAppPurchased: Boolean,
        isInternetConnected: Boolean,
        canRequestAdsConsent: Boolean,
        listener: InterstitialOnLoadCallBack?,
    ) {

        if (isInterstitialLoaded()) {
            Log.i("AdsInformation", "$adType -> loadInterstitial: Already loaded")
            listener?.onResponse()
            return
        }

        if (isInterLoading) {
            Log.d("AdsInformation", "$adType -> loadInterstitial: Ad is already loading...")
            // No need to invoke callback, in some cases (e.g. activity recreation) it interrupts our response, as we are waiting for response in Splash
            // listener?.onResponse()  // Uncomment if u still need to listen this case
            return
        }

        if (isAppPurchased) {
            Log.e("AdsInformation", "$adType -> loadInterstitial: Premium user")
            listener?.onResponse()
            return
        }

        if (adEnable.not()) {
            Log.e("AdsInformation", "$adType -> loadInterstitial: Remote config is off")
            listener?.onResponse()
            return
        }

        if (isInternetConnected.not()) {
            Log.e("AdsInformation", "$adType -> loadInterstitial: Internet is not connected")
            listener?.onResponse()
            return
        }

        if (canRequestAdsConsent.not()) {
            Log.e("AdsInformation", "$adType -> loadBanner: Consent not permitted for ad calls")
            listener?.onResponse()
            return
        }

        if (context == null) {
            Log.e("AdsInformation", "$adType -> loadInterstitial: Context is null")
            listener?.onResponse()
            return
        }

        if (interId.trim().isEmpty()) {
            Log.e("AdsInformation", "$adType -> loadInterstitial: Ad id is empty")
            listener?.onResponse()
            return
        }

        Log.d("AdsInformation", "$adType -> loadInterstitial: Requesting admob server for ad...")
        isInterLoading = true

        InterstitialAd.load(
            context,
            interId.trim(),
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e("AdsInformation", "$adType -> loadInterstitial: onAdFailedToLoad: ${adError.message}")
                    isInterLoading = false
                    mInterstitialAd = null
                    listener?.onResponse()
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.i("AdsInformation", "$adType -> loadInterstitial: onAdLoaded")
                    isInterLoading = false
                    mInterstitialAd = interstitialAd
                    listener?.onResponse()
                }
            })
    }

    protected fun showInterstitial(
        activity: Activity?,
        adType: String,
        isAppPurchased: Boolean,
        listener: InterstitialOnShowCallBack?
    ) {

        if (isInterstitialLoaded().not()) {
            Log.e("AdsInformation", "$adType -> showInterstitial: Interstitial is not loaded yet")
            listener?.onAdFailedToShow()
            return
        }

        if (isAppPurchased) {
            Log.e("AdsInformation", "$adType -> showInterstitial: Premium user")
            if (isInterstitialLoaded()) {
                Log.d("AdsInformation", "$adType -> Destroying loaded inter ad due to Premium user")
                mInterstitialAd = null
            }
            listener?.onAdFailedToShow()
            return
        }

        if (activity == null) {
            Log.e("AdsInformation", "$adType -> showInterstitial: activity reference is null")
            listener?.onAdFailedToShow()
            return
        }

        if (activity.isFinishing || activity.isDestroyed) {
            Log.e("AdsInformation", "$adType -> showInterstitial: activity is finishing or destroyed")
            listener?.onAdFailedToShow()
            return
        }

        Log.d("AdsInformation", "$adType -> showInterstitial: showing ad")

        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d("AdsInformation", "admob Interstitial onAdDismissedFullScreenContent")
                listener?.onAdDismissedFullScreenContent()
                mInterstitialAd = null
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e("AdsInformation", "admob Interstitial onAdFailedToShowFullScreenContent: ${adError.message}")
                listener?.onAdFailedToShow()
                mInterstitialAd = null
            }

            override fun onAdShowedFullScreenContent() {
                Log.d("AdsInformation", "admob Interstitial onAdShowedFullScreenContent")
                listener?.onAdShowedFullScreenContent()
                mInterstitialAd = null
            }

            override fun onAdImpression() {
                Log.d("AdsInformation", "admob Interstitial onAdImpression")
                listener?.onAdImpression()
                Handler(Looper.getMainLooper()).postDelayed({ listener?.onAdImpressionDelayed() }, 300)
            }
        }
        mInterstitialAd?.show(activity)
    }

    fun isInterstitialLoaded(): Boolean {
        return mInterstitialAd != null
    }
}