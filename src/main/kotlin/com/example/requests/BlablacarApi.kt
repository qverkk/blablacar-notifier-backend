package com.example.requests

import arrow.core.Either
import com.example.models.CityLocation
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class BlablacarApi {
    companion object {
        private const val BLABLACAR_API_URL = "https://edge.blablacar.pl"
        private const val BLABLACAR_URL = "https://www.blablacar.pl"
        private var BLABLACAR_BEARER_TOKEN_VALUE = ""
        private var BLABLACAR_DATADOME = ""
    }

    private val client = HttpClient(CIO)

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    suspend fun getRideSeats(
        tripId: String,
        tripSource: String
    ): Either<HttpStatusCode, RideResponse> {
        val response = retryOnceOnError {
            client.get("$BLABLACAR_API_URL/ride") {
                parameter("id", tripId)
                parameter("source", tripSource)
                parameter("requested_seats", "1")
                headers {
                    requiredBasicBlablacarHeaders(apiVersionId = "5")
                    header(HttpHeaders.Cookie, "datadome=$BLABLACAR_DATADOME")
                }
            }
        }

        return response.map { json.decodeFromString(it.bodyAsText()) }
    }

    suspend fun getCarpools(
        fromCity: String,
        toCity: String,
        date: LocalDate
    ): TripsResponse? {
        return getCarpools(
            getLocationPositionById(
                getLocationId(fromCity).orNull() ?: return null
            ).orNull() ?: return null,
            getLocationPositionById(
                getLocationId(toCity).orNull() ?: return null
            ).orNull() ?: return null,
            date
        ).orNull()
    }

    suspend fun getCarpools(
        fromCityLocation: CityLocation,
        toCityLocation: CityLocation,
        date: LocalDate?
    ): Either<HttpStatusCode, TripsResponse> {
        val response = retryOnceOnError {
            client.get("$BLABLACAR_API_URL/trip/search") {
                parameter("from_coordinates", "${fromCityLocation.latitude},${fromCityLocation.longitude}")
                parameter("to_coordinates", "${toCityLocation.latitude},${toCityLocation.longitude}")
                date?.let {
                    parameter("departure_date", it.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                }
                parameter("requested_seats", "1")
                parameter("passenger_gender", "UNKNOWN")
                parameter("search_uuid", "${UUID.randomUUID()}")
                headers {
                    requiredBasicBlablacarHeaders()
                    header(HttpHeaders.Cookie, "datadome=$BLABLACAR_DATADOME")
                }
            }
        }

        return response.map { json.decodeFromString(it.bodyAsText()) }
    }

    suspend fun getLocationId(city: String): Either<HttpStatusCode, String> {
        val response = retryOnceOnError {
            client.get("$BLABLACAR_API_URL/location/suggestions") {
                parameter("query", city)
                headers {
                    requiredBasicBlablacarHeaders()
                }
            }
        }

        return response.map {
            val jsonArray = json.decodeFromString<JsonArray>(it.bodyAsText())
            (jsonArray.first().jsonObject["id"] as JsonPrimitive).content
        }
    }


    suspend fun getLocationPositionById(locationId: String): Either<HttpStatusCode, CityLocation> {
        val response = retryOnceOnError {
            client.get("$BLABLACAR_API_URL/location/details") {
                parameter("id", locationId)
                headers {
                    requiredBasicBlablacarHeaders()
                }
            }
        }

        return response.map {
            val jsonObject = json.decodeFromString<JsonObject>(it.bodyAsText())
            CityLocation(jsonObject["latitude"]!!.toString(), jsonObject["longitude"]!!.toString())
        }
    }

    private suspend fun retryOnceOnError(
        retried: Boolean = false,
        requestExecution: suspend () -> HttpResponse
    ): Either<HttpStatusCode, HttpResponse> {
        if (BLABLACAR_BEARER_TOKEN_VALUE.isEmpty() || BLABLACAR_DATADOME.isEmpty()) {
            fetchNewCredentials()
        }
        return requestExecution().let {
            if (it.status != HttpStatusCode.OK) {
                if (retried) return@let Either.Left(HttpStatusCode.Forbidden)
                fetchNewCredentials()
                return@let retryOnceOnError(true, requestExecution)
            }
            Either.Right(it)
        }
    }

    private suspend fun fetchNewCredentials() {
        val cookieClient = HttpClient(CIO) {
            install(HttpCookies)
        }
        val result = cookieClient.get(BLABLACAR_URL) {
            headers {
                requiredBasicBlablacarHeaders()
            }
        }

        if (result.status == HttpStatusCode.OK) {
            BLABLACAR_BEARER_TOKEN_VALUE = cookieClient
                .cookies(BLABLACAR_URL)
                .firstOrNull { it.name == "app_token" }!!
                .value
            BLABLACAR_DATADOME = cookieClient
                .cookies(BLABLACAR_URL)
                .firstOrNull { it.name == "datadome" }!!
                .value
        }
    }

    private fun HttpRequestBuilder.requiredBasicBlablacarHeaders(apiVersionId: String = "6") {
        header(HttpHeaders.AcceptEncoding, "gzip, deflate, br")
        header(HttpHeaders.UserAgent, "Mozilla/5.0 (X11; Linux x86_64; rv:99.0) Gecko/20100101 Firefox/99.0")
        header(HttpHeaders.Authorization, "Bearer $BLABLACAR_BEARER_TOKEN_VALUE")
        header("x-locale", "pl_PL")
        header("x-visitor-id", "${UUID.randomUUID()}")
        header("x-currency", "PLN")
        header("x-client", "SPA|1.0.0")
        header("X-Blablacar-Accept-Endpoint-Version", apiVersionId)
    }
}
