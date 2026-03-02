package com.example.teene.authentication.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teene.R
import com.example.teene.authentication.AuthenticationNavGraph
import com.example.teene.authentication.composables.ButtonWithIcon
import com.example.teene.ui.animations.AuthorizationNavigationAnimations
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.generated.destinations.RegisterTrainerScreenDestination
import com.ramcosta.composedestinations.generated.destinations.RegisterUserScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

/**
 * Post-signup choice screen shown right after user taps "Register".
 * Lets the user choose to continue as a regular user or proceed as a trainer.
 */
@Destination<AuthenticationNavGraph>(style = AuthorizationNavigationAnimations::class)
@Composable
fun PostSignupChoiceScreen(
    email: String,
    password: String,
    navigator: DestinationsNavigator? = null
) {
    val landingVm = org.koin.androidx.compose.koinViewModel<com.example.teene.ui.viewModel.LandingViewModel>()
    PostSignupChoiceScreenContent(
        email = email,
        password = password,
        onContinueAsUser = { e, p ->
            landingVm.setNeedsPostSignupChoice(true)
            landingVm.setTrainerOnboardingSelected(false)
            navigator?.navigate(
                RegisterUserScreenDestination(
                    email = e,
                    password = p
                )
            ) {
                launchSingleTop = true
            }
        },
        onProceedAsTrainer = {
            landingVm.setTrainerOnboardingSelected(true)
            landingVm.setNeedsPostSignupChoice(true)
            navigator?.navigate(RegisterTrainerScreenDestination) {
                launchSingleTop = true
            }
        }
    )
}

@Composable
fun PostSignupChoiceScreenContent(
    email: String,
    password: String,
    onContinueAsUser: (String, String) -> Unit,
    onProceedAsTrainer: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Congratulations!",
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 32.sp,
                    letterSpacing = (-0.02).sp,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "You have successfully registered.",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 24.sp,
                    letterSpacing = (-0.02).sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF7A7A7A)
                )
            )
            Spacer(Modifier.height(24.dp))

            // Placeholder icon (the client will change later); choose any existing drawable
            Image(
                modifier = Modifier
                    .size(200.dp),
                painter = painterResource(id = R.drawable.successful_icon_muscle),
                contentDescription = "Success icon",
                contentScale = ContentScale.Fit
            )

            Spacer(Modifier.height(24.dp))
            Text(
                text = "How would you like to proceed?",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 22.sp,
                    letterSpacing = (-0.02).sp,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
            )
        }

        // Bottom buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            // Continue as regular user (outlined style, left icon)
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onContinueAsUser(email, password) },
                shape = RoundedCornerShape(4.dp),
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp)
            ) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    painter = painterResource(id = R.drawable.person_icon),
                    contentDescription = "User icon",
                    tint = Color.Black
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Continue as regular user",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 20.sp,
                        letterSpacing = (-0.02).sp,
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )
                )
            }

            Spacer(Modifier.height(12.dp))

            // Proceed as trainer (primary blue background, left icon)
            ButtonWithIcon(
                modifier = Modifier.fillMaxWidth(),
                iconId = R.drawable.rocket_1,
                text = "Proceed as trainer",
                enabled = true,
                containerColor = Color(0xFF009DC3),
                contentColor = Color.White
            ) {
                onProceedAsTrainer()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PostSignupChoiceScreenContentPreviewDefault() {
    PostSignupChoiceScreenContent(
        email = "john@doe.io",
        password = "Password1!",
        onContinueAsUser = { _, _ -> },
        onProceedAsTrainer = {}
    )
}

@Preview(showBackground = true, widthDp = 320, heightDp = 640)
@Composable
private fun PostSignupChoiceScreenContentPreviewSmall() {
    PostSignupChoiceScreenContent(
        email = "very.long.email.address@example.com",
        password = "••••••••",
        onContinueAsUser = { _, _ -> },
        onProceedAsTrainer = {}
    )
}
