import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val assertJVersion: String by project
val jupiterVersion: String by project
val ktlintToolVersion: String by project
val mockkVersion: String by project
val slf4jApiVersion: String by project
val slf4jKextVersion: String by project
val slf4jTestVersion: String by project

plugins {
    kotlin("jvm") version "1.3.50"
    jacoco
    id("com.github.nwillc.vplugin") version "3.0.1"
    id("io.gitlab.arturbosch.detekt") version "1.1.1"
    id("org.jlleitschuh.gradle.ktlint") version "9.0.0"
}

group = "com.github.nwillc"
version = "1.0-SNAPSHOT"

logger.lifecycle("${project.group}.${project.name}@${project.version}")

repositories {
    jcenter()
}

dependencies {
    listOf(
        kotlin("stdlib-jdk8"),
        "org.slf4j:slf4j-api:$slf4jApiVersion",
        "$group:slf4jkext:$slf4jKextVersion"
    )
        .forEach { implementation(it) }

    listOf(
        "io.mockk:mockk:$mockkVersion",
        "org.assertj:assertj-core:$assertJVersion",
        "org.junit.jupiter:junit-jupiter-api:$jupiterVersion",
        "uk.org.lidalia:slf4j-test:$slf4jTestVersion"
    )
        .forEach { testImplementation(it) }

    listOf(
        "org.junit.jupiter:junit-jupiter-engine:$jupiterVersion"
    )
        .forEach { testRuntime(it) }
}

ktlint {
    version.set(ktlintToolVersion)
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
    withType<Test> {
        useJUnitPlatform()
        testLogging {
            showStandardStreams = true
            events("passed", "failed", "skipped")
        }
    }
    withType<JacocoReport> {
        dependsOn("test")
        reports {
            xml.apply {
                isEnabled = true
            }
            html.apply {
                isEnabled = true
            }
        }
    }
    withType<Wrapper> {
        gradleVersion = "5.6.2"
    }
}
