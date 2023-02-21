package com.example.progressupdatedemo.screens.splash

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
import androidx.navigation.NavController
import com.example.progressupdatedemo.components.ApplicationIcon
import com.example.progressupdatedemo.models.LoginDetailsHolder
import com.example.progressupdatedemo.navigation.ApplicationScreens
import com.example.progressupdatedemo.utils.toJson
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    Surface(modifier = Modifier.fillMaxSize()) {
        SplashScreenContent(navController)
    }
}

@Composable
private fun SplashScreenContent(navController: NavController) {
    val scale = remember { Animatable(0f) }
    val userEmail = FirebaseAuth.getInstance().currentUser?.email

    LaunchedEffect(key1 = true) {
        animateScaleIncrease(scale)

        delay(500L)

        if (userEmail.isNullOrEmpty()) {
            navigateToLoginScreenWithNoPresetDetails(navController)
        } else {
            navigateToHomeScreen(navController)
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

private fun navigateToLoginScreenWithNoPresetDetails(navController: NavController) {
    val baseLoginScreenNavigationRoute = ApplicationScreens.LoginScreen.name
    val emptyLoginDetailsHolder = LoginDetailsHolder().toJson().toString()
    navController.navigate("${baseLoginScreenNavigationRoute}/${emptyLoginDetailsHolder}")
}

private fun navigateToHomeScreen(navController: NavController) {
    val baseHomeScreenNavigationRoute = ApplicationScreens.HomeScreen.name
    navController.navigate("${baseHomeScreenNavigationRoute}/notes")
}

