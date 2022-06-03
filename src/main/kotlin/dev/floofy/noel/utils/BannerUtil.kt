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

package dev.floofy.noel.utils

import dev.floofy.noel.NoelInfo
import java.io.File

object BannerUtil {
    fun show() {
        val file = File("./assets/banner.txt").readText(Charsets.UTF_8)
        val lines = file.split("\n")

        for (line in lines) {
            val text = line
                .replace("{{VERSION}}", NoelInfo.VERSION)
                .replace("{{COMMIT_HASH}}", NoelInfo.COMMIT_HASH)
                .replace("{{BUILT_AT}}", NoelInfo.BUILT_AT)
                .replace("{{KOTLIN_VER}}", KotlinVersion.CURRENT.toString())
                .replace("{{JVM_VER}}", System.getProperty("java.version") ?: "Unknown")

            println(text)
        }
    }
}
