package com.example.progressupdatedemo.screens.authentication

import androidx.activity.compose.setContent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.progressupdatedemo.MainActivity
import com.example.progressupdatedemo.core.utils.TestTags
import com.example.progressupdatedemo.di.AppModule
import com.example.progressupdatedemo.domain.models.LoginDetailsHolder
import com.example.progressupdatedemo.navigation.Screen
import com.example.progressupdatedemo.ui.theme.ProgressUpdateDemoTheme
import com.example.progressupdatedemo.utils.fromJson
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AppModule::class)
class LoginScreenKtTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
        composeRule.activity.setContent {
            val navController = rememberNavController()
            ProgressUpdateDemoTheme {
                NavHost(
                    navController = navController,
                    startDestination = "${Screen.LoginScreen.route}/{detailsHolder}"
                ) {
                    composable(
                        route = "${Screen.LoginScreen.route}/{detailsHolder}",
                        arguments = listOf(navArgument("detailsHolder") {
                            type = NavType.StringType
                        })
                    ) { navBackStackEntry ->
                        val detailsHolder = navBackStackEntry.arguments?.getString("detailsHolder")
                            ?.fromJson(LoginDetailsHolder::class.java) ?: LoginDetailsHolder()
                        LoginScreen(navController, detailsHolder)
                    }
                }
            }
        }
    }

    @Test
    fun checkEmailInputField_itExists() {
        composeRule.onNodeWithTag(TestTags.LOGIN_SCREEN_EMAIL_INPUT_FIELD_TAG).assertExists()
    }

    @Test
    fun checkAccountIcon_itExists() {
        composeRule.onNodeWithTag(TestTags.LOGIN_SCREEN_ACCOUNT_ICON_TAG).assertExists()
    }

    @Test
    fun checkPasswordInputField_itExists() {
        composeRule.onNodeWithTag(TestTags.LOGIN_SCREEN_PASSWORD_INPUT_FIELD_TAG).assertExists()
    }

    @Test
    fun checkLoginButton_itExists() {
        composeRule.onNodeWithTag(TestTags.LOGIN_SCREEN_LOGIN_BUTTON_TAG).assertExists()
    }

    @Test
    fun checkSignUpPromptText_itExists() {
        composeRule.onNodeWithTag(TestTags.LOGIN_SCREEN_SIGN_UP_PROMPT_TEXT_TAG).assertExists()
    }

    @Test
    fun checkLoginPromptText_itExists() {
        composeRule.onNodeWithTag(TestTags.LOGIN_SCREEN_LOGIN_PROMPT_TEXT_TAG).assertExists()
    }
}