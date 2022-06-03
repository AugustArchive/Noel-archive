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
import dev.floofy.noel.extensions.toKordColor
import dev.floofy.noel.extensions.toSnowflake
import dev.floofy.noel.modules.discord.DiscordModule
import dev.kord.common.annotation.KordPreview
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.entity.channel.NewsChannel
import dev.kord.rest.builder.message.EmbedBuilder
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable
import java.awt.Color as JavaColour

@Serializable
data class WahResponse(
    val link: String
)

class WahJob(private val http: HttpClient, private val discord: DiscordModule): AbstractJob(
    name = "wahs",
    expression = "0 * * * *"
) {
    @OptIn(KordPreview::class)
    override suspend fun execute() {
        val res: WahResponse = http.get("https://some-random-api.ml/img/red_panda")
        val channel = discord.kord.getChannelOf<NewsChannel>("769530999599267871".toSnowflake()) ?: return

        val wahEmbed = EmbedBuilder().apply {
            title = "Red Pandas :D"
            image = res.link
            color = JavaColour.decode("#F494C6").toKordColor()
        }

        channel.createMessage {
            embeds += wahEmbed
        }.publish()
    }
}
