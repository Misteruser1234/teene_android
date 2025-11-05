package com.example.teene.ui.bottombar

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.teene.R
import com.ramcosta.composedestinations.generated.destinations.EventsScreenDestination
import com.ramcosta.composedestinations.generated.destinations.ExploreScreenDestination
import com.ramcosta.composedestinations.generated.destinations.MySessionsScreenDestination
import com.ramcosta.composedestinations.generated.destinations.ProfileScreenDestination
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

/**
 * Created by 3100lari on 2025/02/12
 */
enum class BottomBarDestination(
    val direction: DirectionDestinationSpec,
    @DrawableRes val icon: Int,
    @StringRes val label: Int
) {
    Explore(ExploreScreenDestination, R.drawable.ic_bottom_bar_search, R.string.explore),
    Events(EventsScreenDestination, R.drawable.ic_bottom_bar_event, R.string.events),
    MySessions(MySessionsScreenDestination, R.drawable.ic_bottom_bar_trophy, R.string.my_sessions),
    Profile(ProfileScreenDestination, R.drawable.ic_bottom_bar_profile, R.string.profile)
}