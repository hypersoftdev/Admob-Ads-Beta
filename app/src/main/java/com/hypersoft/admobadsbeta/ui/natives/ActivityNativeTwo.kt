package com.hypersoft.admobadsbeta.ui.natives

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.hypersoft.admobadsbeta.R
import com.hypersoft.admobadsbeta.ads.utils.AdsType
import com.hypersoft.admobadsbeta.ui.natives.ActivityNativeOne.Companion.nativeAdsConfig

class ActivityNativeTwo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native_two)

        loadNatives()

        findViewById<MaterialButton>(R.id.mb_next).setOnClickListener { startActivity(Intent(this, ActivityNativeThree::class.java)) }
    }

    private fun loadNatives() {
        nativeAdsConfig.loadNativeAd(
            activity = this,
            adType = AdsType.NATIVE_THREE,
            viewGroup = findViewById(R.id.fl_container)
        )
        nativeAdsConfig.loadNativeAd(
            activity = this,
            adType = AdsType.NATIVE_FOUR,
            viewGroup = findViewById(R.id.fl_container_2)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        nativeAdsConfig.onDestroy(adType = AdsType.NATIVE_THREE)
        nativeAdsConfig.onDestroy(adType = AdsType.NATIVE_FOUR)
    }
}