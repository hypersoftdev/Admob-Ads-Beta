package com.hypersoft.admobadsbeta.ui.natives

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.hypersoft.admobadsbeta.MainApplication
import com.hypersoft.admobadsbeta.R
import com.hypersoft.admobadsbeta.ads.natives.NativeAdsConfig
import com.hypersoft.admobadsbeta.ads.utils.AdsType

class ActivityNativeOne : AppCompatActivity() {

    companion object {
        val nativeAdsConfig by lazy { NativeAdsConfig() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native_one)

        updateAppOpen()
        loadNatives()

        findViewById<MaterialButton>(R.id.mb_next).setOnClickListener { startActivity(Intent(this, ActivityNativeTwo::class.java)) }
    }

    private fun updateAppOpen() {
        MainApplication.appOpenAdManager.isSplash = false
    }

    private fun loadNatives() {
        nativeAdsConfig.loadNativeAd(
            activity = this,
            adType = AdsType.NATIVE_ONE,
            viewGroup = findViewById(R.id.fl_container)
        )
        nativeAdsConfig.loadNativeAd(
            activity = this,
            adType = AdsType.NATIVE_TWO,
            viewGroup = findViewById(R.id.fl_container_2)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        nativeAdsConfig.onDestroy(adType = AdsType.NATIVE_ONE)
        nativeAdsConfig.onDestroy(adType = AdsType.NATIVE_TWO)
    }
}