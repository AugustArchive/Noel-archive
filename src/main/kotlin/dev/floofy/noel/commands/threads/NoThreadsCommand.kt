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

package dev.floofy.noel.commands.threads

import dev.floofy.noel.core.annotations.Command
import dev.floofy.noel.core.command.BaseCommand
import dev.floofy.noel.core.command.CommandContext
import dev.floofy.noel.utils.DiscordUtils

@Command(
    name = "no-threads",
    aliases = ["mute-threads", "nt"],
    description = "Removes the ability to use, interact, or create threads"
)
class NoThreadsCommand: BaseCommand() {
    private val roles: (String) -> String? = {
        when (it) {
            "382725233695522816" -> "870133296120594503" // Noel's Igloo
            "824066105102303232" -> "870135794608771133" // Noelware
            "859927607524982804" -> "870135497761099816" // Arisu
            else -> null
        }
    }

    override suspend fun execute(ctx: CommandContext) {
        val guild = ctx.message.getGuildOrNull() ?: return
        val roleId = roles(guild.id.asString)

        if (roleId == null) {
            ctx.reply("<:foxCri:835202546498797578> this guild doesn't have a **No Threads** role. :(")
            return
        }

        val users = DiscordUtils.getMultipleUsers(ctx.args.toList())
        println(users)

        if (users.isEmpty()) {
            ctx.reply("<:foxCri:835202546498797578> no users were specified. dork.")
            return
        }

        ctx.reply("You mentioned ${users.size} users. :D")
    }
}
