package com.example.netflixtv.data

import kotlinx.coroutines.flow.Flow

interface ContentRepository {
    val catalogId: String

    suspend fun loadCategories(): List<Category>
    suspend fun getContentById(contentId: String): Content?
    fun getContentByIdSync(contentId: String): Content?
    fun getAllContent(): List<Content>
    fun getAvailableCatalogs(): List<String>
    fun getItemsByCategory(category: String): List<Content>
    fun observeCategories(): Flow<List<Category>>
}
