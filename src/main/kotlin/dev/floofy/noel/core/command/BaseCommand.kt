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

package dev.floofy.noel.core.command

import dev.floofy.noel.core.annotations.Command

/**
 * Represents a base command execution context. To return the metadata
 * of a specific command, use the [BaseCommand.metadata] getter.
 */
abstract class BaseCommand {
    /**
     * Returns the metadata of a specific command, if none is provided
     * it'll return `null`.
     */
    val metadata: Command?
        get() = this::class.java.getAnnotation(Command::class.java)

    /**
     * Executes the command in this embedded context.
     * @param ctx The command's context
     */
    abstract suspend fun execute(ctx: CommandContext)
}
