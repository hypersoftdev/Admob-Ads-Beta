package com.hypersoft.admobadsbeta.ui.interstitials

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.hypersoft.admobadsbeta.MainApplication
import com.hypersoft.admobadsbeta.R
import com.hypersoft.admobadsbeta.ads.interstitial.InterstitialAdsConfig
import com.hypersoft.admobadsbeta.ads.interstitial.callbacks.InterstitialOnLoadCallBack
import com.hypersoft.admobadsbeta.ads.interstitial.callbacks.InterstitialOnShowCallBack
import com.hypersoft.admobadsbeta.ads.utils.AdsType

class ActivityInterstitials : AppCompatActivity() {

    private val interstitialAdsConfig by lazy { InterstitialAdsConfig(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interstitials)

        updateAppOpen()
        loadInter()

        findViewById<MaterialButton>(R.id.mb_inter).setOnClickListener { showInter() }
    }

    private fun updateAppOpen() {
        MainApplication.appOpenAdManager.isSplash = false
    }

    /* ______________________________________ Interstitial ______________________________________ */

    private fun loadInter() {
        Log.d("AdsInformation", "loadInterstitial -> Validating ad call")

        findViewById<MaterialTextView>(R.id.mtv_title).text = "Interstitial: loading"
        // UseCase # 1
        interstitialAdsConfig.loadInterstitialAd(adType = AdsType.INTER_SPLASH)

        // UseCase # 2
        interstitialAdsConfig.loadInterstitialAd(
            adType = AdsType.INTER_SPLASH,
            listener = object : InterstitialOnLoadCallBack {
                override fun onResponse() {
                    findViewById<MaterialTextView>(R.id.mtv_title).text = "Interstitial: response"
                    findViewById<MaterialButton>(R.id.mb_inter).isEnabled = true
                }
            }
        )
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

                override fun onAdImpressionDelayed() {
                    navigateScreen()
                }
            })
    }

    private fun navigateScreen() {
        // Navigate now
        findViewById<MaterialTextView>(R.id.mtv_title).text = "Interstitial: loading"
        findViewById<MaterialButton>(R.id.mb_inter).isEnabled = false
        loadInter()
    }
}