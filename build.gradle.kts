@file:Suppress("UnstableApiUsage")

import org.gradle.jvm.tasks.Jar
import java.net.URI

plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "io.github.rokuosan"
version = "0.0.1"

repositories {
    mavenCentral()

    maven {
        name = "papermc-repo"
        url = URI.create("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        url = URI.create("https://oss.sonatype.org/content/groups/public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("io.github.rokuosan.treasurehunt.TreasureHuntKt")
}

tasks.withType(ProcessResources::class) {
    mapOf(
        "version" to version,
    ).let { props ->
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }
}

tasks.withType(Jar::class){
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
//    from(configurations.compileClasspath.map { config -> config.map { if (it.isDirectory) it else zipTree(it) } })
}
