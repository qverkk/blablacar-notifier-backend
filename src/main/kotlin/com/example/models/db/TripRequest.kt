package com.example.models.db

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import org.litote.kmongo.newId

@Serializable
data class TripRequest(
    @SerialName("_id")
    @Contextual
    @BsonId
    val id: Id<TripRequest>? = newId(),
    val fromCity: String,
    var fromCityLocationDetails: LocationDetails? = null,
    val toCity: String,
    var toCityLocationDetails: LocationDetails? = null,
    @Contextual
    val startDate: LocalDate,
    @Contextual
    var endDate: LocalDate? = null,
    var userId: String? = null,
    val userRegistrationToken: String
)
