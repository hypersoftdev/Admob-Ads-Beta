package com.hypersoft.admobadsbeta

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.hypersoft.admobadsbeta.ads.banners.enums.BannerType
import com.hypersoft.admobadsbeta.ads.utils.AdsType

class ActivitySecond : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        loadBanner()

        findViewById<MaterialButton>(R.id.mb_back).setOnClickListener { finish() }
    }

    private fun loadBanner() {
        MainActivity.bannerAdsConfig.loadBannerAd(activity = this, adType = AdsType.BANNER_GALLERY, bannerType = BannerType.ADAPTIVE, viewGroup = findViewById(R.id.fl_container))
    }

    override fun onDestroy() {
        super.onDestroy()
        MainActivity.bannerAdsConfig.onDestroy(AdsType.BANNER_GALLERY)
    }
}