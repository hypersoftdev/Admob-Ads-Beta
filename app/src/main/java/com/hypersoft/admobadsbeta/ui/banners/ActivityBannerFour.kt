package com.hypersoft.admobadsbeta.ui.banners

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hypersoft.admobadsbeta.R
import com.hypersoft.admobadsbeta.ads.utils.AdsType
import com.hypersoft.admobadsbeta.ui.banners.ActivityBannerOne.Companion.bannerAdsConfig

class ActivityBannerFour : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner_four)

        loadBanner()
    }

    private fun loadBanner() {
        bannerAdsConfig.loadBannerAd(
            activity = this,
            adType = AdsType.BANNER_FOUR,
            viewGroup = findViewById(R.id.fl_container)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        bannerAdsConfig.destroyBanner(adType = AdsType.BANNER_FOUR)
    }
}