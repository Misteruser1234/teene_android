package com.example.teene.home.presentation

import android.R.attr.bottom
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.teene.home.presentation.composables.CoachesMapHeader
import com.example.teene.home.presentation.composables.TrainersInMap
import com.example.teene.home.presentation.viewModels.CoachesFilterViewModel
import com.example.teene.ui.animations.AuthorizationNavigationAnimations
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

// Helper: animate scroll by a pixel delta with a tween of given duration
private suspend fun animateScrollByWithTween(
    listState: LazyListState,
    targetOffset: Float,
    durationMillis: Int = 1000
)
{
    val start = withFrameNanos { it }
    var last = 0f
    while (true)
    {
        val now = withFrameNanos { it }
        val elapsedMs = ((now - start) / 1_000_000L).toFloat()
        val progress = (elapsedMs / durationMillis).coerceIn(0f, 1f)
        val eased = FastOutSlowInEasing.transform(progress)
        val current = targetOffset * eased
        val delta = current - last
        last = current

        val baseIndex = listState.firstVisibleItemIndex
        val baseOffset = listState.firstVisibleItemScrollOffset
        val newOffset = (baseOffset + delta).toInt().coerceAtLeast(0)
        listState.scrollToItem(baseIndex, newOffset)

        if (progress >= 1f) break
    }
}

/**
 * Full screen map that shows coaches for a given sport.
 * Created by 3100lari on 2025/11/05
 */
