package com.example.progressupdatedemo.navigation

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.progressupdatedemo.models.LoginDetailsHolder
import com.example.progressupdatedemo.models.Note
import com.example.progressupdatedemo.models.SignUpDetailsHolder
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

    NavHost(navController = navController, startDestination = Screen.SplashScreen.route) {
        composable(route = Screen.SplashScreen.route) {
            SplashScreen(navController = navController)
        }

        composable(route = "${Screen.LoginScreen.route}/{detailsHolder}",
            arguments = listOf(navArgument("detailsHolder") {
                type = NavType.StringType
            })) { navBackStackEntry ->
            val detailsHolder = navBackStackEntry.arguments?.getString("detailsHolder")
                ?.fromJson(LoginDetailsHolder::class.java)!!
            LoginScreen(navController, detailsHolder)
            BackHandler(true) { }
        }

        composable(route = "${Screen.SignUpScreen.route}/{detailsHolder}",
            arguments = listOf(navArgument("detailsHolder") {
                type = NavType.StringType
            })) { navBackStackEntry ->
            val signUpDetailsHolder = navBackStackEntry.arguments?.getString("detailsHolder")
                ?.fromJson(SignUpDetailsHolder::class.java)!!
            SignUpScreen(navController = navController, signUpDetailsHolder)
            BackHandler(true) { }
        }

        composable(route = "${Screen.HomeScreen.route}/{tab}",
            arguments = listOf(navArgument("tab") {
                type = NavType.StringType
            })) { backStackEntry ->
            val tab = backStackEntry.arguments?.getString("tab").toString()
            HomeScreen(navController = navController, tab = tab)
            BackHandler(true) { }
        }

        composable(route = Screen.CreateNoteScreen.route) {
            CreateNoteScreen(navController = navController)
        }

        val detailsScreenBaseRoute = Screen.NoteDetailsScreen.route
        composable(
            route = "${detailsScreenBaseRoute}/{note}/{fromTab}",
            arguments = listOf(navArgument("note") {
                type = NavType.StringType
            }, navArgument("fromTab") {
                type = NavType.StringType
            })
        ) { navBackStackEntry ->
            val note = navBackStackEntry.arguments?.getString("note")?.fromJson(Note::class.java)!!
            val tab = navBackStackEntry.arguments?.getString("fromTab")

            BackHandler(true) {
                navController.navigate("${Screen.HomeScreen.route}/$tab")
            }
            NoteDetailsScreen(navController = navController, note = note, fromTab = tab)
        }

        val updateNoteScreenBaseRoute = Screen.UpdateNoteScreen.route
        composable(
            "$updateNoteScreenBaseRoute/{note}/{fromTab}", arguments = listOf(navArgument("note") {
                type = NavType.StringType
            }, navArgument("fromTab") {
                type = NavType.StringType
            })
        ) { navBackStackEntry ->
            val note = navBackStackEntry.arguments?.getString("note")?.fromJson(Note::class.java)!!
            val tab = navBackStackEntry.arguments?.getString("fromTab")!!
            EditNoteScreen(navController, note, fromTab = tab)
        }

        composable(route = "${Screen.UpdateProfileScreen.route}/{userDetails}",
            arguments = listOf(navArgument("userDetails") {
                type = NavType.StringType
            })) { navBackStackEntry ->
            val user =
                navBackStackEntry.arguments?.getString("userDetails")?.fromJson(User::class.java)!!
            UpdateProfileScreen(user, navController)
        }
    }

}