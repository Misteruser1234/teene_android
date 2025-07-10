package com.example.teene.home.presentation.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teene.R

/**
 * Created by 3100lari on 2025/06/12
 */
@Composable
fun TrainerItem(
    imageUrl: String?,
    name: String,
//    availability: String,
    distance: String,
    price: String,
    rating: Double,
    onClick: () -> Unit
)
{

    Row(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clickable {
                onClick()
            }
    ) {
        TrainerImage(
            imageUrl = imageUrl,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(100.dp)
                .fillMaxHeight()
                .padding(8.dp)
        )
        TrainerRowInformation(
            name = name,
//            availability = availability,
            distance = distance,
            price = price,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f)
        )

        TrainerRating(
            rating = rating,
            modifier = Modifier
        )
    }
}

@Composable
private fun TrainerRating(rating: Double, modifier: Modifier)
{
    Row(
        modifier = modifier
    ) {
        Icon(
            painter = androidx.compose.ui.res.painterResource(id = R.drawable.star),
            contentDescription = "Rating Star",
            modifier = Modifier
                .align(Alignment.CenterVertically)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterVertically),
            style = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = Color.Black,
            ),
            text = "$rating"
        )
    }
}

@Composable
private fun TrainerRowInformation(
    name: String,
//    availability: String,
    distance: String,
    price: String,
    modifier: Modifier
)
{
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = name,
            style = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 22.sp
            )
        )
        Spacer(Modifier.height(3.dp))
//        Text(
//            text = availability,
//            style = TextStyle(
//                fontWeight = FontWeight.Normal,
//                fontSize = 13.sp,
//                color = Color.DarkGray,
//            )
//        )
        Spacer(Modifier.height(3.dp))
        Text(
            text = distance,
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = Color.DarkGray,
            )
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = price,
            style = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                lineHeight = 20.sp,
            )
        )
    }
}


@Preview
@Composable
fun TrainerItemPreview()
{
    TrainerItem(
        imageUrl = null,
        name = "Pera Peric",
//        availability = "Available",
        distance = "5 km",
        price = "$50/hr",
        rating = 5.58,
        onClick = {}
    )
}