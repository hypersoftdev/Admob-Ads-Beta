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
import com.hypersoft.admobadsbeta.ads.natives.models.NativeRegular
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @Author: SOHAIB AHMED
 * @Date: 12/03/2024
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

class NativeRegularRepository {

    private var mActivity: Activity? = null
    private var isAppPurchased = false

    private val hashMap = HashMap<String, NativeRegular>()

    fun loadAndShowNative(
        activity: Activity?,
        adType: String,
        nativeId: String,
        nativeType: NativeType,
        isAdEnable: Boolean,
        isAppPurchased: Boolean,
        isInternetConnected: Boolean,
        viewGroup: ViewGroup,
        listener: NativeCallBack?,
    ) {
        this.mActivity = activity
        this.isAppPurchased = isAppPurchased
        if (isAppPurchased) {
            Log.e("AdsInformation", "$adType -> loadAndShowNative: Premium user")
            listener?.onResponse(false)
            return
        }

        if (isAdEnable.not()) {
            Log.e("AdsInformation", "$adType -> loadAndShowNative: Remote config is off")
            listener?.onResponse(false)
            return
        }

        if (isInternetConnected.not()) {
            Log.e("AdsInformation", "$adType -> loadAndShowNative: Internet is not connected")
            listener?.onResponse(false)
            return
        }

        if (activity == null) {
            Log.e("AdsInformation", "$adType -> loadAndShowNative: Context is null")
            listener?.onResponse(false)
            return
        }

        if (activity.isFinishing || activity.isDestroyed) {
            Log.e("AdsInformation", "$adType -> loadAndShowNative: activity is finishing or destroyed")
            listener?.onResponse(false)
            return
        }

        if (nativeId.trim().isEmpty()) {
            Log.e("AdsInformation", "$adType -> loadAndShowNative: Ad id is empty")
            listener?.onResponse(false)
            return
        }

        if (hashMap[adType] != null && hashMap[adType]?.adType == null) {
            Log.e("AdsInformation", "$adType -> loadAndShowNative: Request already exist")
            return
        }

        hashMap.putIfAbsent(adType, NativeRegular(adType = adType, nativeType = nativeType, nativeId = nativeId, nativeAd = null, viewGroup = viewGroup))
        Log.d("AdsInformation", "$adType -> loadAndShowNative: Requesting admob server for ad...")

        CoroutineScope(Dispatchers.IO).launch {
            val adRequest = AdRequest.Builder().build()
            val nativeAdOptions = NativeAdOptions.Builder()
                .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                .setMediaAspectRatio(NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_LANDSCAPE)
                .build()

            AdLoader.Builder(activity, nativeId)
                .forNativeAd { populateNative(adType, it) }
                .withAdListener(getListener(adType, listener))
                .withNativeAdOptions(nativeAdOptions)
                .build()
                .loadAd(adRequest)
        }
    }

    private fun getListener(adType: String, listener: NativeCallBack?): AdListener {
        return object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.i("AdsInformation", "$adType -> loadAndShowNative: onAdLoaded")
                listener?.onResponse(true)
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                super.onAdFailedToLoad(adError)
                Log.e("AdsInformation", "$adType -> loadAndShowNative: onAdFailedToLoad: ${adError.message}")
                listener?.onResponse(false)
            }

            override fun onAdImpression() {
                super.onAdImpression()
                Log.d("AdsInformation", "$adType -> loadAndShowNative: onAdImpression")
            }
        }
    }

    private fun populateNative(adType: String, nativeAd: NativeAd) {
        hashMap[adType]?.nativeAd = nativeAd

        if (isAppPurchased) {
            Log.e("AdsInformation", "$adType -> loadAndShowNative: populateNative: Premium user")
            hashMap[adType]?.viewGroup?.removeAllViews()
            hashMap[adType]?.viewGroup?.visibility = View.GONE
            return
        }

        if (mActivity == null) {
            Log.e("AdsInformation", "$adType -> loadAndShowNative: populateNative: activity reference is null")
            hashMap[adType]?.viewGroup?.removeAllViews()
            hashMap[adType]?.viewGroup?.visibility = View.GONE
            return
        }

        Log.d("AdsInformation", "$adType -> loadAndShowNative: populateNative: showing ad")

        // Get required Native Layout
        val inflater = LayoutInflater.from(mActivity)
        val nativeAdView: NativeAdView = when (hashMap[adType]?.nativeType) {
            // Native Banner
            NativeType.NATIVE_BANNER -> inflater.inflate(R.layout.layout_native_banner, hashMap[adType]?.viewGroup, false)
            NativeType.NATIVE_BANNER_SMART -> inflater.inflate(R.layout.layout_native_banner_smart, hashMap[adType]?.viewGroup, false)

            // Native Medium (old)
            NativeType.NATIVE_MEDIUM_OLD -> inflater.inflate(R.layout.layout_native_medium_old, hashMap[adType]?.viewGroup, false)
            NativeType.NATIVE_MEDIUM_OLD_SMART -> inflater.inflate(R.layout.layout_native_medium_old_smart, hashMap[adType]?.viewGroup, false)

            // Native Medium (new)
            NativeType.NATIVE_MEDIUM -> inflater.inflate(R.layout.layout_native_medium, hashMap[adType]?.viewGroup, false)
            NativeType.NATIVE_MEDIUM_SMART -> inflater.inflate(R.layout.layout_native_medium_smart, hashMap[adType]?.viewGroup, false)

            // Native Large
            NativeType.NATIVE_LARGE -> inflater.inflate(R.layout.layout_native_large, hashMap[adType]?.viewGroup, false)
            else -> inflater.inflate(R.layout.layout_native_medium, hashMap[adType]?.viewGroup, false)
        } as NativeAdView

        // Media Configuration
        if (hashMap[adType]?.nativeType != NativeType.NATIVE_BANNER
            && hashMap[adType]?.nativeType != NativeType.NATIVE_BANNER_SMART
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

        hashMap[adType]?.viewGroup?.visibility = View.VISIBLE
        hashMap[adType]?.viewGroup?.addCleanView(nativeAdView)
        nativeAdView.setNativeAd(nativeAd)
    }

    private fun ViewGroup.addCleanView(view: View?) {
        (view?.parent as? ViewGroup)?.removeView(view)
        this.removeAllViews()
        view?.let { this.addView(it) }
    }


}