package com.example.netflixtv.featureplayer

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.netflixtv.uicommon.R as CommonR
import com.example.netflixtv.uicommon.TvliveColors
import com.example.netflixtv.uicommon.UiConstants
import com.example.netflixtv.uicommon.tvFocusBorder

@Composable
internal fun PlayerErrorOverlayRefactored(
    errorMessage: String,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = UiConstants.Dimens.ERROR_OVERLAY_HORIZONTAL_PADDING.dp)
        ) {
            Text(
                text = "⚠",
                fontSize = UiConstants.Text.SIZE_ERROR_ICON,
                color = TvliveColors.AccentLive
            )

            Spacer(modifier = Modifier.height(UiConstants.Dimens.ERROR_SPACER_AFTER_ICON.dp))

            Text(
                text = errorMessage,
                color = TvliveColors.TextPrimary,
                fontSize = UiConstants.Text.SIZE_ERROR_TEXT,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = UiConstants.Dimens.ERROR_TEXT_HORIZONTAL_PADDING.dp)
            )

            Spacer(modifier = Modifier.height(UiConstants.Dimens.ERROR_SPACER_BEFORE_BUTTONS.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(UiConstants.Dimens.ERROR_BUTTON_SPACING.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val retryInteractionSource = remember { MutableInteractionSource() }

                Button(
                    onClick = onRetry,
                    modifier = Modifier.tvFocusBorder(retryInteractionSource),
                    colors = ButtonDefaults.buttonColors(containerColor = TvliveColors.Primary),
                    shape = RoundedCornerShape(UiConstants.Dimens.ROUNDED_MD),
                    interactionSource = retryInteractionSource
                ) {
                    Text(
                        text = stringResource(CommonR.string.retry),
                        color = TvliveColors.TextPrimary,
                        fontSize = UiConstants.Text.SIZE_ERROR_BUTTON,
                        fontWeight = FontWeight.Bold
                    )
                }

                val backInteractionSource = remember { MutableInteractionSource() }

                Button(
                    onClick = onBack,
                    modifier = Modifier.tvFocusBorder(backInteractionSource),
                    colors = ButtonDefaults.buttonColors(containerColor = TvliveColors.BackgroundElevated),
                    shape = RoundedCornerShape(UiConstants.Dimens.ROUNDED_MD),
                    interactionSource = backInteractionSource
                ) {
                    Text(
                        text = stringResource(CommonR.string.back_with_arrow),
                        color = TvliveColors.TextPrimary,
                        fontSize = UiConstants.Text.SIZE_ERROR_BUTTON,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
