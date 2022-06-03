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

package dev.floofy.noel.kotlin

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Provides a wrapper to created a delegated property for [org.slf4j.Logger],
 * which is read-only.
 *
 * @param baseCls The base class to delegate the property from
 */
class DelegatedSlf4jLogger(private val baseCls: Class<*>): ReadOnlyProperty<Any?, Logger> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Logger =
        LoggerFactory.getLogger(baseCls)
}

/**
 * Inline function to create a [DelegatedSlf4jLogger] instance
 * @param baseCls The base class to delegate the property from
 * @returns The instance of [DelegatedSlf4jLogger]
 */
fun logging(baseCls: Class<*>): DelegatedSlf4jLogger =
    DelegatedSlf4jLogger(baseCls)
