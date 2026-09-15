package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.local.entity.ProjectEntity
import com.example.ui.IdeViewMode
import com.example.ui.components.BoltHeaderBar
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [34])
class GreetingScreenshotTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun bolt_header_screenshot() {
        val sampleProject = ProjectEntity(
            id = "proj1",
            name = "SaaS Landing & Auth",
            description = "Demo SaaS",
            activeFilePath = "src/App.jsx",
            template = "react_saas",
            createdAt = 0L,
            updatedAt = 0L
        )

        composeTestRule.setContent {
            MyApplicationTheme {
                BoltHeaderBar(
                    currentProject = sampleProject,
                    currentViewMode = IdeViewMode.CODE,
                    isGenerating = false,
                    enableHighThinking = true,
                    onViewModeChange = {},
                    onOpenProjectPicker = {},
                    onOpenNewProjectDialog = {}
                )
            }
        }

        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/bolt_header.png")
    }
}
