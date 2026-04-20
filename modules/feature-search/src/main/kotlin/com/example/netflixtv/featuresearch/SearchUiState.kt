package com.example.netflixtv.featuresearch

import com.example.netflixtv.data.Content

data class SearchUiState(
    val query: String = "",
    val results: List<Content> = emptyList(),
    val isSearching: Boolean = false,
    val error: String? = null
)
