package com.example.habit_bridge_demo.ui.screens.challenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habit_bridge_demo.AppContainer
import com.example.habit_bridge_demo.data.remote.ApiException
import com.example.habit_bridge_demo.data.remote.apiCall
import com.example.habit_bridge_demo.data.remote.dto.ChallengeDto
import com.example.habit_bridge_demo.data.remote.dto.CreateChallengeRequest
import com.example.habit_bridge_demo.data.remote.dto.VerificationFrequencyDto
import com.example.habit_bridge_demo.ui.common.appViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class FreqType { DAILY, WEEKLY }

data class CreateChallengeUiState(
    val title: String = "",
    val description: String = "",
    val startDate: LocalDate = LocalDate.now().plusDays(1),
    val durationWeeks: Int = 4,
    val freqType: FreqType = FreqType.WEEKLY,
    val timesPerWeek: Int = 3,
    val verificationMethodDescription: String = "",
    val depositXrp: String = "10",
    val loading: Boolean = false,
    val error: String? = null,
    val created: ChallengeDto? = null,
)

class CreateChallengeViewModel(private val container: AppContainer) : ViewModel() {
    private val _state = MutableStateFlow(CreateChallengeUiState())
    val state: StateFlow<CreateChallengeUiState> = _state.asStateFlow()

    fun onTitle(v: String) = _state.update { it.copy(title = v, error = null) }
    fun onDescription(v: String) = _state.update { it.copy(description = v, error = null) }
    fun onStartDate(v: LocalDate) = _state.update { it.copy(startDate = v) }
    fun onDurationWeeks(v: Int) = _state.update { it.copy(durationWeeks = v.coerceAtLeast(1)) }
    fun onFreqType(v: FreqType) = _state.update { it.copy(freqType = v) }
    fun onTimesPerWeek(v: Int) = _state.update { it.copy(timesPerWeek = v.coerceIn(1, 7)) }
    fun onMethod(v: String) = _state.update { it.copy(verificationMethodDescription = v, error = null) }
    fun onDeposit(v: String) = _state.update { it.copy(depositXrp = v, error = null) }

    fun submit() {
        val s = _state.value
        when {
            s.title.isBlank() -> {
                _state.update { it.copy(error = "제목을 입력해 주세요.") }; return
            }
            s.description.isBlank() -> {
                _state.update { it.copy(error = "소개를 입력해 주세요.") }; return
            }
            s.verificationMethodDescription.isBlank() -> {
                _state.update { it.copy(error = "인증 방법을 입력해 주세요.") }; return
            }
            s.depositXrp.toDoubleOrNull() == null || s.depositXrp.toDouble() <= 0.0 -> {
                _state.update { it.copy(error = "보증금은 0보다 큰 숫자여야 합니다.") }; return
            }
        }
        val freq = when (s.freqType) {
            FreqType.DAILY -> VerificationFrequencyDto(type = "DAILY")
            FreqType.WEEKLY -> VerificationFrequencyDto(type = "WEEKLY", timesPerWeek = s.timesPerWeek)
        }
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val created = apiCall {
                    container.api.challenges.create(
                        CreateChallengeRequest(
                            title = s.title.trim(),
                            description = s.description.trim(),
                            startDate = s.startDate.toString(),
                            durationWeeks = s.durationWeeks,
                            verificationFrequency = freq,
                            verificationMethodDescription = s.verificationMethodDescription.trim(),
                            depositXrp = s.depositXrp.trim(),
                        )
                    )
                }
                _state.update { it.copy(loading = false, created = created) }
            } catch (e: ApiException) {
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    companion object {
        val Factory = appViewModelFactory { c, _ -> CreateChallengeViewModel(c) }
    }
}
