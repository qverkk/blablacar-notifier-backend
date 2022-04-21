package com.example.models.db

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class TripRequest(
    val fromCity: String,
    val toCity: String,
    @Contextual val startDate: LocalDate,
    @Contextual val endDate: LocalDate
)
