package com.hypersoft.admobadsbeta

import android.annotation.SuppressLint
import android.app.Application
import com.hypersoft.admobadsbeta.ads.appOpen.AppOpenAdManager

/**
 * @Author: SOHAIB AHMED
 * @Date: 13/03/2024
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initAppOpen()
    }

    private fun initAppOpen() {
        appOpenAdManager = AppOpenAdManager(this)
        appOpenAdManager.loadAppOpen()
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var appOpenAdManager: AppOpenAdManager
    }
}