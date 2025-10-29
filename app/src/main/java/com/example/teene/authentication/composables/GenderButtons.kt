package com.example.teene.authentication.composables

/**
 * Created by 3100lari on 2025/02/09
 */

sealed class GenderButtons(val text: String?)
{
    data object Unselected : GenderButtons(null) // No button is selected
    data class Male(val gender: String) : GenderButtons(gender)
    data class Female(val gender: String) : GenderButtons(gender)
}