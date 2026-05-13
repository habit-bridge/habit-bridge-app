package com.example.habit_bridge_demo.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateParticipationRequest(
    val challengeId: String,
)

@Serializable
data class VerificationSummaryDto(
    val totalSlots: Int,
    val completedSlots: Int,
    val nextOpenSlotIndex: Int? = null,
)

@Serializable
data class EscrowInfoDto(
    val ledgerTxHash: String? = null,
    val preparedAt: String? = null,
    val confirmedAt: String? = null,
)

@Serializable
data class ParticipationDto(
    val id: String,
    val userId: String,
    val challengeId: String,
    val status: String,
    val challenge: ChallengeDto? = null,
    val verificationSummary: VerificationSummaryDto? = null,
    val escrow: EscrowInfoDto? = null,
    val createdAt: String? = null,
)
