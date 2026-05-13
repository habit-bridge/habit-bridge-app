package com.example.habit_bridge_demo.data.remote.api

import com.example.habit_bridge_demo.data.remote.dto.ChallengeDto
import com.example.habit_bridge_demo.data.remote.dto.CreateChallengeRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ChallengeApi {
    @POST("challenges")
    suspend fun create(@Body body: CreateChallengeRequest): ChallengeDto

    @GET("challenges")
    suspend fun list(
        @Query("status") status: String? = null,
        @Query("participating") participating: String? = null,
    ): List<ChallengeDto>

    @GET("challenges/{id}")
    suspend fun get(@Path("id") id: String): ChallengeDto
}
