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

import dev.floofy.noel.commands.dbots.dbotsCommandModule
import dev.floofy.noel.commands.releases.releaseCommandModule
import dev.floofy.noel.commands.threads.threadCommandsModule
import dev.floofy.noel.commands.todo.todoCommandsModule
import dev.floofy.noel.core.command.BaseCommand
import org.koin.dsl.bind
import org.koin.dsl.module

val commandsModule = module {
    single { SetBirthdayCommand(get()) } bind(BaseCommand::class)
    single { EvalCommand(get()) } bind(BaseCommand::class)
    single { AboutCommand() } bind(BaseCommand::class)
    single { HelloCommand() } bind(BaseCommand::class)
    single { WahCommand(get()) } bind(BaseCommand::class)
    single { HelpCommand() } bind(BaseCommand::class)
    single { PingCommand() } bind(BaseCommand::class)
    single { DocsCommand() } bind(BaseCommand::class)
} + threadCommandsModule + releaseCommandModule + todoCommandsModule
