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
        startDateTimeMillis: Long,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): Result<TrainingBookingResponse> {
        val utcString = toUtcString(Instant.ofEpochMilli(startDateTimeMillis).atZone(zoneId).toLocalDateTime(), zoneId)
        return repository.bookTraining(trainerId, utcString)
    }

    suspend operator fun invoke(
        trainerId: Int,
        startDateTime: LocalDateTime,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): Result<TrainingBookingResponse> {
        val utcString = toUtcString(startDateTime, zoneId)
        return repository.bookTraining(trainerId, utcString)
    }

    /**
     * Builds the UTC string like: 2025-08-17 13:00:00 UTC
     */
    private fun toUtcString(localDateTime: LocalDateTime, zoneId: ZoneId): String {
        val zoned = localDateTime.atZone(zoneId)
        val utcInstant = zoned.withZoneSameInstant(ZoneOffset.UTC).toInstant()
        val utcDateTime = utcInstant.atZone(ZoneOffset.UTC).toLocalDateTime()
        val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss 'UTC'")
        return utcDateTime.format(fmt)
    }
}