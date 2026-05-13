package com.example.habit_bridge_demo.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class VerificationFrequencyDto(
    val type: String,
    val timesPerWeek: Int? = null,
)

@Serializable
data class ChallengeDto(
    val id: String,
    val creatorId: String? = null,
    val title: String,
    val description: String,
    val startDate: String,
    val durationWeeks: Int,
    val verificationFrequency: VerificationFrequencyDto,
    val verificationMethodDescription: String,
    val depositXrp: String,
    val status: String,
    val endsAt: String? = null,
    val createdAt: String? = null,
    val activeParticipantCount: Int? = null,
)

@Serializable
data class CreateChallengeRequest(
    val title: String,
    val description: String,
    val startDate: String,
    val durationWeeks: Int,
    val verificationFrequency: VerificationFrequencyDto,
    val verificationMethodDescription: String,
    val depositXrp: String,
)
