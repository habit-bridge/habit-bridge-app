package com.example.habit_bridge_demo.data.remote

import com.example.habit_bridge_demo.data.local.TokenStore
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Adds Authorization header from TokenStore for all requests except auth endpoints.
 */
class AuthInterceptor(private val tokenStore: TokenStore) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        val skip = path.endsWith("/auth/login") || path.endsWith("/auth/register")

        val token = if (skip) null else runBlocking { tokenStore.get() }

        val newRequest = if (!token.isNullOrBlank()) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }
        return chain.proceed(newRequest)
    }
}
