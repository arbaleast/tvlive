package com.example.netflixtv.featureplayer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.netflixtv.uicommon.R as CommonR
import com.example.netflixtv.uicommon.TvliveColors
import com.example.netflixtv.uicommon.UiConstants
import com.example.netflixtv.uicommon.tvFocusBorder

@Composable
internal fun PlayerControlsOverlay(
    title: String,
    isPlaying: Boolean,
    showControls: Boolean,
    onBackClick: () -> Unit,
    onPipClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onSeekForward: () -> Unit,
    onSeekBackward: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    if (!showControls) return

    Box(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(UiConstants.Dimens.PADDING_CONTENT_H)
                .padding(top = UiConstants.Overscan.VERTICAL)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Dimens.SPACING_M),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val backInteractionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }

            Button(
                onClick = onBackClick,
                modifier = Modifier.tvFocusBorder(backInteractionSource),
                colors = ButtonDefaults.buttonColors(containerColor = TvliveColors.BackgroundElevated.copy(0.7f)),
                shape = RoundedCornerShape(UiConstants.Dimens.ROUNDED_MD),
                interactionSource = backInteractionSource
            ) {
                Text(context.getString(CommonR.string.back_with_arrow), color = TvliveColors.TextPrimary)
            }

            Text(
                text = title,
                color = TvliveColors.TextPrimary.copy(0.9f),
                fontSize = UiConstants.Text.SIZE_BODY,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            val pipInteractionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }

            Button(
                onClick = onPipClick,
                modifier = Modifier.tvFocusBorder(pipInteractionSource),
                colors = ButtonDefaults.buttonColors(containerColor = TvliveColors.BackgroundElevated.copy(0.7f)),
                shape = RoundedCornerShape(UiConstants.Dimens.ROUNDED_MD),
                interactionSource = pipInteractionSource
            ) {
                Text(context.getString(CommonR.string.pip), color = TvliveColors.TextPrimary)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(UiConstants.Dimens.PADDING_CONTENT_H)
                .padding(bottom = UiConstants.Overscan.VERTICAL)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Dimens.PLAYBACK_CONTROLS_BUTTON_SPACING.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val seekBackInteractionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }

            Button(
                onClick = onSeekBackward,
                modifier = Modifier.tvFocusBorder(seekBackInteractionSource),
                colors = ButtonDefaults.buttonColors(containerColor = TvliveColors.BackgroundElevated.copy(0.7f)),
                shape = RoundedCornerShape(UiConstants.Dimens.ROUNDED_MD),
                interactionSource = seekBackInteractionSource
            ) {
                Text(context.getString(CommonR.string.seek_back), color = TvliveColors.TextPrimary)
            }

            val playPauseInteractionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }

            Button(
                onClick = onPlayPauseClick,
                modifier = Modifier.tvFocusBorder(playPauseInteractionSource),
                colors = ButtonDefaults.buttonColors(containerColor = TvliveColors.Primary),
                shape = RoundedCornerShape(UiConstants.Dimens.ROUNDED_MD),
                interactionSource = playPauseInteractionSource
            ) {
                Text(
                    text = if (isPlaying) context.getString(CommonR.string.pause) else context.getString(CommonR.string.play),
                    color = TvliveColors.TextPrimary
                )
            }

            val seekForwardInteractionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }

            Button(
                onClick = onSeekForward,
                modifier = Modifier.tvFocusBorder(seekForwardInteractionSource),
                colors = ButtonDefaults.buttonColors(containerColor = TvliveColors.BackgroundElevated.copy(0.7f)),
                shape = RoundedCornerShape(UiConstants.Dimens.ROUNDED_MD),
                interactionSource = seekForwardInteractionSource
            ) {
                Text(context.getString(CommonR.string.seek_forward), color = TvliveColors.TextPrimary)
            }
        }
    }
}
