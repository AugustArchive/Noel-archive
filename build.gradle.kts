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

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.text.SimpleDateFormat
import dev.floofy.gradle.Version
import java.util.Date

plugins {
    id("com.github.johnrengelman.shadow") version Versions.SHADOW
    kotlin("plugin.serialization") version Versions.KOTLIN
    id("com.diffplug.spotless") version Versions.SPOTLESS
    kotlin("jvm") version Versions.KOTLIN
    application
}

val current = Version(1, 5, 4)

group = "dev.floofy"
version = current.string()

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("https://maven.floofy.dev/repo/releases")
    }
}

dependencies {
    // Kotlin
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib"))
    runtimeOnly(kotlin("scripting-jsr223"))

    // Coroutines (for commands)
    implementation(kotlinx("coroutines-core", "1.5.0"))
    api(kotlinx("coroutines-reactor", "1.5.0"))

    // Serialization
    implementation(kotlinx("serialization-json", "1.2.2"))
    api(kotlinx("serialization-core", "1.2.2"))

    // Koin (Dependency Injection)
    implementation(koin("core-ext"))
    implementation(koin("logger-slf4j"))

    // Logging
    implementation(logbackApi)
    implementation(logback)
    api(slf4j("api"))

    // Configuration (YAML)
    implementation(kaml)

    // Scheduling (Haru)
    implementation(haru)

    // Discord
    implementation(kord("core"))

    // HTTP client
    implementation(ktor("client-okhttp"))

    // Ktor server (for webhooks)
    implementation(ktor("serialization"))
    implementation(ktor("server-netty"))
    implementation(ktor("client-json"))
    implementation(ktor("server-core"))

    // Database (MongoDB)
    implementation(kmongo("coroutine-serialization"))
    implementation(kmongo("id"))

    // Kubernetes Client
    implementation("io.kubernetes:client-java:13.0.0")
}

tasks.register("generateMetadata") {
    val path = sourceSets["main"].resources.srcDirs.first()
    if (!file(path).exists()) path.mkdirs()

    val date = Date()
    val formatter = SimpleDateFormat("MMM dd, yyyy @ hh:mm:ss")

    file("$path/metadata.properties").writeText("""built.at = ${formatter.format(date)}
app.version = ${current.string()}
app.commit  = ${current.commit()}
""".trimIndent())
}

spotless {
    kotlin {
        trimTrailingWhitespace()
        licenseHeaderFile("${rootProject.projectDir}/assets/HEADER")
        endWithNewline()

        // We can't use the .editorconfig file, so we'll have to specify it here
        // issue: https://github.com/diffplug/spotless/issues/142

        // ktlint 0.35.0 (default for Spotless) doesn't support trailing commas
        ktlint("0.40.0")
            .userData(mapOf(
                "no-consecutive-blank-lines" to "true",
                "no-unit-return" to "true",
                "disabled_rules" to "no-wildcard-imports,colon-spacing",
                "indent_size" to "4"
            ))
    }
}

application {
    mainClass.set("dev.floofy.noel.Bootstrap")
    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
        kotlinOptions.javaParameters = true
        kotlinOptions.freeCompilerArgs += listOf(
            "-Xopt-in=kotlin.RequiresOptIn"
        )
    }

    named<ShadowJar>("shadowJar") {
        archiveFileName.set("Noel.jar")
        mergeServiceFiles()

        manifest {
            attributes(mapOf(
                "Manifest-Version" to "1.0.0",
                "Main-Class" to "dev.floofy.noel.Bootstrap"
            ))
        }
    }

    build {
        dependsOn("generateMetadata")
        dependsOn(spotlessApply)
        dependsOn(shadowJar)
    }
}
