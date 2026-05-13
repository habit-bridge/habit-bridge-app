package com.example.habit_bridge_demo.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class VerificationSlotDto(
    val slotIndex: Int,
    val windowStart: String,
    val windowEnd: String,
    val status: String,
    val imageUrl: String? = null,
    val submittedAt: String? = null,
)

@Serializable
data class VerificationSlotsResponse(
    val slots: List<VerificationSlotDto>,
)

@Serializable
data class VerificationDto(
    val id: String,
    val participationId: String,
    val slotIndex: Int,
    val imageUrl: String,
    val submittedAt: String,
    val status: String,
)
