package com.example.netflixtv.uicommon

import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * TV-optimized focus border modifier for D-pad navigation.
 *
 * Wraps the common pattern of tracking focus state via [MutableInteractionSource]
 * and applying a border when focused. Use on any focusable button/control.
 *
 * Usage:
 * ```
 * val interactionSource = remember { MutableInteractionSource() }
 * Button(
 *     modifier = Modifier.tvFocusBorder(interactionSource),
 *     interactionSource = interactionSource,
 *     ...
 * )
 * ```
 */
@Composable
fun Modifier.tvFocusBorder(
    interactionSource: MutableInteractionSource,
    borderWidth: Dp = UiConstants.Dimens.BUTTON_FOCUS_BORDER_WIDTH.dp,
    borderColor: Color = TvliveColors.FocusBorder,
    shape: Shape = RoundedCornerShape(UiConstants.Dimens.ROUNDED_MD)
): Modifier {
    val isFocused by interactionSource.collectIsFocusedAsState()
    return this
        .focusable()
        .then(
            if (isFocused) Modifier.border(borderWidth, borderColor, shape)
            else Modifier
        )
}
