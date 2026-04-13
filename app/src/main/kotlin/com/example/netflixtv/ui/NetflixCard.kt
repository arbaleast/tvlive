package com.example.netflixtv.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.graphics.painter.ColorPainter
import com.example.netflixtv.data.Content

@Composable
fun NetflixCard(
    content: Content,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Card(
        modifier = modifier
            .width(150.dp)
            .height(280.dp)
            .then(
                if (isFocused) {
                    Modifier.border(4.dp, Color.Red, RoundedCornerShape(8.dp))
                } else Modifier
            )
            .focusable(interactionSource = interactionSource),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isFocused) 4.dp else 0.dp
        ),
        onClick = onClick
    ) {
        Box {
            AsyncImage(
                model = content.thumbnailUrl,
                contentDescription = content.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                placeholder = ColorPainter(Color.DarkGray),
                error = ColorPainter(Color.DarkGray)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(
                        if (isFocused) Color.Red.copy(alpha = 0.9f)
                        else Color.Black.copy(alpha = 0.7f)
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = content.title,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
