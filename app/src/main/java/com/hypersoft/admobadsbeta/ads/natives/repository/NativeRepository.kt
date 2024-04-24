package com.hypersoft.admobadsbeta.ads.natives.repository

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.view.isVisible
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.hypersoft.admobadsbeta.R
import com.hypersoft.admobadsbeta.ads.natives.callbacks.NativeCallBack
import com.hypersoft.admobadsbeta.ads.natives.enums.NativeType
import com.hypersoft.admobadsbeta.ads.natives.models.NativeResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @Author: SOHAIB AHMED
 * @Date: 11/03/2024
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

/**
 * Abstract class for managing native ads.
 */
abstract class NativeRepository {

    private var mActivity: Activity? = null
    private var mAdType: String = ""
    private var mNativeId: String = ""
    private var mNativeType: NativeType = NativeType.NATIVE_MEDIUM_SMART
    private var isAppPurchased = false
    private var isInternetConnected = false
    private var canRequestAdsConsent = false
    private var listener: NativeCallBack? = null

    private var mNativeAd: NativeAd? = null
    private var usingNativeAd: NativeAd? = null
    private var isNativeLoading = false

    private var requestList: MutableList<NativeResponse> = mutableListOf()
    private val impressionList: MutableList<NativeResponse> = mutableListOf()
    private val deleteList: MutableList<NativeResponse> = mutableListOf()

    /**
     * Loads a native ad.
     * @param activity The activity where the ad will be loaded.
     * @param adType Type of the ad.
     * @param nativeId ID of the native ad.
     * @param nativeType Type of the native ad.
     * @param isAdEnable Indicates if ads are enabled.
     * @param isAppPurchased Indicates if the app is purchased.
     * @param isInternetConnected Indicates if the internet is connected.
     * @param canRequestAdsConsent Indicates if consent is permitted for ad calls.
     * @param viewGroup ViewGroup where the native ad will be displayed.
     * @param listener Callback for handling ad loading response.
     */
    protected fun loadNative(
        activity: Activity?,
        adType: String,
        nativeId: String,
        nativeType: NativeType,
        isAdEnable: Boolean,
        isAppPurchased: Boolean,
        isInternetConnected: Boolean,
        canRequestAdsConsent: Boolean,
        viewGroup: ViewGroup?,
        listener: NativeCallBack?,
    ) {
        this.mActivity = activity
        this.mAdType = adType
        this.mNativeId = nativeId
        this.mNativeType = nativeType
        this.isAppPurchased = isAppPurchased
        this.isInternetConnected = isInternetConnected
        this.canRequestAdsConsent = canRequestAdsConsent
        this.listener = listener

        // Check if app is purchased
        if (isAppPurchased) {
            Log.e("AdsInformation", "$adType -> loadNative: Premium user")
            listener?.onResponse(false)
            return
        }

        // Check if ads are enabled
        if (isAdEnable.not()) {
            Log.e("AdsInformation", "$adType -> loadNative: Remote config is off")
            listener?.onResponse(false)
            return
        }

        // Check internet connection
        if (isInternetConnected.not()) {
            Log.e("AdsInformation", "$adType -> loadNative: Internet is not connected")
            listener?.onResponse(false)
            return
        }

        // Check if consent is permitted for ad calls
        if (canRequestAdsConsent.not()) {
            Log.e("AdsInformation", "$adType -> loadNative: Consent not permitted for ad calls")
            listener?.onResponse(false)
            return
        }

        // Check activity validity
        if (activity == null) {
            Log.e("AdsInformation", "$adType -> loadNative: Context is null")
            listener?.onResponse(false)
            return
        }

        // Check activity validity
        if (activity.isFinishing || activity.isDestroyed) {
            Log.e("AdsInformation", "$adType -> loadNative: activity is finishing or destroyed")
            listener?.onResponse(false)
            return
        }

        // Check if nativeId is empty
        if (nativeId.trim().isEmpty()) {
            Log.e("AdsInformation", "$adType -> loadNative: Ad id is empty")
            listener?.onResponse(false)
            return
        }

        val shouldAdd = impressionList.indexOfFirst { it.adType == adType }

        // ReShowAd
        if (shouldAdd != -1) {
            Log.d("AdsInformation", "$adType -> loadNative: Reshowing Ad")
            impressionList.find { it.adType == adType }?.let {
                usingNativeAd = it.nativeAd
                it.viewGroup = viewGroup
                populateNative(it)
            }
            return
        }

        val existingNativeResponse = requestList.find { it.adType == adType }
        val nativeAd = existingNativeResponse?.nativeAd
        requestList.remove(existingNativeResponse)
        existingNativeResponse?.let { deleteList.add(it) }

        if (nativeAd == null) {
            // load ad for new Item
            val nativeResponse = NativeResponse(adType = adType, nativeType = nativeType, isAdEnable = isAdEnable, nativeAd = null, viewGroup = viewGroup)
            requestList.add(nativeResponse)

            // check if already loading
            if (!isNativeLoading && mNativeAd == null) {
                Log.d("AdsInformation", "$adType -> loadNative: Requesting admob server for ad...")

                // make a new call to load a ad
                viewGroup?.visibility = View.VISIBLE
                loadAd(activity, nativeId, adType, listener)
            } else {

                // check, maybe a preloaded is available
                mNativeAd?.let { ad ->
                    nativeResponse.nativeAd = ad
                    populateNative(nativeResponse)
                }
            }
        } else {
            val nativeResponse = NativeResponse(adType = adType, nativeType = nativeType, isAdEnable = isAdEnable, nativeAd = nativeAd, viewGroup = viewGroup)
            requestList.add(nativeResponse)
            populateNative(nativeResponse)
        }
    }

