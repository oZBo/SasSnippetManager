package com.sassnippet.manager

import com.sassnippet.manager.database.DatabaseFactory
import com.sassnippet.manager.models.ErrorResponse
import com.sassnippet.manager.plugins.configureApiKeySecurity
import com.sassnippet.manager.repository.SnippetRepository
import com.sassnippet.manager.routes.healthRoutes
import com.sassnippet.manager.routes.snippetRoutes
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main() {
    java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Europe/Kyiv"))
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    install(CORS) {
        anyMethod()
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
        allowHeaders { true }
    }

    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(cause.message ?: "Bad request"))
        }
        exception<Throwable> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Internal server error"))
        }
    }

    configureApiKeySecurity()

    DatabaseFactory.init(environment)

    val repository = SnippetRepository()

    routing {
        snippetRoutes(repository)
        healthRoutes()
    }
}