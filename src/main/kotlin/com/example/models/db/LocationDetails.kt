package com.example.models.db

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id

@Serializable
data class LocationDetails(
    @SerialName("_id")
    @Contextual
    @BsonId
    val id: Id<LocationDetails>,
    val cityName: String,
    val cityLatitude: String,
    val cityLongitude: String
)
