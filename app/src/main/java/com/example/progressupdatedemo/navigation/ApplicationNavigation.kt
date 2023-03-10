package com.example.progressupdatedemo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.progressupdatedemo.core.utils.fromJsonToObject
import com.example.progressupdatedemo.presentation.screen.authentication.LoginScreen
import com.example.progressupdatedemo.presentation.screen.authentication.SignUpScreen
import com.example.progressupdatedemo.presentation.screen.create.CreateNoteScreen
import com.example.progressupdatedemo.presentation.screen.details.NoteDetailsScreen
import com.example.progressupdatedemo.presentation.screen.home.HomeScreen
import com.example.progressupdatedemo.presentation.screen.splash.SplashScreen
import com.example.progressupdatedemo.presentation.screen.update.UpdateNoteScreen
import com.example.progressupdatedemo.presentation.screen.update.UpdateProfileScreen

@Composable
fun ApplicationNavigation() {
//    val navController = rememberNavController()
//
//    NavHost(navController = navController, startDestination = Screen.SplashScreen.route) {
//        composable(route = Screen.SplashScreen.route) {
//            SplashScreen(navController = navController)
//        }
//
//        composable(
//            route = "${Screen.LoginScreen.route}/{detailsHolder}",
//            arguments = listOf(navArgument("detailsHolder") {
//                type = NavType.StringType
//            })
//        ) { navBackStackEntry ->
//            val detailsHolder = navBackStackEntry.arguments?.getString("detailsHolder")
//                ?.fromJson(LoginDetailsHolder::class.java)!!
//            LoginScreen(navController, detailsHolder)
//            BackHandler(true) { }
//        }
//
//        composable(
//            route = "${Screen.SignUpScreen.route}/{detailsHolder}",
//            arguments = listOf(navArgument("detailsHolder") {
//                type = NavType.StringType
//            })
//        ) { navBackStackEntry ->
//            val signUpDetailsHolder = navBackStackEntry.arguments?.getString("detailsHolder")
//                ?.fromJson(SignUpDetailsHolder::class.java)!!
//            SignUpScreen(navController = navController, signUpDetailsHolder)
//            BackHandler(true) { }
//        }
//
//        composable(
//            route = "${Screen.HomeScreen.route}/{selectedHomeScreenTab}",
//            arguments = listOf(navArgument("selectedHomeScreenTab") {
//                type = NavType.StringType
//            })
//        ) { backStackEntry ->
//            val tab = backStackEntry.arguments?.getString("selectedHomeScreenTab").toString()
//            HomeScreen(navController = navController, tab = tab)
//            BackHandler(true) { }
//        }
//
//        composable(route = Screen.CreateNoteScreen.route) {
//            CreateNoteScreen(navController = navController)
//        }
//
//        val detailsScreenBaseRoute = Screen.NoteDetailsScreen.route
//        composable(
//            route = "${detailsScreenBaseRoute}/{note}/{selectedHomeScreenTab}",
//            arguments = listOf(navArgument("note") {
//                type = NavType.StringType
//            }, navArgument("selectedHomeScreenTab") {
//                type = NavType.StringType
//            })
//        ) { navBackStackEntry ->
//            val note = navBackStackEntry.arguments?.getString("note")?.fromJson(Note::class.java)!!
//            val tab = navBackStackEntry.arguments?.getString("selectedHomeScreenTab")!!
//
//            BackHandler(true) {
//                navController.navigate("${Screen.HomeScreen.route}/$tab")
//            }
//            NoteDetailsScreen(
//                navController = navController, note = note, selectedHomeScreenTab = tab
//            )
//        }
//
//        val updateNoteScreenRoute =
//            Screen.UpdateNoteScreen.withArgs("{note}", "{selectedHomeScreenTab}")
//        val updateNoteScreenArguments = listOf(navArgument("note") {
//            type = NavType.StringType
//        }, navArgument("selectedHomeScreenTab") {
//            type = NavType.StringType
//        })
//        composable(
//            route = updateNoteScreenRoute, arguments = updateNoteScreenArguments
//        ) { navBackStackEntry ->
//            val note = mapBackStackEntryArgumentToObject(
//                navBackStackEntry, "note", Note::class.java
//            )
//            val tab = mapBackStackEntryArgumentToObject(
//                navBackStackEntry, "selectedHomeScreenTab", String::class.java
//            )
//            UpdateNoteScreen(navController, note, selectedHomeScreenTab = tab)
//        }
//
//        composable(
//            route = "${Screen.UpdateProfileScreen.route}/{userDetails}",
//            arguments = listOf(navArgument("userDetails") {
//                type = NavType.StringType
//            })
//        ) { navBackStackEntry ->
//            val user = mapBackStackEntryArgumentToObject(
//                navBackStackEntry, "userDetails", User::class.java
//            )
//            UpdateProfileScreen(user, navController)
//        }
//    }

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.SplashScreen.route) {
        // Splash Screen
        buildSplashScreenDestination(navController)

        // Login Screen
        buildLoginScreenDestination(navController)

        // Sign Up Screen
        buildSignUpScreenDestination(navController)

        // Home Screen
        buildHomeScreenDestination(navController)

        // Create Note Screen
        buildCreateNoteScreenDestination(navController)

        // Note Details Screen
        buildNoteDetailsScreenDestination(navController)

        // Update Note Screen
        buildUpdateNoteScreenDestination(navController)

        // Update Profile Screen
        buildUpdateProfileScreenDestination(navController)
    }
}

