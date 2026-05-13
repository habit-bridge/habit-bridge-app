package com.example.habit_bridge_demo.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ApiErrorDto(
    val statusCode: Int? = null,
    val error: String? = null,
    val code: String? = null,
    val message: String? = null,
    val details: JsonElement? = null,
)
