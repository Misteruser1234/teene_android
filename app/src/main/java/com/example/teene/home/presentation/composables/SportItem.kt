package com.example.teene.home.presentation.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
//import coil3.compose.AsyncImage
//import coil3.request.ImageRequest
//import coil3.request.crossfade
import com.example.teene.R
import com.example.teene.home.presentation.models.SportWithTrainers
import com.example.teene.home.presentation.models.TrainerNumberCategory

/**
 * Created by 3100lari on 2025/02/16
 */

@Composable
fun SportItem(
    id: Int,
    trainerNumberCategoryText: String,
    sportName: String,
    imageUrl: String,
    onClick: () -> Unit
)
{
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(165.dp)
            .clickable(onClick= {onClick.invoke()})// Adjust height as needed
    ) {


        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("$imageUrl")
                .crossfade(true)
                .build(),
            contentDescription = "Sport Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Overlaying Text Description
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            SportItemDescription(
                trainerNumberCategoryText = trainerNumberCategoryText,
                sportName = sportName
            )
        }
    }
}

@Preview
@Composable
fun SportItemPreview()
{
    val sport = SportWithTrainers(id= 15,"Paddel", TrainerNumberCategory.TEN_PLUS,"")
    SportItem(
        id = sport.id,
        trainerNumberCategoryText = sport.trainerNumberCategory.displayText,
        sportName = sport.sportName,
        imageUrl = " ",
        onClick = { /* Handle click */ }
    )
}