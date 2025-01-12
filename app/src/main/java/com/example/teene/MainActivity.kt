package com.example.teene

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.teene.di.landingModule
import com.example.teene.ui.theme.TeeneTheme
import org.koin.android.ext.koin.androidContext
import org.koin.compose.KoinApplication

class MainActivity : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContent {
            KoinApplication(application = {
                androidContext(application)
                modules(landingModule)
            }) {
                TeeneTheme {
                    TeneeApp()
                    // A surface container using the 'background' color from the theme
                }
            }
        }
    }
}

