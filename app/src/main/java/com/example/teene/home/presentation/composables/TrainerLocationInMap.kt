package com.example.teene.home.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.teene.R
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

/**
 * Created by 3100lari on 2025/07/10
 */
@Composable
fun TrainerLocationInMap(
    latitude: Double,
    longitude: Double,
    name: String,
    modifier: Modifier = Modifier
)
{
    val pin = LatLng(latitude, longitude)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(pin, 12f)
    }
    GoogleMap(
        cameraPositionState = cameraPositionState, modifier = modifier
    ) {

        MarkerComposable(
            state = rememberMarkerState(position = pin), anchor = Offset(0.5f, 1.0f), title = name
        ) {
            Box(
                modifier = Modifier
                    .background(Color(0xFF03A9C5), shape = RoundedCornerShape(8.dp))
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_bottom_bar_trophy),
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier
                        .size(20.dp)
                        .background(Color.White, shape = RoundedCornerShape(4.dp))
                )
            }
        }
    }
}