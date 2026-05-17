package com.example.habit_bridge_demo

import android.content.Context
import com.example.habit_bridge_demo.data.local.AuthSessionEvents
import com.example.habit_bridge_demo.data.local.TabSelectionEvents
import com.example.habit_bridge_demo.data.local.TokenStore
import com.example.habit_bridge_demo.data.remote.ApiClient

/**
 * Lightweight manual DI container. Lives as a singleton on the [HabitBridgeApp].
 */
class AppContainer(appContext: Context) {
    val tokenStore: TokenStore = TokenStore(appContext)
    val authSessionEvents: AuthSessionEvents = AuthSessionEvents()
    val tabSelectionEvents: TabSelectionEvents = TabSelectionEvents()
    val api: ApiClient = ApiClient(tokenStore, authSessionEvents)
}
