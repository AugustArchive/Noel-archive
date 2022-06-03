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
import dev.floofy.noel.core.command.CommandCategory
import dev.floofy.noel.core.command.CommandContext
import dev.floofy.noel.modules.mongo.MongoModule
import dev.floofy.noel.modules.mongo.collections.User
import org.litote.kmongo.eq
import org.litote.kmongo.setValue
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@Command(
    name = "set-birthday",
    description = "Sets your birthday so people can say happy birthday to you! :D",
    category = CommandCategory.CORE,
    onlyIn = ["382725233695522816"],
    aliases = ["birthday"]
)
class SetBirthdayCommand(private val mongo: MongoModule): BaseCommand() {
    // fuck you america >:(
    // dd/mm/yyyy > mm/dd/yyyy
    private val regex = Regex("(\\d{1,31})[/](\\d+)[/](\\d+)")

    override suspend fun execute(ctx: CommandContext) {
        if (ctx.args.isEmpty()) {
            val users = mongo.db.getCollection<User>("users")
            val document = users.findOne(User::id eq ctx.message.author!!.id.asString)

            val message = if (document!!.birthday == null)
                "No arguments were specified, please run it again with your birthday!"
            else run {
                val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
                val ldt = LocalDateTime.ofInstant(document.birthday, ZoneId.systemDefault())
                val birth = formatter.format(ldt)

                ":cake: Your birthday is set to **$birth** :D"
            }

            ctx.reply(message)
            return
        }

        if (ctx.args.size > 2) {
            ctx.reply("Way too many arguments! No spaces when setting your birthday. :3")
            return
        }

        val birthday = ctx.args[0]
        if (!birthday.matches(regex)) {
            ctx.reply("Invalid argument `$birthday`. Example: `noel set-birthday 24/03/2004`")
            return
        }

        val (day, month, year) = birthday.split("/")
        if (year.toInt() < 1970) {
            ctx.reply("Nice try...")
            return
        }

        val current = Calendar.getInstance()
        val nextYear = current.get(Calendar.YEAR) + 1
        if (year.toInt() > nextYear) {
            ctx.reply("Nice try... you can't be born in $nextYear, sorry...")
            return
        }

        val newCalendar = Calendar.getInstance()
        newCalendar.set(year.toInt(), month.toInt() - 1, day.toInt())

        val instant = newCalendar.toInstant()
        val users = mongo.db.getCollection<User>("users")
        users.updateOne(User::id eq ctx.message.author!!.id.asString, setValue(User::birthday, instant))

        ctx.reply("Birthday has been set to `$birthday`, hopefully...")
    }
}
