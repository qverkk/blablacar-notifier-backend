package com.example.services

import com.example.models.db.TripRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.toKotlinLocalDate
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.CoroutineFindPublisher
import org.litote.kmongo.eq
import org.litote.kmongo.gte
import java.time.LocalDate

class TripRequestsService(
    database: CoroutineDatabase
) {
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

    fun getUserTripRequests(userId: String): CoroutineFindPublisher<TripRequest> {
        return col.find(TripRequest::userId eq userId)
    }

    fun getActiveTripRequests(): CoroutineFindPublisher<TripRequest> {
        return col.find(TripRequest::startDate gte LocalDate.now().toKotlinLocalDate())
    }

    suspend fun deleteTripDetails(id: String, userId: String) {
        col.deleteOne("{_id:ObjectId('$id'), userId: '$userId'}")
    }
}
