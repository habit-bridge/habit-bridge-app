package com.example.habit_bridge_demo.ui.screens.participation

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habit_bridge_demo.AppContainer
import com.example.habit_bridge_demo.data.remote.ApiException
import com.example.habit_bridge_demo.data.remote.apiCall
import com.example.habit_bridge_demo.ui.common.appViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

data class VerificationUploadUiState(
    val pickedUri: Uri? = null,
    val uploading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
)

class VerificationUploadViewModel(
    private val container: AppContainer,
    savedState: SavedStateHandle,
) : ViewModel() {
    val participationId: String = checkNotNull(savedState["participationId"])
    val slotIndex: Int = (savedState.get<String>("slotIndex") ?: "0").toIntOrNull() ?: 0

    private val _state = MutableStateFlow(VerificationUploadUiState())
    val state: StateFlow<VerificationUploadUiState> = _state.asStateFlow()

    fun setUri(uri: Uri?) = _state.update { it.copy(pickedUri = uri, error = null) }

    fun upload(context: Context) {
        val uri = _state.value.pickedUri ?: run {
            _state.update { it.copy(error = "사진을 먼저 선택해 주세요.") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(uploading = true, error = null) }
            try {
                val resolver = context.contentResolver
                val mime = resolver.getType(uri) ?: "image/jpeg"
                val bytes = resolver.openInputStream(uri)?.use { it.readBytes() }
                    ?: throw IllegalStateException("이미지를 읽을 수 없습니다.")
                val fileBody = bytes.toRequestBody(mime.toMediaTypeOrNull())
                val filePart = MultipartBody.Part.createFormData(
                    name = "file",
                    filename = "verification_${System.currentTimeMillis()}.jpg",
                    body = fileBody,
                )
                val slotBody: RequestBody = slotIndex.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                apiCall {
                    container.api.verifications.upload(participationId, filePart, slotBody)
                }
                _state.update { it.copy(uploading = false, success = true) }
            } catch (e: ApiException) {
                _state.update { it.copy(uploading = false, error = e.message) }
            } catch (e: Exception) {
                _state.update { it.copy(uploading = false, error = e.message ?: "업로드 중 오류가 발생했습니다.") }
            }
        }
    }

    companion object {
        val Factory = appViewModelFactory { c, h -> VerificationUploadViewModel(c, h) }
    }
}
