package com.example.habit_bridge_demo.data.remote.api

import com.example.habit_bridge_demo.data.remote.dto.EscrowPrepareRequest
import com.example.habit_bridge_demo.data.remote.dto.EscrowPrepareResponse
import com.example.habit_bridge_demo.data.remote.dto.EscrowSubmitRequest
import com.example.habit_bridge_demo.data.remote.dto.EscrowSubmitResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface EscrowApi {
    @POST("escrow/prepare")
    suspend fun prepare(@Body body: EscrowPrepareRequest): EscrowPrepareResponse

    @POST("escrow/submit")
    suspend fun submit(@Body body: EscrowSubmitRequest): EscrowSubmitResponse
}
