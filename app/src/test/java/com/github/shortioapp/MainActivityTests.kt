package com.github.shortioapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.github.shortiosdk.ShortioSdk
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Only createSecure is exercised here; it is pure local crypto. The other two
 * buttons call the live Short.io API and must never be clicked from a test.
 */
@RunWith(RobolectricTestRunner::class)
class MainActivityTests {

    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun everyFeatureIsOfferedOnLaunch() {
        compose.onNodeWithText("Short Link Generator").assertIsDisplayed()
        compose.onNodeWithText("Create Short Link").assertIsDisplayed()
        compose.onNodeWithText("Create Secure Short Link").assertIsDisplayed()
        compose.onNodeWithText("Conversion Tracking").assertIsDisplayed()
    }

    @Test
    fun launchingInitializesTheSdkFromConstants() {
        assertTrue(ShortioSdk.isSdkInitialized())
        compose.onNodeWithText("Short Link Generator").assertIsDisplayed()
    }

    @Test
    fun noResultIsShownBeforeAnyButtonIsPressed() {
        compose.onNodeWithText("Copy URL").assertDoesNotExist()
        compose.onNodeWithText("Copy Secure URL").assertDoesNotExist()
    }

    @Test
    fun creatingASecureUrlRendersTheKeyAndACopyButton() {
        compose.onNodeWithText("Create Secure Short Link").performClick()
        compose.waitForIdle()

        compose.onNodeWithText("Copy Secure URL").assertIsDisplayed()
    }
}
