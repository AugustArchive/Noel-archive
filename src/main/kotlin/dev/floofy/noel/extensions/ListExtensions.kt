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

fun <T> List<T>.sort(block: (T, T) -> Int): List<T> {
    val comparator: Comparator<T> = Comparator<T> { o1, o2 -> block(o1, o2) }
    return this.sortedWith(comparator)
}

inline fun <S, T> Iterable<T>.reduce(operation: (acc: S, T) -> S, initialValue: S): S {
    val iterator = this.iterator()
    if (!iterator.hasNext()) return initialValue

    var acc: Any? = initialValue
    while (iterator.hasNext()) {
        @Suppress("UNCHECKED_CAST")
        acc = operation(acc as S, iterator.next())
    }

    @Suppress("UNCHECKED_CAST")
    return acc as S
}
