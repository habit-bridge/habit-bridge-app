package com.example.habit_bridge_demo.ui.screens.challenge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habit_bridge_demo.ui.components.PrimaryButton
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateChallengeScreen(
    onClose: () -> Unit,
    onCreated: (challengeId: String) -> Unit,
    viewModel: CreateChallengeViewModel = viewModel(factory = CreateChallengeViewModel.Factory),
) {
    val state by viewModel.state.collectAsState()
    val endDate: LocalDate = state.startDate.plusWeeks(state.durationWeeks.toLong()).minusDays(1)

    LaunchedEffect(state.created) {
        state.created?.let { onCreated(it.id) }
    }

    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("새 챌린지") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Outlined.Close, contentDescription = "닫기")
                    }
                },
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                PrimaryButton(
                    text = "챌린지 만들기",
                    onClick = viewModel::submit,
                    loading = state.loading,
                    modifier = Modifier.padding(16.dp),
                )
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = state.title,
                onValueChange = viewModel::onTitle,
                label = { Text("제목") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.description,
                onValueChange = viewModel::onDescription,
                label = { Text("소개") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth(),
            )

            // Start date
            OutlinedTextField(
                value = state.startDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                onValueChange = {},
                label = { Text("시작일") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    TextButton(onClick = { showDatePicker = true }) { Text("변경") }
                },
            )

            // Duration weeks (NumberStepper)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text("기간(주)", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "${state.durationWeeks}주",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = { viewModel.onDurationWeeks(state.durationWeeks - 1) }) { Text("-") }
                    TextButton(onClick = { viewModel.onDurationWeeks(state.durationWeeks + 1) }) { Text("+") }
                }
            }

            // Frequency
            Text("인증 주기", style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = state.freqType == FreqType.DAILY,
                    onClick = { viewModel.onFreqType(FreqType.DAILY) },
                    label = { Text("매일") },
                )
                FilterChip(
                    selected = state.freqType == FreqType.WEEKLY,
                    onClick = { viewModel.onFreqType(FreqType.WEEKLY) },
                    label = { Text("주 N회") },
                )
            }
            if (state.freqType == FreqType.WEEKLY) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("주당 인증 횟수", style = MaterialTheme.typography.bodyMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = { viewModel.onTimesPerWeek(state.timesPerWeek - 1) }) { Text("-") }
                        Box(modifier = Modifier.size(width = 36.dp, height = 36.dp), contentAlignment = Alignment.Center) {
                            Text("${state.timesPerWeek}회", style = MaterialTheme.typography.titleMedium)
                        }
                        TextButton(onClick = { viewModel.onTimesPerWeek(state.timesPerWeek + 1) }) { Text("+") }
                    }
                }
            }

            OutlinedTextField(
                value = state.verificationMethodDescription,
                onValueChange = viewModel::onMethod,
                label = { Text("인증 방법 설명") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.depositXrp,
                onValueChange = viewModel::onDeposit,
                label = { Text("보증금 (XRP)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
            )

            // Summary
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("요약", style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = "${state.startDate} ~ $endDate (${state.durationWeeks}주)",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = "보증금 ${state.depositXrp} XRP",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            if (state.error != null) {
                Text(
                    text = state.error!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Spacer(Modifier.height(72.dp))
        }
    }

    if (showDatePicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.startDate.atStartOfDay()
                .toInstant(ZoneOffset.UTC).toEpochMilli(),
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis).atOffset(ZoneOffset.UTC).toLocalDate()
                        viewModel.onStartDate(date)
                    }
                    showDatePicker = false
                }) { Text("확인") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("취소") }
            },
        ) {
            DatePicker(state = pickerState)
        }
    }
}
