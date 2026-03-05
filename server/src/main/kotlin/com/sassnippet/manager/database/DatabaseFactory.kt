package com.sassnippet.manager.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.ApplicationEnvironment
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

object DatabaseFactory {

    private val logger = LoggerFactory.getLogger(DatabaseFactory::class.java)

    fun init(environment: ApplicationEnvironment) {
        val jdbcUrl = environment.config.propertyOrNull("database.jdbcUrl")
            ?.getString() ?: "jdbc:postgresql://localhost:5432/sassnippets"
        val username = environment.config.propertyOrNull("database.username")
            ?.getString() ?: "snippet_user"
        val password = environment.config.propertyOrNull("database.password")
            ?.getString() ?: "snippet_pass"
        val maxPoolSize = environment.config.propertyOrNull("database.maxPoolSize")
            ?.getString()?.toInt() ?: 10

        logger.info("Connecting to database: $jdbcUrl")

        val config = HikariConfig().apply {
            this.jdbcUrl = jdbcUrl
            driverClassName = "org.postgresql.Driver"
            this.username = username
            this.password = password
            maximumPoolSize = maxPoolSize
        }

        Database.connect(HikariDataSource(config))

        transaction {
            SchemaUtils.create(SnippetTable)
        }

        logger.info("Database initialized successfully")
    }
}