package com.hypersoft.admobadsbeta.ads.banners.repository

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.hypersoft.admobadsbeta.R
import com.hypersoft.admobadsbeta.ads.banners.models.BannerResponse

/**
 * @Author: SOHAIB AHMED
 * @Date: 07/03/2024
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

class BannerAds {

    private lateinit var mContext: Context
    private var mAdView: AdView? = null
    private var usingAdView: AdView? = null
    private var currentAdType: String = ""
    private var isAdLoading = false


    private var requestList: MutableList<BannerResponse> = mutableListOf()
    private val removeList: MutableList<BannerResponse> = mutableListOf()
    private val deleteList: MutableList<BannerResponse> = mutableListOf()

    fun loadBannerAd(context: Context, adType: String, adContainer: ViewGroup) {
        this.mContext = context
        this.currentAdType = adType

        val shouldAdd = removeList.indexOfFirst { it.adType == adType }

        // ReShowAd
        if (shouldAdd != -1) {
            Log.v("Magic", "$adType -> loadBannerAd: Reshowing Ad: ${adContainer.hashCode()}")
            removeList.find { it.adType == adType }?.apply {
                usingAdView = adView
                viewGroup = adContainer
                viewGroup.addCleanView(adView)
            }
            return
        }

        Log.d("Magic", "$adType -> loadBannerAd: called: ${adContainer.hashCode()}")
        val existingBannerResponse = requestList.find { it.adType == adType }
        val adView = existingBannerResponse?.adView
        requestList.remove(existingBannerResponse)
        existingBannerResponse?.let { deleteList.add(it) }


        if (adView == null) {
            // load ad for new Item
            val bannerResponse = BannerResponse(adType = adType, adView = null, viewGroup = adContainer)
            requestList.add(bannerResponse)


            // check if already loading
            if (!isAdLoading && mAdView == null) {

                // make a new call to load a ad
                loadAd(context)
            } else {

                // check, maybe a preloaded is available
                mAdView?.let { ad ->
                    bannerResponse.adView = ad
                    showAd(bannerResponse)
                }
            }
        } else {
            val bannerResponse = BannerResponse(adType = adType, adView = adView, viewGroup = adContainer)
            requestList.add(bannerResponse)
            showAd(bannerResponse)
        }
    }


    var counter = 0

    private fun loadAd(context: Context) {
        isAdLoading = true
        val adView = AdView(context)
        if (counter == 0) {
            adView.adUnitId = context.getString(R.string.admob_banner_id_2)
            counter++
        } else {
            adView.adUnitId = context.getString(R.string.admob_banner_id)
        }
        //adView.adUnitId = context.getString(R.string.admob_banner_id)
        adView.setAdSize(AdSize.BANNER)
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.i("Magic", "onAdLoaded: called")
                mAdView = adView
                requestList.lastOrNull()?.let {
                    it.adView = adView
                    showAd(it)
                }
                isAdLoading = false
                checkIfThereIsAnyQuwara()
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                Log.e("Magic", "onAdFailedToLoad: ${p0.message}")
                mAdView = null
                isAdLoading = false
            }

            override fun onAdImpression() {
                super.onAdImpression()
                Log.i("Magic", "onAdImpression: called")
                mAdView = null
                checkIfThereIsAnyQuwara()
            }
        }
        adView.loadAd(AdRequest.Builder().build())
    }


    private fun showAd(bannerResponse: BannerResponse) {
        bannerResponse.viewGroup.addCleanView(bannerResponse.adView)
        removeList.add(requestList.removeLast())
    }

    private fun checkIfThereIsAnyQuwara() {
        val bannerResponse = requestList.lastOrNull()
        bannerResponse?.let {
            // No need to load ad, if adType is same on top.
            if (currentAdType == it.adType) return
            loadBannerAd(mContext, adType = it.adType, adContainer = it.viewGroup)
        }
    }

    private fun ViewGroup.addCleanView(view: View?) {
        (view?.parent as? ViewGroup)?.removeView(view)
        this.removeAllViews()
        view?.let { this.addView(it) }
    }

    fun onDestroy(adType: String) {
        removeList.find { it.adType == adType }?.let { node ->
            if (usingAdView == node.adView) {
                usingAdView = null
                return
            }
            Log.d("Magic", "$adType -> onDestroy: called")

            node.adView?.destroy()
            node.viewGroup.removeAllViews()
            removeList.remove(node)
        }
        requestList.find { it.adType == adType }?.let { node ->
            val existingResponse = deleteList.find { it.adType == adType }
            if (existingResponse != null) {
                deleteList.remove(existingResponse)
                return
            }
            Log.d("Magic", "$adType -> onDestroy: called")

            node.adView?.destroy()
            node.viewGroup.removeAllViews()
            requestList.remove(node)
        }
    }
}