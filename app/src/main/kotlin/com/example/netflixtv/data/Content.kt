package com.example.netflixtv.data

data class Content(
    val id: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val backdropUrl: String,
    val videoUrl: String,
    val category: String,
    val releaseYear: Int,
    val rating: String,
    val duration: String
)