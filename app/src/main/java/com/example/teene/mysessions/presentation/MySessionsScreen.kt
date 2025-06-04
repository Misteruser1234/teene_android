package com.example.teene.mysessions.presentation

import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.teene.ui.animations.AuthorizationNavigationAnimations
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.LoginScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

/**
 * Created by 3100lari on 2025/02/12
 */
@Destination<RootGraph>(style = AuthorizationNavigationAnimations::class)
@Composable
fun MySessionsScreen(
    navigator: DestinationsNavigator
)
{
    Text("Welcome to MySessions")
}