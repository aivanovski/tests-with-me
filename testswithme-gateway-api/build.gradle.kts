import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinSerialization)
    id("java-library")
}

val appGroupId = "com.github.aivanovski"

group = appGroupId
version = libs.versions.appVersion

tasks.withType<KotlinCompile> {
    kotlinOptions {
        apiVersion = "1.5"
        languageVersion = "1.5"
        jvmTarget = "11"
    }
}

java {
    withSourcesJar()
    withJavadocJar()

    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    testImplementation(libs.junit.engine)
    testImplementation(libs.kotest.runner)
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.mockk)

    implementation(libs.kotlinx.json)
}