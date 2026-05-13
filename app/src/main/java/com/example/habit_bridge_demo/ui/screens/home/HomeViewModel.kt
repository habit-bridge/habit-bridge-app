package com.example.habit_bridge_demo.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habit_bridge_demo.AppContainer
import com.example.habit_bridge_demo.data.remote.ApiException
import com.example.habit_bridge_demo.data.remote.apiCall
import com.example.habit_bridge_demo.data.remote.dto.ChallengeDto
import com.example.habit_bridge_demo.ui.common.appViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val challenges: List<ChallengeDto> = emptyList(),
)

class HomeViewModel(private val container: AppContainer) : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val list = apiCall {
                    container.api.challenges.list(status = "SCHEDULED,ACTIVE")
                }
                _state.update { it.copy(loading = false, challenges = list) }
            } catch (e: ApiException) {
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    companion object {
        val Factory = appViewModelFactory { c, _ -> HomeViewModel(c) }
    }
}
