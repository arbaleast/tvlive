package com.example.netflixtv.ui

import android.os.Trace
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.netflixtv.data.ContentRepository
import com.example.netflixtv.data.HomeUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val repository: ContentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    companion object {
        private const val TRACE_SECTION = "HomeViewModel.loadData"
    }

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val categories = withContext(Dispatchers.IO) {
                    Trace.beginSection(TRACE_SECTION)
                    try {
                        repository.loadCategories()
                    } finally {
                        Trace.endSection()
                    }
                }
                val heroes = categories.firstOrNull()?.items?.take(5) ?: emptyList()
                _uiState.value = HomeUiState(
                    categories = categories,
                    heroes = heroes,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun refresh() {
        loadData()
    }
}