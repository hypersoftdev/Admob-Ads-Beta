package com.hypersoft.admobadsbeta

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.hypersoft.admobadsbeta.ads.utils.AdsType

class ActivitySecond : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        loadBanner()
        initObserver()

        findViewById<MaterialButton>(R.id.mb_back).setOnClickListener { finish() }
    }

    private fun loadBanner() {
       // bannerAdsConfig.loadBannerAd(this, AdsType.BANNER_GALLERY, BannerType.ADAPTIVE)
        MainActivity.bannerAds.loadBannerAd(this, AdsType.BANNER_GALLERY, findViewById(R.id.fl_container))
    }

    private fun initObserver() {
       /* bannerAdsConfig.bannerObserver.observe(this as LifecycleOwner) { bannerResponse ->
            Log.d("TAG", "b: initObserver: $bannerResponse")
            Toast.makeText(this, "B: ${bannerResponse?.adType}", Toast.LENGTH_SHORT).show()
            when (bannerResponse?.loadState) {
                -1 -> {
                    // Default state
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
        }*/
    }

    override fun onDestroy() {
        super.onDestroy()
        MainActivity.bannerAds.onDestroy(AdsType.BANNER_GALLERY)
    }
}