package com.example.netflixtv.ui

import android.os.Trace
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.netflixtv.data.Content
import com.example.netflixtv.data.ContentRepository
import com.example.netflixtv.data.SearchUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(
    private val repository: ContentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private var allContent: List<Content> = emptyList()

    companion object {
        private const val DEBOUNCE_MS = 250L
        private const val TRACE_SEARCH = "SearchViewModel.search"
    }

    init {
        loadAllContent()
    }

    private fun loadAllContent() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true)
            try {
                allContent = repository.getAllContent()
                _uiState.value = _uiState.value.copy(isSearching = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSearching = false,
                    error = e.message ?: "Failed to load content"
                )
            }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        
        searchJob?.cancel()
        
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(results = emptyList(), isSearching = false)
            return
        }
        
        searchJob = viewModelScope.launch {
            delay(DEBOUNCE_MS)
            _uiState.value = _uiState.value.copy(isSearching = true)
            
            val results = withContext(kotlinx.coroutines.Dispatchers.Default) {
                Trace.beginSection(TRACE_SEARCH)
                try {
                    allContent.filter { it.title.contains(query, ignoreCase = true) }
                } finally {
                    Trace.endSection()
                }
            }
            
            _uiState.value = _uiState.value.copy(
                results = results,
                isSearching = false
            )
        }
    }

    fun clearSearch() {
        searchJob?.cancel()
        _uiState.value = SearchUiState()
        loadAllContent()
    }
}