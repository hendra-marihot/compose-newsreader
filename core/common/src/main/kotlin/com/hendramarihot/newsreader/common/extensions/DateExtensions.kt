package com.hendramarihot.newsreader.common.extensions

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val outputFormatter = DateTimeFormatter
    .ofPattern("MMM d, yyyy")
    .withZone(ZoneId.systemDefault())

fun String.toFormattedDate(): String = runCatching {
    val instant = Instant.parse(this)
    outputFormatter.format(instant)
}.getOrDefault(this)