    private fun loadAd(activity: Activity, nativeId: String, adType: String, listener: NativeCallBack?) {
        isNativeLoading = true

        CoroutineScope(Dispatchers.IO).launch {
            val adRequest = AdRequest.Builder().build()
            val nativeAdOptions = NativeAdOptions.Builder()
                .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                .setMediaAspectRatio(NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_LANDSCAPE)
                .build()

            AdLoader.Builder(activity, nativeId.trim())
                .forNativeAd { mNativeAd = it }
                .withAdListener(getListener(adType, listener))
                .withNativeAdOptions(nativeAdOptions)
                .build()
                .loadAd(adRequest)
        }
    }

    private fun getListener(adType: String, listener: NativeCallBack?): AdListener {
        var currentAdType = adType
        return object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.i("AdsInformation", "$currentAdType -> loadNative: onAdLoaded")

                requestList.lastOrNull()?.let {
                    it.nativeAd = mNativeAd
                    currentAdType = it.adType
                    populateNative(it)
                }
                isNativeLoading = false
                listener?.onResponse(true)
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                super.onAdFailedToLoad(adError)
                Log.e("AdsInformation", "$currentAdType -> loadNative: onAdFailedToLoad: ${adError.message}")
                mNativeAd = null
                isNativeLoading = false
                checkIfThereIsAnymoreToLoad()
                listener?.onResponse(false)
            }

