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

import dev.floofy.noel.NoelInfo
import dev.floofy.noel.core.annotations.Command
import dev.floofy.noel.core.command.BaseCommand
import dev.floofy.noel.core.command.CommandCategory
import dev.floofy.noel.core.command.CommandContext

@Command(
    name = "about",
    description = "Some information about me, Noel: the polar bear.",
    category = CommandCategory.CORE,
    aliases = ["info", "me", "botinfo"]
)
class AboutCommand: BaseCommand() {
    override suspend fun execute(ctx: CommandContext) {
        val region = System.getenv("REGION") ?: "unknown"

        val desc = StringBuilder("<:noelWave:815113902304002058> Hewwo **${ctx.message.author!!.tag}**!").apply {
            appendLine("I am Noel! I help run these servers with anything repeative and in a simple bot! :<")
            appendLine("Unfortunately, you cannot see my source code due to some internal APIs being used, and I don't want to get in trouble...")
            appendLine()
            appendLine("Have some statistics, if you were curious...")
            appendLine("```apache")
            appendLine("Kotlin: v${KotlinVersion.CURRENT}")
            appendLine("Kord:   v0.8.0-M5") // kord doesn't provide a version class :(
            appendLine("Noel:   v${NoelInfo.VERSION} (${NoelInfo.COMMIT_HASH}) - build date ~ ${NoelInfo.BUILT_AT}")
            appendLine("Java:   v${System.getProperty("java.version", "0.0.0")} (${System.getProperty("java.vendor", "unknown")})")
            appendLine("Node:   $region")
            appendLine("```")
        }

        ctx.reply {
            title = "About Noel"
            description = desc.toString()
        }
    }
}
