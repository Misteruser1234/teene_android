package com.example.teene.di

import com.example.teene.data.LandingDataStore
import com.example.teene.landing.LandingViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


val landingModule = module {
    single { LandingDataStore(androidContext()) }
    viewModelOf(::LandingViewModel)
}