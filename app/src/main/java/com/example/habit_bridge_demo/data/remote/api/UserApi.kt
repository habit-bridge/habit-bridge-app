package com.example.habit_bridge_demo.data.remote.api

import com.example.habit_bridge_demo.data.remote.dto.UpdateUserRequest
import com.example.habit_bridge_demo.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface UserApi {
    @GET("users/me")
    suspend fun getMe(): UserDto

    @PATCH("users/me")
    suspend fun updateMe(@Body body: UpdateUserRequest): UserDto
}
