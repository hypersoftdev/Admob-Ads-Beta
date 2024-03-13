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

abstract class RewardedInterRepository {

    private var mRewardedInterstitialAd: RewardedInterstitialAd? = null
    private var isRewardedInterLoading = false

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

        if (isRewardedInterLoaded()) {
            Log.i("AdsInformation", "$adType -> loadRewardedInter: Already loaded")
            listener?.onResponse(true)
            return
        }

        if (isRewardedInterLoading) {
            Log.d("AdsInformation", "$adType -> loadRewardedInter: Ad is already loading...")
            // No need to invoke callback, in some cases (e.g. activity recreation) it interrupts our response, as we are waiting for response in Splash
            // listener?.onResponse(false)  // Uncomment if u still need to listen this case
            return
        }

        if (isAppPurchased) {
            Log.e("AdsInformation", "$adType -> loadRewardedInter: Premium user")
            listener?.onResponse(false)
            return
        }

        if (adEnable.not()) {
            Log.e("AdsInformation", "$adType -> loadRewardedInter: Remote config is off")
            listener?.onResponse(false)
            return
        }

        if (isInternetConnected.not()) {
            Log.e("AdsInformation", "$adType -> loadRewardedInter: Internet is not connected")
            listener?.onResponse(false)
            return
        }

        if (canRequestAdsConsent.not()) {
            Log.e("AdsInformation", "$adType -> loadBanner: Consent not permitted for ad calls")
            listener?.onResponse(false)
            return
        }

        if (context == null) {
            Log.e("AdsInformation", "$adType -> loadRewardedInter: Context is null")
            listener?.onResponse(false)
            return
        }

        if (rewardedInterId.trim().isEmpty()) {
            Log.e("AdsInformation", "$adType -> loadRewardedInter: Ad id is empty")
            listener?.onResponse(false)
            return
        }

        Log.d("AdsInformation", "$adType -> loadRewardedInter: Requesting admob server for ad...")
        isRewardedInterLoading = true

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

    protected fun showRewardedInter(
        activity: Activity?,
        adType: String,
        isAppPurchased: Boolean,
        listener: RewardedOnShowCallBack?
    ) {

        if (isRewardedInterLoaded().not()) {
            Log.e("AdsInformation", "$adType -> showRewardedInter: RewardedInter is not loaded yet")
            listener?.onAdFailedToShow()
            return
        }

        if (isAppPurchased) {
            Log.e("AdsInformation", "$adType -> showRewardedInter: Premium user")
            if (isRewardedInterLoaded()) {
                Log.d("AdsInformation", "$adType -> Destroying loaded RewardedInter ad due to Premium user")
                mRewardedInterstitialAd = null
            }
            listener?.onAdFailedToShow()
            return
        }

        if (activity == null) {
            Log.e("AdsInformation", "$adType -> showRewardedInter: activity reference is null")
            listener?.onAdFailedToShow()
            return
        }

        if (activity.isFinishing || activity.isDestroyed) {
            Log.e("AdsInformation", "$adType -> showRewardedInter: activity is finishing or destroyed")
            listener?.onAdFailedToShow()
            return
        }

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

    fun isRewardedInterLoaded(): Boolean {
        return mRewardedInterstitialAd != null
    }
}