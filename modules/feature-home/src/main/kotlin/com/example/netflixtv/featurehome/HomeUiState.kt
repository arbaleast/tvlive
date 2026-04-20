package com.example.netflixtv.featurehome

import com.example.netflixtv.data.Category
import com.example.netflixtv.data.Content

data class HomeUiState(
    val categories: List<Category> = emptyList(),
    val heroes: List<Content> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
