package com.example.habit_bridge_demo.data.local

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class AuthSessionEvents {
    private val _unauthorized = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
    )
    val unauthorized = _unauthorized.asSharedFlow()

    fun notifyUnauthorized() {
        _unauthorized.tryEmit(Unit)
    }
}
