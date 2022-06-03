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
import dev.floofy.noel.modules.discord.CommandsModule
import org.koin.core.context.GlobalContext

@Command(
    name = "help",
    aliases = ["halp", "h", "?"],
    description = "Shows a list of commands globally and per-guild basis, you cannot view commands that are locked to a specific guild."
)
class HelpCommand: BaseCommand() {
    override suspend fun execute(ctx: CommandContext) = renderHelpCommand(ctx)

    private suspend fun renderHelpCommand(ctx: CommandContext) {
        val commands = GlobalContext.get().get<CommandsModule>()
        val guild = ctx.message.getGuild()
        val cmds = commands.commands.values.filter {
            if (it.metadata!!.onlyIn.isNotEmpty())
                it.metadata!!.onlyIn.contains(guild.id.asString) && !it.metadata!!.ownerOnly
            else
                !it.metadata!!.ownerOnly
        }

        val categories: MutableMap<String, MutableList<BaseCommand>> = mutableMapOf()
        for (command in cmds) {
            if (!categories.containsKey(command.metadata!!.category.cat))
                categories[command.metadata!!.category.cat] = mutableListOf()

            // Skip it
            if (command.metadata!!.onlyIn.isNotEmpty() && command.metadata!!.onlyIn.contains(guild.id.asString))
                continue

            categories[command.metadata!!.category.cat]!!.add(command)
        }

        val formatted = categories.map {
            "• **${it.key}**: ${it.value.joinToString(", ") { "**`${it.metadata!!.name}`**" }}"
        }.joinToString("\n")

        val specificGuildCommands = commands.commands.filter {
            it.value.metadata!!.onlyIn.contains(guild.id.asString)
        }.values.toList()

        val content = buildString {
            append("<:noelWave:815113902304002058> Hewoo **${ctx.message.author!!.tag}**!!!!! Here is a list of commands you can use~")
            appendLine()
            appendLine(formatted)
            appendLine("• **Guild-specific commands**: ${specificGuildCommands.joinToString(", ") { "**`${it.metadata!!.name}`**" } }")
        }

        ctx.reply(content)
    }
}
