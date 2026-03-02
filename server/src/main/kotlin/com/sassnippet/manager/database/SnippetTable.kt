package com.sassnippet.manager.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table

object SnippetTable : IntIdTable("snippets") {
    val title = varchar("title", 255)
    val type = varchar("type", 50)
    val description = text("description")
    val code = text("code")
    val tags = text("tags") //JSON
}