/**
 * Noel is the management bot for my Discord Server: Noel's Igloo.
 * Copyright (c) 2020-2021 Noel <cutie@floofy.dev>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.floofy.noel.modules.mongo

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import dev.floofy.noel.data.Config
import dev.floofy.noel.modules.mongo.collections.Document
import dev.floofy.noel.modules.mongo.collections.Todo
import dev.floofy.noel.modules.mongo.collections.User
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.bson.UuidRepresentation
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.serialization.registerModule

class MongoModule(config: Config) {
    private val connectionString: ConnectionString
    private val client: CoroutineClient
    val db: CoroutineDatabase

    init {
        val connStr = if (config.mongo.replicas != null) {
            val auth = if (config.mongo.auth != null) {
                "${config.mongo.auth.username}:${config.mongo.auth.password}@"
            } else {
                ""
            }

            "mongodb://$auth${config.mongo.replicas.joinToString(",")}/?replicaSet=${config.mongo.replicaSet}${if (config.mongo.auth?.source != null) "&authSource=${config.mongo.auth.source}" else ""}"
        } else {
            val auth = if (config.mongo.auth != null) {
                "${config.mongo.auth.username}@${config.mongo.auth.password}:"
            } else {
                ""
            }

            "mongodb://$auth${config.mongo.host}:${config.mongo.port}/${if (config.mongo.auth?.source != null) "?authSource=${config.mongo.auth.source}" else ""}"
        }

        connectionString = ConnectionString(connStr)
        client = KMongo.createClient(
            MongoClientSettings.builder().apply {
                uuidRepresentation(UuidRepresentation.STANDARD)
                applyConnectionString(connectionString)
            }.build()
        ).coroutine

        db = client.getDatabase(config.mongo.db)

        registerModule(
            SerializersModule {
                polymorphic(Document::class) {
                    subclass(User::class, User.serializer())
                    subclass(Todo::class, Todo.serializer())
                }
            }
        )
    }
}
