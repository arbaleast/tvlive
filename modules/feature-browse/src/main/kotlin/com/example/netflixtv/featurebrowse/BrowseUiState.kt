package com.example.netflixtv.featurebrowse

import com.example.netflixtv.data.Category

data class BrowseUiState(
    val currentCatalog: String = "default",
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true,
    val catalogSwitchInProgress: Boolean = false,
    val error: String? = null
)
