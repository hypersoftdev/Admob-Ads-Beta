package com.hypersoft.admobadsbeta.ads.rewarded.repository

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import com.hypersoft.admobadsbeta.ads.rewarded.callbacks.RewardedOnLoadCallBack
import com.hypersoft.admobadsbeta.ads.rewarded.callbacks.RewardedOnShowCallBack

/**
 * @Author: SOHAIB AHMED
 * @Date: 05/03/2024
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

/**
 * Abstract class for managing rewarded interstitial ads.
 */
abstract class RewardedInterRepository {

    // Rewarded interstitial ad instance
    private var mRewardedInterstitialAd: RewardedInterstitialAd? = null
    // Flag to track whether a rewarded interstitial ad is currently loading
    private var isRewardedInterLoading = false

    /**
     * Loads a rewarded interstitial ad.
     *
     * @param context The context.
     * @param adType The type of the rewarded interstitial ad.
     * @param rewardedInterId The ID of the rewarded interstitial ad.
     * @param adEnable Whether the ad is enabled.
     * @param isAppPurchased Whether the app is purchased.
     * @param isInternetConnected Whether the internet is connected.
     * @param canRequestAdsConsent Whether consent can be requested for ad calls.
     * @param listener Callback to handle load events.
     */
    protected fun loadRewardedInter(
        context: Context?,
        adType: String,
        rewardedInterId: String,
        adEnable: Boolean,
        isAppPurchased: Boolean,
        isInternetConnected: Boolean,
        canRequestAdsConsent: Boolean,
        listener: RewardedOnLoadCallBack?,
    ) {

        // Check if the rewarded interstitial ad is already loaded
        if (isRewardedInterLoaded()) {
            Log.i("AdsInformation", "$adType -> loadRewardedInter: Already loaded")
            listener?.onResponse(true)
            return
        }

        // Check if a rewarded interstitial ad is currently loading
        if (isRewardedInterLoading) {
            Log.d("AdsInformation", "$adType -> loadRewardedInter: Ad is already loading...")
            // No need to invoke callback, in some cases (e.g. activity recreation) it interrupts our response, as we are waiting for response in Splash
            // listener?.onResponse(false)  // Uncomment if u still need to listen this case
            return
        }

        // Check if the user is a premium user
        if (isAppPurchased) {
            Log.e("AdsInformation", "$adType -> loadRewardedInter: Premium user")
            listener?.onResponse(false)
            return
        }

        // Check if the remote config for the ad is disabled
        if (adEnable.not()) {
            Log.e("AdsInformation", "$adType -> loadRewardedInter: Remote config is off")
            listener?.onResponse(false)
            return
        }

        // Check if the internet is not connected
        if (isInternetConnected.not()) {
            Log.e("AdsInformation", "$adType -> loadRewardedInter: Internet is not connected")
            listener?.onResponse(false)
            return
        }

        // Check if consent is not permitted for ad calls
        if (canRequestAdsConsent.not()) {
            Log.e("AdsInformation", "$adType -> loadBanner: Consent not permitted for ad calls")
            listener?.onResponse(false)
            return
        }

        // Check if the context is null
        if (context == null) {
            Log.e("AdsInformation", "$adType -> loadRewardedInter: Context is null")
            listener?.onResponse(false)
            return
        }

        // Check if the rewarded interstitial ad ID is empty
        if (rewardedInterId.trim().isEmpty()) {
            Log.e("AdsInformation", "$adType -> loadRewardedInter: Ad id is empty")
            listener?.onResponse(false)
            return
        }

        Log.d("AdsInformation", "$adType -> loadRewardedInter: Requesting admob server for ad...")
        isRewardedInterLoading = true

        // Load the rewarded interstitial ad
        RewardedInterstitialAd.load(
            context,
            rewardedInterId.trim(),
            AdRequest.Builder().build(),
            object : RewardedInterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e("AdsInformation", "$adType -> loadRewardedInter: onAdFailedToLoad: ${adError.message}")
                    isRewardedInterLoading = false
                    mRewardedInterstitialAd = null
                    listener?.onResponse(false)
                }

                override fun onAdLoaded(rewardedInterstitialAd: RewardedInterstitialAd) {
                    Log.i("AdsInformation", "$adType -> loadRewardedInter: onAdLoaded")
                    isRewardedInterLoading = false
                    mRewardedInterstitialAd = rewardedInterstitialAd
                    listener?.onResponse(true)
                }
            })
    }

    /**
     * Shows a rewarded interstitial ad.
     *
     * @param activity The activity.
     * @param adType The type of the rewarded interstitial ad.
     * @param isAppPurchased Whether the app is purchased.
     * @param listener Callback to handle show events.
     */
    protected fun showRewardedInter(
        activity: Activity?,
        adType: String,
        isAppPurchased: Boolean,
        listener: RewardedOnShowCallBack?
    ) {
        // Check if the rewarded interstitial ad is not loaded
        if (isRewardedInterLoaded().not()) {
            Log.e("AdsInformation", "$adType -> showRewardedInter: RewardedInter is not loaded yet")
            listener?.onAdFailedToShow()
            return
        }

        // Check if the user is a premium user
        if (isAppPurchased) {
            Log.e("AdsInformation", "$adType -> showRewardedInter: Premium user")
            if (isRewardedInterLoaded()) {
                Log.d("AdsInformation", "$adType -> Destroying loaded RewardedInter ad due to Premium user")
                mRewardedInterstitialAd = null
            }
            listener?.onAdFailedToShow()
            return
        }

        // Check if the activity reference is null
        if (activity == null) {
            Log.e("AdsInformation", "$adType -> showRewardedInter: activity reference is null")
            listener?.onAdFailedToShow()
            return
        }

        // Check if the activity is finishing or destroyed
        if (activity.isFinishing || activity.isDestroyed) {
            Log.e("AdsInformation", "$adType -> showRewardedInter: activity is finishing or destroyed")
            listener?.onAdFailedToShow()
            return
        }

        // Set full screen content callback for the rewarded interstitial ad
        mRewardedInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d("AdsInformation", "admob RewardedInter onAdDismissedFullScreenContent")
                listener?.onAdDismissedFullScreenContent()
                mRewardedInterstitialAd = null
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e("AdsInformation", "admob RewardedInter onAdFailedToShowFullScreenContent: ${adError.message}")
                listener?.onAdFailedToShow()
                mRewardedInterstitialAd = null
            }

            override fun onAdShowedFullScreenContent() {
                Log.d("AdsInformation", "admob RewardedInter onAdShowedFullScreenContent")
                listener?.onAdShowedFullScreenContent()
                mRewardedInterstitialAd = null
            }

            override fun onAdImpression() {
                Log.d("AdsInformation", "admob RewardedInter onAdImpression")
                listener?.onAdImpression()
            }
        }

        Log.d("AdsInformation", "$adType -> RewardedInter: showing ad")
        mRewardedInterstitialAd?.show(activity) {
            Log.d("AdsInformation", "admob RewardedInter onUserEarnedReward")
            listener?.onUserEarnedReward()
        }
    }

    /**
     * Checks if a rewarded interstitial ad is loaded.
     *
     * @return True if a rewarded interstitial ad is loaded, otherwise false.
     */
   protected fun isRewardedInterLoaded(): Boolean {
        return mRewardedInterstitialAd != null
    }
}