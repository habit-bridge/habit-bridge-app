package com.example.habit_bridge_demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.habit_bridge_demo.ui.navigation.AppNavHost
import com.example.habit_bridge_demo.ui.theme.HabitbridgedemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HabitbridgedemoTheme {
                AppNavHost()
            }
        }
    }
}
