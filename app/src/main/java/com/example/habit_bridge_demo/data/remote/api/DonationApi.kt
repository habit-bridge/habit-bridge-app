package com.example.habit_bridge_demo.data.remote.api

import com.example.habit_bridge_demo.data.remote.dto.DonationListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DonationApi {
    @GET("donations")
    suspend fun list(
        @Query("challengeId") challengeId: String? = null,
        @Query("userId") userId: String? = null,
        @Query("attribution") attribution: String? = null,
    ): DonationListResponse

    @GET("donations/me")
    suspend fun listMine(): DonationListResponse
}
