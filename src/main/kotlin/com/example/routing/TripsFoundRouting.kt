package com.example.routing

import com.example.services.KratosService
import com.example.services.TripFoundService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

private val kratosSessionHeaderName = "kratos-session"


fun Route.tripsFound(service: TripFoundService, kratosService: KratosService) {
    route("/trips-found") {
        get {
            val session = call.request.headers[kratosSessionHeaderName]!!
            val authenticatedUser = kratosService.authenticateUser(session)

            val allTripRequests = service.getAllUsersTripsFound(authenticatedUser.id)
            call.respond(allTripRequests.toList())
        }
        patch("{id}/notify") {
            val session = call.request.headers[kratosSessionHeaderName]!!
            val tripFoundId = call.parameters.getOrFail("id")
            val authenticatedUser = kratosService.authenticateUser(session)

            service.notifyAgain(tripFoundId, authenticatedUser.id)
            call.respond(HttpStatusCode.OK)
        }
    }
}

