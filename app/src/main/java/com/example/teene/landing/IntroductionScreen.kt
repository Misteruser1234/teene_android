package com.example.teene.landing

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.teene.LoginScreen
import com.example.teene.R
import com.example.teene.ui.composables.PagerSampleItem
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.LoginScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@Destination<RootGraph>(start=true)
@Composable
fun IntroductionScreen(navigator: DestinationsNavigator){
    val landingViewModel = koinViewModel<LandingViewModel>()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val lifecycleEventObserver = LifecycleEventObserver { _, event ->
            when(event){

                Lifecycle.Event.ON_STOP -> {landingViewModel.saveLandingSeen()}
                else->Unit
            }

            // event contains current lifecycle event
        }

        lifecycleOwner.lifecycle.addObserver(lifecycleEventObserver)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleEventObserver)
        }
    }
    val pagerState =
        rememberPagerState(pageCount = { 3 })
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState
        ) {
            PagerSampleItem(page = it)
        }
        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment =
            AbsoluteAlignment.Left,
            modifier = Modifier
                .align(
                    alignment = Alignment
                        .BottomStart
                )
                .padding(bottom = 142.dp, start = 40.dp, end = 144.dp)
        ) {
            LandingViewPagerDescription(pagerState.currentPage)
            Spacer(
                modifier = Modifier.height(
                    32.dp
                )
            )
            Row(
                Modifier
                    .fillMaxWidth()
            ) {
                repeat(3) { iteration ->
                    val color =
                        if (pagerState.currentPage == iteration) Color.White
                        else Color.White.copy(alpha = 0.5f)
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(6.dp)

                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .background(color = Color(0x66000000))
                .border(
                    1.dp, color = Color
                        (0x3F3F3F99)
                )
        ) {
            Text(
                text = stringResource(R.string.landing_join_now_button),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier
                    .clickable {
                        navigator.navigate(LoginScreenDestination) {
                            popUpTo(NavGraphs.root){
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                    .wrapContentHeight()
                    .weight(1f)
                    .padding(vertical = 16.dp),
                lineHeight = 20.sp,
                color = Color.White
            )
            VerticalDivider(color = Color(0x3F3F3F99))
            Text(
                text = stringResource(R.string.landing_login_button),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight()
                    .padding(vertical = 16.dp),
                lineHeight = 20.sp,
                color = Color.White
            )
        }
    }

}