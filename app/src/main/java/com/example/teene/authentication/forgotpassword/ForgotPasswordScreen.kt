package com.example.teene.authentication.forgotpassword

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.teene.authentication.composables.BackToLogin
import com.example.teene.authentication.composables.ButtonWithIcon
import com.example.teene.authentication.composables.EmailInputField
import com.example.teene.authentication.composables.HeaderWithDescription
import com.example.teene.ui.animations.AuthorizationNavigationAnimations
import com.example.teene.ui.viewModel.ForgotPasswordViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.LoginScreenDestination
import com.ramcosta.composedestinations.generated.destinations.PasswordResetScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

/**
 * Created by 3100lari on 2025/07/22
 */
@Destination<RootGraph>(style = AuthorizationNavigationAnimations::class)
@Composable
fun ForgotPasswordScreen(
    navigator: DestinationsNavigator,
)
{
    val forgotPasswordViewModel: ForgotPasswordViewModel = koinViewModel()

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 64.dp)
    ) {

        HeaderWithDescription(
            "Forgot Password",
            "No worries! We'll send you the reset instructions"
        )

        Spacer(Modifier.height(32.dp))
        var email by remember { mutableStateOf("") }

        val isEmailValid = remember(email) {
            android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        EmailInputField(
            email = email,
            isEmailValid = isEmailValid,
            onEmailChange = { email = it }
        )
        Spacer(Modifier.height(16.dp))

        ButtonWithIcon(
            modifier = Modifier.fillMaxWidth(),
            iconId = null,
            enabled = isEmailValid,
            text = "Reset Password",
            onClick = {
                forgotPasswordViewModel.forgotPassword(email)
                navigator.navigate(PasswordResetScreenDestination(userEmail = email))
                // Handle send reset instructions click
                // For example, call a ViewModel function to handle the logic
            }
        )
        Spacer(Modifier.height(32.dp))

        BackToLogin(
            Modifier
                .align(Alignment.CenterHorizontally)
                .clickable(onClick = { navigator.navigate(LoginScreenDestination) })
        )


    }
}