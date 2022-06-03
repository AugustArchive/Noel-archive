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

package dev.floofy.noel.commands.releases

import dev.floofy.noel.core.annotations.Command
import dev.floofy.noel.core.annotations.OnInit
import dev.floofy.noel.core.command.BaseCommand
import dev.floofy.noel.core.command.CommandContext
import dev.floofy.noel.data.Config
import dev.floofy.noel.kotlin.logging
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable

@Serializable
data class RetrieveProvidersResult(
    val providers: List<String>
)

@Serializable
data class PostProjectBody(
    val provider: String,
    val name: String
)

@Command(
    name = "add-project",
    description = "Adds a project to newreleases.io",
    aliases = ["ap", "release"],
    ownerOnly = true,
    onlyIn = ["382725233695522816"]
)
class AddProjectCommand(private val http: HttpClient, private val config: Config): BaseCommand() {
    private var providers: List<String> = listOf()
    private val logger by logging(this::class.java)

    override suspend fun execute(ctx: CommandContext) {
        if (config.releasesToken == null) {
            ctx.reply(":pensive: Cannot retrieve providers without an API key.")
            return
        }

        if (ctx.args.isEmpty()) {
            ctx.reply {
                title = "Providers"
                description = buildString {
                    appendLine("You didn't provide a provider to use, so I'll list them for you...")
                    appendLine()

                    for (provider in providers)
                        appendLine("• **$provider** (`noel release $provider <repo/url>`)")
                }
            }

            return
        }

        val repo = try {
            ctx.args[1]
        } catch (e: IndexOutOfBoundsException) {
            null
        }

        if (repo == null) {
            ctx.reply(":pensive: Repository was not provided, please add one. :(")
            return
        }

        val message = ctx.reply(":pencil2: **Adding $repo -> ${ctx.args[0]} to the list...**")
        val res = try {
            http.post<ProjectResult>("https://api.newreleases.io/v1/projects") {
                header("Content-Type", "application/json")
                header("X-Key", config.releasesToken)

                body = PostProjectBody(provider = ctx.args[0], repo)
            }
        } catch (ex: Exception) {
            null
        }

        if (res == null) {
            message.delete()
            ctx.reply(":pencil2: **Unable to add project.** :(")

            return
        }

        message.delete()
        ctx.reply("<:success:464708611260678145> **Added ${res.name} -> ${res.provider}**.")
    }

    @OnInit
    @Suppress("UNUSED")
    suspend fun init() {
        logger.info("Retrieving all providers into cache...")
        if (config.releasesToken == null) {
            logger.warn("Cannot retrieve providers without an API key.")
            return
        }

        val res = http.get<RetrieveProvidersResult>("https://api.newreleases.io/v1/providers") {
            header("X-Key", config.releasesToken)
        }

        logger.info("Found ${res.providers.size} providers.")
        providers = res.providers
    }
}
