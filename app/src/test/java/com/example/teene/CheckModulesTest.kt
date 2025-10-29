package com.example.teene

import com.example.teene.di.landingModule
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.KoinTest
import org.koin.test.verify.definition
import org.koin.test.verify.injectedParameters
import org.koin.test.verify.verify


class CheckModulesTest : KoinTest {

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun checkAllModules() {
         landingModule.verify(
             injections = injectedParameters(
                 definition<com.example.teene.data.LandingDataStore>(android.content.Context::class)
             )
         )

    }
}