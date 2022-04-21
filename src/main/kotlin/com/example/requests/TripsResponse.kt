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
    val displayName: String
)

@Serializable
data class PriceDetails(
    val price: String
)
