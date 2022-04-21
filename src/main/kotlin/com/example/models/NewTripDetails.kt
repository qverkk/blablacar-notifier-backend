package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class NewTripDetails(
    val fromCity: String,
    val toCity: String,
    val startDate: String,
    val endDate: String? = null
)
