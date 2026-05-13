package com.example.habit_bridge_demo.ui.screens.participation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EventNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habit_bridge_demo.data.remote.dto.ParticipationDto
import com.example.habit_bridge_demo.ui.components.EmptyState
import com.example.habit_bridge_demo.ui.components.ErrorBox
import com.example.habit_bridge_demo.ui.components.LoadingBox
import com.example.habit_bridge_demo.ui.components.StatusBadge
import com.example.habit_bridge_demo.ui.components.statusBadgeFor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyParticipationsScreen(
    onOpenParticipation: (participationId: String) -> Unit,
    onOpenResult: (participationId: String) -> Unit,
    onBrowseChallenges: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: MyParticipationsViewModel = viewModel(factory = MyParticipationsViewModel.Factory),
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("내 참여") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
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
            state.participations.isEmpty() -> EmptyState(
                title = "참여 중인 챌린지가 없어요",
                description = "홈에서 챌린지에 참여해 보세요.",
                actionLabel = "챌린지 둘러보기",
                onAction = onBrowseChallenges,
                icon = Icons.Outlined.EventNote,
                modifier = Modifier.fillMaxSize().padding(innerPadding),
            )
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = merged,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(state.participations, key = { it.id }) { p ->
                    val title = p.challenge?.title
                        ?: state.challengeMap[p.challengeId]?.title
                        ?: "챌린지 #${p.challengeId.take(6)}"
                    ParticipationCard(
                        participation = p,
                        title = title,
                        onClick = {
                            if (p.status.uppercase().startsWith("COMPLETED"))
                                onOpenResult(p.id)
                            else
                                onOpenParticipation(p.id)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun ParticipationCard(
    participation: ParticipationDto,
    title: String,
    onClick: () -> Unit,
) {
    val (label, kind) = statusBadgeFor(participation.status)
    val summary = participation.verificationSummary
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusBadge(text = label, kind = kind)
            }
            Text(title, style = MaterialTheme.typography.titleMedium)
            if (summary != null && summary.totalSlots > 0) {
                val progress = (summary.completedSlots.toFloat() / summary.totalSlots.toFloat()).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = "${summary.completedSlots} / ${summary.totalSlots} 인증 완료",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
