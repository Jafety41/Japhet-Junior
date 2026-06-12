package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
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
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    composeTestRule.setContent {
      MyApplicationTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF0F172A)) {
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
              modifier = Modifier
                .padding(20.dp)
                .background(Color(0xFF1E293B), RoundedCornerShape(16.dp))
                .padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "Smart Campus",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Academic & Trade Student Hub",
                fontSize = 14.sp,
                color = Color(0xFF0EA5E9),
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}

