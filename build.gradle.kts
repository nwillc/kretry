import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val publicationName = "maven"

val assertJVersion: String by project
val jupiterVersion: String by project
val ktlintToolVersion: String by project
val mockkVersion: String by project
val slf4jApiVersion: String by project
val slf4jKextVersion: String by project
val slf4jTestVersion: String by project

plugins {
    kotlin("jvm") version "1.3.61"
    jacoco
    `maven-publish`
    id("org.jetbrains.dokka") version "0.10.0"
    id("com.github.nwillc.vplugin") version "3.0.1"
    id("com.jfrog.bintray") version "1.8.4"
    id("io.gitlab.arturbosch.detekt") version "1.2.0"
    id("org.jlleitschuh.gradle.ktlint") version "9.1.1"
}

group = "com.github.nwillc"
version = "0.2.2-SNAPSHOT"

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
        .forEach { testRuntimeOnly(it) }
}

ktlint {
    version.set(ktlintToolVersion)
}

val sourcesJar by tasks.registering(Jar::class) {
    classifier = "sources"
    from(sourceSets["main"].allSource)
}

val javadocJar by tasks.registering(Jar::class) {
    dependsOn("dokka")
    classifier = "javadoc"
    from("$buildDir/dokka")
}

publishing {
    publications {
        create<MavenPublication>(publicationName) {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(components["java"])
            artifact(sourcesJar.get())
            artifact(javadocJar.get())
        }
    }
}

bintray {
    user = System.getenv("BINTRAY_USER")
    key = System.getenv("BINTRAY_API_KEY")
    dryRun = false
    publish = true
    setPublications(publicationName)
    pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
        repo = publicationName
        name = project.name
        desc = "Guava retrying inspired Kotlin library."
        websiteUrl = "https://github.com/nwillc/${project.name}"
        issueTrackerUrl = "https://github.com/nwillc/${project.name}/issues"
        vcsUrl = "https://github.com/nwillc/${project.name}.git"
        version.vcsTag = "v${project.version}"
        setLicenses("ISC")
        setLabels("kotlin", "retry")
        publicDownloadNumbers = true
    })
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
    named<Jar>("jar") {
        manifest.attributes["Automatic-Module-Name"] = "${project.group}.${project.name}"
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
    withType<DokkaTask> {
        outputFormat = "html"
        outputDirectory = "$buildDir/dokka"
    }
    withType<Wrapper> {
        gradleVersion = "5.6.2"
    }
    withType<GenerateMavenPom> {
        destination = file("$buildDir/libs/${project.name}-${project.version}.pom")
    }
    withType<BintrayUploadTask> {
        onlyIf {
            if (project.version.toString().contains('-')) {
                logger.lifecycle("Version v${project.version} is not a release version - skipping upload.")
                false
            } else {
                true
            }
        }
    }
}
