/*
 * Copyright (c) 2020, nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

object PluginVersions {
    const val bintray = "1.8.5"
    const val detekt = "1.8.0"
    const val dokka = "0.10.1"
    const val kotlin = "1.3.72"
    const val ktlint = "9.2.1"
    const val vplugin = "3.0.3"
}

object ArtifactVersions {
    const val assertJ = "3.15.0"
    const val jupiter = "5.6.1"
    const val mockk = "1.9.3"
    const val slf4j = "1.7.0"
    const val slf4jKext = "1.1.1"
    const val slf4jTest = "1.2.0"
}

object ToolVersions {
    const val ktlint = "0.36.0"
    const val jacoco = "0.8.5"
}

object Dependencies {
    val plugins = mapOf(
        "com.github.nwillc.vplugin" to PluginVersions.vplugin,
        "com.jfrog.bintray" to PluginVersions.bintray,
        "io.gitlab.arturbosch.detekt" to PluginVersions.detekt,
        "org.jetbrains.dokka" to PluginVersions.dokka,
        "org.jetbrains.kotlin.jvm" to PluginVersions.kotlin,
        "org.jlleitschuh.gradle.ktlint" to PluginVersions.ktlint
    )
    val artifacts = mapOf(
        "com.github.nwillc:slf4jkext" to ArtifactVersions.slf4jKext,
        "io.mockk:mockk" to ArtifactVersions.mockk,
        "org.assertj:assertj-core" to ArtifactVersions.assertJ,
        "org.jetbrains.kotlin:kotlin-stdlib-jdk8" to PluginVersions.kotlin,
        "org.junit.jupiter:junit-jupiter" to ArtifactVersions.jupiter,
        "org.slf4j:slf4j-api" to ArtifactVersions.slf4j,
        "uk.org.lidalia:slf4j-test" to ArtifactVersions.slf4jTest
    )

    fun plugins(vararg keys: String, block: (Pair<String, String>) -> Unit) =
        keys
            .map { it to (plugins[it] ?: error("No plugin $it registered in Dependencies.")) }
            .forEach {
                block(it)
            }

    fun artifacts(vararg keys: String, block: (String) -> Unit) =
        keys
            .map { it to (Dependencies.artifacts[it] ?: error("No artifact $it registered in Dependencies.")) }
            .forEach { (n, v) ->
                block("$n:$v")
            }
}
