import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinSerialization)
    id("java-library")
    id("jacoco")
}

val appGroupId = "com.github.aivanovski"
val appArtifactId = "tests-with-me"

group = appGroupId
version = libs.versions.appVersion

tasks.withType<KotlinCompile> {
    kotlinOptions {
        apiVersion = "1.9"
        languageVersion = "1.9"
        jvmTarget = "11"
    }
}

java {
    withSourcesJar()
    withJavadocJar()

    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.jacocoTestReport {
    reports {
        val coverageDir = File("$buildDir/reports/coverage")
        csv.required.set(true)
        csv.outputLocation.set(File(coverageDir, "coverage.csv"))
        html.required.set(true)
        html.outputLocation.set(coverageDir)
    }

    dependsOn(allprojects.map { it.tasks.named<Test>("test") })
}

tasks.test {
    useJUnitPlatform()
    finalizedBy("jacocoTestReport")
}

dependencies {
    testImplementation(libs.junit.engine)
    testImplementation(libs.kotest.runner)
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.mockk)

    implementation(libs.jproc)
    implementation(libs.kotlinx.json)

    // Yaml
    implementation(libs.jackson.kotlin)
    implementation(libs.jackson.yaml)

    // Arrow
    implementation(libs.arrow.core)
    implementation(libs.arrow.coroutines)
}