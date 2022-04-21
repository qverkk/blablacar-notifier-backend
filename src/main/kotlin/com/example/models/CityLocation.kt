package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class CityLocation(
    val latitude: String,
    val longitude: String
)