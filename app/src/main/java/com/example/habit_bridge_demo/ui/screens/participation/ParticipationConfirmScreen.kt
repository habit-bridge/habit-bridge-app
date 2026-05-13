package com.example.habit_bridge_demo.ui.screens.participation

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habit_bridge_demo.ui.components.ErrorBox
import com.example.habit_bridge_demo.ui.components.LoadingBox
import com.example.habit_bridge_demo.ui.components.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipationConfirmScreen(
    onBack: () -> Unit,
    onSigningStarted: (participationId: String) -> Unit,
    viewModel: ParticipationConfirmViewModel = viewModel(factory = ParticipationConfirmViewModel.Factory),
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state.prepareResult, state.participationId) {
        val url = state.prepareResult?.xumm?.next?.always
        val pid = state.participationId
        if (url != null && pid != null) {
            runCatching {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
            onSigningStarted(pid)
        } else if (url == null && pid != null) {
            // No Xaman URL — proceed to pending screen anyway
            onSigningStarted(pid)
        }
    }

    Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { Text("참여 확인") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "뒤로")
                    }
                },
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                PrimaryButton(
                    text = "Xaman으로 서명하기",
                    onClick = viewModel::startSigning,
                    enabled = state.acknowledged,
                    loading = state.signing,
                    modifier = Modifier.padding(16.dp),
                )
            }
        },
    ) { padding ->
        when {
            state.loading -> LoadingBox(modifier = Modifier.fillMaxSize().padding(padding))
            state.error != null && state.challenge == null -> ErrorBox(
                message = state.error!!,
                onRetry = viewModel::load,
                modifier = Modifier.fillMaxSize().padding(padding),
            )
            else -> {
                val c = state.challenge!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(1.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("참여 챌린지", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(c.title, style = MaterialTheme.typography.titleMedium)
                        }
                    }

                    // Deposit emphasis box
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("보증금", style = MaterialTheme.typography.labelMedium)
                            Text(
                                "${c.depositXrp} XRP",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                "위 금액이 XRPL Escrow로 예치됩니다.",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }

                    Text("결과에 따른 기부", style = MaterialTheme.typography.titleSmall)
                    BulletRow("성공 시", "당신의 이름으로 기부됩니다.")
                    BulletRow("실패 시", "서비스 이름으로 기부됩니다.")

                    Spacer(Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Checkbox(
                            checked = state.acknowledged,
                            onCheckedChange = viewModel::setAcknowledged,
                        )
                        Text("위 내용을 확인했습니다.", style = MaterialTheme.typography.bodyMedium)
                    }

                    if (state.error != null) {
                        Text(
                            text = state.error!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    Spacer(Modifier.height(72.dp))
                }
            }
        }
    }
}

@Composable
private fun BulletRow(label: String, message: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text("• ", style = MaterialTheme.typography.bodyMedium)
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(message, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
