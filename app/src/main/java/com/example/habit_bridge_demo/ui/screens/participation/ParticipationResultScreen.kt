package com.example.habit_bridge_demo.ui.screens.participation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habit_bridge_demo.ui.components.BadgeKind
import com.example.habit_bridge_demo.ui.components.ErrorBox
import com.example.habit_bridge_demo.ui.components.LoadingBox
import com.example.habit_bridge_demo.ui.components.StatusBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipationResultScreen(
    onBack: () -> Unit,
    onOpenChallenge: (challengeId: String) -> Unit,
    viewModel: ParticipationResultViewModel = viewModel(factory = ParticipationResultViewModel.Factory),
) {
    val state by viewModel.state.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("결과") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "뒤로")
                    }
                },
            )
        },
    ) { padding ->
        when {
            state.loading -> LoadingBox(modifier = Modifier.fillMaxSize().padding(padding))
            state.error != null -> ErrorBox(
                message = state.error!!,
                onRetry = viewModel::load,
                modifier = Modifier.fillMaxSize().padding(padding),
            )
            state.participation != null -> {
                val p = state.participation!!
                val success = p.status.uppercase() == "COMPLETED_SUCCESS"
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(Modifier.height(8.dp))
                    StatusBadge(
                        text = if (success) "성공" else "실패",
                        kind = if (success) BadgeKind.Success else BadgeKind.Fail,
                    )
                    Text(
                        text = state.challenge?.title ?: "챌린지",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                    )

                    // Verification summary card
                    val sum = p.verificationSummary
                    if (sum != null && sum.totalSlots > 0) {
                        val progress = (sum.completedSlots.toFloat() / sum.totalSlots.toFloat()).coerceIn(0f, 1f)
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("인증 요약", style = MaterialTheme.typography.titleSmall)
                                LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
                                Text("${sum.completedSlots} / ${sum.totalSlots} 인증 완료", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }

                    // Donation card
                    val donation = state.donation
                    val attribution = donation?.attribution?.uppercase()
                    val attributedName = when (attribution) {
                        "USER" -> donation?.attributedDisplayName ?: "사용자"
                        "SERVICE" -> donation?.attributedDisplayName ?: "Habit Bridge"
                        else -> if (success) "사용자" else "Habit Bridge"
                    }
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("기부 귀속", style = MaterialTheme.typography.labelMedium)
                            Text(
                                text = "$attributedName 이름으로 기부되었어요",
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                text = "${donation?.amountXrp ?: state.challenge?.depositXrp ?: "-"} XRP",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }

                    state.challenge?.let { c ->
                        TextButton(onClick = { onOpenChallenge(c.id) }) {
                            Text("원본 챌린지 보기")
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}
