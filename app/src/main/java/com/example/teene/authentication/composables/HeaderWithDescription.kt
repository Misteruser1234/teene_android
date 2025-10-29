package com.example.teene.authentication.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Created by 3100lari on 2025/07/22
 */
@Composable
fun HeaderWithDescription(headerText: String, descriptionText: String)
{
    Text(
        text = headerText,
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
        text = descriptionText,
        style = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 24.sp,
            letterSpacing = (-0.02).sp,
            textAlign = TextAlign.Start,
            color = Color(0xFF7A7A7A)
        )
    )
}
