package com.example.teene.authentication.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teene.R

/**
 * Created by 3100lari on 2025/02/09
 */
@Composable
fun ToggleableGender(onSelectedGender: (GenderButtons) -> Unit = {})
{
    val buttons = listOf(
        GenderButtons.Male(stringResource(R.string.male)),
        GenderButtons.Female(stringResource(R.string.female))
    )
    var selectedButton by remember { mutableStateOf<GenderButtons>(GenderButtons.Unselected) } // Defaults to
    Column {
        Text(
            text = "Gender",
            modifier = Modifier.padding(bottom = 8.dp),
            fontSize = 14.sp,
            color = Color.Black
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            buttons.forEachIndexed { index, button ->
                SelectableButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .height(48.dp),
                    text = button.text ?: "",
                    isSelected = button == selectedButton,
                    onClick = {
                        val newSelection =
                            if (selectedButton == button) GenderButtons.Unselected else button
                        selectedButton = newSelection
                        onSelectedGender(selectedButton)

                    }
                )

                if (index < buttons.size - 1)
                {
                    Spacer(modifier = Modifier.width(16.dp))
                }
            }
        }
    }
}

@Preview
@Composable
fun ToggleableGenderPreview()
{
    var selectedGender by remember { mutableStateOf<String?>("") }

    ToggleableGender(onSelectedGender = {
        selectedGender = when (it)
        {
            is GenderButtons.Female -> "female"
            is GenderButtons.Male -> "male"
            GenderButtons.Unselected -> ""
        }
    })
}