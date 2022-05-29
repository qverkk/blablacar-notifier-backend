package com.example.routing

import com.example.models.db.TripRequest
import com.example.services.KratosService
import com.example.services.TripFoundService
import com.example.services.TripRequestsService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

private val kratosSessionHeaderName = "kratos-session"

fun Route.tripDetails(
    tripFoundService: TripFoundService,
    tripRequestsService: TripRequestsService,
    kratosService: KratosService
) {
    route("/trip-details") {
        post {
            val newTrip = call.receive<TripRequest>()
            val session = call.request.headers[kratosSessionHeaderName]!!
            val authenticatedUser = kratosService.authenticateUser(session)

            newTrip.userId = authenticatedUser.id
            tripRequestsService.addTripRequestAsync(newTrip)
            call.respond(HttpStatusCode.Created)
        }
        get {
            val session = call.request.headers[kratosSessionHeaderName]!!
            val authenticatedUser = kratosService.authenticateUser(session)

            val allTripRequests = tripRequestsService.getUserTripRequests(authenticatedUser.id)
            call.respond(allTripRequests.toList())
        }
        delete("{id}") {
            val id = call.parameters.getOrFail("id")
            val session = call.request.headers[kratosSessionHeaderName]!!
            val authenticatedUser = kratosService.authenticateUser(session)

            tripRequestsService.deleteTripDetails(id, authenticatedUser.id)
            call.respond(HttpStatusCode.OK)
        }
        get("{id}/found-trips/count") {
            val session = call.request.headers[kratosSessionHeaderName]!!
            val tripRequestId = call.parameters.getOrFail("id")
            val authenticatedUser = kratosService.authenticateUser(session)

            val result = tripFoundService.findTripsForUser(tripRequestId, authenticatedUser.id)
            call.respond(result.toList().count())
        }
        get("{id}/found-trips") {
            val session = call.request.headers[kratosSessionHeaderName]!!
            val tripRequestId = call.parameters.getOrFail("id")
            val authenticatedUser = kratosService.authenticateUser(session)

            val result = tripFoundService.findTripsForUser(tripRequestId, authenticatedUser.id)
            call.respond(result.toList())
        }
    }
}