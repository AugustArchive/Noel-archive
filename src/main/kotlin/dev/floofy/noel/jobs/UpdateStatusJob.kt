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

package dev.floofy.noel.jobs

import dev.floofy.haru.abstractions.AbstractJob
import dev.floofy.noel.modules.discord.DiscordModule
import dev.kord.common.entity.PresenceStatus

class UpdateStatusJob(private val discord: DiscordModule): AbstractJob(
    name = "update:noel:status",
    expression = "*/15 * * * *"
) {
    private val statuses = listOf(
        "with beeps and boops...",
        "i love my boyfriend~ \uD83D\uDC9E \uD83D\uDC9E",
        "hey senpai, you seem a bit... unhappy, what's wrong?",
        "hai senpai!",
        "daily reminder Ice is adorable~ \uD83D\uDC9E",
        "homf, I'm not cute, you are!"
    )

    override suspend fun execute() {
        val statusText = statuses.random()
        discord.kord.editPresence {
            status = PresenceStatus.Online
            playing(statusText)
        }
    }
}
