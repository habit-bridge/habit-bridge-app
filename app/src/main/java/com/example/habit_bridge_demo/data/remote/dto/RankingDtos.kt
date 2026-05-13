package com.example.habit_bridge_demo.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DonationRankItemDto(
    val rank: Int,
    val userId: String,
    val displayName: String? = null,
    val totalDonationXrp: String,
)

@Serializable
data class DonationRankingResponse(
    val ranked: List<DonationRankItemDto>,
)

@Serializable
data class SuccessRankItemDto(
    val rank: Int,
    val userId: String,
    val displayName: String? = null,
    val successCount: Int,
)

@Serializable
data class SuccessRankingResponse(
    val ranked: List<SuccessRankItemDto>,
)
