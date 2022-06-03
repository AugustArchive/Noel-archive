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

fun kotlinx(module: String, version: String? = Versions.KOTLIN): String
        = "org.jetbrains.kotlinx:kotlinx-$module:$version"

fun koin(module: String): String
        = "io.insert-koin:koin-$module:${Versions.KOIN}"

fun slf4j(module: String): String
        = "org.slf4j:slf4j-$module:${Versions.SLF4J}"

fun ktor(module: String): String
        = "io.ktor:ktor-$module:${Versions.KTOR}"

fun kord(module: String): String
        = "dev.kord:kord-$module:${Versions.KORD}"

fun kmongo(module: String): String
        = "org.litote.kmongo:kmongo-$module:${Versions.KMONGO}"

const val kaml = "com.charleskorn.kaml:kaml:${Versions.KAML}"
const val logback = "ch.qos.logback:logback-classic:${Versions.LOGBACK}"
const val logbackApi = "ch.qos.logback:logback-core:${Versions.LOGBACK}"
const val haru = "dev.floofy.haru:Haru:${Versions.HARU}"
const val aws = "software.amazon.awssdk:s3:${Versions.S3}"
