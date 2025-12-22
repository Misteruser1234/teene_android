package com.example.teene.home.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.teene.home.presentation.composables.CommonHeader
import com.example.teene.home.presentation.composables.SearchBarWithFilters
import com.example.teene.home.presentation.composables.SportItem
import com.example.teene.home.presentation.composables.TopicsSelectableList
import com.example.teene.home.presentation.viewModels.ExploreViewModel
import com.example.teene.ui.animations.AuthorizationNavigationAnimations
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.CoachesMapScreenDestination
import com.ramcosta.composedestinations.generated.destinations.ExploreFilterScreenDestination
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
    // Use Activity-scoped ViewModel so Explore and ExploreFilter share the same instance
    val activity = androidx.compose.ui.platform.LocalContext.current as androidx.activity.ComponentActivity
    val exploreViewModel = koinViewModel<ExploreViewModel>(viewModelStoreOwner = activity)
    Column(
        Modifier
            .fillMaxWidth()
    ) {
        CommonHeader("Explore", onMapClick = {
            navigator?.navigate(CoachesMapScreenDestination())
        })
        TopicsSelectableList(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 10.dp)
        )
        HorizontalDivider(color = Color(0x3F3F3F99))
        val suggestions = exploreViewModel.suggestions.collectAsStateWithLifecycle()
        val suggestionsDetailed = exploreViewModel.suggestionsDetailed.collectAsStateWithLifecycle()
        // Preload features for the filter screen so they are ready on first open
        val featuresState = exploreViewModel.features.collectAsStateWithLifecycle()
        val filtersCount = exploreViewModel.activeFiltersCount.collectAsStateWithLifecycle().value
        SearchBarWithFilters(
                    modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp),
                    onQueryChange = { query ->
                        exploreViewModel.updateSearchQuery(query)
                    },
                    searchResults = suggestions.value,
                    suggestionItems = suggestionsDetailed.value,
                    onQueryTyping = { q -> exploreViewModel.updateSuggestionQuery(q) },
                    onFilterClick = { navigator?.navigate(ExploreFilterScreenDestination) },
                    activeFiltersCount = filtersCount
                )

        val sports = exploreViewModel.sportsList.collectAsStateWithLifecycle()
        val query = exploreViewModel.committedQuery.collectAsStateWithLifecycle()
        val isSearching = query.value.isNotBlank()
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(sports.value) { index, sport ->
                SportItem(
                    id = sport.id,
                    trainerNumberCategoryText = sport.trainerNumberCategory.displayText,
                    sportName = sport.sportName,
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
                    },
                    highlightQuery = query.value
                )
                if (isSearching && index < sports.value.lastIndex) {
                    HorizontalDivider(
                        color = Color(0xFF4CAF50), // colored divider (green-ish); adjust as needed
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(4.dp))
                }
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