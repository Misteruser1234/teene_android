package com.example.teene

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.teene.authentication.AuthenticationNavGraph
import com.example.teene.authentication.forgotpassword.ForgotPasswordScreen
import com.example.teene.authentication.forgotpassword.PasswordResetScreen
import com.example.teene.ui.bottombar.BottomBar
import com.example.teene.ui.viewModel.LandingViewModel
import com.example.teene.ui.composables.TeneeScaffold
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.BookScreenDestination
import com.ramcosta.composedestinations.generated.destinations.CoachDetailsScreenDestination
import com.ramcosta.composedestinations.generated.destinations.CoachesMapScreenDestination
import com.ramcosta.composedestinations.generated.destinations.ExploreFilterScreenDestination
import com.ramcosta.composedestinations.generated.destinations.ExploreScreenDestination
import com.ramcosta.composedestinations.generated.destinations.ForgotPasswordScreenDestination
import com.ramcosta.composedestinations.generated.destinations.LoginScreenDestination
import com.ramcosta.composedestinations.generated.destinations.PasswordResetScreenDestination
import com.ramcosta.composedestinations.generated.destinations.RegisterScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SportScreenDestination
import com.ramcosta.composedestinations.generated.navgraphs.AuthenticationGraph
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.utils.startDestination
import org.koin.androidx.compose.koinViewModel


@Composable
fun TeneeApp()
{

    val landingViewModel = koinViewModel<LandingViewModel>()
    val navController = rememberNavController()

    // 👇 this avoids a jump in the UI that would happen if we relied only on ShowLoginWhenLoggedOut
    val tokenExists = landingViewModel.tokenExists.collectAsStateWithLifecycle().value
    val start =
        if (!tokenExists)
        {
            AuthenticationGraph
        }
        else
        {
            ExploreScreenDestination
        }

    TeneeScaffold(
        navController = navController,
        topBar = { dest, backStackEntry ->
            if (dest.shouldShowScaffoldElements)
            {
                //                TopBar(dest, backStackEntry)
            }
        },
        bottomBar = {
            if (it.shouldShowScaffoldElements)
            {
                BottomBar(navController)
            }
        }
    ) {
        DestinationsNavHost(
            navController = navController,
            navGraph = NavGraphs.root,
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                ),
            start = start
        )

        // Has to be called after calling DestinationsNavHost because only
        // then does NavController have a graph associated that we need for
        // `appCurrentDestinationAsState` method
        //        ShowLoginWhenLoggedOut(vm, navController)
    }
}

private val DestinationSpec.shouldShowScaffoldElements get() = this !in AuthenticationGraph.destinations
    && this != SportScreenDestination
    && this != CoachDetailsScreenDestination
    && this != BookScreenDestination
    && this != ForgotPasswordScreenDestination
    && this != PasswordResetScreenDestination
    && this != CoachesMapScreenDestination
    && this != ExploreFilterScreenDestination
// Compare this snippet from app/src/main/java/com/example/teene/ui/composables/TeneeScaffold.kt:

//@Composable
//private fun ShowLoginWhenLoggedOut(
//    vm: MainViewModel,
//    navController: NavHostController
//) {
//    val currentDestination by navController.currentDestinationAsState()
//    val isLoggedIn by vm.isLoggedInFlow.collectAsState()
//    val navigator = navController.rememberDestinationsNavigator()
//
//    if (!isLoggedIn && currentDestination != LoginScreenDestination) {
//        // everytime destination changes or logged in state we check
//        // if we have to show Login screen and navigate to it if so
//        navigator.navigate(LoginScreenDestination) {
//            launchSingleTop = true
//        }
//    }
//}
