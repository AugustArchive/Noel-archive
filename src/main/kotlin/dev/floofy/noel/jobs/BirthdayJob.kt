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
import dev.floofy.noel.extensions.toSnowflake
import dev.floofy.noel.kotlin.logging
import dev.floofy.noel.modules.discord.DiscordModule
import dev.floofy.noel.modules.mongo.MongoModule
import dev.floofy.noel.modules.mongo.collections.User
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.entity.channel.TextChannel
import dev.kord.rest.builder.message.create.allowedMentions
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class BirthdayJob(
    private val mongo: MongoModule,
    private val discord: DiscordModule
): AbstractJob(
    name = "birthdays :3",
    expression = "0 0 * * *"
) {
    private val logger by logging(this::class.java)

    override suspend fun execute() {
        logger.info("ahhh a new day has begun, which means it could be someone's birthday :D")

        val listOfUsers = mongo.db.getCollection<User>("users").find().toList()
        val calendar = Calendar.getInstance()
        val filteredUsersByCalendar = listOfUsers.filter {
            if (it.birthday == null)
                return@filter false

            val zdt = ZonedDateTime.ofInstant(it.birthday, ZoneId.systemDefault())
            val birthCalendar = GregorianCalendar.from(zdt)
            birthCalendar.get(Calendar.DATE) == calendar.get(Calendar.DATE) && birthCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
        }

        if (filteredUsersByCalendar.isEmpty()) {
            logger.info("It is no one's birthday today :(")

            // remove the birthday role if needed
            val guild = discord.kord.getGuild("382725233695522816".toSnowflake())!!
            guild.members.filter {
                it.roleIds.contains("658968788611497994".toSnowflake())
            }.map {
                it.removeRole("658968788611497994".toSnowflake(), "[Birthdays] remove birthday role :D")
            }

            return
        }

        logger.info("It is ${filteredUsersByCalendar.size} users birthdays!")
        val userTags = filteredUsersByCalendar.mapNotNull {
            discord.kord.getUser(it.id.toSnowflake())
        }

        for (u in userTags) {
            u
                .asMember("382725233695522816".toSnowflake())
                .addRole("658968788611497994".toSnowflake(), "[Birthday] It's their birthday! :3")
        }

        val channel = discord.kord.getChannelOf<TextChannel>("794101954158526474".toSnowflake())
        channel?.createMessage {
            content = ":cake: <@&801849426736840724> It is ${userTags.joinToString(", ") { "<@!${it.id.asString}>" }}'s birthday(s) today :D"
            allowedMentions {
                roles += "801849426736840724".toSnowflake()
                users += userTags.map { it.id }
            }
        }
    }
}
