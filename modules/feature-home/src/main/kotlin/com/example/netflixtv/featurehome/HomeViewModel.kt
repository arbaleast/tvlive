package com.example.netflixtv.featurehome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadChannels()
    }

    private fun loadChannels() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val channels = listOf(
                ChannelItem(id = "cctv1", title = "CCTV-1 综合"),
                ChannelItem(id = "cctv2", title = "CCTV-2 财经")
            )
            _uiState.value = HomeUiState(channels = channels, isLoading = false)
        }
    }

    fun refresh() = loadChannels()
}
