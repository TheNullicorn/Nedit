@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    // Publishing to Maven Central
    id("java")
    id("signing")
    id("maven-publish")

    // Documentation
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.dokka)

    // Binary Compatibility
    // Ensures we don't accidentally change the public API in any way that breaks users' code
    alias(libs.plugins.compatibility)
}

group = "me.nullicorn"
version = "2.2.0"
description = property("description") as String

// Must go after group & version or else it won't be able to access them.
apply(from = "gradle/publish.gradle.kts")

repositories {
    mavenLocal()
    maven("https://repo.maven.apache.org/maven2/")
}

dependencies {
    // Use JUnit Jupiter for unit testing
    testImplementation(libs.junit)

    // Use Java syntax instead of Kotlin when documenting method & class signatures with Dokka.
    dokkaHtmlPlugin(libs.dokka.plugin.java)
}

java {
    // Generate a jar with the source code.
    // Required for publishing to Maven Central.
    withSourcesJar()

    // Use Java 8
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

// Source files are encoded using UTF-8
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

// Use JUnit for running tests
tasks.test {
    useJUnitPlatform()
}