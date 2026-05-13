package com.example.habit_bridge_demo.data.remote

import com.example.habit_bridge_demo.data.remote.dto.ApiErrorDto
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

class ApiException(
    val statusCode: Int,
    val code: String? = null,
    message: String,
) : RuntimeException(message)

/**
 * Wraps a suspend block returning a Retrofit result and converts errors into [ApiException]s
 * carrying the server-provided `code` / `message` when available.
 */
suspend fun <T> apiCall(block: suspend () -> T): T {
    return try {
        block()
    } catch (e: HttpException) {
        val raw = e.response()?.errorBody()?.string().orEmpty()
        val parsed = runCatching {
            NetworkModule.json.decodeFromString(ApiErrorDto.serializer(), raw)
        }.getOrNull()
        throw ApiException(
            statusCode = e.code(),
            code = parsed?.code,
            message = parsed?.message ?: defaultMessage(e.code()),
        )
    } catch (e: IOException) {
        throw ApiException(
            statusCode = 0,
            code = "NETWORK_ERROR",
            message = "네트워크 연결을 확인해 주세요.",
        )
    } catch (e: SerializationException) {
        throw ApiException(
            statusCode = 0,
            code = "PARSE_ERROR",
            message = "응답을 해석할 수 없습니다.",
        )
    }
}

private fun defaultMessage(code: Int): String = when (code) {
    400 -> "잘못된 요청입니다."
    401 -> "로그인이 필요합니다."
    403 -> "권한이 없습니다."
    404 -> "리소스를 찾을 수 없습니다."
    409 -> "이미 처리된 요청입니다."
    in 500..599 -> "서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요."
    else -> "요청이 실패했습니다. ($code)"
}