private fun NavGraphBuilder.buildSplashScreenDestination(navController: NavHostController) {
    val splashScreenRoute = Screen.SplashScreen.route

    composable(route = splashScreenRoute) {
        SplashScreen(navController = navController)
    }
}

private fun NavGraphBuilder.buildLoginScreenDestination(navController: NavHostController) {
    val loginScreenRoute = Screen.LoginScreen.route

    composable(route = loginScreenRoute) {
        LoginScreen(navController)
    }
}

private fun NavGraphBuilder.buildSignUpScreenDestination(navController: NavHostController) {
    val signUpScreenRoute = Screen.SignUpScreen.route

    composable(route = signUpScreenRoute) {
        SignUpScreen(navController = navController)
    }
}

private fun NavGraphBuilder.buildHomeScreenDestination(navController: NavHostController) {
    val homeScreenRoute = "${Screen.HomeScreen.route}/{selectedTab}"
    val homeScreenNavArguments = listOf(navArgument("selectedTab") {
        type = NavType.StringType
    })

    composable(route = homeScreenRoute, arguments = homeScreenNavArguments) {
        val selectedTab = mapBackStackEntryArgumentToString(it, "selectedTab")
        HomeScreen(navController = navController, selectedTab = selectedTab)
    }
}

private fun NavGraphBuilder.buildCreateNoteScreenDestination(navController: NavHostController) {
    val createNoteScreenRoute = Screen.CreateNoteScreen.route

    composable(route = createNoteScreenRoute) {
        CreateNoteScreen(navController = navController)
    }
}

private fun NavGraphBuilder.buildNoteDetailsScreenDestination(navController: NavHostController) {
    val noteDetailsScreenRoute =
        "${Screen.NoteDetailsScreen.route}/{noteUUID}/{selectedHomeScreenTab}"
    val noteDetailsScreenNavArguments = listOf(navArgument("noteUUID") {
        type = NavType.StringType
    }, navArgument("selectedHomeScreenTab") {
        type = NavType.StringType
    })

    composable(
        route = noteDetailsScreenRoute, arguments = noteDetailsScreenNavArguments
    ) {
        val noteUUID = mapBackStackEntryArgumentToString(it, "noteUUID")
        val selectedHomeScreenTab = mapBackStackEntryArgumentToString(it, "selectedHomeScreenTab")
        NoteDetailsScreen(navController, noteUUID, selectedHomeScreenTab)
    }
}

private fun NavGraphBuilder.buildUpdateNoteScreenDestination(navController: NavHostController) {
    val updateNoteScreenRoute =
        "${Screen.UpdateNoteScreen.route}/{noteUUID}/{previousSelectedHomeScreenTab}"
    val updateNoteScreenNavArguments = listOf(navArgument("noteUUID") {
        type = NavType.StringType
    }, navArgument("previousSelectedHomeScreenTab") {
        type = NavType.StringType
    })

    composable(
        route = updateNoteScreenRoute, arguments = updateNoteScreenNavArguments
    ) {
        val noteUUID = mapBackStackEntryArgumentToString(it, "noteUUID")
        val previousSelectedHomeScreenTab =
            mapBackStackEntryArgumentToString(it, "previousSelectedHomeScreenTab")
        UpdateNoteScreen(navController, noteUUID, previousSelectedHomeScreenTab)
    }
}

private fun NavGraphBuilder.buildUpdateProfileScreenDestination(navController: NavHostController) {
    val updateProfileScreenRoute = Screen.UpdateProfileScreen.route

    composable(route = updateProfileScreenRoute) {
        UpdateProfileScreen(navController)
    }
}

private fun mapBackStackEntryArgumentToString(
    navBackStackEntry: NavBackStackEntry,
    argumentName: String,
) = navBackStackEntry.arguments?.getString(argumentName)?.fromJsonToObject(String::class.java)!!
