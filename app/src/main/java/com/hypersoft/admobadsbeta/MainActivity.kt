package com.hypersoft.admobadsbeta

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.hypersoft.admobadsbeta.ads.banners.BannerAdsConfig
import com.hypersoft.admobadsbeta.ads.banners.enums.BannerType
import com.hypersoft.admobadsbeta.ads.interstitial.InterstitialAdsConfig
import com.hypersoft.admobadsbeta.ads.interstitial.callbacks.InterstitialOnShowCallBack
import com.hypersoft.admobadsbeta.ads.utils.AdsType

class MainActivity : AppCompatActivity() {

    private val interstitialAdsConfig by lazy { InterstitialAdsConfig(this) }

    companion object{
        val bannerAdsConfig by lazy { BannerAdsConfig() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadBanner()
        initObserver()

        //findViewById<MaterialButton>(R.id.mb_call).setOnClickListener { loadInter() }
        findViewById<MaterialButton>(R.id.mb_call).setOnClickListener { startActivity(Intent(this, ActivitySecond::class.java)) }
    }

    private fun loadBanner() {
        bannerAdsConfig.loadBannerAd(this, AdsType.BANNER_HOME, BannerType.ADAPTIVE)
    }

    private fun initObserver() {
        bannerAdsConfig.bannerObserver.observe(this as LifecycleOwner) { bannerResponse ->
            Log.d("TAG", "a: initObserver: $bannerResponse")
            Toast.makeText(this, "A: ${bannerResponse?.adType}", Toast.LENGTH_SHORT).show()
            when (bannerResponse?.loadState) {
                -1 -> {
                    // Default state
                    bannerAdsConfig.loadBannerAd(this, AdsType.BANNER_HOME, BannerType.ADAPTIVE)
                }

                0 -> {
                    // Failure case (hide container)
                    findViewById<FrameLayout>(R.id.fl_container).removeAllViews()
                    findViewById<FrameLayout>(R.id.fl_container).visibility = View.GONE
                }

                1 -> {
                    // Success case
                    bannerAdsConfig.showBannerAd(
                        adType = AdsType.BANNER_HOME,
                        adView = bannerResponse.adView,
                        viewGroup = findViewById<FrameLayout>(R.id.fl_container)
                    )
                }

                2 -> {
                    // ad is loading
                }
            }
        }
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
}