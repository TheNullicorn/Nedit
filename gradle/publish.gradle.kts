apply(plugin = "signing")
apply(plugin = "maven-publish")
apply(plugin = "org.jetbrains.dokka")

///////////////////////////////////////////////////////////////////////////
// Read Project's Configuration
///////////////////////////////////////////////////////////////////////////

// Set in "../gradle.properties".
val authorUrl = project.extra["author.url"] as String
val projectName = project.extra["name"] as String

// Both values should be set in "~/.gradle/gradle.properties".
val ossrhUsername = project.extra["ossrhUsername"] as String
val ossrhPassword = project.extra["ossrhPassword"] as String

///////////////////////////////////////////////////////////////////////////
// Documentation HTML Jar
///////////////////////////////////////////////////////////////////////////

// Include jar with the lib's KDoc HTML.
val kdocJar by tasks.registering(Jar::class) {
    val htmlTask = tasks["dokkaHtml"]
    dependsOn(htmlTask)

    // Create the Jar from the generated HTML files.
    from(htmlTask)
    archiveClassifier.set("javadoc")
}

///////////////////////////////////////////////////////////////////////////
// Artifact Signing
///////////////////////////////////////////////////////////////////////////

// Sign all of our artifacts for Nexus.
configure<SigningExtension> {
    val publishing = extensions["publishing"] as PublishingExtension
    sign(publishing.publications)
}

///////////////////////////////////////////////////////////////////////////
// Publishing to OSSRH & Maven Central
///////////////////////////////////////////////////////////////////////////

configure<PublishingExtension> {
    publications {
        create<MavenPublication>("ossrh") {
            from(components["java"])
            artifact(kdocJar)

            // Add extra metadata for the JVM jar's pom.xml.
            pom {
                val projectUrl = "$authorUrl/$projectName"

                url.set("https://$projectUrl")
                name.set(projectName)
                description.set(project.description)

                developers {
                    developer {
                        name.set("TheNullicorn")
                        email.set("bennullicorn@gmail.com")
                    }
                }

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/mit-license.php")
                    }
                }

                scm {
                    url.set("https://$projectUrl/tree/main")
                    connection.set("scm:git:git://$projectUrl.git")
                    developerConnection.set("scm:git:ssh://$projectUrl.git")
                }
            }
        }
    }

    repositories {
        maven {
            name = "ossrh"

            val repoId = if (version.toString().endsWith("SNAPSHOT")) "snapshot" else "staging"
            url = uri(project.extra["repo.$repoId.url"] as String)

            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }
}