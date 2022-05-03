package com.example.routing

import arrow.core.getOrHandle
import arrow.core.handleError
import com.example.requests.BlablacarApi
import com.example.services.KratosService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import java.time.LocalDate

fun Route.blablacar(blablacarApi: BlablacarApi, kratosService: KratosService) {
    get("/search") {
        val from = call.request.queryParameters.getOrFail("from")
        val to = call.request.queryParameters.getOrFail("to")
        val date = call.request.queryParameters["date"]
        val result = blablacarApi.getCarpools(
            blablacarApi.getLocationPositionById(
                blablacarApi.getLocationId(from).getOrHandle { return@get call.respond(it) }
            ).getOrHandle { return@get call.respond(it) },
            blablacarApi.getLocationPositionById(
                blablacarApi.getLocationId(to).getOrHandle { return@get call.respond(it) }
            ).getOrHandle { return@get call.respond(it) },
            date?.let {
                LocalDate.parse(it)
            }
        )

        result.map { call.respond(it) }
            .handleError { call.respond(it) }
    }
}
