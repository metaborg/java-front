pluginManagement {
    repositories {
        mavenLocal()
        maven("https://artifacts.metaborg.org/content/groups/public/")
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "java8-lang-spoofax3"
