package com.example.netflixtv.featurebrowse

import android.os.Trace
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.netflixtv.data.ContentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BrowseViewModel(
    private val repository: ContentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BrowseUiState())
    val uiState: StateFlow<BrowseUiState> = _uiState.asStateFlow()

    private var currentCatalogId: String = repository.catalogId

    companion object {
        private const val TRACE_LOAD = "BrowseViewModel.loadCatalog"
    }

    init {
        loadCatalog(currentCatalogId)
    }

    fun loadCatalog(catalogId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                catalogSwitchInProgress = true,
                error = null
            )

            try {
                val categories = withContext(Dispatchers.IO) {
                    Trace.beginSection(TRACE_LOAD)
                    try {
                        repository.loadCategories()
                    } finally {
                        Trace.endSection()
                    }
                }

                currentCatalogId = catalogId
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

    fun getAvailableCatalogs(): List<String> = repository.getAvailableCatalogs()

    fun refresh() {
        loadCatalog(_uiState.value.currentCatalog)
    }
}
