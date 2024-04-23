package com.hypersoft.admobadsbeta.ads.rewarded.repository

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
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
 * Abstract class for managing rewarded ads.
 */
abstract class RewardedRepository {

    // Rewarded ad instance
    private var mRewardedAd: RewardedAd? = null

    // Flag to track whether a rewarded ad is currently loading
    private var isRewardedLoading = false

    /**
     * Loads a rewarded ad.
     *
     * @param context The context.
     * @param adType The type of the rewarded ad.
     * @param rewardedId The ID of the rewarded ad.
     * @param adEnable Whether the ad is enabled.
     * @param isAppPurchased Whether the app is purchased.
     * @param isInternetConnected Whether the internet is connected.
     * @param canRequestAdsConsent Whether consent can be requested for ad calls.
     * @param listener Callback to handle load events.
     */
    protected fun loadRewarded(
        context: Context?,
        adType: String,
        rewardedId: String,
        adEnable: Boolean,
        isAppPurchased: Boolean,
        isInternetConnected: Boolean,
        canRequestAdsConsent: Boolean,
        listener: RewardedOnLoadCallBack?,
    ) {

        // Check if the rewarded ad is already loaded
        if (isRewardedLoaded()) {
            Log.i("AdsInformation", "$adType -> loadRewarded: Already loaded")
            listener?.onResponse(true)
            return
        }

        // Check if a rewarded ad is currently loading
        if (isRewardedLoading) {
            Log.d("AdsInformation", "$adType -> loadRewarded: Ad is already loading...")
            // No need to invoke callback, in some cases (e.g. activity recreation) it interrupts our response, as we are waiting for response in Splash
            // listener?.onResponse(false)  // Uncomment if u still need to listen this case
            return
        }

        // Check if the user is a premium user
        if (isAppPurchased) {
            Log.e("AdsInformation", "$adType -> loadRewarded: Premium user")
            listener?.onResponse(false)
            return
        }

        // Check if the remote config for the ad is disabled
        if (adEnable.not()) {
            Log.e("AdsInformation", "$adType -> loadRewarded: Remote config is off")
            listener?.onResponse(false)
            return
        }

        // Check if the internet is not connected
        if (isInternetConnected.not()) {
            Log.e("AdsInformation", "$adType -> loadRewarded: Internet is not connected")
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
            Log.e("AdsInformation", "$adType -> loadRewarded: Context is null")
            listener?.onResponse(false)
            return
        }

        // Check if the rewarded ad ID is empty
        if (rewardedId.trim().isEmpty()) {
            Log.e("AdsInformation", "$adType -> loadRewarded: Ad id is empty")
            listener?.onResponse(false)
            return
        }

        Log.d("AdsInformation", "$adType -> loadRewarded: Requesting admob server for ad...")
        isRewardedLoading = true

        // Load the rewarded ad
        RewardedAd.load(
            context,
            rewardedId.trim(),
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e("AdsInformation", "$adType -> loadRewarded: onAdFailedToLoad: ${adError.message}")
                    isRewardedLoading = false
                    mRewardedAd = null
                    listener?.onResponse(false)
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    Log.i("AdsInformation", "$adType -> loadRewarded: onAdLoaded")
                    isRewardedLoading = false
                    mRewardedAd = rewardedAd
                    listener?.onResponse(true)
                }
            })
    }

    /**
     * Shows a rewarded ad.
     *
     * @param activity The activity.
     * @param adType The type of the rewarded ad.
     * @param isAppPurchased Whether the app is purchased.
     * @param listener Callback to handle show events.
     */
    protected fun showRewarded(
        activity: Activity?,
        adType: String,
        isAppPurchased: Boolean,
        listener: RewardedOnShowCallBack?
    ) {

        // Check if the rewarded ad is not loaded
        if (isRewardedLoaded().not()) {
            Log.e("AdsInformation", "$adType -> showRewarded: Rewarded is not loaded yet")
            listener?.onAdFailedToShow()
            return
        }

        // Check if the user is a premium user
        if (isAppPurchased) {
            Log.e("AdsInformation", "$adType -> showRewarded: Premium user")
            if (isRewardedLoaded()) {
                Log.d("AdsInformation", "$adType -> Destroying loaded rewarded ad due to Premium user")
                mRewardedAd = null
            }
            listener?.onAdFailedToShow()
            return
        }

        // Check if the activity reference is null
        if (activity == null) {
            Log.e("AdsInformation", "$adType -> showRewarded: activity reference is null")
            listener?.onAdFailedToShow()
            return
        }

        // Check if the activity is finishing or destroyed
        if (activity.isFinishing || activity.isDestroyed) {
            Log.e("AdsInformation", "$adType -> showRewarded: activity is finishing or destroyed")
            listener?.onAdFailedToShow()
            return
        }

        // Set full screen content callback for the rewarded ad
        mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d("AdsInformation", "admob Rewarded onAdDismissedFullScreenContent")
                listener?.onAdDismissedFullScreenContent()
                mRewardedAd = null
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e("AdsInformation", "admob Rewarded onAdFailedToShowFullScreenContent: ${adError.message}")
                listener?.onAdFailedToShow()
                mRewardedAd = null
            }

            override fun onAdShowedFullScreenContent() {
                Log.d("AdsInformation", "admob Rewarded onAdShowedFullScreenContent")
                listener?.onAdShowedFullScreenContent()
                mRewardedAd = null
            }

            override fun onAdImpression() {
                Log.d("AdsInformation", "admob Rewarded onAdImpression")
                listener?.onAdImpression()
            }
        }

        Log.d("AdsInformation", "$adType -> Rewarded: showing ad")
        mRewardedAd?.show(activity) {
            Log.d("AdsInformation", "admob Rewarded onUserEarnedReward")
            listener?.onUserEarnedReward()
        }
    }

    /**
     * Checks if a rewarded ad is loaded.
     *
     * @return True if a rewarded ad is loaded, otherwise false.
     */
    protected fun isRewardedLoaded(): Boolean {
        return mRewardedAd != null
    }
}