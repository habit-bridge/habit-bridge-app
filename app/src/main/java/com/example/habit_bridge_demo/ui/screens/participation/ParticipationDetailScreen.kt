package com.example.habit_bridge_demo.ui.screens.participation

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habit_bridge_demo.data.remote.dto.VerificationSlotDto
import com.example.habit_bridge_demo.ui.components.BadgeKind
import com.example.habit_bridge_demo.ui.components.ErrorBox
import com.example.habit_bridge_demo.ui.components.LoadingBox
import com.example.habit_bridge_demo.ui.components.PrimaryButton
import com.example.habit_bridge_demo.ui.components.StatusBadge
import com.example.habit_bridge_demo.ui.components.statusBadgeFor
import com.example.habit_bridge_demo.util.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipationDetailScreen(
    onBack: () -> Unit,
    onUpload: (slotIndex: Int) -> Unit,
    onResult: () -> Unit,
    viewModel: ParticipationDetailViewModel = viewModel(factory = ParticipationDetailViewModel.Factory),
) {
    val state by viewModel.state.collectAsState()
    val openSlot = state.slots.firstOrNull { it.status.uppercase() == "OPEN" }
    val pStatus = state.participation?.status?.uppercase()
    val finished = pStatus == "COMPLETED_SUCCESS" || pStatus == "COMPLETED_FAIL"

    Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { Text(state.challenge?.title ?: "참여 상세") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "뒤로")
                    }
                },
            )
        },
        bottomBar = {
            if (state.participation != null && !finished) {
                Surface(shadowElevation = 8.dp) {
                    val label = when {
                        openSlot != null -> "이번 주기 인증하기"
                        state.slots.any { it.status.uppercase() == "SUBMITTED" } -> "이번 주기 인증 완료"
                        else -> "인증 마감"
                    }
                    PrimaryButton(
                        text = label,
                        enabled = openSlot != null,
                        onClick = { openSlot?.let { onUpload(it.slotIndex) } },
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
            state.participation != null -> {
                val p = state.participation!!
                val summary = p.verificationSummary
                val (label, kind) = statusBadgeFor(p.status)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatusBadge(text = label, kind = kind)
                        if (state.challenge != null) {
                            Text(
                                text = "${state.challenge!!.depositXrp} XRP",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }

                    // Progress
                    if (summary != null && summary.totalSlots > 0) {
                        val progress = (summary.completedSlots.toFloat() / summary.totalSlots.toFloat()).coerceIn(0f, 1f)
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("진행률", style = MaterialTheme.typography.titleSmall)
                                LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
                                Text(
                                    text = "${summary.completedSlots} / ${summary.totalSlots} 인증 완료",
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }
                    }

                    if (finished) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.fillMaxWidth().clickable(onClick = onResult),
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text("결과 보러 가기", style = MaterialTheme.typography.titleSmall)
                                Text(
                                    "정산 결과와 기부 귀속을 확인할 수 있어요.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }

                    Text("인증 슬롯", style = MaterialTheme.typography.titleMedium)
                    if (state.slots.isEmpty()) {
                        Text(
                            "참여가 시작되면 인증 슬롯이 표시됩니다.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    } else {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column {
                                state.slots.forEachIndexed { idx, slot ->
                                    SlotRow(
                                        slot = slot,
                                        onClick = if (slot.status.uppercase() == "OPEN")
                                            { { onUpload(slot.slotIndex) } } else null,
                                    )
                                    if (idx < state.slots.lastIndex) HorizontalDivider()
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(72.dp))
                }
            }
        }
    }
}

@Composable
private fun SlotRow(slot: VerificationSlotDto, onClick: (() -> Unit)?) {
    val (label, kind) = when (slot.status.uppercase()) {
        "SUBMITTED" -> "완료" to BadgeKind.Success
        "MISSED" -> "놓침" to BadgeKind.Fail
        "OPEN" -> "대기" to BadgeKind.Info
        "PENDING_REVIEW" -> "검토 중" to BadgeKind.Warning
        else -> slot.status to BadgeKind.Neutral
    }
    val rowModifier = if (onClick != null)
        Modifier.fillMaxWidth().clickable(onClick = onClick)
    else
        Modifier.fillMaxWidth()
    Row(
        modifier = rowModifier.padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text("슬롯 #${slot.slotIndex + 1}", style = MaterialTheme.typography.titleSmall)
            Text(
                text = "${formatDate(slot.windowStart)} ~ ${formatDate(slot.windowEnd)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        StatusBadge(text = label, kind = kind)
    }
}
