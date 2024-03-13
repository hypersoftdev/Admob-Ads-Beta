package com.hypersoft.admobadsbeta.ui.banners

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.hypersoft.admobadsbeta.MainApplication
import com.hypersoft.admobadsbeta.R
import com.hypersoft.admobadsbeta.ads.banners.BannerAdsConfig
import com.hypersoft.admobadsbeta.ads.utils.AdsType

class ActivityBannerOne : AppCompatActivity() {

    companion object {
        val bannerAdsConfig by lazy { BannerAdsConfig() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner_one)

        updateAppOpen()
        loadBanner()

        findViewById<MaterialButton>(R.id.mb_next).setOnClickListener { startActivity(Intent(this, ActivityBannerTwo::class.java)) }
    }

    private fun updateAppOpen() {
        MainApplication.appOpenAdManager.isSplash = false
    }

    private fun loadBanner() {
        bannerAdsConfig.loadBannerAd(
            activity = this,
            adType = AdsType.BANNER_ONE,
            viewGroup = findViewById(R.id.fl_container)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        bannerAdsConfig.destroyBanner(adType = AdsType.BANNER_ONE)
    }
}