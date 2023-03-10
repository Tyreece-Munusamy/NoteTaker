package com.example.progressupdatedemo.presentation.screen.splash

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.progressupdatedemo.presentation.component.icons.ApplicationIcon
import com.example.progressupdatedemo.navigation.Screen
import com.example.progressupdatedemo.presentation.screen.authentication.AuthenticationViewModel
import com.example.progressupdatedemo.core.constants.Constants
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    Surface(modifier = Modifier.fillMaxSize()) {
        SplashScreenContent(navController)
    }
}

@Composable
private fun SplashScreenContent(
    navController: NavController,
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
) {
    val scale = remember { Animatable(0f) }
    val isUserAuthenticated = authenticationViewModel.isUserAuthenticated

    LaunchedEffect(key1 = true) {
        animateScaleIncrease(scale)

        delay(500L)

        if (isUserAuthenticated) {
            navigateToHomeScreenWithNotesTabSelected(navController)
        } else {
            navigateToLoginScreen(navController)
        }
    }

    ApplicationIconWithIncreasingScale(scale)
}

@Composable
private fun ApplicationIconWithIncreasingScale(scale: Animatable<Float, AnimationVector1D>) {
    ApplicationIcon(Modifier.scale(scale.value))
}

private suspend fun animateScaleIncrease(scale: Animatable<Float, AnimationVector1D>) {
    scale.animateTo(targetValue = 0.7f, animationSpec = tween(delayMillis = 1100, easing = {
        OvershootInterpolator(4f).getInterpolation(it)
    }))
}

private fun navigateToLoginScreen(navController: NavController) {
    navController.navigate(Screen.LoginScreen.route)
}

private fun navigateToHomeScreenWithNotesTabSelected(navController: NavController) {
    navController.navigate(Screen.HomeScreen.withArgs(Constants.HOME_SCREEN_NOTES_TAB))
}

