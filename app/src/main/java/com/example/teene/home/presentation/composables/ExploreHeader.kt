package com.example.teene.home.presentation.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teene.R

/**
 * Created by 3100lari on 2025/02/13
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonHeader(headerText: String, onMapClick: (() -> Unit)? = null)
{
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 32.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = headerText,
            style = TextStyle(
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 32.sp,
                letterSpacing = (-0.02).sp,
                textAlign = TextAlign.Start,
                color = Color.Black
            )
        )
        if (onMapClick != null) {
            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .border(width = 1.dp, color = Color(0xFFF0F0F0), shape = CircleShape)
                    .clickable { onMapClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(id = R.drawable.map_1),
                    contentDescription = "Open coaches map",
                )
            }
        }
    }
}

@Preview
@Composable
fun ExploreHeaderPreview()
{
    CommonHeader("Explore")
}
