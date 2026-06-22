package com.example.teene

import android.app.Application
import com.example.teene.di.eventsModule
import com.example.teene.di.homeModule
import com.example.teene.di.inboxModule
import com.example.teene.di.landingModule
import com.example.teene.di.networkModule
import com.example.teene.inbox.data.QuickbloxManager
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TeeneApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Start Koin once at application startup to avoid re-creating singletons like DataStore
        startKoin {
            androidContext(this@TeeneApplication)
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