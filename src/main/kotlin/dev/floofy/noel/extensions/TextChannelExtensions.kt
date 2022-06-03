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

package dev.floofy.noel.extensions

import dev.kord.common.entity.OverwriteType
import dev.kord.common.entity.Snowflake
import dev.kord.core.cache.data.PermissionOverwriteData
import dev.kord.core.entity.PermissionOverwrite
import dev.kord.core.entity.channel.TextChannel
import dev.kord.rest.builder.channel.PermissionOverwriteBuilder
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
fun TextChannel.addOverwrite(id: Snowflake, type: OverwriteType, block: PermissionOverwriteBuilder.() -> Unit, reason: String? = null): PermissionOverwrite {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val builder = PermissionOverwriteBuilder(type, id).apply(block)
    val overwrite = builder.toOverwrite()

    return PermissionOverwrite(
        data = PermissionOverwriteData(
            allowed = overwrite.allow,
            denied = overwrite.deny,
            id = id,
            type = type
        )
    )
}
