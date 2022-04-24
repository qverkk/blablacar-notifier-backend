package com.example.serializers

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import org.litote.kmongo.serialization.TemporalExtendedJsonSerializer

object LocalDateSerializer : TemporalExtendedJsonSerializer<LocalDate>() {
    override fun epochMillis(temporal: LocalDate): Long {
        return temporal.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
    }

    override fun instantiate(date: Long): LocalDate {
        return kotlinx.datetime.Instant.fromEpochMilliseconds(date).toLocalDateTime(TimeZone.UTC).date
    }
}
