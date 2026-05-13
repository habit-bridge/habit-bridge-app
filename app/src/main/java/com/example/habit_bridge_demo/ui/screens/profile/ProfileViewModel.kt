package com.example.habit_bridge_demo.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habit_bridge_demo.AppContainer
import com.example.habit_bridge_demo.data.remote.ApiException
import com.example.habit_bridge_demo.data.remote.apiCall
import com.example.habit_bridge_demo.data.remote.dto.UpdateUserRequest
import com.example.habit_bridge_demo.data.remote.dto.UserDto
import com.example.habit_bridge_demo.ui.common.appViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val loading: Boolean = false,
    val saving: Boolean = false,
    val error: String? = null,
    val user: UserDto? = null,
)

class ProfileViewModel(private val container: AppContainer) : ViewModel() {
    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val u = apiCall { container.api.users.getMe() }
                _state.update { it.copy(loading = false, user = u) }
            } catch (e: ApiException) {
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    fun saveDisplayName(name: String) {
        viewModelScope.launch {
            _state.update { it.copy(saving = true, error = null) }
            try {
                val u = apiCall { container.api.users.updateMe(UpdateUserRequest(displayName = name)) }
                _state.update { it.copy(saving = false, user = u) }
            } catch (e: ApiException) {
                _state.update { it.copy(saving = false, error = e.message) }
            }
        }
    }

    fun saveXrplAddress(address: String?) {
        viewModelScope.launch {
            _state.update { it.copy(saving = true, error = null) }
            try {
                val u = apiCall {
                    container.api.users.updateMe(UpdateUserRequest(xrplAddress = address?.takeIf { it.isNotBlank() }))
                }
                _state.update { it.copy(saving = false, user = u) }
            } catch (e: ApiException) {
                _state.update { it.copy(saving = false, error = e.message) }
            }
        }
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            container.tokenStore.clear()
            onDone()
        }
    }

    companion object {
        val Factory = appViewModelFactory { c, _ -> ProfileViewModel(c) }
    }
}
