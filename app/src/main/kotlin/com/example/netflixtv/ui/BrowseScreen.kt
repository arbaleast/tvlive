package com.example.netflixtv.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.netflixtv.data.Content
import com.example.netflixtv.data.ContentRepository

@Composable
fun BrowseScreen(
    repository: ContentRepository,
    onContentClick: (Content) -> Unit,
    onBackClick: () -> Unit
) {
    var expandedCategories by remember { mutableStateOf(setOf<String>()) }
    var selectedCatalog by remember { mutableStateOf("default") }
    val catalogs = remember { repository.getAvailableCatalogs() }
    val currentRepository = remember(selectedCatalog, repository) {
        if (selectedCatalog == "default") repository
        else ContentRepository(repository.context, selectedCatalog)
    }
    val categories = remember(currentRepository) { currentRepository.loadCategories() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "Browse",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        catalogs.forEach { catalog ->
                            val isSelected = catalog == selectedCatalog
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isSelected) Color.Red else Color.DarkGray)
                                    .border(1.dp, if (isSelected) Color.Red else Color.Gray, RoundedCornerShape(20.dp))
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .focusable()
                            ) {
                                Text(
                                    text = catalog.uppercase(),
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            items(categories) { category ->
                val isExpanded = expandedCategories.contains(category.name)

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).focusable(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = category.name, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text(text = if (isExpanded) "Show Less" else "See All (${category.items.size})", color = Color.Red, fontSize = 14.sp)
                    }

                    if (isExpanded) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(category.items) { content ->
                                NetflixCard(content = content, onClick = { onContentClick(content) })
                            }
                        }
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            category.items.take(5).forEach { content ->
                                NetflixCard(content = content, onClick = { onContentClick(content) })
                            }
                            if (category.items.size > 5) {
                                Box(modifier = Modifier.size(width = 150.dp, height = 225.dp).background(Color.DarkGray), contentAlignment = Alignment.Center) {
                                    Text(text = "+${category.items.size - 5}", color = Color.White, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
