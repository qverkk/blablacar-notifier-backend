package com.example.services

import com.example.models.db.TripRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.litote.kmongo.coroutine.CoroutineFindPublisher
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import java.time.LocalDate

class TripRequestsService {
    private val client = KMongo.createClient("mongodb://mongo:mongopassword@localhost:27017").coroutine
    private val database = client.getDatabase("blablacar")
    private val col = database.getCollection<TripRequest>()

    suspend fun addTripRequest(
        fromCity: String,
        toCity: String,
        startDate: LocalDate,
        endDate: LocalDate
    ) = coroutineScope {
        async {
            col.insertOne(TripRequest(fromCity, toCity, startDate, endDate))
        }
    }.await()

    fun getAllTripRequests(): CoroutineFindPublisher<TripRequest> {
        return col.find()
    }
}
