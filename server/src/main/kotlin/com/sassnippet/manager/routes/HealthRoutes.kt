package com.sassnippet.manager.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class HealthResponse(
    val status: String,
    val database: String
)

fun Route.healthRoutes() {
    get("/health") {
        val dbStatus = try {
            transaction { "UP" }
        } catch (e: Exception) {
            "DOWN"
        }

        val httpStatus = if (dbStatus == "UP") HttpStatusCode.OK else HttpStatusCode.ServiceUnavailable
        call.respond(httpStatus, HealthResponse(status = if (dbStatus == "UP") "UP" else "DOWN", database = dbStatus))
    }
}