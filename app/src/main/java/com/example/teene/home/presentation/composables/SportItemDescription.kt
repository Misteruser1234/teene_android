package com.example.teene.home.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Created by 3100lari on 2025/02/16
 */

@Composable
fun SportItemDescription(
    trainerNumberCategoryText: String,
    sportName: String,
    highlightQuery: String? = null
)
{
    Column {
        Text(
            text = trainerNumberCategoryText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White, // Ensuring text is fully white
            modifier = Modifier
                .clip(RoundedCornerShape(3.dp)) // Apply corner radius
                .background(Color(0x12FFFFFF)) // White with transparency
                .padding(8.dp) // Padding inside the background
        )
        Spacer(modifier = Modifier.height(1.dp))
        val annotated = run {
            val q = highlightQuery?.takeIf { it.isNotBlank() }
            if (q != null) {
                val start = sportName.indexOf(q, ignoreCase = true)
                if (start >= 0) {
                    val end = start + q.length
                    androidx.compose.ui.text.buildAnnotatedString {
                        append(sportName)
                        addStyle(
                            style = androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold),
                            start = start,
                            end = end
                        )
                    }
                } else {
                    androidx.compose.ui.text.AnnotatedString(sportName)
                }
            } else {
                androidx.compose.ui.text.AnnotatedString(sportName)
            }
        }
        androidx.compose.material3.Text(
            text = annotated,
            fontSize = 32.sp,
            color = Color.White,
            lineHeight = 46.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Preview
@Composable
fun SportItemDescriptionPreview()
{
    Box(Modifier.fillMaxSize().background(Color.Black)) {
        SportItemDescription("50+ trainers", "PADDEL")
    }
}


