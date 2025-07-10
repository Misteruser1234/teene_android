package com.example.teene.home.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teene.R
import com.example.teene.home.presentation.composables.FooterWithAction
import com.example.teene.home.presentation.composables.TrainerImage
import com.example.teene.ui.animations.AuthorizationNavigationAnimations
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph

/**
 * Created by 3100lari on 2025/07/03
 */
@Destination<RootGraph>(style = AuthorizationNavigationAnimations::class)
@Composable
fun CoachDetailsScreen(
    trainerName: String,
    trainerImageUrl: String?,
    about: String,
    featureNames: String,
    locationLat: Double,
    locationLng: Double,
    rate: Double,
    currency: String
)
{
    Column(modifier = Modifier.fillMaxSize()) {
        TrainerImage(
            trainerImageUrl, modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
        Column(Modifier.padding(horizontal = 24.dp, vertical = 14.dp)) {
            Text(
                text = trainerName,
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(Modifier.height(8.dp))

            Text(
                text = featureNames,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.LightGray
                )
            )

            Spacer(Modifier.height(64.dp))

            Text(
                text = "About",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = about,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.LightGray
                )
            )
            Spacer(modifier = Modifier.weight(1f))

            FooterWithAction(
                leftContent = {
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("$rate ")
                            }
                            append(currency)
                        },
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                },
                rightContent = {
                    Button(
                        onClick = { /* Book action */ },
                        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A5D7)) // Similar to your image
                    ) {
                        Icon(painter = painterResource(R.drawable.rocket_1), contentDescription = null)
                        Spacer(modifier = Modifier.width(14.dp))
                        Text("Book")
                    }
                }
            )

        }
    }

}