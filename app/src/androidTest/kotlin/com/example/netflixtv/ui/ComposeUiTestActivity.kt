package com.example.netflixtv.ui

import android.os.Bundle
import androidx.activity.ComponentActivity

/**
 * Minimal Activity for Compose UI testing.
 * createAndroidComposeRule launches this and injects content via Activity.setContent.
 * Does NOT call immersive mode - safe for TV (API 28).
 */
class ComposeUiTestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Content is set by createAndroidComposeRule via Activity.setContent
    }
}
