package com.example

import com.example.requests.BlablacarApi
import com.example.routing.blablacar
import com.example.routing.tripDetails
import com.example.serializers.LocalDateSerializer
import com.example.services.TripRequestsService
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.conditionalheaders.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.litote.kmongo.id.serialization.IdKotlinXSerializationModule
import org.litote.kmongo.serialization.registerSerializer
import org.slf4j.event.Level

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        mainWithDependencies()
    }.start(wait = true)
}

fun Application.mainWithDependencies() {
    install(DefaultHeaders)

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(ConditionalHeaders)

    install(ContentNegotiation) {
        json(Json {
            serializersModule = IdKotlinXSerializationModule
            registerSerializer(LocalDateSerializer)
        })
    }

    routing {
        blablacar(BlablacarApi())
        tripDetails(TripRequestsService())
    }
}
