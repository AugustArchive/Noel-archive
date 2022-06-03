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

package dev.floofy.noel.core.pagination

import dev.floofy.noel.core.SuspendCloseable
import dev.floofy.noel.extensions.injectable
import dev.floofy.noel.modules.discord.DiscordModule
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.edit
import dev.kord.core.entity.Message
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.message.ReactionAddEvent
import dev.kord.core.on
import dev.kord.rest.builder.message.EmbedBuilder
import org.koin.core.context.GlobalContext

/**
 * Represents a embed that represents a paginated message.
 * @param channel The text channel the message belongs in
 */
class PaginationEmbed(
    private val channel: TextChannel,
    private var embeds: List<EmbedBuilder>,
    private val invoker: User
): SuspendCloseable {
    private lateinit var message: Message
    private var listening = false
    private var current = 0

    companion object {
        val REACTIONS = mapOf(
            "stop" to "\u23F9\uFE0F",
            "right" to "\u27A1\uFE0F",
            "left" to "\u2B05\uFE0F",
            "first" to "\u23EE\uFE0F",
            "last" to "\u23ED\uFE0F"
        )
    }

    override suspend fun close() {
        message.delete("Pagination embed was closed")
        if (listening) listening = false
    }

    // TODO: migrate to buttons over reactions :(
    suspend fun run() {
        message = channel.createMessage {
            embeds += this@PaginationEmbed.embeds[current].apply {
                footer {
                    text = "Page ${current + 1}/${this@PaginationEmbed.embeds.size}"
                }
            }
        }

        val discord = GlobalContext.injectable<DiscordModule>().kord
        discord.rest.channel.createReaction(
            channelId = channel.id,
            messageId = message.id,
            emoji = REACTIONS["left"]!!
        )

        discord.rest.channel.createReaction(
            channelId = channel.id,
            messageId = message.id,
            emoji = REACTIONS["first"]!!
        )

        discord.rest.channel.createReaction(
            channelId = channel.id,
            messageId = message.id,
            emoji = REACTIONS["stop"]!!
        )

        discord.rest.channel.createReaction(
            channelId = channel.id,
            messageId = message.id,
            emoji = REACTIONS["last"]!!
        )

        discord.rest.channel.createReaction(
            channelId = channel.id,
            messageId = message.id,
            emoji = REACTIONS["right"]!!
        )

        listening = true
        discord.on<ReactionAddEvent> {
            if (!listening) return@on

            onReactionAdd(this)
        }
    }

    private suspend fun onReactionAdd(event: ReactionAddEvent) {
        val channel = event.getChannelOrNull() ?: return
        val reactor = event.getUserOrNull() ?: return
        val reaction = event.emoji

        // Check if the channels are the same
        if (channel.id.value != this.channel.id.value)
            return

        // Check if reactors are the same
        if (reactor.id.value != invoker.id.value)
            return

        // Delete the reaction
        event.kord.rest.channel.deleteReaction(
            channelId = channel.id,
            messageId = this.message.id,
            userId = invoker.id,
            emoji = reaction.name
        )

        // Check if the reaction is the same
        when (reaction.name) {
            // if the reaction was stop, then close this pagination
            REACTIONS["stop"]!! -> close()

            // if the reaction was left, go to the previous page
            REACTIONS["left"]!! -> {
                current -= 1

                // Force to go to the last page if went to -1 on accident :(
                if (current < 0) current = embeds.size - 1

                this.message.edit {
                    embeds = mutableListOf(
                        this@PaginationEmbed.embeds[current].apply {
                            footer {
                                text = "Page ${current + 1}/${this@PaginationEmbed.embeds.size}"
                            }
                        }
                    )
                }
            }

            // if the reaction was right, go to the next page
            REACTIONS["right"]!! -> {
                current += 1
                if (current == embeds.size) current = 0

                this.message.edit {
                    embeds = mutableListOf(
                        this@PaginationEmbed.embeds[current].apply {
                            footer {
                                text = "Page ${current + 1}/${this@PaginationEmbed.embeds.size}"
                            }
                        }
                    )
                }
            }

            // if the reaction was first, go to index 0
            REACTIONS["first"]!! -> {
                // Skip if we are at the first index
                if (current == 0)
                    return

                current = 0
                this.message.edit {
                    embeds = mutableListOf(
                        this@PaginationEmbed.embeds[current].apply {
                            footer {
                                text = "Page ${current + 1}/${this@PaginationEmbed.embeds.size}"
                            }
                        }
                    )
                }
            }

            // if the reaction was last, go to index $INSERT_NUMBER_HERE
            REACTIONS["last"]!! -> {
                val last = embeds.size - 1
                if (current == last)
                    return

                current = last
                this.message.edit {
                    embeds = mutableListOf(
                        this@PaginationEmbed.embeds[current].apply {
                            footer {
                                text = "Page ${current + 1}/${this@PaginationEmbed.embeds.size}"
                            }
                        }
                    )
                }
            }
        }
    }
}
