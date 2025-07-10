package com.example.teene.mysessions.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.teene.ui.animations.AuthorizationNavigationAnimations
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.LoginScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

/**
 * Created by 3100lari on 2025/02/12
 */
@Destination<RootGraph>(style = AuthorizationNavigationAnimations::class)
@Composable
fun MySessionsScreen(
    navigator: DestinationsNavigator
)
{
val belgradeLocations = listOf(
    LatLng(44.7866, 20.4489), // Belgrade center
    LatLng(44.8206, 20.4622), // Zemun
    LatLng(44.8000, 20.4667)  // New Belgrade
)
val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(belgradeLocations[0], 12f)
}
GoogleMap(
    modifier = Modifier.fillMaxSize(),
    cameraPositionState = cameraPositionState
) {
    belgradeLocations.forEachIndexed { index, location ->
        Marker(
            state = rememberMarkerState(position = location),
            title = "Marker ${index + 1}",
            snippet = "Location ${index + 1} in Belgrade"
        )
    }
}
}