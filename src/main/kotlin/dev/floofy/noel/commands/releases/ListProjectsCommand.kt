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
import dev.floofy.noel.core.command.BaseCommand
import dev.floofy.noel.core.command.CommandContext
import dev.floofy.noel.data.Config
import dev.floofy.noel.extensions.firstUpper
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ListProjectsResult(
    val projects: List<ProjectResult>,

    @SerialName("total_pages")
    val totalPages: Int
)

@Serializable
data class ProjectResult(
    val id: String,
    val provider: String,
    val name: String,
    val url: String,

    @SerialName("email_notification")
    val emailNotification: String
)

@Command(
    name = "list-projects",
    aliases = ["projects"],
    onlyIn = ["382725233695522816"],
    description = "Lists all the projects available for showing releases."
)
class ListProjectsCommand(private val http: HttpClient, private val config: Config): BaseCommand() {
    override suspend fun execute(ctx: CommandContext) {
        if (config.releasesToken == null) {
            ctx.reply(":pensive: No API key in config, did I do it wrong? ;w;")
            return
        }

        val res = http.get<ListProjectsResult>("https://api.newreleases.io/v1/projects") {
            header("X-Key", config.releasesToken)
        }

        val embed = ctx.asEmbed().apply {
            description = buildString {
                appendLine("**You will see the following projects in <#873771638146105364>**:")
                for (project in res.projects) {
                    appendLine("• [**${project.name}**](${project.url}) from **${project.provider.firstUpper()}**")
                }
            }
        }

        ctx.reply(embed)
    }
}
