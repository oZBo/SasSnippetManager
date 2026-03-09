package com.sassnippet.manager.routes

import com.sassnippet.manager.models.ConvertRequest
import com.sassnippet.manager.models.ConvertResponse
import com.sassnippet.manager.models.GroqMessage
import com.sassnippet.manager.models.GroqRequest
import com.sassnippet.manager.models.GroqResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.json.Json

private const val GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions"
private const val GROQ_MODEL = "llama-3.3-70b-versatile"

private val groqClient = HttpClient(OkHttp) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
        })
    }
}

fun Route.aiRoutes() {
    route("/api/ai") {

        post("/convert-to-r") {
            val apiKey = System.getenv("GROQ_API_KEY")
            if (apiKey.isNullOrBlank()) {
                call.respond(
                    HttpStatusCode.ServiceUnavailable,
                    mapOf("error" to "AI conversion is not configured. Set GROQ_API_KEY environment variable.")
                )
                return@post
            }

            val request = call.receive<ConvertRequest>()
            if (request.code.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Code cannot be empty"))
                return@post
            }

            val groqResponse = groqClient.post(GROQ_API_URL) {
                header("Authorization", "Bearer $apiKey")
                contentType(ContentType.Application.Json)
                setBody(
                    GroqRequest(
                        model = GROQ_MODEL,
                        messages = listOf(
                            GroqMessage(
                                role = "system",
                                content = "You are a SAS to R code converter. Convert SAS code to equivalent R code. " +
                                        "Return only the R code without any explanation, comments, or markdown formatting. " +
                                        "Do not include ```r or ``` code fences."
                            ),
                            GroqMessage(
                                role = "user",
                                content = "Convert this SAS code to R:\n\n${request.code}"
                            )
                        )
                    )
                )
            }.body<GroqResponse>()

            val rCode = groqResponse.choices.firstOrNull()?.message?.content
                ?: return@post call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Empty response from AI")
                )

            call.respond(HttpStatusCode.OK, ConvertResponse(rCode = rCode.trim()))
        }
    }
}
