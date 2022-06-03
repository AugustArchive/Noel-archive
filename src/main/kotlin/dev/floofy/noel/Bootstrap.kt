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

package dev.floofy.noel

import dev.floofy.haru.Scheduler
import dev.floofy.haru.abstractions.AbstractJob
import dev.floofy.noel.commands.commandsModule
import dev.floofy.noel.data.dataModule
import dev.floofy.noel.extensions.injectable
import dev.floofy.noel.jobs.jobsModule
import dev.floofy.noel.kotlin.logging
import dev.floofy.noel.listeners.*
import dev.floofy.noel.modules.discord.CommandsModule
import dev.floofy.noel.modules.discord.DiscordModule
import dev.floofy.noel.modules.mongo.MongoModule
import dev.floofy.noel.utils.BannerUtil
import kotlinx.coroutines.*
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

object Bootstrap {
    val startedAt = System.currentTimeMillis()
    private val logger by logging(this::class.java)

    init {
        Runtime.getRuntime().addShutdownHook(Thread(::shutdown, "Noel-ShutdownThread"))
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun shutdown() {
        logger.warn("Shutting down Noel...")

        val koin = GlobalContext.get()
        val discord = koin.get<DiscordModule>()
        val scheduler = koin.get<Scheduler>()
        val server = koin.get<WebhookServer>()

        server.shutdown()
        scheduler.unschedule()

        // block the shutdown thread until Kord is done with it's shit
        runBlocking {
            try {
                discord.kord.shutdown()
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        Thread.currentThread().name = "Noel-MainThread"
        BannerUtil.show()

        logger.info("Bootstrapping Noel...")
        startKoin {
            modules(noelModule, dataModule, dev.floofy.noel.modules.modules, jobsModule, *commandsModule.toTypedArray())
        }

        val koin = GlobalContext.get()

        // Init commands and Mongo
        koin.get<CommandsModule>()
        koin.get<MongoModule>()

        runBlocking {
            val discord = koin.get<DiscordModule>()
            val jobs = koin.getAll<AbstractJob>()
            val scheduler = koin.get<Scheduler>()

            // Register jobs
            for (job in jobs) {
                scheduler.schedule(job, start = true)
            }

            discord.kord.onDisconnect()
            discord.kord.onReady()
            discord.kord.onMessageCreate()
            discord.kord.onMessageEdit()
            discord.kord.onGuildMemberJoin()
            discord.kord.onGuildMemberLeave()
            discord.kord.onGuildJoin()

            // Login to Discord and register events
            discord.kord.login()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun launchServer() {
        val server = GlobalContext.injectable<WebhookServer>()
        logger.info("Launching webhook server...")

        GlobalScope.launch {
            server.start()
        }
    }
}
