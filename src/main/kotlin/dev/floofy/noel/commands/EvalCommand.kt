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

import dev.floofy.noel.core.annotations.Command
import dev.floofy.noel.core.command.BaseCommand
import dev.floofy.noel.core.command.CommandCategory
import dev.floofy.noel.core.command.CommandContext
import dev.floofy.noel.modules.discord.DiscordModule
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.core.context.GlobalContext
import java.time.Duration
import java.time.Instant
import javax.script.ScriptEngineManager

@Serializable
data class HastebinResult(
    val key: String
)

@Command(
    name = "eval",
    description = "Evaluate some Kotlin code and get a result back :D",
    ownerOnly = true,
    category = CommandCategory.NOEL
)
class EvalCommand(private val http: HttpClient): BaseCommand() {
    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun execute(ctx: CommandContext) {
        val message = ctx.reply(":pencil2: Now evaluating...")
        val script = ctx.args.joinToString(" ")
        val engine = ScriptEngineManager().getEngineByName("kotlin")
        val koin = GlobalContext.get()
        val kord = koin.get<DiscordModule>().kord

        engine.put("this", this)
        engine.put("koin", koin)
        engine.put("discord", kord)

        val time = Instant.now()
        var response: Any? = null
        GlobalScope.launch {
            try {
                response = engine.eval(script)
            } catch (e: Exception) {
                e.message
            }
        }

        val computationTime = Duration.between(time, Instant.now()).toMillis() / 1000.0
        message.delete()

        if (response != null && response.toString().length > 2000) {
            // TODO: use haste.red-panda.red instead of hastebin :<
            val res: HastebinResult = http.post("https://hastebin.com/documents") {
                body = response.toString()
            }

            ctx.reply("<:noelHug:815113851133624342> Result was too long, so I uploaded it to hastebin: <https://hastebin.com/${res.key}.kotlin>")
            return
        }

        ctx.reply {
            title = "<:noelHug:815113851133624342> Evaluation Result"
            description = "```kotlin\n${response ?: "// No results."}```"
            footer {
                text = "⏱ ${"%.2f".format(computationTime)}ms"
                icon = "https://cdn.floofy.dev/images/trans.png"
            }
        }
    }
}
