package com.example.habit_bridge_demo.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habit_bridge_demo.AppContainer
import com.example.habit_bridge_demo.data.remote.ApiException
import com.example.habit_bridge_demo.data.remote.apiCall
import com.example.habit_bridge_demo.data.remote.dto.LoginRequest
import com.example.habit_bridge_demo.data.remote.dto.RegisterRequest
import com.example.habit_bridge_demo.ui.common.appViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
)

class LoginViewModel(private val container: AppContainer) : ViewModel() {
    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onEmailChange(value: String) = _state.update { it.copy(email = value, error = null) }
    fun onPasswordChange(value: String) = _state.update { it.copy(password = value, error = null) }

    fun submit() {
        val s = _state.value
        if (s.email.isBlank() || s.password.isBlank()) {
            _state.update { it.copy(error = "이메일과 비밀번호를 입력해 주세요.") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val res = apiCall { container.api.auth.login(LoginRequest(s.email.trim(), s.password)) }
                container.tokenStore.save(res.accessToken)
                _state.update { it.copy(loading = false, success = true) }
            } catch (e: ApiException) {
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    companion object {
        val Factory = appViewModelFactory { c, _ -> LoginViewModel(c) }
    }
}

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val passwordConfirm: String = "",
    val displayName: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
)

class RegisterViewModel(private val container: AppContainer) : ViewModel() {
    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()

    fun onEmailChange(v: String) = _state.update { it.copy(email = v, error = null) }
    fun onPasswordChange(v: String) = _state.update { it.copy(password = v, error = null) }
    fun onPasswordConfirmChange(v: String) = _state.update { it.copy(passwordConfirm = v, error = null) }
    fun onDisplayNameChange(v: String) = _state.update { it.copy(displayName = v, error = null) }

    fun submit() {
        val s = _state.value
        when {
            s.email.isBlank() -> {
                _state.update { it.copy(error = "이메일을 입력해 주세요.") }; return
            }
            !s.email.contains('@') -> {
                _state.update { it.copy(error = "올바른 이메일 형식이 아닙니다.") }; return
            }
            s.password.length < 8 -> {
                _state.update { it.copy(error = "비밀번호는 8자 이상으로 설정해 주세요.") }; return
            }
            s.password != s.passwordConfirm -> {
                _state.update { it.copy(error = "비밀번호가 일치하지 않습니다.") }; return
            }
        }
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                apiCall {
                    container.api.auth.register(
                        RegisterRequest(
                            email = s.email.trim(),
                            password = s.password,
                            displayName = s.displayName.ifBlank { null },
                        )
                    )
                }
                _state.update { it.copy(loading = false, success = true) }
            } catch (e: ApiException) {
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    companion object {
        val Factory = appViewModelFactory { c, _ -> RegisterViewModel(c) }
    }
}
