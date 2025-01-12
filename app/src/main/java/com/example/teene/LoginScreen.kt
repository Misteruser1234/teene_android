package com.example.teene

import androidx.compose.foundation.content.MediaType.Companion.Text
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@Destination<RootGraph>() // sets this as the start destination of the "root" nav graph
@Composable
fun LoginScreen(
    navigator: DestinationsNavigator
) {
    Text(text = "Navigated to login", color = Color.Red)
}