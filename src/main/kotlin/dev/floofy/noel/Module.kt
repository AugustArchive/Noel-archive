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

package dev.floofy.noel

import dev.floofy.haru.Scheduler
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import org.slf4j.LoggerFactory

val noelModule = module {
    single {
        val logger = LoggerFactory.getLogger(Scheduler::class.java)
        Scheduler {
            handleError { job, ex ->
                logger.error("Received exception while running job ${job.name}:", ex)
            }
        }
    }

    single {
        Json {
            ignoreUnknownKeys = true
        }
    }

    single {
        HttpClient(OkHttp) {
            engine {
                config {
                    followRedirects(true)
                }
            }

            install(JsonFeature) {
                serializer = KotlinxSerializer(get())
            }

            install(UserAgent) {
                agent = "Noel/DiscordBot v${NoelInfo.VERSION}"
            }
        }
    }

    single {
        WebhookServer()
    }
}
