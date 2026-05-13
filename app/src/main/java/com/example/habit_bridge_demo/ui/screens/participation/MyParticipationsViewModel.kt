package com.example.habit_bridge_demo.ui.screens.participation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habit_bridge_demo.AppContainer
import com.example.habit_bridge_demo.data.remote.ApiException
import com.example.habit_bridge_demo.data.remote.apiCall
import com.example.habit_bridge_demo.data.remote.dto.ChallengeDto
import com.example.habit_bridge_demo.data.remote.dto.ParticipationDto
import com.example.habit_bridge_demo.ui.common.appViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MyParticipationsUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val participations: List<ParticipationDto> = emptyList(),
    val challengeMap: Map<String, ChallengeDto> = emptyMap(),
)

class MyParticipationsViewModel(private val container: AppContainer) : ViewModel() {
    private val _state = MutableStateFlow(MyParticipationsUiState())
    val state: StateFlow<MyParticipationsUiState> = _state.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val list = apiCall { container.api.participations.listMine() }
                // Resolve challenge titles for participations that don't embed them
                val missing = list
                    .filter { it.challenge == null }
                    .map { it.challengeId }
                    .distinct()
                val resolved = mutableMapOf<String, ChallengeDto>()
                for (id in missing) {
                    runCatching { apiCall { container.api.challenges.get(id) } }
                        .getOrNull()?.let { resolved[id] = it }
                }
                list.mapNotNull { it.challenge }.forEach { resolved[it.id] = it }
                _state.update {
                    it.copy(loading = false, participations = list, challengeMap = resolved)
                }
            } catch (e: ApiException) {
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    companion object {
        val Factory = appViewModelFactory { c, _ -> MyParticipationsViewModel(c) }
    }
}
