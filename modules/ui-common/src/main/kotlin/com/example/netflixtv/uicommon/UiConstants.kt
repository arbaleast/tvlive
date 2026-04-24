package com.example.netflixtv.uicommon

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object UiConstants {

    object Overscan {
        val VERTICAL: Dp = 28.dp
    }

    object Animation {
        const val CONTROLS_HIDE_DELAY_MS = 4000L
    }

    object Dimens {
        val PADDING_CONTENT_H: Dp = 24.dp
        val SPACING_M: Dp = 16.dp
        val ROUNDED_MD: Dp = 8.dp

        const val GRADIENT_HEIGHT_TOP = 120
        const val SEEK_FORWARD_MS = 10_000L
        const val SEEK_BACKWARD_MS = 10_000L
        const val PLAYBACK_CONTROLS_BUTTON_SPACING = 24

        const val ERROR_OVERLAY_HORIZONTAL_PADDING = 48
        const val ERROR_SPACER_AFTER_ICON = 24
        const val ERROR_TEXT_HORIZONTAL_PADDING = 16
        const val ERROR_SPACER_BEFORE_BUTTONS = 32
        const val ERROR_BUTTON_SPACING = 24

        const val BUTTON_FOCUS_BORDER_WIDTH = 3
    }

    object Text {
        val SIZE_BODY: TextUnit = 18.sp
        val SIZE_ERROR_ICON: TextUnit = 64.sp
        val SIZE_ERROR_TEXT: TextUnit = 18.sp
        val SIZE_ERROR_BUTTON: TextUnit = 16.sp
    }
}
