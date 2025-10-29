package com.example.teene.profile.presentation.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.teene.ui.animations.AuthorizationNavigationAnimations
import com.example.teene.ui.viewModel.RegisterViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.LoginScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

/**
 * Created by 3100lari on 2025/02/12
 */
@Destination<RootGraph>(style = AuthorizationNavigationAnimations::class)
@Composable
fun ProfileScreen(
    navigator: DestinationsNavigator
)
{
    Column {
        val profileViewModel = koinViewModel<ProfileViewModel>()
        Button(modifier = Modifier.wrapContentSize(), onClick = {
            profileViewModel.logout()
            navigator.navigate(LoginScreenDestination)
        }) {
            Text("Logout")
        }
    }
}