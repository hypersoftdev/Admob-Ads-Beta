package com.hypersoft.admobadsbeta.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.hypersoft.admobadsbeta.MainApplication
import com.hypersoft.admobadsbeta.R
import com.hypersoft.admobadsbeta.ads.appOpen.AppOpenAdManager
import com.hypersoft.admobadsbeta.ui.banners.ActivityBannerOne
import com.hypersoft.admobadsbeta.ui.interstitials.ActivityInterstitials
import com.hypersoft.admobadsbeta.ui.natives.ActivityNativeOne

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPremium()

        findViewById<MaterialButton>(R.id.mb_banner).setOnClickListener { startActivity(Intent(this, ActivityBannerOne::class.java)) }
        findViewById<MaterialButton>(R.id.mb_native).setOnClickListener { startActivity(Intent(this, ActivityNativeOne::class.java)) }
        findViewById<MaterialButton>(R.id.mb_inter).setOnClickListener { startActivity(Intent(this, ActivityInterstitials::class.java)) }
    }

    private fun checkPremium() {
        // if user has subscribed to be premium
        // MainApplication.appOpenAdManager.reset()
    }
}