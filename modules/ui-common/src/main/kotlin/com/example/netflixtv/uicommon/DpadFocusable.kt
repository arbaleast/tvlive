package com.example.netflixtv.uicommon

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * D-pad compatible focusable wrapper.
 * Replaces the repeated interactionSource + collectIsFocusedAsState pattern.
 *
 * Usage:
 *   DpadFocusable(focusBorderColor = TvliveColors.Primary, cornerRadius = 10.dp) {
 *     YourContent()
 *   }
 */
@Composable
fun DpadFocusable(
    modifier: Modifier = Modifier,
    focusBorderColor: Color = Color.Red,
    cornerRadius: Dp = 8.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Box(
        modifier = modifier
            .then(
                if (isFocused) {
                    Modifier.border(
                        width = 2.dp,
                        color = focusBorderColor,
                        shape = RoundedCornerShape(cornerRadius)
                    )
                } else {
                    Modifier
                }
            )
            .clip(RoundedCornerShape(cornerRadius))
            .focusable(interactionSource = interactionSource),
        content = content
    )
}
