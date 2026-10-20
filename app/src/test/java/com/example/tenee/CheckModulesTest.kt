package com.example.tenee

import com.example.tenee.di.landingModule
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
                 definition<com.example.tenee.data.LandingDataStore>(android.content.Context::class)
             )
         )

    }
}