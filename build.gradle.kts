import pl.sg.build.CodeArtifactAccess
import pl.sg.release.VersionUtil

plugins {
    id("java")
    id("maven-publish")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://sg-repository-215372400964.d.codeartifact.eu-central-1.amazonaws.com/maven/sg-repository/")
        credentials {
            username = "aws"
            password = CodeArtifactAccess.getToken()
        }
    }
}

val getCurrentSemver by tasks.creating(Task::class) {
    doLast {
        file(project.rootDir.resolve("current.semver")).appendText(
            VersionUtil.getCurrentVersion(project.rootDir).toString()
        )
    }
}

val getNextSemver by tasks.creating(Task::class) {
    doLast {
        file(project.rootDir.resolve("next.semver")).appendText(
            VersionUtil.getNextVersion(
                project.rootDir,
                System.getenv("LEVEL")
            ).toString()
        )
    }
}

publishing {
    publications {
        create<MavenPublication>("releaseCurrent") {
            groupId = "pl.sg"
            artifactId = "sg-gradle-utils"
            version = VersionUtil.getCurrentVersion(project.rootDir).toString()
            from(components["java"])
        }
    }

    repositories {
        maven {
            url =
                uri("https://sg-repository-215372400964.d.codeartifact.eu-central-1.amazonaws.com/maven/sg-repository/")
            credentials {
                username = "aws"
                password = CodeArtifactAccess.getToken()
            }
        }
    }
}