package com.sassnippet.manager.routes

import com.sassnippet.manager.models.CreateSnippetRequest
import com.sassnippet.manager.models.SaveRCodeRequest
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

        get {
            val page = call.request.queryParameters["page"]?.toIntOrNull()?.coerceAtLeast(1)
            val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull()?.coerceIn(1, 100) ?: 20
            val result = repository.getPaged(page ?: 1, pageSize)
            call.respond(HttpStatusCode.OK, result)
        }

        // POST /api/snippets
        post {
            val request = call.receive<CreateSnippetRequest>()
            request.validate()
            val snippet = repository.create(
                title = request.title,
                type = request.type,
                description = request.description,
                code = request.code,
                tags = request.tags
            )
            call.respond(HttpStatusCode.Created, snippet)
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid ID")

            val request = call.receive<CreateSnippetRequest>()
            request.validate()
            val snippet = repository.update(id, request)
                ?: return@put call.respond(HttpStatusCode.NotFound, "Snippet not found")

            call.respond(HttpStatusCode.OK, snippet)
        }

        // PATCH /api/snippets/{id}/r-code
        patch("/{id}/r-code") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@patch call.respond(HttpStatusCode.BadRequest, "Invalid ID")

            val request = call.receive<SaveRCodeRequest>()
            val snippet = repository.saveRCode(id, request.rCode)
                ?: return@patch call.respond(HttpStatusCode.NotFound, "Snippet not found")

            call.respond(HttpStatusCode.OK, snippet)
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid ID")

            val deleted = repository.delete(id)
            if (deleted) {
                call.respond(HttpStatusCode.NoContent)
            } else {
               call.respond(HttpStatusCode.NotFound, "Snippet not found")
            }
        }

    }

}