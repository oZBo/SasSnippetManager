package com.sassnippet.manager.plugins

import com.sassnippet.manager.models.ErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

fun Application.configureApiKeySecurity() {
    val apiKey = System.getenv("API_KEY") ?: "dev-secret-key"

    intercept(ApplicationCallPipeline.Plugins) {
        val path = call.request.path()
        if (path.startsWith("/api/")) {
            val key = call.request.headers["X-API-Key"]
            if(key != apiKey) {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Invalid or missing API key"))
                finish()
            }
        }
    }
}