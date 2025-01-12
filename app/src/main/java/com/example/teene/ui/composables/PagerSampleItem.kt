package com.example.teene.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.teene.R

@Composable
fun PagerSampleItem(page: Int)
{
    val imageResourceId = when (page)
    {
        0 -> R.drawable.landing_image_1
        1 -> R.drawable.landing_image_2
        2 -> R.drawable.landing_image_3
        else ->
        {
            R.drawable.landing_image_2
        }
    }

    Image(
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
        painter = painterResource(id = imageResourceId),
        contentDescription = stringResource(id = R.string.man_climbing)
    )
}
