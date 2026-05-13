package com.example.habit_bridge_demo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.habit_bridge_demo.ui.theme.StatusFail
import com.example.habit_bridge_demo.ui.theme.StatusInfo
import com.example.habit_bridge_demo.ui.theme.StatusNeutral
import com.example.habit_bridge_demo.ui.theme.StatusSuccess
import com.example.habit_bridge_demo.ui.theme.StatusWarning

enum class BadgeKind { Success, Fail, Warning, Info, Neutral }

@Composable
fun StatusBadge(
    text: String,
    kind: BadgeKind = BadgeKind.Neutral,
    modifier: Modifier = Modifier,
) {
    val color = when (kind) {
        BadgeKind.Success -> StatusSuccess
        BadgeKind.Fail -> StatusFail
        BadgeKind.Warning -> StatusWarning
        BadgeKind.Info -> StatusInfo
        BadgeKind.Neutral -> StatusNeutral
    }
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = Color.White,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(color)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}

/**
 * Maps a backend status string to a user-facing label + badge color.
 */
fun statusBadgeFor(status: String): Pair<String, BadgeKind> = when (status.uppercase()) {
    "SCHEDULED" -> "시작 예정" to BadgeKind.Info
    "ACTIVE" -> "진행 중" to BadgeKind.Info
    "ENDED" -> "종료" to BadgeKind.Neutral
    "PENDING_DEPOSIT" -> "보증금 대기" to BadgeKind.Warning
    "DEPOSIT_SUBMITTED" -> "서명 확인 중" to BadgeKind.Warning
    "COMPLETED_SUCCESS" -> "성공" to BadgeKind.Success
    "COMPLETED_FAIL" -> "실패" to BadgeKind.Fail
    "CANCELLED" -> "취소됨" to BadgeKind.Neutral
    else -> status to BadgeKind.Neutral
}
