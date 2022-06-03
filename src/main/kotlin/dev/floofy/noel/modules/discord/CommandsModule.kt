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

package dev.floofy.noel.modules.discord

import dev.floofy.noel.core.annotations.OnInit
import dev.floofy.noel.core.command.BaseCommand
import dev.floofy.noel.core.command.CommandContext
import dev.floofy.noel.kotlin.logging
import dev.floofy.noel.modules.mongo.MongoModule
import dev.floofy.noel.modules.mongo.collections.User
import dev.kord.common.entity.ChannelType
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.message.MessageCreateEvent
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext
import org.litote.kmongo.eq
import java.lang.reflect.Method
import kotlin.coroutines.suspendCoroutine

private fun <T> List<T>.asPair(): Pair<T, List<T>> = Pair(first(), drop(1))

@OptIn(DelicateCoroutinesApi::class)
fun Method.invokeSuspend(obj: Any, vararg args: Any?): Any? {
    var result: Any? = null
    GlobalScope.launch {
        suspendCoroutine {
            result = invoke(obj, *args, it)
        }
    }

    return result
}

@OptIn(DelicateCoroutinesApi::class)
class CommandsModule(private val discord: DiscordModule, private val mongo: MongoModule) {
    private val owners = listOf("280158289667555328", "302604426781261824", "743701282790834247")
    private val logger by logging(this::class.java)
    val commands: MutableMap<String, BaseCommand> = mutableMapOf()

    init {
        val koin = GlobalContext.get()
        val all = koin.getAll<BaseCommand>()

        logger.info("Found ${all.size} commands to register. :3")
        for (cmd in all) {
            if (cmd.metadata == null) {
                logger.warn("Skipping on command class ${cmd::class.simpleName ?: "anonymous-command"} due to no @Command metadata.")
                continue
            }

            logger.info("Registered command ${cmd.metadata!!.name} under category ${cmd.metadata!!.category.cat} :D")
            commands[cmd.metadata!!.name] = cmd

            // Check for @OnInit
            val methods = cmd::class.java.methods.filter {
                it.isAnnotationPresent(OnInit::class.java)
            }

            if (methods.isNotEmpty()) {
                val meth = methods.first()
                meth.invokeSuspend(cmd)
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun onCommandReceive(event: MessageCreateEvent) {
        // Check if the author isn't a webhook (returns `null`)
        if (event.message.author == null)
            return

        // Check if the author is a bot
        if (event.message.author!!.isBot)
            return

        val guild = event.getGuild() ?: return
        val collection = mongo.db.getCollection<User>("users")

        // Birthdays only occur on my personal server
        // and I don't plan to have it on Noelware or Arisu servers.
        if (guild.id.asString == "382725233695522816") {
            val document = collection.findOne(User::id eq event.message.author!!.id.asString)
            if (document == null)
                collection.save(User(id = event.message.author!!.id.asString, birthday = null))
        }

        // Sandboxing commands only belongs in my testing guild
        if (guild.id.asString == "743698927039283201") {
            val channel = event.message.channel.asChannel()
            if (channel.type == ChannelType.GuildText && (channel as TextChannel).name.matches("sandbox-\\d{15,21}".toRegex())) {
                val (name, args) = event.message.content.split("\\s+".toRegex()).asPair()
                when (name) {
                    "note", "add-note" -> return commands["add-note"]!!.execute(CommandContext(event, args))
                    "unsandbox", "usb" -> return commands["unsandbox"]!!.execute(CommandContext(event, args))
                    "verify" -> return commands["verify"]!!.execute(CommandContext(event, args))
                    "delete" -> return commands["delete"]!!.execute(CommandContext(event, args))
                }
            }
        }

        val selfMember = guild.members.filter {
            it.id == discord.kord.selfId
        }.firstOrNull() ?: return

        val wasMentioned = event.message.content.startsWith("<@${discord.kord.selfId}>") || event.message.content.startsWith("<@!${discord.kord.selfId}>")
        val prefixLen = if (wasMentioned) selfMember.mention.length + 1 else "noel ".length

        if (!event.message.content.startsWith("noel ") && !wasMentioned)
            return

        if (wasMentioned && !event.message.content.contains(" "))
            return

        val content = event.message.content.substring(prefixLen).trim()
        val (name, args) = content.split("\\s+".toRegex()).asPair()
        val command = name.lowercase()
        val context = CommandContext(event, args)

        val found = commands[command]
            ?: commands.values.firstOrNull { it.metadata!!.aliases.contains(command) }
            ?: return

        if (found.metadata!!.ownerOnly && !owners.contains(event.message.author!!.id.asString))
            return

        if (found.metadata!!.onlyIn.isNotEmpty() && !found.metadata!!.onlyIn.contains(event.guildId!!.asString))
            return

        when (command) {
            "verify", "usb", "unsandbox", "note", "add-note", "delete" -> {
                context.reply("Cannot use sandbox commands in an unsandboxed environment.")
                return
            }
        }

        try {
            GlobalScope.launch {
                found.execute(context)
            }
        } catch (e: Exception) {
            logger.error("Unable to run command $command:", e.cause ?: e)
            event.message.channel.createMessage("<:noelThisIsFine:815114008582815764> Unable to run command **$command** :<")

            return
        }
    }
}
