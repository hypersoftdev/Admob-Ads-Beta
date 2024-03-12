package com.hypersoft.admobadsbeta.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.hypersoft.admobadsbeta.R
import com.hypersoft.admobadsbeta.ads.banners.BannerAdsConfig
import com.hypersoft.admobadsbeta.ads.banners.enums.BannerType
import com.hypersoft.admobadsbeta.ads.interstitial.InterstitialAdsConfig
import com.hypersoft.admobadsbeta.ads.interstitial.callbacks.InterstitialOnShowCallBack
import com.hypersoft.admobadsbeta.ads.natives.NativeAdsConfig
import com.hypersoft.admobadsbeta.ads.utils.AdsType

class MainActivity : AppCompatActivity() {

    private val interstitialAdsConfig by lazy { InterstitialAdsConfig(this) }

    companion object {
        val bannerAdsConfig by lazy { BannerAdsConfig() }
        val nativeAdsConfig by lazy { NativeAdsConfig() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //loadBanner()
        loadNative()

        //findViewById<MaterialButton>(R.id.mb_call).setOnClickListener { loadInter() }
        findViewById<MaterialButton>(R.id.mb_call).setOnClickListener {
            startActivity(Intent(this, ActivitySecond::class.java))
            //finish()
        }
    }

    private fun loadNative() {
        nativeAdsConfig.loadNativeAd(activity = this, adType = AdsType.NATIVE_LANGUAGE, viewGroup = findViewById(R.id.fl_container))
    }

    private fun loadBanner() {
        bannerAdsConfig.loadBannerAd(activity = this, adType = AdsType.BANNER_HOME, bannerType = BannerType.COLLAPSIBLE_BOTTOM, viewGroup = findViewById(R.id.fl_container))
    }

    override fun onRestart() {
        super.onRestart()
        //bannerAdsConfig.loadBannerAd(activity = this, adType = AdsType.BANNER_HOME, bannerType = BannerType.COLLAPSIBLE_BOTTOM, viewGroup = findViewById(R.id.fl_container))
        //bannerAds.loadBannerAd(this, AdsType.BANNER_HOME, findViewById(R.id.fl_container))
    }

    /* ______________________________________ Interstitial ______________________________________ */

    private fun loadInter() {
        Log.d("AdsInformation", "loadInterstitial -> Validating ad call")
        // UseCase # 1
        interstitialAdsConfig.loadInterstitialAd(adType = AdsType.INTER_SPLASH)

        // UseCase # 2
        /*interstitialAdsConfig.loadInterstitialAd(
            adType = AdsType.INTER_SPLASH,
            listener = object : InterstitialOnLoadCallBack {
                override fun onResponse() {
                    // Proceed app
                }
            }
        )*/
    }

    /**
     * @param adType:   Key of the Ad, it should be unique id and should be case-sensitive
     * @param remoteCounter:   Pass remote counter value, if the value is n, it will load on "n-1". In case of <= 2, it will load everytime
     * @param loadOnStart:  Determine whether ad should be load on the very first time or not?
     *
     *  e.g. remoteCounter = 4, ad will  load on "n-1" = 3
     *      if (loadOnStart) {
     *          // 1, 0, 0, 1, 0, 0, 1, 0, 0 ... so on
     *      } else {
     *          // 0, 0, 1, 0, 0, 1, 0, 0, 1 ... so on
     *      }
     */

    private fun loadInterCounter() {
        interstitialAdsConfig.loadInterstitialAd(adType = AdsType.INTER_SPLASH, remoteCounter = 4, loadOnStart = true)
    }

    private fun showInter() {
        when (interstitialAdsConfig.isInterstitialLoaded()) {
            true -> showInterAd()
            false -> navigateScreen()
        }
    }

    private fun showInterAd() {
        interstitialAdsConfig.showInterstitialAd(this, adType = AdsType.INTER_SPLASH)

        interstitialAdsConfig.showInterstitialAd(
            activity = this,
            adType = AdsType.INTER_SPLASH,
            listener = object : InterstitialOnShowCallBack {
                override fun onAdFailedToShow() {

                }
            })
    }

    private fun navigateScreen() {
        // Navigate now
    }

    override fun onDestroy() {
        super.onDestroy()
        bannerAdsConfig.onDestroy(AdsType.BANNER_HOME, bannerType = BannerType.COLLAPSIBLE_BOTTOM)
    }
}