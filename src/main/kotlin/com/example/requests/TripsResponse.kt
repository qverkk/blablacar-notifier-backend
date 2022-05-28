package com.example.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TripsResponse(
    val trips: List<Trip>
)

@Serializable
data class DisabledSelection(
    val message: String
)

@Serializable
data class Waypoint(
    @SerialName("main_text")
    val city: String,
    @SerialName("departure_datetime")
    val departureTime: String
)

@Serializable
data class Trip(
    @SerialName("multimodal_id")
    val modalId: ModalId,
    @SerialName("price_details")
    val priceDetails: PriceDetails,
    val driver: Driver,
    val waypoints: List<Waypoint>,
    @SerialName("disabled_selection")
    val disabledSelection: DisabledSelection? = null
)

@Serializable
data class ModalId(
    val id: String,
    val source: String
)

@Serializable
data class Driver(
    val thumbnail: String,
    @SerialName("display_name")
    val displayName: String,
    val rating: DriverRating,
    @SerialName("verification_status")
    val verificationStatus: DriverVerificationStatus
)

@Serializable
data class DriverVerificationStatus(
    val label: String? = null,
    val code: String
)

@Serializable
data class DriverRating(
    val overall: Double,
    @SerialName("total_number")
    val totalNumber: Int
)

@Serializable
data class PriceDetails(
    val price: String
)
