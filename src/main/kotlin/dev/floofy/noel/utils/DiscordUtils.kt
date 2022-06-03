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

package dev.floofy.noel.utils

import dev.floofy.noel.extensions.injectable
import dev.floofy.noel.extensions.toSnowflake
import dev.floofy.noel.modules.discord.DiscordModule
import dev.kord.core.entity.User
import dev.kord.core.exception.EntityNotFoundException
import org.koin.core.context.GlobalContext

object DiscordUtils {
    private val ID_REGEX = "/^\\d+\$/".toRegex()
    private val USER_MENTION_REGEX: Regex = "/^<@!?([0-9]+)>\$/".toRegex()

    suspend fun getMultipleUsers(users: List<String>): List<User> {
        val discord = GlobalContext.injectable<DiscordModule>()

        // Check for users with @mention
        val mentionedUsers = users.filter {
            it.matches(USER_MENTION_REGEX)
        }

        val mappedByIds = users.filter {
            it.matches(ID_REGEX)
        }

        if (mentionedUsers.isEmpty() || mappedByIds.isEmpty())
            return emptyList()

        // merge them together
        val mentionedIds = mentionedUsers.mapNotNull {
            try {
                discord.kord.getUser(USER_MENTION_REGEX.findAll(it).toList()[1].value.toSnowflake())
            } catch (e: EntityNotFoundException) {
                null
            }
        }

        val mentionedByActualIDs = mappedByIds.mapNotNull {
            try {
                discord.kord.getUser(it.toSnowflake())
            } catch (e: EntityNotFoundException) {
                null
            }
        }

        val listOfUsers = mentionedByActualIDs + mentionedIds
        return listOfUsers.ifEmpty { return emptyList() }
    }
}
