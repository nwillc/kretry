import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val publicationName = "maven"
val dokkaDir = "$projectDir/docs/dokka"

plugins {
    jacoco
    idea
    `maven-publish`
    Dependencies.plugins.forEach { (n, v) -> id(n) version v }
}

group = "com.github.nwillc"
version = "0.4.3"

logger.lifecycle("${project.group}.${project.name}@${project.version}")

repositories {
    jcenter()
}

dependencies {
    Dependencies.artifacts(
          "io.gitlab.arturbosch.detekt:detekt-cli",
          "io.gitlab.arturbosch.detekt:detekt-formatting"
      ) { detekt(it) }

    Dependencies.artifacts(
        "org.jetbrains.kotlin:kotlin-stdlib-jdk8",
        "org.slf4j:slf4j-api",
        "$group:slf4jkext"
    ) { implementation(it) }

    Dependencies.artifacts(
        "io.mockk:mockk",
        "org.assertj:assertj-core",
        "org.junit.jupiter:junit-jupiter",
        "uk.org.lidalia:slf4j-test"
    ) { testImplementation(it) }
}

detekt {
    toolVersion = PluginVersions.detekt
    reports {
        html.enabled = true
        txt.enabled = true
    }
}

jacoco {
    toolVersion = ToolVersions.jacoco
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

val javadocJar by tasks.registering(Jar::class) {
    dependsOn("dokka")
    archiveClassifier.set("javadoc")
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
        kotlinOptions.freeCompilerArgs += listOf("-Xallow-result-return-type")
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
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
            xml.isEnabled = true
            html.isEnabled = true
        }
    }
    withType<DokkaTask> {
        outputFormat = "html"
        outputDirectory = dokkaDir
        configuration {
            jdkVersion = 8
            includes = listOf("Module.md")
        }
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
