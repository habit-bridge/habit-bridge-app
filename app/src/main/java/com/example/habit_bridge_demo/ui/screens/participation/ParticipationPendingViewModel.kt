package com.example.habit_bridge_demo.ui.screens.participation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habit_bridge_demo.AppContainer
import com.example.habit_bridge_demo.data.remote.ApiException
import com.example.habit_bridge_demo.data.remote.apiCall
import com.example.habit_bridge_demo.ui.common.appViewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ParticipationPendingUiState(
    val status: String = "DEPOSIT_SUBMITTED",
    val error: String? = null,
    val confirmed: Boolean = false,
    val ledgerTxHash: String? = null,
    val polling: Boolean = false,
    val timedOut: Boolean = false,
)

class ParticipationPendingViewModel(
    private val container: AppContainer,
    savedState: SavedStateHandle,
) : ViewModel() {

    val participationId: String = checkNotNull(savedState["participationId"])

    private val _state = MutableStateFlow(ParticipationPendingUiState())
    val state: StateFlow<ParticipationPendingUiState> = _state.asStateFlow()

    private var pollJob: Job? = null

    init { startPolling() }

    /** Cancels any in-flight polling and starts a fresh window. Used for the "다시 확인" CTA. */
    fun retry() {
        startPolling()
    }

    private fun startPolling() {
        pollJob?.cancel()
        _state.update { it.copy(error = null, timedOut = false, polling = true) }
        pollJob = viewModelScope.launch {
            // Webhook delivery + processing can take a while when developing through ngrok.
            // Poll for up to ~120s (60 attempts * 2s).
            val maxAttempts = 60
            repeat(maxAttempts) {
                try {
                    val p = apiCall { container.api.participations.get(participationId) }
                    _state.update {
                        it.copy(
                            status = p.status,
                            ledgerTxHash = p.escrow?.ledgerTxHash,
                            error = null,
                        )
                    }
                    when (p.status.uppercase()) {
                        "ACTIVE" -> {
                            _state.update { it.copy(confirmed = true, polling = false) }
                            return@launch
                        }
                        "CANCELLED", "COMPLETED_FAIL" -> {
                            _state.update {
                                it.copy(
                                    polling = false,
                                    error = "참여가 취소되었습니다.",
                                )
                            }
                            return@launch
                        }
                    }
                } catch (_: ApiException) {
                    // Transient network/server hiccup — keep polling silently.
                }
                delay(2_000)
            }
            if (!_state.value.confirmed) {
                _state.update {
                    it.copy(
                        polling = false,
                        timedOut = true,
                        error = "확인이 지연되고 있어요. 서명을 마쳤다면 ‘다시 확인’을 눌러주세요.",
                    )
                }
            }
        }
    }

    companion object {
        val Factory = appViewModelFactory { c, h -> ParticipationPendingViewModel(c, h) }
    }
}
