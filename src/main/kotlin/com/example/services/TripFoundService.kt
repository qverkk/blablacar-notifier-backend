package com.example.services

import com.example.models.db.TripFound
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.CoroutineFindPublisher
import org.litote.kmongo.eq

class TripFoundService(
    database: CoroutineDatabase
) {
    private val col = database.getCollection<TripFound>()

    suspend fun addTripFound(
        newTripFound: TripFound
    ) = coroutineScope {
        async {
            col.insertOne(newTripFound)
        }
    }

    suspend fun updateTripFound(
        tripFound: TripFound
    ) = coroutineScope {
        async {
            col.updateOne(TripFound::id eq tripFound.id, tripFound)
        }
    }

    fun getAllNotNotifiedFoundTrips() = col.find(TripFound::notified eq false)

    fun getAllNotifyFreeSeatsTrips() = col.find(TripFound::notifyFreeSeats eq true)

    fun getAllUsersTripsFound(userId: String) = col.find(TripFound::userId eq userId)

    suspend fun exists(tripId: String, userToken: String): Boolean {
        return col.findOne(TripFound::blablacarTripId eq tripId, TripFound::userRegisteredToken eq userToken) != null
    }

    suspend fun notifyAgain(tripFoundId: String, userId: String) {
        col.findOne("{_id:ObjectId('$tripFoundId'), userId: '$userId'}")?.apply {
            this.notifyFreeSeats = true
        }?.let {
            col.updateOne("{_id:ObjectId('$tripFoundId'), userId: '$userId'}", it)
        }
    }

    suspend fun notifyAllAgain(requestTripId: String, userId: String) {
        val foundTrips = col.find("{tripRequestId:'$requestTripId', userId: '$userId'}")
        foundTrips.toList().forEach {
            it.notifyFreeSeats = true
            col.updateOne(TripFound::id eq it.id, it)
        }
    }

    suspend fun updateTrips(trips: List<TripFound>) {
        trips.forEach {
            col.updateOne(TripFound::id eq it.id, it)
        }
    }

    fun findTripsForUser(tripRequestId: String, userId: String): CoroutineFindPublisher<TripFound> {
        return col.find("{tripRequestId: '$tripRequestId', userId: '$userId'}")
    }
}
