package com.example.teene.authentication.forgotpassword

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teene.authentication.composables.BackToLogin
import com.example.teene.authentication.composables.ButtonWithIcon
import com.example.teene.ui.animations.AuthorizationNavigationAnimations
import com.example.teene.ui.composables.ColoredAndClickableText
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.LoginScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

/**
 * Created by 3100lari on 2025/07/22
 */
@Destination<RootGraph>(style = AuthorizationNavigationAnimations::class)
@Composable
fun PasswordResetScreen(
    navigator: DestinationsNavigator,
    userEmail: String
)
{
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 64.dp)
    ) {
        Text(
            text = "Password reset",
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
        DescriptionWithColoredMail(
            firstString = "We sent a code to",
            secondString = userEmail
        )
        Spacer(Modifier.height(32.dp))

        var isFourDigits by remember { mutableStateOf(false) }

        CodeInputScreen(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .wrapContentWidth(),
            isFourDigits = isFourDigits,
            onSubmitCode = { code -> print("Submitted code: $code") })

        Spacer(Modifier.height(32.dp))

        ButtonWithIcon(
            modifier = Modifier.fillMaxWidth(),
            iconId = null,
            text = "Continue",
            onClick = {
                //                navigator.navigate(PasswordResetScreenDestination(userEmail = emailText))
                // Handle send reset instructions click
                // For example, call a ViewModel function to handle the logic
            }
        )
        Spacer(Modifier.height(32.dp))

        ColoredAndClickableText(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally),
            firstString = "Didn't receive the Email?",
            secondString = "Click to resend",
            onClick = {

            }
        )

        Spacer(Modifier.height(16.dp))

        BackToLogin(
            Modifier
                .align(Alignment.CenterHorizontally)
                .clickable(onClick = { navigator.navigate(LoginScreenDestination) })
        )

    }
}


@Composable
private fun DescriptionWithColoredMail(
    modifier: Modifier = Modifier,
    firstString: String,
    secondString: String,
)
{
    val annotatedString = buildAnnotatedString {
        append(firstString)
        append(" ")
        withStyle(
            SpanStyle(
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        ) {
            append(secondString)
        }
    }

    Text(
        modifier = modifier,
        text = annotatedString,
        textAlign = TextAlign.Center,
    )

}

@Composable
private fun CodeInputScreen(
    modifier: Modifier = Modifier, isFourDigits: Boolean, onSubmitCode:
        (String)
    ->
    Unit = { }
)
{
    val focusManager = LocalFocusManager.current
    val (focusRequester1, focusRequester2, focusRequester3, focusRequester4) =
        List(4) { remember { FocusRequester() } }

    var code1 by remember { mutableStateOf("") }
    var code2 by remember { mutableStateOf("") }
    var code3 by remember { mutableStateOf("") }
    var code4 by remember { mutableStateOf("") }

    LaunchedEffect(code1, code2, code3, code4) {
        if (code1.length == 1 && code2.length == 1 && code3.length == 1 && code4.length == 1)
        {
            val fullCode = code1 + code2 + code3 + code4
            onSubmitCode(fullCode)
        }
    }

    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(
            Pair(code1, { newValue: String ->
                if (newValue.length <= 1)
                {
                    code1 = newValue
                    if (newValue.isNotEmpty()) focusRequester2.requestFocus()
                }
            }),
            Pair(code2, { newValue: String ->
                if (newValue.length <= 1)
                {
                    code2 = newValue
                    if (newValue.isNotEmpty()) focusRequester3.requestFocus()
                }
            }),
            Pair(code3, { newValue: String ->
                if (newValue.length <= 1)
                {
                    code3 = newValue
                    if (newValue.isNotEmpty()) focusRequester4.requestFocus()
                }
            }),
            Pair(code4, { newValue: String ->
                if (newValue.length <= 1)
                {
                    code4 = newValue
                    if (newValue.isNotEmpty()) focusManager.clearFocus()
                }
            })
        ).forEachIndexed { index, (value, onValueChange) ->
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                modifier = Modifier
                    .width(48.dp)
                    .focusRequester(
                        listOf(
                            focusRequester1,
                            focusRequester2,
                            focusRequester3,
                            focusRequester4
                        )[index]
                    ),
                textStyle = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    }
}


