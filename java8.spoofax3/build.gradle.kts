plugins {
    `java-library`
    `maven-publish`
    id("org.metaborg.spoofax.compiler.gradle.language") version("999.9.9-refret-SNAPSHOT") apply false
    id("org.metaborg.spoofax.compiler.gradle.adapter") version("999.9.9-refret-SNAPSHOT") apply false
    id("org.metaborg.spoofax.lwb.compiler.gradle.language") version("999.9.9-refret-SNAPSHOT")
}

group = "org.metaborg"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    maven("https://artifacts.metaborg.org/content/groups/public/")
    mavenCentral()
}

val spoofax2Version: String = System.getProperty("spoofax2Version")
val spoofax2BaselineVersion: String = System.getProperty("spoofax2BaselineVersion")
val spoofax2DevenvVersion: String = System.getProperty("spoofax2DevenvVersion")

ext["spoofax2Version"] = spoofax2Version
ext["spoofax2BaselineVersion"] = spoofax2BaselineVersion
ext["spoofax2DevenvVersion"] = spoofax2DevenvVersion

val javaVersion = JavaLanguageVersion.of(11)

java {
    toolchain.languageVersion.set(javaVersion)
    withSourcesJar()
    withJavadocJar()
}

configure<JavaPluginExtension> {
    toolchain.languageVersion.set(javaVersion)
    withSourcesJar()
    withJavadocJar()
}

val service = project.extensions.getByType<JavaToolchainService>()
val customLauncher = service.launcherFor {
    languageVersion.set(javaVersion)
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Use the org.metaborg:*:2.6.0-SNAPSHOT dependencies instead of the Spoofax 3 dependencies
configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.metaborg.devenv") {
            useTarget("org.metaborg:${requested.name}:${spoofax2Version}")
        }
    }
}