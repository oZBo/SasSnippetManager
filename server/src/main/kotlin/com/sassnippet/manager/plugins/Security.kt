package com.sassnippet.manager.plugins

import com.sassnippet.manager.models.ErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.slf4j.LoggerFactory

fun Application.configureApiKeySecurity() {
    val logger = LoggerFactory.getLogger("SecurityConfig")
    val apiKey = System.getenv("API_KEY") ?: "dev-secret-key"
    logger.info("Security configured with key: ${apiKey.take(4)}****")

    val allEnvVars = System.getenv()
        .filter { it.key.contains("API", ignoreCase = true) }
        .map { "${it.key}=${it.value.take(4)}****" }
    logger.info("API-related env vars: $allEnvVars")

    intercept(ApplicationCallPipeline.Plugins) {
        val path = call.request.path()
        if (path.startsWith("/api/")) {
            val key = call.request.headers["X-API-Key"]
            logger.info("Server key: ${apiKey.take(4)}****")
            logger.info("Received key: ${key?.take(4)}****")
            if (key != apiKey) {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Invalid or missing API key"))
                finish()
            }
        }
    }
}