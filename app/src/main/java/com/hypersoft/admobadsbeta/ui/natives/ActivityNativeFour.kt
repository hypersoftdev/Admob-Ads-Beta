package com.hypersoft.admobadsbeta.ui.natives

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hypersoft.admobadsbeta.R
import com.hypersoft.admobadsbeta.ads.utils.AdsType
import com.hypersoft.admobadsbeta.ui.natives.ActivityNativeOne.Companion.nativeAdsConfig

class ActivityNativeFour : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native_four)

        loadNatives()
    }

    private fun loadNatives() {
        nativeAdsConfig.loadAndShowNativeAd(
            activity = this,
            adType = AdsType.NATIVE_SEVEN,
            viewGroup = findViewById(R.id.fl_container)
        )
    }
}