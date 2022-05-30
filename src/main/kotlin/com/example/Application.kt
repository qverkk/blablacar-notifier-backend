package com.example

import com.example.config.firebase.FirebaseAdmin
import com.example.notification.firebase.FirebaseNotification
import com.example.requests.BlablacarApi
import com.example.routing.tripDetails
import com.example.routing.tripsFound
import com.example.scheduler.TripScanScheduler
import com.example.serializers.LocalDateSerializer
import com.example.services.KratosService
import com.example.services.LocationsService
import com.example.services.TripFoundService
import com.example.services.TripRequestsService
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.conditionalheaders.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.id.serialization.IdKotlinXSerializationModule
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.serialization.registerSerializer
import org.slf4j.event.Level


private val client = KMongo.createClient("mongodb://mongo:mongopassword@localhost:27017").coroutine
private val database = client.getDatabase("blablacar")
private lateinit var tripScanScheduler: TripScanScheduler

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        mainWithDependencies()
    }.start(wait = true)
        .addShutdownHook { tripScanScheduler.cancel() }
}

fun Application.mainWithDependencies() {
    FirebaseAdmin.init()

    val kratosService = KratosService()

    install(DoubleReceive)

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

    val blablacarApi = BlablacarApi()
    val locationService = LocationsService(blablacarApi, database)
    val tripRequestsService = TripRequestsService(database, locationService)
    val tripFoundService = TripFoundService(database)
    tripScanScheduler = TripScanScheduler(tripRequestsService, tripFoundService, blablacarApi, FirebaseNotification())
    tripScanScheduler.schedule()

    routing {
        tripDetails(tripFoundService, tripRequestsService, kratosService)
        tripsFound(tripFoundService, kratosService)
    }
}
