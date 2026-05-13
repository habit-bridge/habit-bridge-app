package com.example.habit_bridge_demo.data.remote

import com.example.habit_bridge_demo.BuildConfig
import com.example.habit_bridge_demo.data.local.TokenStore
import com.example.habit_bridge_demo.data.remote.api.AuthApi
import com.example.habit_bridge_demo.data.remote.api.ChallengeApi
import com.example.habit_bridge_demo.data.remote.api.DonationApi
import com.example.habit_bridge_demo.data.remote.api.EscrowApi
import com.example.habit_bridge_demo.data.remote.api.ParticipationApi
import com.example.habit_bridge_demo.data.remote.api.RankingApi
import com.example.habit_bridge_demo.data.remote.api.UserApi
import com.example.habit_bridge_demo.data.remote.api.VerificationApi
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object NetworkModule {
    val json: Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        coerceInputValues = true
    }

    fun buildRetrofit(tokenStore: TokenStore): Retrofit {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenStore))
            .addInterceptor(logging)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }
}

class ApiClient(tokenStore: TokenStore) {
    private val retrofit = NetworkModule.buildRetrofit(tokenStore)

    val auth: AuthApi = retrofit.create(AuthApi::class.java)
    val users: UserApi = retrofit.create(UserApi::class.java)
    val challenges: ChallengeApi = retrofit.create(ChallengeApi::class.java)
    val participations: ParticipationApi = retrofit.create(ParticipationApi::class.java)
    val escrow: EscrowApi = retrofit.create(EscrowApi::class.java)
    val verifications: VerificationApi = retrofit.create(VerificationApi::class.java)
    val donations: DonationApi = retrofit.create(DonationApi::class.java)
    val rankings: RankingApi = retrofit.create(RankingApi::class.java)
}
