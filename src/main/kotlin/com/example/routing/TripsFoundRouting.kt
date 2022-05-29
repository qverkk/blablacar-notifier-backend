package com.example.routing

import com.example.services.KratosService
import com.example.services.TripFoundService
import com.example.services.getAuthenticatedUser
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*


fun Route.tripsFound(service: TripFoundService, kratosService: KratosService) {
    route("/trips-found") {
        get {
            val authenticatedUser = call.getAuthenticatedUser(kratosService)

            val allTripRequests = service.getAllUsersTripsFound(authenticatedUser.id)
            call.respond(allTripRequests.toList())
        }
        patch("{id}/notify") {
            val authenticatedUser = call.getAuthenticatedUser(kratosService)
            val tripFoundId = call.parameters.getOrFail("id")

            service.notifyAgain(tripFoundId, authenticatedUser.id)
            call.respond(HttpStatusCode.OK)
        }
    }
}

