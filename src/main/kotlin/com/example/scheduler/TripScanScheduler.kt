package com.example.scheduler

import com.example.models.db.TripFound
import com.example.notification.firebase.FirebaseNotification
import com.example.requests.BlablacarApi
import com.example.services.TripFoundService
import com.example.services.TripRequestsService
import kotlinx.coroutines.*
import kotlinx.datetime.toJavaLocalDate
import org.litote.kmongo.newId
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.seconds

class TripScanScheduler(
    private val tripRequestsService: TripRequestsService,
    private val tripFoundService: TripFoundService,
    private val blablacarApi: BlablacarApi,
    private val notifier: FirebaseNotification
) : CoroutineScope {
    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    fun cancel() {
        job.cancel()
    }

    fun schedule() = launch {
        while (true) {
            println("Scanning...")
            delay(10.seconds)
            findNewTrips()
            notifyAboutFoundRequests()
            notifyAboutAvailableSeats()
        }
    }

    private suspend fun notifyAboutAvailableSeats() {
        val updatedTripRides = tripFoundService.getAllNotifyFreeSeatsTrips().toList().map {
            blablacarApi.getRideSeats(it.blablacarTripId, it.blablacarSource).map { response ->
                it.maxSeats = response.seats.total
                it.remainingSeats = response.seats.remaining
            }
            return@map it
        }

        tripFoundService.updateTrips(updatedTripRides)

        updatedTripRides
            .filter { it.remainingSeats != 0 }
            .onEach {
                it.notifyFreeSeats = false
                tripFoundService.updateTripFound(it)
            }
            .groupingBy { it.userRegisteredToken }
            .eachCount()
            .forEach {
                notifier.send(
                    mapOf(
                        "numberOfNewTrips" to it.value.toString()
                    ),
                    it.key
                )
            }
    }

    private suspend fun notifyAboutFoundRequests() {
        val notNotifiedTrips = tripFoundService.getAllNotNotifiedFoundTrips().toList()
        notNotifiedTrips.forEach {
            it.notified = true
            tripFoundService.updateTripFound(it)
        }

        notNotifiedTrips.groupingBy { it.userRegisteredToken }.eachCount().forEach {
            notifier.send(
                mapOf(
                    "numberOfNewTrips" to it.value.toString()
                ),
                it.key
            )
        }
    }

    private suspend fun findNewTrips() {
        tripRequestsService.getActiveTripRequests().toList().forEach { request ->
            blablacarApi.getCarpools(
                request.fromCity,
                request.toCity,
                request.startDate.toJavaLocalDate()
            )?.trips?.forEach {
                if (!tripFoundService.exists(it.modalId.id, request.userRegistrationToken)) {
                    tripFoundService.addTripFound(
                        TripFound(
                            newId(),
                            request.id!!.toString(),
                            it.modalId.id,
                            it.modalId.source,
                            notified = false,
                            notifyFreeSeats = false,
                            it.priceDetails.price,
                            0,
                            1,
                            request.userRegistrationToken,
                            request.userId!!,
                            it.driver.rating.overall,
                            it.driver.displayName,
                            it.driver.rating.totalNumber,
                            it.driver.verificationStatus.code,
                            it.driver.verificationStatus.label,
                            it.waypoints[0].departureTime,
                            it.waypoints[1].departureTime
                        )
                    )
                }
            }
        }
    }
}
