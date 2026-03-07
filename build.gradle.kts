import org.jetbrains.intellij.platform.gradle.tasks.RunIdeTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.nio.file.Files
import java.nio.file.Paths

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.3.0"
    id("org.jetbrains.intellij.platform") version "2.11.0"
}

group = "com.alaje.intellijplugins"
version = "1.4.6"

repositories {
    mavenCentral()
    intellijPlatform{
        defaultRepositories()
    }
}

dependencies {
    implementation(files("libs/svgSalamander-1.1.4.jar"))
    intellijPlatform {
        intellijIdeaUltimate("2025.3")
        // Must be compatible with IntelliJ version
        plugins("Dart:503.0.0")
        bundledPlugins("com.intellij.java", "org.jetbrains.kotlin")
        pluginVerifier()
    }
}

intellijPlatform {
    buildSearchableOptions = false

    pluginConfiguration {
        ideaVersion {
            sinceBuild = "253.28294"
            untilBuild = "253.*"
        }
        val changeLogFile = Paths.get("CHANGELOG.md")
        changeNotes = Files.readString(changeLogFile)
    }
    publishing {
        token = System.getenv("PUBLISH_TOKEN")
    }
    signing {
        certificateChain = System.getenv("CERTIFICATE_CHAIN")
        privateKey = System.getenv("PRIVATE_KEY")
        password = System.getenv("PRIVATE_KEY_PASSWORD")
    }
}

tasks {
    // Java 21 is required since 2024.2
    val jvmVersion = "21"
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = jvmVersion
        targetCompatibility = jvmVersion
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }
}

tasks.named<RunIdeTask>("runIde") {
    jvmArgumentProviders += CommandLineArgumentProvider {
        listOf("-Didea.kotlin.plugin.use.k2=true")
    }
}