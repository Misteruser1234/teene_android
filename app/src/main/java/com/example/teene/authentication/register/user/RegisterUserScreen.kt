package com.example.teene.authentication.register.user

import ToggleablePhysicalLevel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teene.R
import com.example.teene.authentication.AuthenticationNavGraph
import com.example.teene.authentication.composables.ButtonWithIcon
import com.example.teene.authentication.composables.GenderButtons
import com.example.teene.authentication.composables.ToggleableGender
import com.example.teene.ui.animations.AuthorizationNavigationAnimations
import com.example.teene.ui.viewModel.RegisterViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.ExploreScreenDestination
import com.ramcosta.composedestinations.generated.destinations.RegisterUserScreenDestination

import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

/**
 * Created by 3100lari on 2025/02/09
 */
@Destination<AuthenticationNavGraph>(style = AuthorizationNavigationAnimations::class)
@Composable
fun RegisterUserScreen(
    email: String,
    password: String,
    navigator: DestinationsNavigator? = null
) {
    val registerViewModel = koinViewModel<RegisterViewModel>()
    var ageText by rememberSaveable { mutableStateOf("") }
    var phoneNumberText by rememberSaveable { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf<String?>("") }
    Column(
        Modifier
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Almost ready!",
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 32.sp,
                    letterSpacing = (-0.02).sp,
                    textAlign = TextAlign.Start,
                    color = Color.Black
                )
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = "To help the coach prepare more effectively for your session, it would be useful to know a few basic details about you",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 24.sp,
                    letterSpacing = (-0.02).sp,
                    textAlign = TextAlign.Start,
                    color = Color(0xFF7A7A7A)
                )
            )
            Spacer(Modifier.height(40.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = ageText,
                onValueChange = { ageText = it },
                placeholder = { Text("Type your age here") },
                label = { Text("Age") },
                colors =
                    OutlinedTextFieldDefaults.colors(
                        unfocusedTextColor = Color(0xFF7A7A7A),
                        unfocusedBorderColor = Color(0xFFF0F0F0),
                        focusedBorderColor = Color.Black,
                        focusedLabelColor = Color.Black
                    )
            )

            Spacer(Modifier.height(40.dp))
            ToggleableGender(onSelectedGender = {
                selectedGender = when (it) {
                    is GenderButtons.Female -> "female"
                    is GenderButtons.Male -> "male"
                    GenderButtons.Unselected -> ""
                }
            })

            Spacer(Modifier.height(40.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = phoneNumberText,
                onValueChange = { phoneNumberText = it },
                placeholder = { Text("Type your phone number here") },
                label = { Text("Phone Number") },
                colors =
                    OutlinedTextFieldDefaults.colors(
                        unfocusedTextColor = Color(0xFF7A7A7A),
                        unfocusedBorderColor = Color(0xFFF0F0F0),
                        focusedBorderColor = Color.Black,
                        focusedLabelColor = Color.Black
                    ),

                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            Spacer(Modifier.height(40.dp))

            ToggleablePhysicalLevel(onSelectedLevel = {})
        }

        Column {

            HorizontalDivider(Modifier.padding(top = 32.dp), color = Color(0x3F3F3F99))

            Spacer(Modifier.height(24.dp))
            ButtonWithIcon(
                enabled = true,
                modifier = Modifier.fillMaxWidth(),
                iconId = R.drawable.rocket_1,
                text = "Continue to the App"
            ) {
                registerViewModel.createUser(
                    emailInput = email,
                    passwordInput = password,
                    age = ageText.toIntOrNull(),
                    gender = selectedGender,
                    phoneNumber = phoneNumberText,
                )
                navigator?.navigate(ExploreScreenDestination) {
                    launchSingleTop = true

                    popUpTo(RegisterUserScreenDestination) {
                        inclusive = true
                    }
                }
            }
        }

    }
}

@Preview
@Composable
fun AlmostReadyScreenPreview() {
    RegisterUserScreen(
        "testEmail", "testPassword",
    )
}