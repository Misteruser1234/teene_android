package com.example.teene.home.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.teene.R
import com.example.teene.home.presentation.composables.ExploreHeader
import com.example.teene.home.presentation.composables.MapViewButton
import com.example.teene.home.presentation.composables.SearchBarWithFilters
import com.example.teene.home.presentation.composables.TopicsSelectableList
import com.example.teene.home.presentation.composables.TrainerItem
import com.example.teene.home.presentation.composables.TrainersInMap
import com.example.teene.home.presentation.models.TrainerWithCoordinates
import com.example.teene.home.presentation.viewModels.SportViewModel
import com.example.teene.ui.animations.AuthorizationNavigationAnimations
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.CoachDetailsScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

/**
 * Created by 3100lari on 2025/06/12
 */
@Destination<RootGraph>(style = AuthorizationNavigationAnimations::class)
@Composable
fun SportScreen(
    sportName: String,
    sportId: Int,
    navigator: DestinationsNavigator? = null
)
{
    val sportViewModel = koinViewModel<SportViewModel>()

    LaunchedEffect(sportId) {
        // You can use this effect to perform any side effects related to the sportId
        // For example, you might want to load data specific to the sportId
        sportViewModel.loadTrainersForSport(sportId)

    }

    Box(Modifier.fillMaxSize()) {
        var showMapView by remember { mutableStateOf(false) }
        Column(
            Modifier
                .fillMaxSize()
        ) {
            ExploreHeader(sportName)
            TopicsSelectableList(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 10.dp)
            )
            HorizontalDivider(color = Color(0x3F3F3F99))
            SearchBarWithFilters(modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp))

            val trainers = sportViewModel.trainersList.collectAsStateWithLifecycle().value

            if (showMapView)
            {
                TrainersInMap(trainers)
            }
            else
            {
                TrainersListView(trainers = trainers) { trainer ->
                    navigator?.navigate(
                        CoachDetailsScreenDestination(
                            trainerId = trainer.id,
                            trainerName = "${trainer.firstName} ${trainer.lastName}",
                            about = trainer.about ?: "",
                            trainerImageUrl = trainer.imageUrl,
                            featureNames = trainer.features.joinToString(", ") { feature -> feature.name },
                            locationLat = trainer.latitude ?: 0.0,
                            locationLng = trainer.longitude ?: 0.0,
                            rate = trainer.rate ?: 0.0,
                            currency = trainer.currency ?: "USD",
                            address = trainer.address ?: "",
                        )
                    )
                }
            }

        }

        MapViewButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            showMapView
        ) {
            showMapView = !showMapView
        }
    }

}

@Composable
private fun TrainersListView(
    trainers: List<TrainerWithCoordinates>,
    onTrainerClick: (TrainerWithCoordinates) -> Unit
)
{
    Text(
        text = pluralStringResource(R.plurals.coaches, trainers.size),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        style = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            color = Color.DarkGray
        )
    )

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(trainers) { trainer ->
            TrainerItem(
                imageUrl = trainer.imageUrl,
                name = "${trainer.firstName} ${trainer.lastName}",
                distance = "${trainer.distance} km", // Placeholder, replace with actual data
                price = "${trainer.rate} ${trainer.currency}", // Placeholder, replace with actual data
                rating = trainer.rating ?: 0.0, // Placeholder, replace with actual data
                onClick = {
                    onTrainerClick(trainer)
                    // Handle trainer item click, e.g., navigate to trainer details
                    //                        navigator?.navigate(SportScreenDestination(trainer.id))
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}