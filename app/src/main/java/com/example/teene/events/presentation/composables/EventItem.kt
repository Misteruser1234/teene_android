package com.example.teene.events.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.teene.events.domain.models.Event

/**
 * Full-width Event item styled like Explore's SportItem: image across the whole row with overlayed texts.
 */
@Composable
fun EventItem(
    event: Event,
    onClick: () -> Unit = {}
) {
    val topLine = when {
        !event.city.isNullOrBlank() -> event.city
        !event.address.isNullOrBlank() -> event.address
        else -> ""
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(165.dp)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(event.imageUrl ?: "")
                .crossfade(true)
                .build(),
            contentDescription = "Event image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Overlay content: top tag + title + subtitle row (city and date)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            androidx.compose.foundation.layout.Column {
                // Title (event name)
                androidx.compose.material3.Text(
                    text = event.name.uppercase(),
                    fontSize = 32.sp,
                    color = Color.White,
                    lineHeight = 46.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Subtitle row: City • Date
                val dateText = buildDateText(event)
                if (!event.city.isNullOrBlank() || dateText.isNotBlank()) {
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(6.dp))
                    androidx.compose.foundation.layout.Row {
                        val parts = mutableListOf<String>()
                        if (!event.city.isNullOrBlank()) parts.add(event.city!!)
                        if (dateText.isNotBlank()) parts.add(dateText)
                        androidx.compose.material3.Text(
                            text = parts.joinToString(separator = "  •  "),
                            color = Color.White,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}


private fun buildDateText(event: com.example.teene.events.domain.models.Event): String {
    val fromRaw = event.dateFrom?.trim().orEmpty()
    val toRaw = event.dateTo?.trim().orEmpty()

    val outFmt = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val dateRegex = Regex("(\\d{4}-\\d{2}-\\d{2})")

    fun normalize(input: String): String {
        var s = input.trim()
        if (s.endsWith(" UTC", ignoreCase = true)) {
            // Replace literal UTC with Z (UTC designator) so ISO parsers can handle it
            s = s.removeSuffix(" UTC") + "Z"
        }
        return s
    }

    fun tryFormat(input: String): String {
        if (input.isBlank()) return ""
        val s = normalize(input)
        // 1) Pure LocalDate
        try {
            val ld = java.time.LocalDate.parse(s, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE)
            return ld.format(outFmt)
        } catch (_: Throwable) {}
        // 2) Instant (e.g., 2025-08-21T10:00:00Z or with millis)
        try {
            val inst = java.time.Instant.parse(s)
            return java.time.ZonedDateTime.ofInstant(inst, java.time.ZoneId.systemDefault()).toLocalDate().format(outFmt)
        } catch (_: Throwable) {}
        // 3) OffsetDateTime (e.g., +00:00 / +0100)
        try {
            val odt = java.time.OffsetDateTime.parse(s)
            return odt.toLocalDate().format(outFmt)
        } catch (_: Throwable) {}
        // 4) ZonedDateTime
        try {
            val zdt = java.time.ZonedDateTime.parse(s)
            return zdt.toLocalDate().format(outFmt)
        } catch (_: Throwable) {}
        // 5) LocalDateTime (assume system zone)
        try {
            val ldt = java.time.LocalDateTime.parse(s)
            return ldt.toLocalDate().format(outFmt)
        } catch (_: Throwable) {}
        // 6) Extract just the date portion if present (yyyy-MM-dd)
        val m = dateRegex.find(s)
        if (m != null) {
            return try {
                java.time.LocalDate.parse(m.groupValues[1]).format(outFmt)
            } catch (_: Throwable) {
                ""
            }
        }
        // Last resort: hide raw string to avoid showing long UTC; return empty so UI omits it
        return ""
    }

    val from = tryFormat(fromRaw)
    val to = tryFormat(toRaw)
    return when {
        from.isNotEmpty() && to.isNotEmpty() && from != to -> "$from - $to"
        from.isNotEmpty() -> from
        to.isNotEmpty() -> to
        else -> ""
    }
}
