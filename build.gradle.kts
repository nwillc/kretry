import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val assertJVersion: String by project
val jupiterVersion: String by project
val ktlintToolVersion: String by project
val mockkVersion: String by project

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
        kotlin("stdlib-jdk8")
    )
        .forEach { implementation(it) }

    listOf(
        "org.junit.jupiter:junit-jupiter-api:$jupiterVersion",
        "org.assertj:assertj-core:$assertJVersion",
        "io.mockk:mockk:$mockkVersion"
    )
        .forEach { testImplementation(it) }

    testRuntime("org.junit.jupiter:junit-jupiter-engine:$jupiterVersion")
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
}
