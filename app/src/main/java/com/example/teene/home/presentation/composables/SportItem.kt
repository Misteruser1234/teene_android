package com.example.teene.home.presentation.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.teene.R
import com.example.teene.home.presentation.models.SportWithTrainers
import com.example.teene.home.presentation.models.TrainerNumberCategory

/**
 * Created by 3100lari on 2025/02/16
 */

@Composable
fun SportItem(
    trainerNumberCategoryText: String,
    sportName: String,
    imageUrl: String
)
{
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(165.dp) // Adjust height as needed
    ) {
        // Background Image
//        Image(
//            painter = painterResource(id = imageRes),
//            contentDescription = "Item description",
//            modifier = Modifier.fillMaxSize(),
//            contentScale = ContentScale.Crop
//        )

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
    val sport = SportWithTrainers("Paddel", TrainerNumberCategory.TEN_PLUS,"")
    SportItem(
        trainerNumberCategoryText = sport.trainerNumberCategory.displayText,
        sportName = sport.sportName,
        imageUrl = " " // Replace with your actual image resource
    )
}