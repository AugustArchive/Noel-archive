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

package dev.floofy.noel.core.annotations

import dev.floofy.noel.core.command.CommandCategory

/**
 * Represents a command embedded in a base command context. This
 * annotation will apply extra metadata on a normal command executor.
 */
@Retention
@Target(AnnotationTarget.CLASS)
annotation class Command(
    /**
     * The name of the command
     */
    val name: String,

    /**
     * The description of the command
     */
    val description: String = "No description has been added.",

    /**
     * Any additional aliases to execute this command.
     */
    val aliases: Array<String> = [],

    /**
     * The category of the command, useful for printing out the
     * help command.
     */
    val category: CommandCategory = CommandCategory.CORE,

    /**
     * If the execution of the command should be ran by the
     * whitelisted users.
     */
    val ownerOnly: Boolean = false,

    /**
     * List of whitelisted guild IDs to use, apply with an empty array
     * if this command should be used in all guilds Noel is in.
     */
    val onlyIn: Array<String> = []
)
