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

package dev.floofy.noel.modules.discord

import dev.floofy.noel.data.Config
import dev.kord.core.Kord
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import io.ktor.client.*
import kotlinx.coroutines.*

@OptIn(PrivilegedIntent::class)
class DiscordModule(private val config: Config, private val http: HttpClient) {
    var kord: Kord

    init {
        runBlocking {
            kord = Kord(config.token) {
                httpClient = http
                intents = Intents(
                    Intent.GuildMessages,
                    Intent.Guilds,
                    Intent.GuildMembers,
                    Intent.GuildMessageReactions
                )
            }
        }
    }
}
