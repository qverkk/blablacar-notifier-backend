package com.example.requests

import kotlinx.serialization.Serializable

@Serializable
data class RideResponse(
    val seats: RideSeats
)

@Serializable
data class RideSeats(
    val remaining: Int,
    val total: Int
)
