package com.example.progressupdatedemo.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.progressupdatedemo.models.LoginDetailsHolder
import com.example.progressupdatedemo.models.Note
import com.example.progressupdatedemo.models.User
import com.example.progressupdatedemo.screens.authentication.LoginScreen
import com.example.progressupdatedemo.screens.authentication.SignUpScreen
import com.example.progressupdatedemo.screens.create.CreateNoteScreen
import com.example.progressupdatedemo.screens.details.NoteDetailsScreen
import com.example.progressupdatedemo.screens.home.HomeScreen
import com.example.progressupdatedemo.screens.splash.SplashScreen
import com.example.progressupdatedemo.screens.update.EditNoteScreen
import com.example.progressupdatedemo.screens.update.UpdateProfileScreen
import com.example.progressupdatedemo.utils.fromJson

@Composable
fun ApplicationNavigation() {
    val navController = rememberNavController()
    val startDestination = ApplicationScreens.SplashScreen.name

    NavHost(navController = navController, startDestination = startDestination) {
        composable(route = startDestination) {
            SplashScreen(navController = navController)
        }

        composable(route ="${ApplicationScreens.LoginScreen.name}/{detailsHolder}", arguments = listOf(
            navArgument("detailsHolder") {
                type = NavType.StringType
            }
        )) { navBackStackEntry ->
            val detailsHolder = navBackStackEntry.arguments?.getString("detailsHolder")?.fromJson(LoginDetailsHolder::class.java)!!
            LoginScreen(navController, detailsHolder)
            BackHandler(true) { }
        }

        composable(route = ApplicationScreens.SignUpScreen.name) {
            SignUpScreen(navController = navController)
            BackHandler(true) { }
        }
        composable(route = "${ApplicationScreens.HomeScreen.name}/{tab}", arguments = listOf(
            navArgument("tab") {
                type = NavType.StringType
            }
        )) { backStackEntry ->
            val tab = backStackEntry.arguments?.getString("tab").toString()
            HomeScreen(navController = navController, tab = tab)
            BackHandler(true) { }
        }

        composable(route = ApplicationScreens.CreateNoteScreen.name) {
            CreateNoteScreen(navController = navController)
        }

        val detailsScreenBaseRoute = ApplicationScreens.NoteDetailsScreen.name
        composable(route = "${detailsScreenBaseRoute}/{note}/{fromTab}", arguments = listOf(navArgument("note") {
            type = NavType.StringType
        }, navArgument("fromTab") {
            type = NavType.StringType
        })) { navBackStackEntry ->
            val note = navBackStackEntry.arguments?.getString("note")?.fromJson(Note::class.java)!!
            val tab = navBackStackEntry.arguments?.getString("fromTab")

            BackHandler(true) {
                navController.navigate("${ApplicationScreens.HomeScreen.name}/$tab")
            }
            NoteDetailsScreen(navController = navController, note = note, fromTab = tab)
        }

        val updateNoteScreenBaseRoute = ApplicationScreens.UpdateNoteScreen.name
        composable("$updateNoteScreenBaseRoute/{note}/{fromTab}", arguments = listOf(navArgument("note") {
            type = NavType.StringType
        }, navArgument("fromTab"){
            type = NavType.StringType
        })) { navBackStackEntry ->
            val note = navBackStackEntry.arguments?.getString("note")?.fromJson(Note::class.java)!!
            val tab = navBackStackEntry.arguments?.getString("fromTab")!!
            EditNoteScreen(navController, note, fromTab = tab)
        }

        composable(route = "${ApplicationScreens.UpdateProfileScreen.name}/{userDetails}", arguments = listOf(
            navArgument("userDetails") {
                type = NavType.StringType
            }
        )) {
            navBackStackEntry ->
            val user = navBackStackEntry.arguments?.getString("userDetails")?.fromJson(User::class.java)!!
            UpdateProfileScreen(user, navController)
        }
    }

}