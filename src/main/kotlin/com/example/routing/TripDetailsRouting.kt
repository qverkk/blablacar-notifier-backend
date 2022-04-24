package com.example.routing

import com.example.models.db.TripRequest
import com.example.services.TripRequestsService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.tripDetails(service: TripRequestsService) {
    route("/trip-details") {
        post {
            val newTrip = call.receive<TripRequest>()
            service.addTripRequestAsync(newTrip)
            call.respond(HttpStatusCode.Created)
        }
        get {
            val allTripRequests = service.getAllTripRequests()
            call.respond(allTripRequests.toList())
        }
    }
}