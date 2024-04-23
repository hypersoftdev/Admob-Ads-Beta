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

/**
 * Custom Application class for initializing components and managing application-wide resources.
 */
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize App Open Ad Manager
        initAppOpen()
    }

    /**
     * Initializes the App Open Ad Manager.
     */
    private fun initAppOpen() {
        appOpenAdManager = AppOpenAdManager(this)
        appOpenAdManager.loadAppOpen()
    }

    /**
     * Companion object to hold static references.
     */
    companion object {
        // Static reference to App Open Ad Manager
        @SuppressLint("StaticFieldLeak")
        lateinit var appOpenAdManager: AppOpenAdManager
    }
}