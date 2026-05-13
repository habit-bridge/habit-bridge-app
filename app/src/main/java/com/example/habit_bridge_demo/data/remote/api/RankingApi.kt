package com.example.habit_bridge_demo.data.remote.api

import com.example.habit_bridge_demo.data.remote.dto.DonationRankingResponse
import com.example.habit_bridge_demo.data.remote.dto.SuccessRankingResponse
import retrofit2.http.GET

interface RankingApi {
    @GET("rankings/donations/top")
    suspend fun donationTop(): DonationRankingResponse

    @GET("rankings/success/top")
    suspend fun successTop(): SuccessRankingResponse
}
