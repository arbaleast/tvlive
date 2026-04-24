package com.example.netflixtv.featurehome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.netflixtv.data.ChannelDefinitions
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
            val channels = ChannelDefinitions.ALL.map { ch ->
                ChannelItem(id = ch.id, title = ch.title)
            }
            _uiState.value = HomeUiState(channels = channels, isLoading = false)
        }
    }

    fun refresh() = loadChannels()
}
