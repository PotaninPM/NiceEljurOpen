package com.team.common.functions

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatDateTime(input: String): Pair<String, String> {
    val parser = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US)
    val dateTime = LocalDateTime.parse(input, parser)

    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM", Locale.ENGLISH)
    val formattedDate = dateTime.format(dateFormatter)

    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
    val formattedTime = dateTime.format(timeFormatter)

    return formattedDate to formattedTime
}