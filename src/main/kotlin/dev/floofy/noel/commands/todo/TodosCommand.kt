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

package dev.floofy.noel.commands.todo

import dev.floofy.noel.core.annotations.Command
import dev.floofy.noel.core.command.BaseCommand
import dev.floofy.noel.core.command.CommandContext
import dev.floofy.noel.modules.mongo.MongoModule
import dev.floofy.noel.modules.mongo.collections.Todo
import org.litote.kmongo.eq

@Command(
    name = "todos",
    aliases = ["list-todos"],
    description = "Lists all the todos",
    ownerOnly = true
)
class TodosCommand(private val mongo: MongoModule): BaseCommand() {
    override suspend fun execute(ctx: CommandContext) {
        val todos = mongo.db.getCollection<Todo>()
        val doc = todos.find(
            Todo::id eq ctx.message.author!!.id.asString
        )

        if (doc.toList().isEmpty()) {
            ctx.reply("<:noelHeart:815113838743519253> **You have no todos on your list~**")
            return
        }

        val list = doc.toList()
        val embed = ctx.asEmbed().apply {
            title = "[ Todos for ${ctx.message.author!!.tag} ]"
            description = buildString {
                appendLine("There are a total of **${list.size}** todos on their plate. :<")
                appendLine()

                for (todo in list) {
                    appendLine("**•** ${todo.todo} [**${todo.completeId}**]")
                }
            }
        }

        ctx.reply(embed)
    }
}
