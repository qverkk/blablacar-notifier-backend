package com.example.routing

import com.example.models.db.TripRequest
import com.example.services.KratosService
import com.example.services.TripFoundService
import com.example.services.TripRequestsService
import com.example.services.getAuthenticatedUser
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*


fun Route.tripDetails(
    tripFoundService: TripFoundService,
    tripRequestsService: TripRequestsService,
    kratosService: KratosService
) {
    route("/trip-details") {
        post {
            val newTrip = call.receive<TripRequest>()
            val authenticatedUser = call.getAuthenticatedUser(kratosService)

            newTrip.userId = authenticatedUser.id
            tripRequestsService.addTripRequestAsync(newTrip)
            call.respond(HttpStatusCode.Created)
        }
        get {
            val authenticatedUser = call.getAuthenticatedUser(kratosService)

            val allTripRequests = tripRequestsService.getUserTripRequests(authenticatedUser.id)
            call.respond(allTripRequests.toList())
        }
        delete("{id}") {
            val id = call.parameters.getOrFail("id")
            val authenticatedUser = call.getAuthenticatedUser(kratosService)

            tripRequestsService.deleteTripDetails(id, authenticatedUser.id)
            call.respond(HttpStatusCode.OK)
        }
        get("{id}/found-trips/count") {
            val tripRequestId = call.parameters.getOrFail("id")
            val authenticatedUser = call.getAuthenticatedUser(kratosService)

            val result = tripFoundService.findTripsForUser(tripRequestId, authenticatedUser.id)
            call.respond(result.toList().count())
        }
        get("{id}/found-trips") {
            val tripRequestId = call.parameters.getOrFail("id")
            val authenticatedUser = call.getAuthenticatedUser(kratosService)

            val result = tripFoundService.findTripsForUser(tripRequestId, authenticatedUser.id)
            call.respond(result.toList())
        }
    }
}