package com.example.habit_bridge_demo.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DonationDto(
    val id: String,
    val challengeId: String,
    val participationId: String,
    val amountXrp: String,
    val attribution: String,
    val attributedDisplayName: String? = null,
    val userId: String? = null,
    val createdAt: String,
)

@Serializable
data class DonationListResponse(
    val items: List<DonationDto>,
    val nextCursor: String? = null,
)
