package com.example.serializers

import kotlinx.datetime.*
import org.litote.kmongo.serialization.TemporalExtendedJsonSerializer

object LocalDateSerializer : TemporalExtendedJsonSerializer<LocalDate>() {
    override fun epochMillis(temporal: LocalDate): Long {
        return temporal.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
    }

    override fun instantiate(date: Long): LocalDate {
        return Instant.fromEpochMilliseconds(date).toLocalDateTime(TimeZone.UTC).date
    }
}
