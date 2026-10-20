package com.example.tenee

import android.app.Application
import com.example.tenee.di.eventsModule
import com.example.tenee.di.homeModule
import com.example.tenee.di.inboxModule
import com.example.tenee.di.landingModule
import com.example.tenee.di.networkModule
import com.example.tenee.inbox.data.QuickbloxManager
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TeneeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Start Koin once at application startup to avoid re-creating singletons like DataStore
        startKoin {
            androidContext(this@TeneeApplication)
            modules(
                landingModule,
                networkModule,
                homeModule,
                eventsModule,
                inboxModule
            )
        }

        // Initialize the QuickBlox SDK once for the process.
        get<QuickbloxManager>().init()
    }
}