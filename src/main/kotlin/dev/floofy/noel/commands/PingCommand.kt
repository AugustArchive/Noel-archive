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

package dev.floofy.noel.commands

import dev.floofy.noel.core.annotations.Command
import dev.floofy.noel.core.command.BaseCommand
import dev.floofy.noel.core.command.CommandContext

@Command(
    name = "ping",
    aliases = ["pong", "are-you-even-alive", "ded"],
    description = "Returns the latency between Discord -> Noel."
)
class PingCommand: BaseCommand() {
    private val responses: List<String> = listOf(
        ":thinking: is potatoes really food?",
        "<:polardogBlep:834270964069433345>",
        "I-I'm no polar dog!!! D:",
        "yiff",
        "iunno i am just polar boi",
        "OWO"
    )

    override suspend fun execute(ctx: CommandContext) {
        val startPing = System.currentTimeMillis()
        val response = responses.random()
        val message = ctx.reply(response)

        val endPing = System.currentTimeMillis() - startPing
        message.delete()

        ctx.reply(":ping_pong: **${endPing}ms**")
    }
}
