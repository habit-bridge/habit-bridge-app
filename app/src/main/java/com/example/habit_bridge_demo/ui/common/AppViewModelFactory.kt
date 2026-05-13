package com.example.habit_bridge_demo.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.habit_bridge_demo.AppContainer
import com.example.habit_bridge_demo.HabitBridgeApp

/**
 * Helper to obtain the app-wide [AppContainer] inside composables.
 */
@Composable
fun appContainer(): AppContainer {
    val ctx = LocalContext.current.applicationContext as HabitBridgeApp
    return ctx.container
}

/**
 * Build a [ViewModelProvider.Factory] using lambda initializers that have access to [AppContainer].
 */
inline fun <reified VM : ViewModel> appViewModelFactory(
    crossinline create: (AppContainer, SavedStateHandle) -> VM,
): ViewModelProvider.Factory = viewModelFactory {
    initializer {
        val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HabitBridgeApp)
        val handle = this.createSavedStateHandle()
        create(app.container, handle)
    }
}
