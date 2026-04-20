package com.example.netflixtv

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView

/**
 * Minimal ComponentActivity for Compose UI testing.
 * Lives in main sourceset so the test instrumentation (same process) can find it.
 *
 * Provides setTestContent{} for tests to inject composable content.
 * Tests must call composeTestRule.waitForIdle() after setTestContent{}.
 */
class TestMainActivity : ComponentActivity() {

    private lateinit var container: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        container = FrameLayout(this).apply { id = View.generateViewId() }
        setContentView(container)
    }

    /** Sets composable content on the ComposeView. Must be called from test thread. */
    fun setTestContent(content: @Composable () -> Unit) {
        runOnUiThread {
            val composeView = container.getChildAt(0) as? ComposeView
                ?: ComposeView(this@TestMainActivity).also {
                    container.addView(it, FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    ))
                }
            composeView.setContent(content)
        }
    }
}
