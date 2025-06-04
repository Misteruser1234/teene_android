package com.example.teene.authentication.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teene.R
import com.example.teene.ui.composables.ColoredAndClickableText
import com.ramcosta.composedestinations.generated.destinations.LoginScreenDestination

@Composable
fun AuthenticationHeader(
    modifier: Modifier = Modifier,
    headerTitle: String,
    headerDescription: String,
    clickableString: String? = null,
    onClick: () -> Unit = { }
)
{
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            modifier = Modifier.size(92.dp),
            contentScale = ContentScale.Crop,
            painter = painterResource(id = R.drawable.logo),
            contentDescription = stringResource(id = R.string.man_climbing)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = headerTitle,
            style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 32.sp,
                letterSpacing = (-0.02).sp,
                textAlign = TextAlign.Center,
                color = Color.Black
            )
        )
        Spacer(Modifier.height(12.dp))
        ColoredAndClickableText(
            Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            firstString = headerDescription,
            secondString = clickableString ?: ""
        ) {
            onClick()
        }
    }
}

@Preview
@Composable
fun AuthHeaderPreview()
{
    AuthenticationHeader(
        headerTitle = "Welcome Back!",
        headerDescription = "Start your journey with us",
        clickableString = "Click here",
        onClick = {}

    )
}