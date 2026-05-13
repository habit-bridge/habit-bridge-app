package com.example.habit_bridge_demo.data.remote.api

import com.example.habit_bridge_demo.data.remote.dto.LoginRequest
import com.example.habit_bridge_demo.data.remote.dto.LoginResponse
import com.example.habit_bridge_demo.data.remote.dto.RegisterRequest
import com.example.habit_bridge_demo.data.remote.dto.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): RegisterResponse

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse
}
