package com.example.teene.authentication.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.teene.ui.theme.TeeneTheme

/**
 * Created by 3100lari on 2025/02/09
 */


@Composable
fun ButtonWithIcon(
    modifier: Modifier = Modifier,
    iconId: Int?,
    text: String,
    enabled: Boolean = false,
    onClick: () -> Unit,
)
{
        Button(
            modifier = modifier,
            onClick = { onClick.invoke() },
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp),
            shape = RoundedCornerShape(4.dp)
        ) {
            iconId?.let {
                Icon(
                    modifier = Modifier.size(16.dp),
                    painter = painterResource(id = iconId),
                    contentDescription = "Continue to the ",
                )
                Spacer(Modifier.width(12.dp))
            }

            Text(
                text = text,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp,
                    letterSpacing = (-0.02).sp,
                    textAlign = TextAlign.Center,
                )
            )
        }
}
