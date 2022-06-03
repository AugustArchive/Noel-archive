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

import dev.floofy.noel.extensions.injectable
import dev.floofy.noel.extensions.toKordColor
import dev.floofy.noel.extensions.toSnowflake
import dev.floofy.noel.kotlin.logging
import dev.floofy.noel.kotlin.serializers.InstantSerializer
import dev.floofy.noel.modules.discord.DiscordModule
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.entity.channel.TextChannel
import dev.kord.rest.builder.message.EmbedBuilder
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.context.GlobalContext
import java.awt.Color
import java.time.Instant
import java.util.concurrent.TimeUnit

@Serializable
data class NewReleasePacket(
    val provider: String,
    val project: String,
    val version: String,

    @Serializable(with = InstantSerializer::class)
    val time: Instant,

    @SerialName("is_prerelease")
    val preRelease: Boolean = false,

    @SerialName("is_updated")
    val updated: Boolean = false,
    val note: NewReleaseNote,
)

@Serializable
data class NewReleaseNote(
    val title: String,
    val message: String
)

/**
 * Represents a webhook server to interact with **newreleases.io**
 *
 * @author Noel
 * @since Aug 07, 2021 - 20:47
 */
class WebhookServer {
    private lateinit var service: NettyApplicationEngine
    private val logger by logging(this::class.java)

    fun start() {
        logger.info("Starting webhook server on port 44321...")
        service = embeddedServer(Netty, port = 44321) {
            install(ContentNegotiation) {
                // Serialize with kotlinx.serialization
                json(GlobalContext.injectable())
            }

            routing {
                get("/") {
                    call.respond(HttpStatusCode.OK, "{\"message\":\"get out here stinky\"}")
                }

                get("/new-release") {
                    call.respond(HttpStatusCode.MethodNotAllowed, "{\"message\":\"Only POST requests are allowed.\"}")
                }

                post("/new-release") {
                    val packet = call.receive<NewReleasePacket>()
                    val discord = GlobalContext.injectable<DiscordModule>()

                    if (!packet.updated)
                        return@post call.respond(HttpStatusCode.NoContent)

                    logger.info("Received new release on project ${packet.project} ${packet.version}~")
                    val channel = discord.kord.getChannelOf<TextChannel>("873771638146105364".toSnowflake()) ?: return@post call.respond(
                        HttpStatusCode.NoContent
                    )

                    val releaseEmbed = EmbedBuilder().apply {
                        color = Color.decode("#F494C6").toKordColor()
                        title = packet.project
                        description = buildString {
                            appendLine("• Pre Release: ${if (packet.preRelease) "<:success:464708611260678145>" else "<:xmark:464708589123141634>"}")
                            appendLine()
                            appendLine("```md")
                            appendLine("# ${packet.note.title}")
                            appendLine(packet.note.message)
                            appendLine("```")
                        }
                    }

                    channel.createMessage {
                        embeds += releaseEmbed
                    }

                    call.respond(HttpStatusCode.NoContent)
                }
            }
        }

        service.start(wait = true)
    }

    fun shutdown() {
        logger.warn("Shutting down server...")
        service.stop(1L, 1L, TimeUnit.SECONDS)
    }
}
