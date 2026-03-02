package com.sassnippet.manager.network

import com.sassnippet.manager.model.CreateSnippetRequest
import com.sassnippet.manager.model.Snippet
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class SnippetApiClient(private val baseUrl: String) {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    suspend fun getAll(): List<Snippet> =
        client.get("$baseUrl/api/snippets").body()

    suspend fun getById(id: Int): Snippet =
        client.get("$baseUrl/api/snippets/$id").body()

    suspend fun search(query: String): List<Snippet> =
        client.get("$baseUrl/api/snippets/search") {
            parameter("q", query)
        }.body()

    suspend fun create(request: CreateSnippetRequest): Snippet =
        client.post("$baseUrl/api/snippets") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

}