            override fun onAdImpression() {
                super.onAdImpression()
                Log.d("AdsInformation", "$currentAdType -> loadNative: onAdImpression")
                mNativeAd = null
                checkIfThereIsAnymoreToLoad()
            }
        }
    }

    private fun populateNative(nativeResponse: NativeResponse) {
        if (nativeResponse.viewGroup == null) {
            Log.d("AdsInformation", "${nativeResponse.adType} -> showNative: Preload available, no view found to show")
            return
        }
        if (isAppPurchased) {
            Log.e("AdsInformation", "${nativeResponse.adType} -> showNative: Premium user")
            nativeResponse.viewGroup?.removeAllViews()
            nativeResponse.viewGroup?.visibility = View.GONE
            return
        }

        if (mActivity == null) {
            Log.e("AdsInformation", "${nativeResponse.adType} -> showNative: activity reference is null")
            nativeResponse.viewGroup?.removeAllViews()
            nativeResponse.viewGroup?.visibility = View.GONE
            return
        }

        val nativeAd = nativeResponse.nativeAd
        if (nativeAd == null) {
            Log.e("AdsInformation", "${nativeResponse.adType} -> showNative: Ad is null")
            nativeResponse.viewGroup?.removeAllViews()
            nativeResponse.viewGroup?.visibility = View.GONE
            return
        }

        Log.d("AdsInformation", "${nativeResponse.adType} -> showNative: showing ad")

        // Get required Native Layout
        val inflater = LayoutInflater.from(mActivity)
        val nativeAdView: NativeAdView = when (nativeResponse.nativeType) {
            // Native Banner
            NativeType.NATIVE_BANNER -> inflater.inflate(R.layout.layout_native_banner, nativeResponse.viewGroup, false)
            NativeType.NATIVE_BANNER_SMART -> inflater.inflate(R.layout.layout_native_banner_smart, nativeResponse.viewGroup, false)

            // Native Medium (old)
            NativeType.NATIVE_MEDIUM_OLD -> inflater.inflate(R.layout.layout_native_medium_old, nativeResponse.viewGroup, false)
            NativeType.NATIVE_MEDIUM_OLD_SMART -> inflater.inflate(R.layout.layout_native_medium_old_smart, nativeResponse.viewGroup, false)

            // Native Medium (new)
            NativeType.NATIVE_MEDIUM -> inflater.inflate(R.layout.layout_native_medium, nativeResponse.viewGroup, false)
            NativeType.NATIVE_MEDIUM_SMART -> inflater.inflate(R.layout.layout_native_medium_smart, nativeResponse.viewGroup, false)

            // Native Large
            NativeType.NATIVE_LARGE -> inflater.inflate(R.layout.layout_native_large, nativeResponse.viewGroup, false)

        } as NativeAdView

        // Media Configuration
        if (nativeResponse.nativeType != NativeType.NATIVE_BANNER
            && nativeResponse.nativeType != NativeType.NATIVE_BANNER_SMART
        ) {
            val mvMedia: MediaView = nativeAdView.findViewById(R.id.ad_media_view)
            nativeAdView.mediaView = mvMedia
            mvMedia.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
        }

        // findViewById
        val ifvIcon: ImageFilterView = nativeAdView.findViewById(R.id.ad_app_icon)
        val mtvTitle: MaterialTextView = nativeAdView.findViewById(R.id.ad_headline)
        val mtvBody: MaterialTextView = nativeAdView.findViewById(R.id.ad_body)
        val mbAction: MaterialButton = nativeAdView.findViewById(R.id.ad_call_to_action)

        // Assigning views
        nativeAdView.iconView = ifvIcon
        nativeAdView.headlineView = mtvTitle
        nativeAdView.bodyView = mtvBody
        nativeAdView.callToActionView = mbAction

        // Filling up views
        ifvIcon.setImageDrawable(nativeAd.icon?.drawable)
        mtvTitle.text = nativeAd.headline
        mtvBody.text = nativeAd.body
        mbAction.text = nativeAd.callToAction

        // Validating views
        ifvIcon.isVisible = nativeAd.icon?.drawable != null
        mbAction.isVisible = nativeAd.callToAction.isNullOrEmpty().not()

        nativeResponse.viewGroup?.visibility = View.VISIBLE
        nativeResponse.viewGroup?.addCleanView(nativeAdView)
        nativeAdView.setNativeAd(nativeAd)

        // Added to impressionList
        if (requestList.isNotEmpty()) {
            impressionList.add(requestList.removeLast())
        }
    }

    private fun checkIfThereIsAnymoreToLoad() {
        val nativeResponse = requestList.lastOrNull()
        nativeResponse?.let {
            // No need to load ad, if adType is same on top.
            if (mAdType == it.adType) return

            // loading ad for backstack
            loadNative(
                activity = mActivity,
                adType = it.adType,
                nativeId = mNativeId,
                nativeType = it.nativeType,
                isAdEnable = it.isAdEnable,
                isAppPurchased = isAppPurchased,
                isInternetConnected = isInternetConnected,
                canRequestAdsConsent = canRequestAdsConsent,
                viewGroup = it.viewGroup,
                listener = listener
            )
        }
    }

    private fun ViewGroup.addCleanView(view: View?) {
        (view?.parent as? ViewGroup)?.removeView(view)
        this.removeAllViews()
        view?.let { this.addView(it) }
    }

    /**
     * Cleans up resources associated with the specified ad type.
     * @param adType Type of the ad to be destroyed.
     */
    fun onDestroy(adType: String) {
        Log.d("AdsInformation", "$adType -> NativeRepository: Request generated for destruction...")
        impressionList.find { it.adType == adType }?.let { node ->
            if (usingNativeAd == node.nativeAd) {
                usingNativeAd = null
                return
            }
            Log.d("AdsInformation", "$adType -> loadNative: onDestroy")

            node.nativeAd?.destroy()
            node.viewGroup?.removeAllViews()
            impressionList.remove(node)
        }
        requestList.find { it.adType == adType }?.let { node ->
            val existingResponse = deleteList.find { it.adType == adType }
            if (existingResponse != null) {
                deleteList.remove(existingResponse)
                //return
            }
            Log.d("AdsInformation", "$adType -> loadNative: onDestroy")

            node.nativeAd?.destroy()
            node.viewGroup?.removeAllViews()
            requestList.remove(node)
        }
    }
}