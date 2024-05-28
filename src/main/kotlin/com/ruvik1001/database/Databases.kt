package com.ruvik1001.database

import com.mongodb.MongoClientSettings
import com.mongodb.ServerAddress
import com.mongodb.client.*
import com.mongodb.connection.ClusterSettings
import io.ktor.server.application.*
import io.ktor.server.config.*

fun Application.connectToMongoDB(): MongoDatabase {
    val user = environment.config.tryGetString("db.mongo.user")
    val password = environment.config.tryGetString("db.mongo.password")
    val hosts = environment.config.tryGetString("db.mongo.hosts") ?: "127.0.0.1:27017"
    val maxPoolSize = environment.config.tryGetString("db.mongo.maxPoolSize")?.toInt() ?: 20
    val databaseName = environment.config.tryGetString("db.mongo.database.name") ?: "myDatabase"

    val credentials = user?.let { userVal -> password?.let { passwordVal -> "$userVal:$passwordVal@" } }.orEmpty()
    val hostList = hosts.split(",").map { it.trim() }
    val serverAddresses = hostList.map {
        val (host, port) = it.split(":")
        ServerAddress(host, port.toInt())
    }

    val settings = MongoClientSettings.builder()
        .applyToClusterSettings { builder: ClusterSettings.Builder ->
            builder.hosts(serverAddresses)
        }
        .build()

    val mongoClient = MongoClients.create(settings)

    // Check if database exists and drop it if it does
    if (mongoClient.listDatabaseNames().contains(databaseName)) {
        mongoClient.getDatabase(databaseName).drop()
        println("Database $databaseName existed and was dropped.")
    } else {
        println("Database $databaseName does not exist, no need to drop.")
    }

    val database = mongoClient.getDatabase(databaseName)

    environment.monitor.subscribe(ApplicationStopped) {
        mongoClient.close()
    }

    return database
}