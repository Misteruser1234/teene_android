package com.example.teene.ui.animations

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavBackStackEntry
import com.ramcosta.composedestinations.spec.DestinationStyle

object AuthorizationNavigationAnimations : DestinationStyle.Animated()
{

    override val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition? =
        {
            fadeIn()
            slideInHorizontally(
                initialOffsetX = { 1000 },
                animationSpec = tween(700)
            )
        }

    override val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition? =
        {
            fadeOut()
            slideOutHorizontally(
                targetOffsetX = { -1000 },
                animationSpec = tween(700)
            )
        }

    override val popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition? =
        {
            fadeIn()
            slideInHorizontally(
                initialOffsetX = { -1000 },
                animationSpec = tween(700)
            )
        }

    override val popExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition? =
        {
            fadeOut()
            slideOutHorizontally(
                targetOffsetX = { 1000 },
                animationSpec = tween(700)
            )
        }
}