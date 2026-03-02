package com.sassnippet.manager.routes

import com.sassnippet.manager.models.CreateSnippetRequest
import com.sassnippet.manager.repository.SnippetRepository
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.snippetRoutes(repository: SnippetRepository) {

    route("/api/snippets") {

        // GET /api/snippets
        get {
            val snippets = repository.getAll()
            call.respond(HttpStatusCode.OK, snippets)
        }

        // GET /api/snippets/search?q=keyword
        get("/search") {
            val query = call.request.queryParameters["q"] ?: ""
            val snippets = repository.search(query)
            call.respond(HttpStatusCode.OK, snippets)
        }

        // GET /api/snippets/{id}
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid ID")

            val snippet = repository.getById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound, "Snippet not found")

            call.respond(HttpStatusCode.OK, snippet)
        }

        // POST /api/snippets
        post {
            val request = call.receive<CreateSnippetRequest>()
            val snippet = repository.create(
                title = request.title,
                type = request.type,
                description = request.description,
                code = request.code,
                tags = request.tags
            )
            call.respond(HttpStatusCode.Created, snippet)
        }
    }

}