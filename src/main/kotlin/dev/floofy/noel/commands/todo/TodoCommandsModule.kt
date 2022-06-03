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

import dev.floofy.noel.core.command.BaseCommand
import org.koin.dsl.bind
import org.koin.dsl.module

val todoCommandsModule = module {
    single { AddTodoCommand(get()) } bind BaseCommand::class
    single { CompleteTodoCommand(get()) } bind BaseCommand::class
    single { TodosCommand(get()) } bind BaseCommand::class
}
