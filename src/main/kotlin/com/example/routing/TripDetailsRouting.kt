package com.example.routing

import com.example.models.NewTripDetails
import com.example.services.TripRequestsService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDate

fun Route.tripDetails(service: TripRequestsService) {
    route("/trip-details") {
        post {
            val newTrip = call.receive<NewTripDetails>()
            service.addTripRequest(
                newTrip.fromCity,
                newTrip.toCity,
                LocalDate.parse(newTrip.startDate),
                newTrip.endDate?.let {
                    LocalDate.parse(it)
                } ?: LocalDate.parse(newTrip.startDate)
            )
            call.respond(HttpStatusCode.Created)
        }
        get {
            call.respond(service.getAllTripRequests().toList())
        }
    }
}