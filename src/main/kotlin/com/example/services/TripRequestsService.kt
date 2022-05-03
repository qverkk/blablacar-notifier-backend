package com.example.services

import com.example.models.db.TripRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.litote.kmongo.coroutine.CoroutineFindPublisher
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo

class TripRequestsService {
    private val client = KMongo.createClient("mongodb://qverkk:qverkkpassword@localhost:27017").coroutine
    private val database = client.getDatabase("blablacar")
    private val col = database.getCollection<TripRequest>()

    suspend fun addTripRequestAsync(
        newTrip: TripRequest
    ) = coroutineScope {
        async {
            val existingTripRequest = col.find(
                TripRequest::fromCity eq newTrip.fromCity,
                TripRequest::toCity eq newTrip.toCity,
                TripRequest::startDate eq newTrip.startDate,
                TripRequest::endDate eq newTrip.endDate
            )

            if (existingTripRequest.first() == null) {
                col.insertOne(newTrip.apply {
                    this.endDate = this.endDate ?: startDate
                })
            }
        }
    }

    fun getAllTripRequests(userId: String): CoroutineFindPublisher<TripRequest> {
        return col.find(TripRequest::userId eq userId)
    }
}
