package com.example.habit_bridge_demo.data.remote.api

import com.example.habit_bridge_demo.data.remote.dto.VerificationDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface VerificationApi {
    @Multipart
    @POST("participations/{id}/verifications")
    suspend fun upload(
        @Path("id") participationId: String,
        @Part file: MultipartBody.Part,
        @Part("slotIndex") slotIndex: RequestBody,
    ): VerificationDto
}
