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

package dev.floofy.noel.core.command

import dev.floofy.noel.extensions.toKordColor
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.entity.Message
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.rest.builder.message.EmbedBuilder
import java.awt.Color

/**
 * Represents the execution context of a specific [command][BaseCommand].
 * @param event The message create event for creating this context.
 */
class CommandContext(event: MessageCreateEvent, val args: List<String>) {
    private val kord: Kord = event.kord

    /**
     * Returns the message for this [command execution context][CommandContext].
     */
    val message: Message = event.message

    suspend fun reply(content: String): Message {
        return message.channel.createMessage(content)
    }

    suspend fun reply(e: EmbedBuilder): Message {
        return message.channel.createMessage { embeds += e }
    }

    suspend fun reply(block: EmbedBuilder.() -> Unit): Message {
        val e = EmbedBuilder()
        e.color = Color.decode("#F494C6").toKordColor()

        return message.channel.createMessage {
            embeds += e.apply(block)
        }
    }

    fun asEmbed(): EmbedBuilder = EmbedBuilder().apply {
        color = Color.decode("#F494C6").toKordColor()
    }
}
