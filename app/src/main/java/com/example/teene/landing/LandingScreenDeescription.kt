package com.example.teene.landing

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teene.R

/**
 * Created by 3100lari on 2024/12/17
 */
@Composable
internal fun LandingViewPagerDescription(currentPage: Int)
{

    val titleString = when (currentPage)
    {
        0 -> stringResource(id = R.string.landing_title_1)
        1 -> stringResource(id = R.string.landing_title_2)
        2 -> stringResource(id = R.string.landing_title_3)
        else ->
        {
            stringResource(id = R.string.landing_title_1)
        }
    }

    val descriptionString = when (currentPage)
    {
        0 -> stringResource(id = R.string.landing_description_1)
        1 -> stringResource(id = R.string.landing_description_2)
        2 -> stringResource(id = R.string.landing_description_3)
        else ->
        {
            stringResource(id = R.string.landing_description_1)
        }
    }


    Column {
        Text(
            text = titleString.uppercase(),
            fontSize = 32.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            minLines = 3,
            lineHeight = 46.sp
        )
        Spacer(modifier =Modifier.height(6.dp))
        Text(
            text = descriptionString,
            fontSize = 14.sp,
            color = Color.White,
            minLines = 3,
            lineHeight = 24.sp
        )
    }
}