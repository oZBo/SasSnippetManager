package com.sassnippet.manager

import com.sassnippet.manager.database.SnippetTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object TestDatabaseFactory {

    fun init(){
        Database.connect(
            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;",
            driver = "org.h2.Driver"
        )
        transaction {
            SchemaUtils.create(SnippetTable)
        }
    }

    fun cleanup() {
        transaction {
            SchemaUtils.drop(SnippetTable)
        }
    }

}