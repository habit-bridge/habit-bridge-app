package com.example.habit_bridge_demo

import android.content.Context
import com.example.habit_bridge_demo.data.local.TokenStore
import com.example.habit_bridge_demo.data.remote.ApiClient

/**
 * Lightweight manual DI container. Lives as a singleton on the [HabitBridgeApp].
 */
class AppContainer(appContext: Context) {
    val tokenStore: TokenStore = TokenStore(appContext)
    val api: ApiClient = ApiClient(tokenStore)
}
