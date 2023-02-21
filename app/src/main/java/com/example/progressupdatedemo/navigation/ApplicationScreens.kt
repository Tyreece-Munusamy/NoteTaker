package com.example.progressupdatedemo.navigation

//enum class ApplicationScreens {
//    SplashScreen,
//    SignUpScreen,
//    LoginScreen,
//    HomeScreen,
//    NoteDetailsScreen,
//    CreateNoteScreen,
//    UpdateNoteScreen,
//    UpdateProfileScreen,
//}

sealed class Screen(val route: String) {
    object SplashScreen : Screen("splash_screen")
    object SignUpScreen : Screen("sign_up_screen")
    object LoginScreen : Screen("login_screen")
    object HomeScreen : Screen("home_screen")
    object NoteDetailsScreen : Screen("note_details_screen")
    object CreateNoteScreen : Screen("create_note_screen")
    object UpdateNoteScreen : Screen("update_note_screen")
    object UpdateProfileScreen : Screen("update_profile_screen")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach {
                append("/$it")
            }
        }
    }
}