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

package dev.floofy.noel.listeners

import dev.floofy.noel.modules.discord.CommandsModule
import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.event.message.MessageUpdateEvent
import dev.kord.core.on
import org.koin.core.context.GlobalContext

fun Kord.onMessageCreate() = on<MessageCreateEvent> {
    val koin = GlobalContext.get()
    return@on koin.get<CommandsModule>().onCommandReceive(this)
}

fun Kord.onMessageEdit() = on<MessageUpdateEvent> {
    val koin = GlobalContext.get()

    if (this.old != null && this.old!!.content != this.message.asMessageOrNull()?.content) {
        val message = this.message.asMessage()
        val updated = MessageCreateEvent(
            message,
            message.getGuildOrNull()?.id,
            message.getAuthorAsMember(),
            this.shard,
            this.supplier
        )

        return@on koin.get<CommandsModule>().onCommandReceive(updated)
    }
}
