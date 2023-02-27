package com.example.progressupdatedemo.screens.splash

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.example.progressupdatedemo.MainActivity
import com.example.progressupdatedemo.core.utils.TestTags
import com.example.progressupdatedemo.di.AppModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AppModule::class)
class SplashScreenTest {
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun checkApplicationIcon_itExists() {
        composeRule.onNodeWithTag(TestTags.SPLASH_SCREEN_APPLICATION_ICON_ICON).assertExists()
        composeRule.onNodeWithTag(TestTags.SPLASH_SCREEN_APPLICATION_ICON_TEXT).assertExists()
    }
}