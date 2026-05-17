package com.example.habit_bridge_demo.data.local

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Cross-screen channel used to request that the main bottom-tab scaffold switch to a specific
 * tab (e.g. opening the profile tab from an inner detail screen).
 */
class TabSelectionEvents {
    private val _requestedTab = MutableSharedFlow<String>(
        extraBufferCapacity = 1,
    )
    val requestedTab = _requestedTab.asSharedFlow()

    fun requestTab(route: String) {
        _requestedTab.tryEmit(route)
    }
}
