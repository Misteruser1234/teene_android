package com.example.teene.authentication.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Created by 3100lari on 2025/02/09
 */
@Composable
fun SelectableButton(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    isEnabled: Boolean = true
) {
    OutlinedButton(
        modifier = modifier,
        onClick = onClick,
        border = BorderStroke(
            1.dp,
            color = if (isSelected) Color.Transparent else Color(0xFFE8E7E5)
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = when {
                isSelected -> MaterialTheme.colorScheme.primary
                !isEnabled -> Color.LightGray
                else -> Color.White
            },
            contentColor = when {
                isSelected -> Color.White
                !isEnabled -> Color.DarkGray
                else -> Color.Black
            }
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(text)
    }
}
