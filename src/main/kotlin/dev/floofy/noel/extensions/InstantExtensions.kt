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

package dev.floofy.noel.extensions

import kotlinx.datetime.Instant

// Credit: https://github.com/DV8FromTheWorld/Yui/blob/master/src/main/java/net/dv8tion/discord/commands/UptimeCommand.java#L34
fun Instant.humanize(): String {
    val months = this.epochSeconds / 2592000000L % 12
    val weeks = this.epochSeconds / 604800000L % 7
    val days = this.epochSeconds / 86400000L % 30

    return buildString {
        if (months > 0) append("**$months** ${if (months > 1L) "months" else "month"}, ")
        if (weeks > 0) append("**$weeks** ${if (weeks > 1L) "weeks" else "week"}, ")
        if (days > 0) append("**$days** ${if (days > 1L) "days" else "day"}")
    }
}
