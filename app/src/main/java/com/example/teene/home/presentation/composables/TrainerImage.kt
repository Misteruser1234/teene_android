package com.example.teene.home.presentation.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.teene.R

/**
 * Created by 3100lari on 2025/07/03
 */
@Composable
fun TrainerImage(imageUrl: String?, modifier: Modifier)
{
    val model = if (imageUrl.isNullOrEmpty())
    {
        R.drawable.trainer_image_placeholder
    }
    else
    {
        ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build()
    }

    AsyncImage(
        model = model,
        contentDescription = "Trainer Image",
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}