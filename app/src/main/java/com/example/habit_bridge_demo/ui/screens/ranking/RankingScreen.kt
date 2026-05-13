package com.example.habit_bridge_demo.ui.screens.ranking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habit_bridge_demo.ui.components.EmptyState
import com.example.habit_bridge_demo.ui.components.ErrorBox
import com.example.habit_bridge_demo.ui.components.LoadingBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen(
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: RankingViewModel = viewModel(factory = RankingViewModel.Factory),
) {
    val state by viewModel.state.collectAsState()
    var tab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("랭킹") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            PrimaryTabRow(selectedTabIndex = tab) {
                Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Donation TOP 10") })
                Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Success TOP 10") })
            }
            when {
                state.loading -> LoadingBox(modifier = Modifier.fillMaxSize())
                state.error != null -> ErrorBox(
                    message = state.error!!,
                    onRetry = viewModel::refresh,
                    modifier = Modifier.fillMaxSize(),
                )
                else -> {
                    val items: List<Triple<Int, String, String>> = if (tab == 0) {
                        state.donation.map { Triple(it.rank, it.displayName ?: "이름 없음", "${it.totalDonationXrp} XRP") }
                    } else {
                        state.success.map { Triple(it.rank, it.displayName ?: "이름 없음", "${it.successCount}회") }
                    }
                    if (items.isEmpty()) {
                        EmptyState(
                            title = "아직 집계된 랭킹이 없어요",
                            modifier = Modifier.fillMaxSize(),
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                top = 8.dp,
                                bottom = contentPadding.calculateBottomPadding() + 16.dp,
                            ),
                        ) {
                            itemsIndexed(items, key = { _, it -> it.first }) { idx, t ->
                                RankRow(rank = t.first, name = t.second, valueText = t.third)
                                if (idx < items.lastIndex) HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RankRow(rank: Int, name: String, valueText: String) {
    val (bg, fg) = when (rank) {
        1 -> Color(0xFFFFD54F) to Color.Black
        2 -> Color(0xFFCFD8DC) to Color.Black
        3 -> Color(0xFFFFB07A) to Color.Black
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(bg),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = rank.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = fg,
                fontWeight = FontWeight.SemiBold,
            )
        }
        androidx.compose.foundation.layout.Spacer(Modifier.size(12.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = valueText,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

