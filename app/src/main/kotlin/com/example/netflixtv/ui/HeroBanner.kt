package com.example.netflixtv.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.graphics.painter.ColorPainter

@Composable
fun HeroBanner(
    imageUrl: String,
    title: String,
    description: String,
    onCtaClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val playInteractionSource = remember { MutableInteractionSource() }
    val isPlayFocused by playInteractionSource.collectIsFocusedAsState()

    val moreInfoInteractionSource = remember { MutableInteractionSource() }
    val isMoreInfoFocused by moreInfoInteractionSource.collectIsFocusedAsState()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(Color.Black),
            error = ColorPainter(Color.Black)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(600.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.8f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 48.dp, bottom = 48.dp)
                .width(500.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = title,
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = description,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(
                    modifier = Modifier
                        .then(
                            if (isPlayFocused) {
                                Modifier.border(
                                    width = 4.dp,
                                    color = Color.Red,
                                    shape = RoundedCornerShape(8.dp)
                                )
                            } else Modifier
                        )
                        .padding(3.dp)
                ) {
                    Button(
                        onClick = onCtaClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        ),
                        modifier = Modifier
                            .focusable(interactionSource = playInteractionSource),
                        interactionSource = playInteractionSource
                    ) {
                        Text(
                            text = "▶ Play",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .then(
                            if (isMoreInfoFocused) {
                                Modifier.border(
                                    width = 4.dp,
                                    color = Color.Red,
                                    shape = RoundedCornerShape(8.dp)
                                )
                            } else Modifier
                        )
                        .padding(3.dp)
                ) {
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier
                            .focusable(interactionSource = moreInfoInteractionSource),
                        interactionSource = moreInfoInteractionSource
                    ) {
                        Text(
                            text = "ℹ More Info",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
