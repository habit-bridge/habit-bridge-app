package com.example.habit_bridge_demo.ui.screens.challenge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habit_bridge_demo.data.remote.dto.ChallengeDto
import com.example.habit_bridge_demo.ui.components.ErrorBox
import com.example.habit_bridge_demo.ui.components.LoadingBox
import com.example.habit_bridge_demo.ui.components.PrimaryButton
import com.example.habit_bridge_demo.ui.components.StatusBadge
import com.example.habit_bridge_demo.ui.components.statusBadgeFor
import com.example.habit_bridge_demo.util.formatDate
import com.example.habit_bridge_demo.util.formatFrequency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeDetailScreen(
    onBack: () -> Unit,
    onParticipate: (challengeId: String) -> Unit,
    onOpenMyParticipation: (participationId: String) -> Unit,
    viewModel: ChallengeDetailViewModel = viewModel(factory = ChallengeDetailViewModel.Factory),
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.challenge?.title ?: "챌린지") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "뒤로")
                    }
                },
            )
        },
        bottomBar = {
            val c = state.challenge
            if (c != null) {
                Surface(shadowElevation = 8.dp) {
                    val ended = c.status.uppercase() == "ENDED"
                    val label: String
                    val enabled: Boolean
                    val onClick: () -> Unit
                    when {
                        ended -> {
                            label = "종료된 챌린지"; enabled = false; onClick = {}
                        }
                        state.alreadyParticipating -> {
                            label = "내 참여 보기"; enabled = true
                            onClick = { state.myParticipationId?.let(onOpenMyParticipation) }
                        }
                        else -> {
                            label = "참여하기"; enabled = true
                            onClick = { onParticipate(c.id) }
                        }
                    }
                    PrimaryButton(
                        text = label,
                        onClick = onClick,
                        enabled = enabled,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }
        },
    ) { padding ->
        when {
            state.loading -> LoadingBox(modifier = Modifier.fillMaxSize().padding(padding))
            state.error != null -> ErrorBox(
                message = state.error!!,
                onRetry = viewModel::load,
                modifier = Modifier.fillMaxSize().padding(padding),
            )
            state.challenge != null -> ChallengeDetailBody(
                challenge = state.challenge!!,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            )
        }
    }
}

@Composable
private fun ChallengeDetailBody(challenge: ChallengeDto, modifier: Modifier = Modifier) {
    val (label, kind) = statusBadgeFor(challenge.status)
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StatusBadge(text = label, kind = kind)
        Text(challenge.title, style = MaterialTheme.typography.headlineSmall)

        // Meta card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(1.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                MetaRow("기간", "${formatDate(challenge.startDate)} ~ ${formatDate(challenge.endsAt)} (${challenge.durationWeeks}주)")
                MetaRow("인증 주기", formatFrequency(challenge.verificationFrequency))
                MetaRow("보증금", "${challenge.depositXrp} XRP", emphasize = true)
                challenge.activeParticipantCount?.let {
                    MetaRow("현재 참여자", "${it}명")
                }
            }
        }

        SectionTitle("챌린지 소개")
        Text(challenge.description, style = MaterialTheme.typography.bodyMedium)

        SectionTitle("인증 방법")
        Text(challenge.verificationMethodDescription, style = MaterialTheme.typography.bodyMedium)

        // Deposit notice
        Spacer(Modifier.height(4.dp))
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
                    .copy(alpha = 0.4f),
            ),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("성공 시", style = MaterialTheme.typography.labelMedium)
                Text("당신의 이름으로 기부됩니다.", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(4.dp))
                Text("실패 시", style = MaterialTheme.typography.labelMedium)
                Text("서비스 이름으로 기부됩니다.", style = MaterialTheme.typography.bodyMedium)
            }
        }
        Spacer(Modifier.height(72.dp))
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 8.dp),
    )
}

@Composable
private fun MetaRow(label: String, value: String, emphasize: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = value,
            style = if (emphasize) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            color = if (emphasize) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        )
    }
}

