package com.sassnippet.manager.plugins

import com.sassnippet.manager.models.ErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.slf4j.LoggerFactory

fun Application.configureApiKeySecurity() {
    val logger = LoggerFactory.getLogger("SecurityConfig")

    // спробуємо всі можливі способи
    val fromEnv = System.getenv("X_API_KEY")
    val fromProps = System.getProperty("X_API_KEY")
    logger.info("fromEnv: ${fromEnv?.take(4)}****")
    logger.info("fromProps: ${fromProps?.take(4)}****")

    // логуємо ВСІ змінні середовища
    System.getenv().forEach { (k, v) ->
        logger.info("ENV: $k=${v.take(2)}**")
    }

    val apiKey = fromEnv ?: fromProps ?: "dev-secret-key"

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