@Destination<RootGraph>(style = AuthorizationNavigationAnimations::class)
@Composable
fun CoachesMapScreen(
    sportId: Int? = null,
    navigator: DestinationsNavigator? = null
)
{
    val filtersViewModel = koinViewModel<CoachesFilterViewModel>()

    LaunchedEffect(sportId) {
        filtersViewModel.setSportId(sportId)
    }

    val trainers = filtersViewModel.trainers.collectAsStateWithLifecycle().value
    val currentFilters = filtersViewModel.filters.collectAsStateWithLifecycle().value
    val sportsList = filtersViewModel.sports.collectAsStateWithLifecycle().value
    val currentSportName = currentFilters.sportId?.let { id -> sportsList.firstOrNull { it.id == id }?.sportName }

    // Selection + list state
    val idToIndex = androidx.compose.runtime.remember(trainers) {
        trainers.mapIndexed { index, t -> t.id to index }.toMap()
    }
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
    var enableSnapFling by androidx.compose.runtime.remember {
        androidx.compose.runtime.mutableStateOf(
            true
        )
    }
    val listFlingBehavior = if (enableSnapFling)
    {
        androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior(lazyListState = listState)
    }
    else
    {
        androidx.compose.foundation.gestures.ScrollableDefaults.flingBehavior()
    }
    var selectedTrainerId by androidx.compose.runtime.saveable.rememberSaveable {
        androidx.compose.runtime.mutableStateOf<Int?>(
            null
        )
    }

    Box(Modifier.fillMaxSize()) {

        if (trainers.isNotEmpty())
        {
            TrainersInMap(
                trainers = trainers,
                selectedTrainerId = selectedTrainerId,
                onMarkerClick = { trainerId ->
                    selectedTrainerId = trainerId
                    val index = idToIndex[trainerId] ?: -1
                    if (index >= 0)
                    {
                        enableSnapFling = false
                        coroutineScope.launch {
                            try
                            {
                                // Ensure target is roughly visible to compute a precise pixel delta
                                var layoutInfo = listState.layoutInfo
                                val isVisible =
                                    layoutInfo.visibleItemsInfo.any { it.index == index }
                                if (!isVisible)
                                {
                                    // Bring the item into the viewport without animation to avoid a huge jump
                                    listState.scrollToItem(index)
                                    layoutInfo = listState.layoutInfo
                                }
                                // Compute the pixel delta to center the target item and animate by that amount
                                val itemInfo =
                                    layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }
                                if (itemInfo != null)
                                {
                                    val viewportCenter =
                                        (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
                                    val itemCenter = itemInfo.offset + (itemInfo.size / 2)
                                    val targetOffset = (itemCenter - viewportCenter).toFloat()
                                    // Smooth scroll by computed delta using our tweened helper for ~1000ms animation
                                    animateScrollByWithTween(
                                        listState = listState,
                                        targetOffset = targetOffset,
                                        durationMillis = 1000
                                    )
                                }
                            } finally
                            {
                                enableSnapFling = true
                            }
                        }
                    }
                }
            )
        }

        // Header on top
        CoachesMapHeader(
            onBackClick = { navigator?.popBackStack() },
            onFilterClick = { /* TODO: open filters sheet */ }
        )


        if (trainers.isNotEmpty())
        {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp)
            ) {
                LazyRow(
                    state = listState,
                    flingBehavior = listFlingBehavior,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                ) {
                    items(
                        count = trainers.size,
                        key = { idx -> trainers[idx].id }
                    ) { i ->
                        val t = trainers[i]
                        val isSelected = t.id == selectedTrainerId
                        TrainerMiniCard(
                            sportName = currentSportName,
                            trainerName = "${t.firstName} ${t.lastName}",
                            location = t.address,
                            price = "${t.currency} ${t.rate}",
                            rating = t.rating ?: 0.0,
                            distance = t.distance,
                            selected = isSelected,
                            onClick = {
                                selectedTrainerId = t.id
                                coroutineScope.launch {
                                    // Bring item into view if needed, then animate by precise pixel delta to center it
                                    var layoutInfo = listState.layoutInfo
                                    val isVisible =
                                        layoutInfo.visibleItemsInfo.any { it.index == i }
                                    if (!isVisible)
                                    {
                                        listState.scrollToItem(i)
                                        layoutInfo = listState.layoutInfo
                                    }
                                    val itemInfo =
                                        layoutInfo.visibleItemsInfo.firstOrNull { it.index == i }
                                    if (itemInfo != null)
                                    {
                                        val viewportCenter =
                                            (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
                                        val itemCenter = itemInfo.offset + (itemInfo.size / 2)
                                        val targetOffset = (itemCenter - viewportCenter).toFloat()
                                        animateScrollByWithTween(
                                            listState = listState,
                                            targetOffset = targetOffset,
                                            durationMillis = 1000
                                        )
                                    }
                                }
                            }
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TrainerMiniCard(
    sportName: String?,
    trainerName: String,
    location: String?,
    price: String,
    rating: Double,
    distance: Double,
    selected: Boolean,
    onClick: () -> Unit,
)
{
    val animatedBg by androidx.compose.animation.animateColorAsState(
        targetValue = if (selected) Color(0xFFF5F9FF) else Color.White,
        animationSpec = androidx.compose.animation.core.tween(
            durationMillis = 250,
            easing = androidx.compose.animation.core.FastOutSlowInEasing
        ),
        label = "cardBg"
    )
    val animatedElevation by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (selected) 8.dp else 2.dp,
        animationSpec = androidx.compose.animation.core.tween(
            durationMillis = 250,
            easing = androidx.compose.animation.core.FastOutSlowInEasing
        ),
        label = "cardElevation"
    )

    androidx.compose.material3.Card(
        modifier = Modifier
            .clickable { onClick() },
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = animatedBg
        ),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(
            defaultElevation = animatedElevation
        )
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leading icon placeholder
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Color(0xFFEFEFEF),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.Icon(
                    painter = androidx.compose.ui.res.painterResource(id = com.example.teene.R.drawable.ic_bottom_bar_profile),
                    contentDescription = null,
                    tint = Color.Black,
                )
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(12.dp))
            androidx.compose.foundation.layout.Column {
                // 1. Sport name (fallback to "All sports" when unknown)
                androidx.compose.material3.Text(
                    text = sportName ?: "All sports",
                    color = Color.Black
                )
                // 2. with <Trainer name>
                androidx.compose.material3.Text(
                    text = "with $trainerName",
                    color = Color(0xFF7A7A7A)
                )
                // 3. Location
                if (!location.isNullOrBlank()) {
                    androidx.compose.material3.Text(
                        text = location,
                        color = Color(0xFF7A7A7A)
                    )
                }
                // 4. <currency><price> / per session
                androidx.compose.material3.Text(
                    text = "$price / per session",
                    color = Color(0xFF03A9C5)
                )
            }
        }
    }
}

