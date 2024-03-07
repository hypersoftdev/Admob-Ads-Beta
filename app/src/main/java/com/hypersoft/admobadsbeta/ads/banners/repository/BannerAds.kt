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

    private var isAdLoading = false
    private var mAdView: AdView? = null

    private var list: MutableList<BannerResponse> = mutableListOf()
    private val removeList: MutableList<BannerResponse> = mutableListOf()
    private lateinit var mContext: Context

    fun loadBannerAd(context: Context, adType: String, adContainer: ViewGroup) {        // Home
        Log.d("Magic", "--------------------------------------------------------------------------------")
        Log.d("Magic", "$adType load, ListSize {${list.size}}-- $adContainer")
        this.mContext = context
        val shouldAdd = removeList.indexOfFirst { it.adType == adType }
        if (shouldAdd == -1) {
            Log.d("Magic", "if ****")
            val bannerResponse = list.find { it.adType == adType }
            if (bannerResponse == null) {
                list.add(BannerResponse(adType = adType, adView = null, viewGroup = adContainer))
            } else {
                bannerResponse.viewGroup = adContainer
            }

            if (isAdLoading.not() && mAdView == null) {
                Log.d("Magic", "loadBannerAd: ---------------------------------")
                Log.d("Magic", "loadBannerAd: called")
                loadAd(mContext, adType)
            } else {
                Log.d("Magic", "inner else ===")
                val bannerResponse = list.lastOrNull()
                bannerResponse?.let {
                    mAdView?.let { adView ->
                        Log.d("Magic", "inner else => assigning")
                        bannerResponse.adView = adView
                        showAd(bannerResponse.viewGroup, adView)
                    }
                }

                if (isAdLoading.not()) {
                    checkIfThereIsAnyQuwara()
                }
            }
        } else {
            Log.d("Magic", "else ****")
            val bannerResponse = removeList.find { it.adType == adType }
            bannerResponse?.let {
                it.viewGroup = adContainer
                it.viewGroup.addCleanView(it.adView)
            }
        }
    }

    private fun loadAd(context: Context, adType: String) {
        isAdLoading = true
        val adView = AdView(context)
        adView.adUnitId = context.getString(R.string.admob_banner_id)
        adView.setAdSize(AdSize.BANNER)
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.i("Magic", "onAdLoaded: called")

                mAdView = adView
                val bannerResponse = list.lastOrNull()
                bannerResponse?.let {
                    Log.d("Magic", "${it.adType} show -- ${it.viewGroup}")
                    it.adView = adView
                    showAd(it.viewGroup, adView)
                }
                isAdLoading = false
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                Log.e("Magic", "onAdFailedToLoad: ${p0.message}")
                mAdView = null
                isAdLoading = false
            }

            override fun onAdImpression() {
                super.onAdImpression()
                Log.w("Magic", "onAdImpression: called")
                mAdView = null
                //isAdLoading = false
                checkIfThereIsAnyQuwara()
            }
        }
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun showAd(viewGroup: ViewGroup?, adView: AdView?) {
        viewGroup?.addCleanView(adView)
        val removeLast = list.removeLast()
        removeList.add(removeLast)
        Log.d("Magic", "showAd: called")
        /*currentFrameLayout?.removeAllViews()
        val (fragmentName, adContainer) = adRequestQueue.lastOrNull() ?: return
        currentFrameLayout = adContainer
        adContainer.addView(adView)
        adRequestQueue.clear()*/
    }

    private fun checkIfThereIsAnyQuwara() {
        val bannerResponse = list.lastOrNull()
        bannerResponse?.let {
            Log.d("Magic", "filling up quwara's need: called")
            loadBannerAd(mContext, adType = it.adType, adContainer = it.viewGroup)
        }
    }

    private fun ViewGroup.addCleanView(view: View?) {
        (view?.parent as? ViewGroup)?.removeView(view)
        this.removeAllViews()
        view?.let { this.addView(it) }
    }

    fun onDestroy(adType: String) {
        Log.d("Magic", "onDestroy: $adType $removeList, $list")
        val node = removeList.find { it.adType == adType }
        val node2 = list.find { it.adType == adType }
        node?.adView?.destroy()
        node2?.adView?.destroy()
        val result = removeList.remove(node)
        Log.d("Magic", "$adType -> RemoveList: onDestroy: $result -- ${node?.viewGroup}")
        val result2 = list.remove(node2)

        Log.d("Magic", "$adType -> List: onDestroy: $result2 -- ${node2?.viewGroup}")
    }
}