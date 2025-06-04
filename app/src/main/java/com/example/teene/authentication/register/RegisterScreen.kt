package com.example.teene.authentication.register

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teene.R
import com.example.teene.authentication.AuthenticationNavGraph
import com.example.teene.authentication.composables.AuthenticationHeader
import com.example.teene.ui.animations.AuthorizationNavigationAnimations
import com.example.teene.ui.composables.ColoredAndClickableText
import com.example.teene.ui.viewModel.RegisterViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.generated.destinations.LoginScreenDestination
import com.ramcosta.composedestinations.generated.destinations.RegisterTrainerScreenDestination
import com.ramcosta.composedestinations.generated.destinations.RegisterUserScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@Destination<AuthenticationNavGraph>(style = AuthorizationNavigationAnimations::class)
@Composable
fun RegisterScreen(
    navigator: DestinationsNavigator
)
{
    val registerViewModel = koinViewModel<RegisterViewModel>()
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        AuthenticationHeader(
            Modifier.padding(top = 64.dp, bottom = 32.dp),
            headerTitle = "Welcome!",
            headerDescription = "Start you journey as a trainer",
            clickableString = "Click here",
            onClick = {
                Log.i("BOBAN", "clicking")
                navigator.navigate(RegisterTrainerScreenDestination) {
                    launchSingleTop = true
                }
            }
        )
        var emailText by rememberSaveable { mutableStateOf("") }

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            value = emailText,
            onValueChange = { emailText = it },
            placeholder = { Text("Type your Email address here") },
            label = { Text("Email") },
            colors =
                OutlinedTextFieldDefaults.colors(
                    unfocusedTextColor = Color(0xFF7A7A7A),
                    unfocusedBorderColor = Color(0xFFF0F0F0),
                    focusedBorderColor = Color.Black,
                    focusedLabelColor = Color.Black
                )
        )
        Spacer(Modifier.height(16.dp))

        val showPassword = remember { mutableStateOf(false) }
        var passwordText by rememberSaveable { mutableStateOf("") }

        var passwordError by remember { mutableStateOf("") }
        val matchError = remember { mutableStateOf(false) }

        // Password regex: At least 8 characters, includes a digit, an uppercase letter, and a special character
        val passwordPattern = Regex("^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%^&*(),.?\":{}|<>]).{8,}$")
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            value = passwordText,
            isError = passwordError.isNotEmpty(),
            supportingText = { Text(passwordError) },
            placeholder = { Text("Enter your password") },
            visualTransformation = if (showPassword.value) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val (imageResourceId, iconColor) = if (showPassword.value)
                {
                    Pair(
                        R.drawable.eye_closed,
                        Color(0xFF7A7A7A)
                    )
                }
                else
                {
                    Pair(R.drawable.view_1, Color(0xFF7A7A7A))
                }

                IconButton(onClick = { showPassword.value = !showPassword.value }) {
                    Icon(
                        painter = painterResource(id = imageResourceId),
                        contentDescription = "Visibility",
                        tint = iconColor
                    )
                }
            },
            onValueChange = {
                passwordText = it
                passwordError = when
                {
                    passwordText.isEmpty() -> "Password cannot be empty"
                    !passwordPattern.matches(passwordText) -> "Password must be at least 8 characters, include a digit, an uppercase letter, and a special character"
                    else -> ""
                }
            },
            label = { Text("Password") },
            colors =
                OutlinedTextFieldDefaults.colors(
                    unfocusedTextColor = Color(0xFF7A7A7A),
                    unfocusedBorderColor = Color(0xFFF0F0F0),
                    focusedBorderColor = Color.Black,
                    focusedLabelColor = Color.Black
                )
        )

        var confirmPasswordText by rememberSaveable { mutableStateOf("") }
        var confirmPasswordError by remember { mutableStateOf("") }

        // Password regex: At least 8 characters, includes a digit, an uppercase letter, and a special character
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            value = confirmPasswordText,
            isError = confirmPasswordError.isNotEmpty(),
            supportingText = { Text(confirmPasswordError) },
            placeholder = { Text("Please enter the password agai.") },
            visualTransformation = if (showPassword.value) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val (imageResourceId, iconColor) = if (showPassword.value)
                {
                    Pair(
                        R.drawable.eye_closed,
                        Color(0xFF7A7A7A)
                    )
                }
                else
                {
                    Pair(R.drawable.view_1, Color(0xFF7A7A7A))
                }

                IconButton(onClick = { showPassword.value = !showPassword.value }) {
                    Icon(
                        painter = painterResource(id = imageResourceId),
                        contentDescription = "Visibility",
                        tint = iconColor
                    )
                }
            },
            onValueChange = {
                confirmPasswordText = it
                confirmPasswordError = when
                {
                    confirmPasswordText != passwordText -> "Please make sure that the password " +
                        "and password confirmation match."

                    else -> ""
                }
            },
            label = { Text("Confirm password") },
            colors =
                OutlinedTextFieldDefaults.colors(
                    unfocusedTextColor = Color(0xFF7A7A7A),
                    unfocusedBorderColor = Color(0xFFF0F0F0),
                    focusedBorderColor = Color.Black,
                    focusedLabelColor = Color.Black
                )
        )

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                navigator.navigate(
                    RegisterUserScreenDestination(
                        email = emailText,
                        password = passwordText
                    )
                )
                registerViewModel.createUser(
                    emailInput = emailText,
                    passwordInput = passwordText
                )
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF7A7A7A),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Register",
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp,
                    letterSpacing = (-0.02).sp,
                    textAlign = TextAlign.Center
                )
            )
        }
        Spacer(Modifier.height(24.dp))
        Text(
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
            text = "- or -",
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
        OutlinedButton(
            onClick = { /* Handle login logic here */ },
            border = BorderStroke(1.dp, Color(0xFFE8E7E5)),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Image(
                modifier = Modifier.size(32.dp),
                painter = painterResource(id = R.drawable.google_logo),
                contentDescription = "Google icon",

                )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "Continue with Google", textAlign = TextAlign.Center,
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
        Spacer(Modifier.height(16.dp))
        OutlinedButton(
            onClick = { /* Handle login logic here */ },
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(1.dp, Color(0xFFE8E7E5)),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.apple_logo),
                contentDescription = "Apple Icon",
                tint = Color.Black
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "Continue with Apple", textAlign = TextAlign.Center,
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
        Spacer(Modifier.height(24.dp))
        ColoredAndClickableText(
            Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            firstString = "Already have an account?",
            secondString = "Login"
        ) {
            navigator.navigate(LoginScreenDestination) {
                launchSingleTop = true
            }
        }
    }
}