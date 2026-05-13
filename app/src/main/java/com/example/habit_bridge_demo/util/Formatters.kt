package com.example.habit_bridge_demo.util

import com.example.habit_bridge_demo.data.remote.dto.VerificationFrequencyDto
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val displayDateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy.MM.dd")

fun formatDate(raw: String?): String {
    if (raw.isNullOrBlank()) return "-"
    // Try LocalDate (yyyy-MM-dd) first, then ISO instant
    return try {
        LocalDate.parse(raw).format(displayDateFormatter)
    } catch (e: DateTimeParseException) {
        try {
            Instant.parse(raw).atZone(ZoneId.systemDefault()).toLocalDate().format(displayDateFormatter)
        } catch (e2: DateTimeParseException) {
            raw
        }
    }
}

fun formatDateRange(start: String?, end: String?): String {
    return "${formatDate(start)} ~ ${formatDate(end)}"
}

fun formatFrequency(freq: VerificationFrequencyDto?): String {
    if (freq == null) return "-"
    return when (freq.type.uppercase()) {
        "DAILY" -> "매일 인증"
        "WEEKLY" -> "주 ${freq.timesPerWeek ?: 1}회 인증"
        else -> freq.type
    }
}
