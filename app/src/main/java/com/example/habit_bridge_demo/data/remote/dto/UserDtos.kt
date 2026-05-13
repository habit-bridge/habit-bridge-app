package com.example.habit_bridge_demo.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val displayName: String? = null,
    val xrplAddress: String? = null,
)

@Serializable
data class UpdateUserRequest(
    val displayName: String? = null,
    val xrplAddress: String? = null,
)
