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

import dev.floofy.noel.Bootstrap
import dev.kord.common.entity.PresenceStatus
import dev.kord.core.Kord
import dev.kord.core.event.gateway.DisconnectEvent
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.on
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("dev.floofy.noel.listeners.VoidListener")

fun Kord.onReady() = on<ReadyEvent> {
    logger.info("Ready as ${this.self.tag}!")
    this.kord.editPresence {
        status = PresenceStatus.Online
        playing("ohayo senpai!!!")
    }

    Bootstrap.launchServer()
}

fun Kord.onDisconnect() = on<DisconnectEvent> {
    logger.warn("Disconnected on shard #${this.shard} :3")
}
