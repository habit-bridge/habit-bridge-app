package com.example.habit_bridge_demo.ui.screens.participation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habit_bridge_demo.AppContainer
import com.example.habit_bridge_demo.data.remote.ApiException
import com.example.habit_bridge_demo.data.remote.apiCall
import com.example.habit_bridge_demo.ui.common.appViewModelFactory
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
)

class ParticipationPendingViewModel(
    private val container: AppContainer,
    savedState: SavedStateHandle,
) : ViewModel() {

    val participationId: String = checkNotNull(savedState["participationId"])

    private val _state = MutableStateFlow(ParticipationPendingUiState())
    val state: StateFlow<ParticipationPendingUiState> = _state.asStateFlow()

    init { poll() }

    private fun poll() {
        viewModelScope.launch {
            repeat(30) { // ~ up to 60s
                try {
                    val p = apiCall { container.api.participations.get(participationId) }
                    _state.update { it.copy(status = p.status, ledgerTxHash = p.escrow?.ledgerTxHash) }
                    when (p.status.uppercase()) {
                        "ACTIVE" -> {
                            _state.update { it.copy(confirmed = true) }
                            return@launch
                        }
                        "CANCELLED", "COMPLETED_FAIL" -> {
                            _state.update { it.copy(error = "참여가 취소되었습니다.") }
                            return@launch
                        }
                    }
                } catch (e: ApiException) {
                    _state.update { it.copy(error = e.message) }
                }
                delay(2_000)
            }
            if (!_state.value.confirmed) {
                _state.update { it.copy(error = "확인이 지연되고 있어요. 잠시 후 다시 확인해 주세요.") }
            }
        }
    }

    companion object {
        val Factory = appViewModelFactory { c, h -> ParticipationPendingViewModel(c, h) }
    }
}
