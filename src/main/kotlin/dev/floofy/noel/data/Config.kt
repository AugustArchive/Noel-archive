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

package dev.floofy.noel.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val helperToken: String? = null,
    val releasesToken: String? = null,
    val autorole: List<Autorole>, // autoroles :D
    val guilds: List<GuildConfig>, // list of whitelisted guilds
    val mongo: MongoConfig, // mongo config
    val token: String, // discord token
    val s3: S3Config? = null,

    @SerialName("status_pages")
    val statusPages: Map<String, InstatusConfig>? = null
)

@Serializable
data class Autorole(
    val botRole: String,
    val guild: String,
    val role: String
)

@Serializable
data class GuildConfig(
    val welcomeChannelID: String? = null,
    val welcome: Boolean = false,
    val id: String
)
