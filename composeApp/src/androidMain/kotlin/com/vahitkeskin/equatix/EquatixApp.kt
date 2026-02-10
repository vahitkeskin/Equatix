package com.vahitkeskin.equatix

import android.app.Application
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.work.Configuration
import com.vahitkeskin.equatix.data.local.AppDatabase
import com.vahitkeskin.equatix.di.AppModule
// 1. BU IMPORT'U EKLE (Platform tarafındaki değişkene erişmek için)
import android.app.Activity
import android.os.Bundle
import com.vahitkeskin.equatix.platform.currentActivity
import com.google.android.gms.ads.MobileAds
import com.vahitkeskin.equatix.platform.AdManager
import com.vahitkeskin.equatix.platform.AppOpenAdManager
import com.vahitkeskin.equatix.platform.appContext
import kotlinx.coroutines.Dispatchers

class EquatixApp : Application(), Configuration.Provider {

    companion object {
        lateinit var instance: EquatixApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // 2. BU SATIRI EKLE (Kritik olan kısım burası)
        // KeyValueStorage'ın kullandığı değişkeni burada başlatıyoruz.
        appContext = this

        // Activity Lifecycle Takibi
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                currentActivity = activity
            }
            override fun onActivityStarted(activity: Activity) {
                currentActivity = activity
            }
            override fun onActivityResumed(activity: Activity) {
                currentActivity = activity
            }
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {
                if (currentActivity == activity) {
                    currentActivity = null
                }
            }
        })

        // --- Veritabanı İşlemleri ---
        val dbFile = applicationContext.getDatabasePath("equatix.db")

        val db = Room.databaseBuilder<AppDatabase>(
            context = applicationContext,
            name = dbFile.absolutePath
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()

        AppModule.database = db

        // MobileAds SDK Başlatma
        MobileAds.initialize(this) { }
        
        // App Open Ad Başlatma
        AppOpenAdManager(this).fetchAd()
        
        // Interstitial ve Rewarded ön yükleme
        AdManager.loadInterstitial(this)
        AdManager.loadRewarded(this)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}