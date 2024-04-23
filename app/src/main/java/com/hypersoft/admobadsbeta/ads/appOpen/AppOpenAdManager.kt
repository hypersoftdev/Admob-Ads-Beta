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


/**
 * Class responsible for managing App Open Ads in the application.
 * This class implements Application.ActivityLifecycleCallbacks to track the lifecycle of activities.
 *
 * @property mainApplication The instance of the main application.
 */
class AppOpenAdManager(private val mainApplication: MainApplication) : Application.ActivityLifecycleCallbacks {

    // Dependency Injection Component
    private val diComponent = DiComponent()

    // Current activity reference
    private var currentActivity: Activity? = null

    // App Open Ad instance
    private var appOpenAd: AppOpenAd? = null

    // Timestamp for when the ad was loaded
    private var loadTime = 0L

    // Flags to manage ad state
    private var isShowingAd = false
    private var isLoadingAd = false

    // Flag to determine if the app is in splash mode
    var isSplash = true

    /* -------------------------- Manage -------------------------- */

    // Default lifecycle observer to show ad on app start
    private val defaultLifecycleObserver = object : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            super.onStart(owner)
            Log.d("AdsInformation", "OpenApp -> defaultLifecycleObserver: onStart: Called")
            Handler(Looper.getMainLooper()).post { showAd() }
        }
    }

    /**
     * Initialization block to register lifecycle callbacks.
     */
    init {
        mainApplication.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(defaultLifecycleObserver)
    }

    /* -------------------------- Activity LifeCycle -------------------------- */

    override fun onActivityStarted(activity: Activity) {
        Log.d("AdsInformation", "OpenApp -> onActivityStarted: called")
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivityDestroyed(activity: Activity) {}
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    /* -------------------------- Load & Show -------------------------- */

    // Lazy initialization of App Open Ad ID
    private val appOpenId by lazy { mainApplication.getString(R.string.admob_app_open_id) }

    /**
     * Loads the App Open Ad if conditions are met.
     */
    fun loadAppOpen() {
        // Check if ad is already available
        if (isAdAvailable()) {
            Log.e("AdsInformation", "OpenApp -> loadAppOpen: Ad already available")
            return
        }

        // Check if user has premium access
        if (diComponent.isAppPurchased) {
            Log.e("AdsInformation", "OpenApp -> loadAppOpen: User has premium access")
            return
        }
        // Check if ad loading is already in progress
        if (isLoadingAd) {
            Log.e("AdsInformation", "OpenApp -> loadAppOpen: Ad is already getting load")
            return
        }

        // Check if Ad ID is empty
        if (appOpenId.trim().isEmpty()) {
            Log.e("AdsInformation", "OpenApp -> loadAppOpen: Ad Id should not be empty: $appOpenId")
            return
        }

        // Check if user has provided consent to show ads
        if (diComponent.canRequestAdsConsent.not()) {
            Log.e("AdsInformation", "OpenApp -> loadAppOpen: Consent: cannot request ads")
            return
        }

        // Check if App Open Ad is enabled remotely
        if (diComponent.rcvAppOpen == 0) {
            Log.e("AdsInformation", "OpenApp -> loadAppOpen: Remote Configuration: Ad is off")
            return
        }

        // Check for internet connection
        if (diComponent.isInternetConnected.not()) {
            Log.e("AdsInformation", "OpenApp -> loadAppOpen: No Internet connection")
            return
        }

        // Set loading flag
        isLoadingAd = true

        // Build Ad request
        val request = AdRequest.Builder().build()
        // Load App Open Ad
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

    /**
     * Shows the App Open Ad if conditions are met.
     */
    fun showAd() {
        Log.d("AdsInformation", "OpenApp -> showAd: called")
        // If the app open ad is already showing, do not show the ad again.
        if (isShowingAd) {
            Log.e("AdsInformation", "OpenApp -> showAd: Ad is already showing")
            return
        }

        // Check if user has premium access
        if (diComponent.isAppPurchased) {
            Log.e("AdsInformation", "OpenApp -> showAd: Premium User")
            return
        }

        // Check if current activity is null
        if (currentActivity == null) {
            Log.e("AdsInformation", "OpenApp -> showAd: CurrentActivity is Null")
            return
        }

        // Check if another ad is already showing
        if (currentActivity is AdActivity) {
            Log.e("AdsInformation", "OpenApp -> showAd: Another Ad is showing")
            return
        }

        // Check if it's the splash screen
        if (isSplash) {
            Log.e("AdsInformation", "OpenApp -> showAd: Cannot show on Splash")
            return
        }

        // Check if ad is available, otherwise load it
        if (!isAdAvailable()) {
            Log.e("AdsInformation", "OpenApp -> showAd: Ad is not available")
            loadAppOpen()
            return
        }

        // Set full-screen content callback
        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d("AdsInformation", "OpenApp -> showAd: onAdDismissedFullScreenContent: dismissed")
                appOpenAd = null
                isShowingAd = false
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
        currentActivity?.let { appOpenAd?.show(it) }
    }

    /**
     * Checks if the App Open Ad is available and not expired.
     *
     * @return true if the ad is available and not expired, false otherwise.
     */
    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && !wasAdExpired()
    }

    /**
     * Checks if the loaded App Open Ad has expired.
     *
     * @return true if the ad has expired, false otherwise.
     */
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

    /**
     * Resets the App Open Ad manager when you exit the app
     * Unregisters lifecycle callbacks and clears references.
     */
    fun reset() {
        ProcessLifecycleOwner.get().lifecycle.removeObserver(defaultLifecycleObserver)
        mainApplication.unregisterActivityLifecycleCallbacks(this)
        currentActivity = null
        appOpenAd = null
        isSplash = true
        Log.e("AdsInformation", "OpenApp -> reset: appOpenAd")
    }
}