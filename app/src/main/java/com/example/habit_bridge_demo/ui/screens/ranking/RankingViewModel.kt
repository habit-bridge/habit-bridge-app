package com.example.habit_bridge_demo.ui.screens.ranking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habit_bridge_demo.AppContainer
import com.example.habit_bridge_demo.data.remote.ApiException
import com.example.habit_bridge_demo.data.remote.apiCall
import com.example.habit_bridge_demo.data.remote.dto.DonationRankItemDto
import com.example.habit_bridge_demo.data.remote.dto.SuccessRankItemDto
import com.example.habit_bridge_demo.ui.common.appViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RankingUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val donation: List<DonationRankItemDto> = emptyList(),
    val success: List<SuccessRankItemDto> = emptyList(),
)

class RankingViewModel(private val container: AppContainer) : ViewModel() {
    private val _state = MutableStateFlow(RankingUiState())
    val state: StateFlow<RankingUiState> = _state.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val d = apiCall { container.api.rankings.donationTop() }
                val s = apiCall { container.api.rankings.successTop() }
                _state.update {
                    it.copy(loading = false, donation = d.ranked, success = s.ranked)
                }
            } catch (e: ApiException) {
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    companion object {
        val Factory = appViewModelFactory { c, _ -> RankingViewModel(c) }
    }
}
