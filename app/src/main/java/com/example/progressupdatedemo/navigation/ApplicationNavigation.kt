package com.example.progressupdatedemo.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
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

        composable(
            route = "${Screen.LoginScreen.route}/{detailsHolder}",
            arguments = listOf(navArgument("detailsHolder") {
                type = NavType.StringType
            })
        ) { navBackStackEntry ->
            val detailsHolder = navBackStackEntry.arguments?.getString("detailsHolder")
                ?.fromJson(LoginDetailsHolder::class.java)!!
            LoginScreen(navController, detailsHolder)
            BackHandler(true) { }
        }

        composable(
            route = "${Screen.SignUpScreen.route}/{detailsHolder}",
            arguments = listOf(navArgument("detailsHolder") {
                type = NavType.StringType
            })
        ) { navBackStackEntry ->
            val signUpDetailsHolder = navBackStackEntry.arguments?.getString("detailsHolder")
                ?.fromJson(SignUpDetailsHolder::class.java)!!
            SignUpScreen(navController = navController, signUpDetailsHolder)
            BackHandler(true) { }
        }

        composable(
            route = "${Screen.HomeScreen.route}/{tab}", arguments = listOf(navArgument("tab") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val tab = backStackEntry.arguments?.getString("tab").toString()
            HomeScreen(navController = navController, tab = tab)
            BackHandler(true) { }
        }

        composable(route = Screen.CreateNoteScreen.route) {
            CreateNoteScreen(navController = navController)
        }

        val detailsScreenBaseRoute = Screen.NoteDetailsScreen.route
        composable(
            route = "${detailsScreenBaseRoute}/{note}/{selectedHomeScreenTab}",
            arguments = listOf(navArgument("note") {
                type = NavType.StringType
            }, navArgument("selectedHomeScreenTab") {
                type = NavType.StringType
            })
        ) { navBackStackEntry ->
            val note = navBackStackEntry.arguments?.getString("note")?.fromJson(Note::class.java)!!
            val tab = navBackStackEntry.arguments?.getString("selectedHomeScreenTab")!!

            BackHandler(true) {
                navController.navigate("${Screen.HomeScreen.route}/$tab")
            }
            NoteDetailsScreen(navController = navController, note = note, selectedHomeScreenTab = tab)
        }

        val updateNoteScreenRoute = Screen.UpdateNoteScreen.withArgs("{note}", "{fromTab}")
        val updateNoteScreenArguments = listOf(navArgument("note") {
            type = NavType.StringType
        }, navArgument("fromTab") {
            type = NavType.StringType
        })
        composable(route = updateNoteScreenRoute, arguments = updateNoteScreenArguments) {
                navBackStackEntry ->
            val note = mapBackStackEntryArgumentToObject(
                navBackStackEntry, "note", Note::class.java
            )
            val tab = mapBackStackEntryArgumentToObject(
                navBackStackEntry, "fromTab", String::class.java
            )
            EditNoteScreen(navController, note, fromTab = tab)
        }

        composable(
            route = "${Screen.UpdateProfileScreen.route}/{userDetails}",
            arguments = listOf(navArgument("userDetails") {
                type = NavType.StringType
            })
        ) { navBackStackEntry ->
            val user = mapBackStackEntryArgumentToObject(
                navBackStackEntry, "userDetails", User::class.java
            )
            UpdateProfileScreen(user, navController)
        }
    }
}

private fun <C> mapBackStackEntryArgumentToObject(
    navBackStackEntry: NavBackStackEntry,
    argumentName: String, type: Class<C>,
) = navBackStackEntry.arguments?.getString(argumentName)?.fromJson(type)!!
