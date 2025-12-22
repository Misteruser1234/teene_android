package com.example.teene.home.domain.usecases

import com.example.teene.home.data.repositories.TrainingBookingsRepository
import com.example.teene.home.data.models.TrainingBookingResponse
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Creates a training booking using repository.
 * Formats start time to: "yyyy-MM-dd HH:mm:ss UTC"
 */
class BookTrainingUseCase(
    private val repository: TrainingBookingsRepository
) {
    suspend operator fun invoke(
        trainerId: Int,
        selectedDateMillis: Long,
        selectedTimeHHmm: String,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): Result<TrainingBookingResponse> {
        val utcString = toUtcString(selectedDateMillis, selectedTimeHHmm, zoneId)
        return repository.bookTraining(trainerId, utcString)
    }

    /**
     * Builds the UTC string like: 2025-08-17 13:00:00 UTC
     */
    private fun toUtcString(dateMillis: Long, timeHHmm: String, zoneId: ZoneId): String {
        val localDate = Instant.ofEpochMilli(dateMillis).atZone(zoneId).toLocalDate()
        val localTime = LocalTime.parse(timeHHmm.padStart(5, '0')) // expect HH:mm
        val localDateTime = LocalDateTime.of(localDate, localTime)
        val utcInstant = localDateTime.atZone(zoneId).withZoneSameInstant(ZoneOffset.UTC).toInstant()
        val utcDateTime = utcInstant.atZone(ZoneOffset.UTC).toLocalDateTime()
        val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss 'UTC'")
        return utcDateTime.format(fmt)
    }
}