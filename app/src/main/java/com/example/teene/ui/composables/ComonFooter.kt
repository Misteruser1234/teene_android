package com.example.teene.ui.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.RowScope
import com.example.teene.authentication.composables.ButtonWithIcon

/**
 * Common footer used at the bottom of filter-like screens.
 *
 * Structure:
 * - Divider
 * - Spacer(16.dp)
 * - Row(left slot + primary action button)
 *
 * The [leftContent] is a composable slot so callers can provide anything
 * (e.g., an underlined Reset text, a counter, etc.).
 * The primary action button is the app-standard [ButtonWithIcon] with
 * customizable text, enabled state, optional icon, and click handler.
 */
@Composable
fun ComonFooter(
    modifier: Modifier = Modifier,
    leftContent: @Composable RowScope.() -> Unit,
    buttonText: String,
    onButtonClick: () -> Unit,
    buttonEnabled: Boolean = true,
    @DrawableRes buttonIconId: Int? = null,
) {
    Column(modifier = modifier) {
        HorizontalDivider(color = Color(0x3F3F3F99))
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            leftContent()
            ButtonWithIcon(
                iconId = buttonIconId,
                text = buttonText,
                enabled = buttonEnabled,
                onClick = onButtonClick
            )
        }
    }
}