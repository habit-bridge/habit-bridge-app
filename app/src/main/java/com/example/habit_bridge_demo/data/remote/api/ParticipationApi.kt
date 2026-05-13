package com.example.habit_bridge_demo.data.remote.api

import com.example.habit_bridge_demo.data.remote.dto.CreateParticipationRequest
import com.example.habit_bridge_demo.data.remote.dto.ParticipationDto
import com.example.habit_bridge_demo.data.remote.dto.VerificationSlotsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ParticipationApi {
    @POST("participations")
    suspend fun create(@Body body: CreateParticipationRequest): ParticipationDto

    @GET("participations/me")
    suspend fun listMine(
        @Query("status") status: String? = null,
    ): List<ParticipationDto>

    @GET("participations/{id}")
    suspend fun get(@Path("id") id: String): ParticipationDto

    @GET("participations/{id}/verification-slots")
    suspend fun getSlots(@Path("id") id: String): VerificationSlotsResponse
}
