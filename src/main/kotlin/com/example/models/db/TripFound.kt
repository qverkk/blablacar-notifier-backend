package com.example.models.db

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import org.litote.kmongo.newId

@Serializable
data class TripFound(
    @SerialName("_id")
    @Contextual
    @BsonId
    val id: Id<TripFound> = newId(),
    val tripRequestId: String,
    val blablacarTripId: String,
    val blablacarSource: String,
    var notified: Boolean,
    var notifyFreeSeats: Boolean,
    val price: String,
    var maxSeats: Int = 0,
    var remainingSeats: Int = 0,
    val userRegisteredToken: String,
    val userId: String,
    val driverRating: Double,
    val driverDisplayName: String,
    val driverRatingsCount: Int,
    val driverStatusCode: String,
    val driverStatusLabel: String?
)
