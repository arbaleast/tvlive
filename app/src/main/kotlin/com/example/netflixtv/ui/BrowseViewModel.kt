package com.example.netflixtv.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.netflixtv.data.BrowseUiState
import com.example.netflixtv.data.Category
import com.example.netflixtv.data.ContentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BrowseViewModel(
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(BrowseUiState())
    val uiState: StateFlow<BrowseUiState> = _uiState.asStateFlow()

    private var currentRepository: ContentRepository? = null

    init {
        loadCatalog("default")
    }

    fun loadCatalog(catalogId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                catalogSwitchInProgress = true,
                error = null
            )
            
            try {
                val repository = withContext(Dispatchers.IO) {
                    ContentRepository(context, catalogId)
                }
                currentRepository = repository
                
                val categories = withContext(Dispatchers.IO) {
                    repository.loadCategories()
                }
                
                _uiState.value = BrowseUiState(
                    currentCatalog = catalogId,
                    categories = categories,
                    isLoading = false,
                    catalogSwitchInProgress = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    catalogSwitchInProgress = false,
                    error = e.message ?: "Failed to load catalog"
                )
            }
        }
    }

    fun switchCatalog(catalogId: String) {
        if (catalogId != _uiState.value.currentCatalog) {
            loadCatalog(catalogId)
        }
    }

    fun getAvailableCatalogs(): List<String> {
        return currentRepository?.getAvailableCatalogs() ?: listOf("default", "v2")
    }

    fun refresh() {
        loadCatalog(_uiState.value.currentCatalog)
    }
}