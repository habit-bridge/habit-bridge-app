package com.example.habit_bridge_demo.ui.screens.participation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habit_bridge_demo.AppContainer
import com.example.habit_bridge_demo.data.remote.ApiException
import com.example.habit_bridge_demo.data.remote.apiCall
import com.example.habit_bridge_demo.data.remote.dto.ChallengeDto
import com.example.habit_bridge_demo.data.remote.dto.ParticipationDto
import com.example.habit_bridge_demo.data.remote.dto.VerificationSlotDto
import com.example.habit_bridge_demo.ui.common.appViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ParticipationDetailUiState(
    val loading: Boolean = true,
    val error: String? = null,
    val participation: ParticipationDto? = null,
    val challenge: ChallengeDto? = null,
    val slots: List<VerificationSlotDto> = emptyList(),
)

class ParticipationDetailViewModel(
    private val container: AppContainer,
    savedState: SavedStateHandle,
) : ViewModel() {

    val participationId: String = checkNotNull(savedState["participationId"])

    private val _state = MutableStateFlow(ParticipationDetailUiState())
    val state: StateFlow<ParticipationDetailUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val p = apiCall { container.api.participations.get(participationId) }
                val c = p.challenge ?: runCatching {
                    apiCall { container.api.challenges.get(p.challengeId) }
                }.getOrNull()
                val slots = runCatching {
                    apiCall { container.api.participations.getSlots(participationId) }.slots
                }.getOrDefault(emptyList())
                _state.update {
                    it.copy(loading = false, participation = p, challenge = c, slots = slots)
                }
            } catch (e: ApiException) {
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    companion object {
        val Factory = appViewModelFactory { c, h -> ParticipationDetailViewModel(c, h) }
    }
}
