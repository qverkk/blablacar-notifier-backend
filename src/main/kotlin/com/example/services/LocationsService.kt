package com.example.services

import arrow.core.getOrHandle
import com.example.models.db.LocationDetails
import com.example.requests.BlablacarApi
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.newId

class LocationsService(
    private val blablacarApi: BlablacarApi,
    database: CoroutineDatabase
) {
    private val col = database.getCollection<LocationDetails>()

    suspend fun getLocationDetails(cityName: String): LocationDetails {
        val lowercaseCityName = cityName.lowercase().trim()
        val foundDetails = col.findOne(LocationDetails::cityName eq lowercaseCityName)
        if (foundDetails != null) {
            return foundDetails
        }

        val cityLocation = blablacarApi.getLocationPositionById(
            blablacarApi.getLocationId(lowercaseCityName).getOrHandle { throw RuntimeException() }
        ).getOrHandle { throw RuntimeException() }

        val result = LocationDetails(
            newId(),
            lowercaseCityName,
            cityLocation.latitude,
            cityLocation.longitude
        )
        col.insertOne(result)
        return result
    }

}
