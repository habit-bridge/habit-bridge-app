package com.example.habit_bridge_demo.ui.screens.challenge

import androidx.lifecycle.SavedStateHandle
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

data class ChallengeDetailUiState(
    val loading: Boolean = true,
    val error: String? = null,
    val challenge: ChallengeDto? = null,
    val alreadyParticipating: Boolean = false,
    val myParticipationId: String? = null,
)

class ChallengeDetailViewModel(
    private val container: AppContainer,
    savedState: SavedStateHandle,
) : ViewModel() {

    private val challengeId: String = checkNotNull(savedState["challengeId"])

    private val _state = MutableStateFlow(ChallengeDetailUiState())
    val state: StateFlow<ChallengeDetailUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val c = apiCall { container.api.challenges.get(challengeId) }
                val mine = runCatching {
                    apiCall { container.api.participations.listMine() }
                }.getOrNull().orEmpty()
                val mine1 = mine.firstOrNull { it.challengeId == challengeId }
                _state.update {
                    it.copy(
                        loading = false,
                        challenge = c,
                        alreadyParticipating = mine1 != null,
                        myParticipationId = mine1?.id,
                    )
                }
            } catch (e: ApiException) {
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    companion object {
        val Factory = appViewModelFactory { c, h -> ChallengeDetailViewModel(c, h) }
    }
}
