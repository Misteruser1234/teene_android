package com.example.teene.ui.bottombar

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.utils.currentDestinationAsState
import com.ramcosta.composedestinations.utils.startDestination
import com.ramcosta.composedestinations.utils.toDestinationsNavigator

/**
 * Created by 3100lari on 2025/02/13
 */
@Composable
fun BottomBar(
    // you can get one `DestinationsNavigator` by calling `navController.toDestinationsNavigator()
    // or navController.rememberDestinationsNavigator() when in a Composable function`
    navController: NavHostController
) {
    val currentDestination: DestinationSpec = navController.currentDestinationAsState().value
        ?: NavGraphs.root.startDestination
    NavigationBar(containerColor = Color.White)  {
        BottomBarDestination.entries.forEach { destination ->
            NavigationBarItem(
                selected = currentDestination == destination.direction,
                onClick = {
                    navController.toDestinationsNavigator().navigate(destination.direction) {
                        launchSingleTop = true
                    }
                },
                icon = { Icon(painter= painterResource( destination.icon), contentDescription =
                    stringResource
                    (destination.label))},
                label = { Text(stringResource(destination.label)) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    unselectedIconColor = Color(0xFF9E9E9E),
                    selectedTextColor = Color.Black,
                    unselectedTextColor = Color(0xFF9E9E9E),
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}