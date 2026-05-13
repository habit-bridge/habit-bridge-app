package com.example.habit_bridge_demo.ui.screens.participation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipationPendingScreen(
    onActive: (participationId: String) -> Unit,
    onBackground: () -> Unit,
    viewModel: ParticipationPendingViewModel = viewModel(factory = ParticipationPendingViewModel.Factory),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.confirmed) {
        if (state.confirmed) onActive(viewModel.participationId)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("서명 확인 중") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator(modifier = Modifier.size(56.dp))
            Spacer(Modifier.height(20.dp))
            Text(
                text = "서명을 확인하고 있어요",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Xaman에서 서명을 마치면 자동으로 진행됩니다. 최대 1분 정도 걸릴 수 있어요.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(16.dp))
            StepIndicator(currentStatus = state.status)
            Spacer(Modifier.height(24.dp))
            if (state.error != null) {
                Text(
                    text = state.error!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            TextButton(onClick = onBackground) {
                Text("백그라운드에서 계속 진행")
            }
        }
    }
}

@Composable
private fun StepIndicator(currentStatus: String) {
    val s = currentStatus.uppercase()
    val stepIndex = when (s) {
        "DEPOSIT_SUBMITTED" -> 1
        "ACTIVE" -> 2
        else -> 0
    }
    val labels = listOf("서명 수신", "트랜잭션 확인", "참여 활성화")
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        labels.forEachIndexed { i, label ->
            val active = i <= stepIndex
            Text(
                text = (if (active) "✓ " else "○ ") + label,
                style = MaterialTheme.typography.bodyMedium,
                color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
