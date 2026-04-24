package com.example.netflixtv.featurehome

data class ChannelItem(
    val id: String,
    val title: String,
    val thumbnailUrl: String = ""
)

data class HomeUiState(
    val channels: List<ChannelItem> = emptyList(),
    val isLoading: Boolean = true
)
