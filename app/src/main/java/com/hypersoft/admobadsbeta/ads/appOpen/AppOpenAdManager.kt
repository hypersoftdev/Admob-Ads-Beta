package com.hypersoft.admobadsbeta.ads.appOpen

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.hypersoft.admobadsbeta.MainApplication
import com.hypersoft.admobadsbeta.R
import com.hypersoft.admobadsbeta.di.DiComponent
import java.util.Date

/**
 * @Author: SOHAIB AHMED
 * @Date: 13/03/2024
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

class AppOpenAdManager(private val mainApplication: MainApplication) : Application.ActivityLifecycleCallbacks {

    private val diComponent = DiComponent()

    private var currentActivity: Activity? = null
    private var appOpenAd: AppOpenAd? = null

    private var loadTime = 0L

    private var showingAppOpen = false
    private var isLoadingAd = false
    private var isShowingAd = false
    var isSplash = true

    /* --------------------------------------- Manage --------------------------------------- */

    private val defaultLifecycleObserver = object : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            super.onStart(owner)
            Log.d("AdsInformation", "OpenApp -> defaultLifecycleObserver: onStart: Called")
            Handler(Looper.getMainLooper()).post { showAd() }
        }
    }

    init {
        mainApplication.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(defaultLifecycleObserver)
    }

    /* --------------------------------------- Activity LifeCycle --------------------------------------- */

    override fun onActivityStarted(activity: Activity) {
        Log.d("AdsInformation", "OpenApp -> onActivityStarted: called")
        if (!isShowingAd) {
            currentActivity = activity
        }
    }

    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivityDestroyed(activity: Activity) {}
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    /* --------------------------------------- Load & Show --------------------------------------- */

    private val appOpenId by lazy { mainApplication.getString(R.string.admob_app_open_id) }
    var dismissCallback: (() -> Unit)? = null

    fun loadAppOpen() {
        if (isAdAvailable()) {
            Log.e("AdsInformation", "OpenApp -> loadAppOpen: Ad already available")
            return
        }

        if (diComponent.isAppPurchased) {
            Log.e("AdsInformation", "OpenApp -> loadAppOpen: User has premium access")
            return
        }

        if (isLoadingAd) {
            Log.e("AdsInformation", "OpenApp -> loadAppOpen: Ad is already getting load")
            return
        }

        if (appOpenId.trim().isEmpty()) {
            Log.e("AdsInformation", "OpenApp -> loadAppOpen: Ad Id should not be empty: $appOpenId")
            return
        }

        if (diComponent.canRequestAdsConsent.not()) {
            Log.e("AdsInformation", "OpenApp -> loadAppOpen: Consent: cannot request ads")
            return
        }

        if (diComponent.rcvAppOpen != 1) {
            Log.e("AdsInformation", "OpenApp -> loadAppOpen: Remote Configuration: Ad is off")
            return
        }

        if (diComponent.isInternetConnected.not()) {
            Log.e("AdsInformation", "OpenApp -> loadAppOpen: No Internet connection")
            return
        }

        isLoadingAd = true

        val request = AdRequest.Builder().build()
        AppOpenAd.load(mainApplication, appOpenId.trim(), request, object : AppOpenAd.AppOpenAdLoadCallback() {
            override fun onAdLoaded(ad: AppOpenAd) {
                Log.i("AdsInformation", "OpenApp -> loadAppOpen: onAdLoaded: loaded")
                appOpenAd = ad
                loadTime = Date().time
                isLoadingAd = false
            }

            override fun onAdFailedToLoad(loadAppOpenError: LoadAdError) {
                Log.e("AdsInformation", "OpenApp -> loadAppOpen: onAdFailedToLoad: ", Exception(loadAppOpenError.message))
                isLoadingAd = false
            }
        })
    }

    fun showAd() {
        Log.d("AdsInformation", "OpenApp -> showAd: called")
        // If the app open ad is already showing, do not show the ad again.
        if (isShowingAd) {
            Log.e("AdsInformation", "OpenApp -> showAd: Ad is already showing")
            return
        }

        if (diComponent.isAppPurchased) {
            Log.e("AdsInformation", "OpenApp -> showAd: Premium User")
            return
        }

        if (currentActivity == null) {
            Log.e("AdsInformation", "OpenApp -> showAd: CurrentActivity is Null")
            return
        }

        if (currentActivity is AdActivity) {
            Log.e("AdsInformation", "OpenApp -> showAd: Another Ad is showing")
            return
        }

        if (isSplash) {
            Log.e("AdsInformation", "OpenApp -> showAd: Cannot show on Splash")
            return
        }

        if (!isAdAvailable()) {
            Log.e("AdsInformation", "OpenApp -> showAd: Ad is not available")
            loadAppOpen()
            return
        }

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d("AdsInformation", "OpenApp -> showAd: onAdDismissedFullScreenContent: dismissed")
                appOpenAd = null
                isShowingAd = false
                dismissCallback?.invoke()
                loadAppOpen()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e("AdsInformation", "OpenApp -> loadAppOpen: onAdFailedToShowFullScreenContent: ", Exception(adError.message))
                isShowingAd = false
            }

            override fun onAdShowedFullScreenContent() {
                Log.d("AdsInformation", "OpenApp -> showAd: onAdShowedFullScreenContent: shown")
            }
        }
        isShowingAd = true
        showingAppOpen = true
        currentActivity?.let { appOpenAd?.show(it) }
    }

    private fun isAdAvailable() = appOpenAd != null && !wasAdExpired()

    private fun wasAdExpired(): Boolean {
        val dateDifference: Long = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        val isExpired = dateDifference > numMilliSecondsPerHour * 4
        if (isExpired) {
            Log.e("AdsInformation", "OpenApp -> isAdAvailable: wasAdExpired: ", IllegalStateException("Ad is expired!"))
            appOpenAd = null
        }
        return isExpired
    }

    fun reset() {
        ProcessLifecycleOwner.get().lifecycle.removeObserver(defaultLifecycleObserver)
        mainApplication.unregisterActivityLifecycleCallbacks(this)
        currentActivity = null
        appOpenAd = null
        isSplash = true
        Log.e("AdsInformation", "OpenApp -> reset: appOpenAd")
    }

}