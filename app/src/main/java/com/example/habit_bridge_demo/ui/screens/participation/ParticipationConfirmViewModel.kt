package com.example.habit_bridge_demo.ui.screens.participation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habit_bridge_demo.AppContainer
import com.example.habit_bridge_demo.data.remote.ApiException
import com.example.habit_bridge_demo.data.remote.apiCall
import com.example.habit_bridge_demo.data.remote.dto.ChallengeDto
import com.example.habit_bridge_demo.data.remote.dto.CreateParticipationRequest
import com.example.habit_bridge_demo.data.remote.dto.EscrowPrepareRequest
import com.example.habit_bridge_demo.data.remote.dto.EscrowPrepareResponse
import com.example.habit_bridge_demo.ui.common.appViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ParticipationConfirmUiState(
    val loading: Boolean = true,
    val error: String? = null,
    val challenge: ChallengeDto? = null,
    val acknowledged: Boolean = false,
    val signing: Boolean = false,
    val prepareResult: EscrowPrepareResponse? = null,
    val participationId: String? = null,
    /** Server signalled XRPL_ADDRESS_REQUIRED — the user must set an XRPL address first. */
    val needsXrplAddress: Boolean = false,
)

class ParticipationConfirmViewModel(
    private val container: AppContainer,
    savedState: SavedStateHandle,
) : ViewModel() {

    private val challengeId: String = checkNotNull(savedState["challengeId"])

    private val _state = MutableStateFlow(ParticipationConfirmUiState())
    val state: StateFlow<ParticipationConfirmUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val c = apiCall { container.api.challenges.get(challengeId) }
                _state.update { it.copy(loading = false, challenge = c) }
            } catch (e: ApiException) {
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    fun setAcknowledged(v: Boolean) = _state.update { it.copy(acknowledged = v) }

    fun startSigning() {
        val s = _state.value
        if (!s.acknowledged) return
        viewModelScope.launch {
            _state.update { it.copy(signing = true, error = null, needsXrplAddress = false) }
            try {
                // 1) create participation
                val p = apiCall {
                    container.api.participations.create(CreateParticipationRequest(challengeId))
                }
                // 2) prepare escrow
                val prep = apiCall {
                    container.api.escrow.prepare(EscrowPrepareRequest(participationId = p.id, walletProvider = "XUMM"))
                }
                _state.update {
                    it.copy(signing = false, participationId = p.id, prepareResult = prep)
                }
            } catch (e: ApiException) {
                _state.update {
                    it.copy(
                        signing = false,
                        error = e.message,
                        needsXrplAddress = e.code == "XRPL_ADDRESS_REQUIRED",
                    )
                }
            }
        }
    }

    fun clearNeedsXrplAddress() {
        _state.update { it.copy(needsXrplAddress = false, error = null) }
    }

    companion object {
        val Factory = appViewModelFactory { c, h -> ParticipationConfirmViewModel(c, h) }
    }
}
