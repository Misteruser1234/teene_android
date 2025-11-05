package com.example.teene.events.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.teene.home.presentation.composables.ExploreHeader
import com.example.teene.home.presentation.composables.SearchBarWithFilters
import com.example.teene.ui.animations.AuthorizationNavigationAnimations
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.generated.destinations.EventsFilterScreenDestination
import org.koin.androidx.compose.koinViewModel

/**
 * Created by 3100lari on 2025/02/12
 */
@Destination<RootGraph>(style = AuthorizationNavigationAnimations::class)
@Composable
fun EventsScreen(
    navigator: DestinationsNavigator
) {
    val vm = koinViewModel<EventsViewModel>()
    val state = vm.uiState.collectAsStateWithLifecycle().value

    Column(Modifier.fillMaxWidth()) {
        ExploreHeader("Events")
        HorizontalDivider(color = Color(0x3F3F3F99))
        SearchBarWithFilters(
            modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp),
            onFilterClick = { navigator.navigate(EventsFilterScreenDestination) }
        )

        when (state) {
            is EventsUiState.Initial, is EventsUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
            is EventsUiState.Error -> {
                Text("Failed to load events", color = Color.Red, modifier = Modifier.padding(16.dp))
            }
            is EventsUiState.Success -> {
                if (state.events.isEmpty()) {
                    // Empty state UI
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 48.dp)
                    ) {
                        androidx.compose.foundation.layout.Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                        ) {
                            androidx.compose.foundation.Image(
                                painter = androidx.compose.ui.res.painterResource(com.example.teene.R.drawable.m_events_no_events),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp)
                            )
                            Spacer(Modifier.height(24.dp))
                            Text(
                                text = "No Events found",
                                color = Color.Black
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "No events match your filters. Adjust them to get back in the game.",
                                color = Color.DarkGray
                            )
                        }
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.events) { event ->
                            com.example.teene.events.presentation.composables.EventItem(
                                event = event,
                                onClick = { /* TODO: navigate to event details when available */ }
                            )
                            Spacer(Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
    }
}