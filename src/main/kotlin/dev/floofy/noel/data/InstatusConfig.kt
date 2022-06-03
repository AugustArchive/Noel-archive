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

import kotlinx.serialization.Serializable

/**
 * Represents the configuration for monitoring pods and sends the status
 * to instatus. Possible configuration is like:
 *
 * ```yml
 * statuspages:
 *    - token: <some token>
 *      components:
 *         nino-prod: <component id> # The pod will match by the name without the randomized string
 * ```
 */
@Serializable
data class InstatusConfig(
    val token: String,
    val components: Map<String, String>
)
