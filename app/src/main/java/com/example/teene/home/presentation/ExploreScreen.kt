package com.example.teene.home.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.teene.R
import com.example.teene.home.presentation.composables.ExploreHeader
import com.example.teene.home.presentation.composables.SearchBarWithFilters
import com.example.teene.home.presentation.composables.SportItem
import com.example.teene.home.presentation.composables.TopicsSelectableList
import com.example.teene.home.presentation.viewModels.ExploreViewModel
import com.example.teene.ui.animations.AuthorizationNavigationAnimations
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.SportScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

/**
 * Created by 3100lari on 2025/02/09
 */
@Destination<RootGraph>(style = AuthorizationNavigationAnimations::class)
@Composable
fun ExploreScreen(
    navigator: DestinationsNavigator? = null
)
{
    val exploreViewModel = koinViewModel<ExploreViewModel>()
    Column(
        Modifier
            .fillMaxWidth()
    ) {
        ExploreHeader("Explore")
        TopicsSelectableList(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 10.dp)
        )
        HorizontalDivider(color = Color(0x3F3F3F99))
        SearchBarWithFilters(modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp))

        val sports = exploreViewModel.sportsList.collectAsStateWithLifecycle()
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(sports.value) { sport ->
                SportItem(
                    id = sport.id,
                    trainerNumberCategoryText = sport.trainerNumberCategory.displayText,
                    sportName = sport.sportName.uppercase(),
                    imageUrl = sport.imageUrl,
                    onClick = {
                        exploreViewModel.onSportItemClick(sport.id)
                        navigator?.navigate(
                            SportScreenDestination(
                                sportName = sport.sportName,
                                sportId = sport.id
                            )
                        )
                        // Handle item click if needed
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Preview
@Composable
fun ExploreScreenPreview()
{
    ExploreScreen()
}