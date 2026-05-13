package com.example.habit_bridge_demo.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habit_bridge_demo.ui.components.ChallengeCard
import com.example.habit_bridge_demo.ui.components.EmptyState
import com.example.habit_bridge_demo.ui.components.ErrorBox
import com.example.habit_bridge_demo.ui.components.LoadingBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onChallengeClick: (String) -> Unit,
    onCreateChallenge: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0),
        topBar = {
            TopAppBar(
                title = { Text("Habit Bridge") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateChallenge) {
                Icon(Icons.Outlined.Add, contentDescription = "새 챌린지")
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        val merged = PaddingValues(
            top = innerPadding.calculateTopPadding(),
            bottom = innerPadding.calculateBottomPadding() + contentPadding.calculateBottomPadding(),
            start = 16.dp,
            end = 16.dp,
        )
        when {
            state.loading -> LoadingBox(modifier = Modifier.fillMaxSize().padding(innerPadding))
            state.error != null -> ErrorBox(
                message = state.error!!,
                onRetry = viewModel::refresh,
                modifier = Modifier.fillMaxSize().padding(innerPadding),
            )
            state.challenges.isEmpty() -> EmptyState(
                title = "참여할 챌린지가 없어요",
                description = "직접 만들어서 친구와 함께 시작해 보세요.",
                actionLabel = "새 챌린지 만들기",
                onAction = onCreateChallenge,
                icon = Icons.Outlined.EventAvailable,
                modifier = Modifier.fillMaxSize().padding(innerPadding),
            )
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = merged,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(state.challenges, key = { it.id }) { c ->
                    ChallengeCard(challenge = c, onClick = { onChallengeClick(c.id) })
                }
            }
        }
    }
}
