package com.sassnippet.manager.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init() {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:postgresql://localhost:5432/sassnippets"
            driverClassName = "org.postgresql.Driver"
            username = "snippet_user"
            password = "snippet_pass"
            maximumPoolSize = 10
        }

        Database.connect(HikariDataSource(config))

        transaction {
            SchemaUtils.create(SnippetTable)
        }

    }

}