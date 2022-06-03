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

package dev.floofy.noel.listeners

import dev.floofy.noel.data.Config
import dev.floofy.noel.extensions.injectable
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.guild.MemberJoinEvent
import dev.kord.core.event.guild.MemberLeaveEvent
import dev.kord.core.on
import kotlinx.coroutines.flow.toList
import org.koin.core.context.GlobalContext
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("dev.floofy.noel.listeners.GuildMemberListener")

fun Kord.onGuildMemberJoin() = on<MemberJoinEvent> {
    val koin = GlobalContext.get()
    val config = koin.get<Config>()

    logger.info("Member ${this.member.displayName} (${this.member.tag}) has joined ${this.guild.asGuild().name} :D")
    val autorole = config.autorole.find {
        it.guild == this.guildId.asString
    } ?: return@on

    val snowflake = if (this.member.isBot) {
        Snowflake(autorole.botRole)
    } else {
        Snowflake(autorole.role)
    }

    this.member.addRole(snowflake, "Autorole on member join :D")

    val guild = config.guilds.find {
        it.id == this.guild.id.asString
    } ?: return@on

    if (guild.welcome && guild.welcomeChannelID != null) {
        val channel = this.guild.channels.toList().find {
            it.id.asString == guild.welcomeChannelID
        } ?: return@on

        val g = this.guild.asGuildOrNull() ?: return@on
        if (channel.type == ChannelType.GuildText) {
            (channel as TextChannel).createMessage("<:noelTransHeart:808820757211119657> Welcome **${this.member.tag}** to **${g.name}** :D")
        }
    }
}

fun Kord.onGuildMemberLeave() = on<MemberLeaveEvent>() {
    logger.info("Member ${this.user.tag} has left ${this.guild.asGuild().name} :(")

    val gu = this.guild.asGuild()
    val config = GlobalContext.injectable<Config>()

    val guild = config.guilds.find {
        it.id == this.guild.id.asString
    } ?: return@on

    if (guild.welcome && guild.welcomeChannelID != null) {
        val channel = this.guild.channels.toList().find {
            it.id.asString == guild.welcomeChannelID
        } ?: return@on

        if (channel.type == ChannelType.GuildText) {
            (channel as TextChannel).createMessage("<:foxCri:835202546498797578> **${this.user.tag}** left **${gu.name}** :c")
        }
    }
}
