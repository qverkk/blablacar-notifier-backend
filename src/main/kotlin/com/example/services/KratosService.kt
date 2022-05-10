package com.example.services

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

class KratosService {
    private val kratosUrl = "http://192.168.0.199:4433"
    private val client = HttpClient(CIO)
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    suspend fun authenticateUser(sessionToken: String): AuthorizedUser {
        val result = client.get("$kratosUrl/sessions/whoami") {
            headers {
                header(HttpHeaders.Accept, "application/json")
                header("X-Session-Token", sessionToken)
            }
        }

        if (result.status != HttpStatusCode.OK) {
            throw RuntimeException()
        }
        val decoded = json.decodeFromString<JsonObject>(result.bodyAsText())
        val identity = decoded["identity"] as JsonObject
        return AuthorizedUser(
            identity["id"]!!.jsonPrimitive.content
        )
    }
}

data class AuthorizedUser(
    val id: String
)
