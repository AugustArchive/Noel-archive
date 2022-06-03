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
import java.lang.IllegalArgumentException
import java.util.*

@Command(
    name = "complete",
    description = "Completes a todo."
)
class CompleteTodoCommand(private val mongo: MongoModule): BaseCommand() {
    override suspend fun execute(ctx: CommandContext) {
        if (ctx.args.isEmpty()) {
            ctx.reply("You need to add a todo completion ID. You can get the UUID from `noel todos`.")
            return
        }

        val uuid = try {
            UUID.fromString(ctx.args.first())
        } catch (e: IllegalArgumentException) {
            ctx.reply("**${ctx.args.first()}** was not a UUID.")
            return
        }

        val todos = mongo.db.getCollection<Todo>()
        val todo = todos.findOne(
            Todo::id eq ctx.message.author!!.id.asString, Todo::completeId eq uuid
        )

        if (todo == null) {
            ctx.reply("TODO with UUID **$uuid** was not found.")
            return
        }

        todos.deleteOne(Todo::id eq ctx.message.author!!.id.asString, Todo::completeId eq uuid)
        ctx.reply("Deleted todo **${todo.todo}**")
    }
}
