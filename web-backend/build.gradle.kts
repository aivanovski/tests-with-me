import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.shadowjar)
    alias(libs.plugins.jpa)
    id("jacoco")
}

val appGroupId = "com.github.aivanovski.testswithme"

group = appGroupId
version = libs.versions.appVersion.get()

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
        }
    }

    test {
        useJUnitPlatform()
        finalizedBy("jacocoTestReport")
    }

    jacocoTestReport {
        reports {
            val coverageDir = File("$buildDir/reports/coverage")
            csv.required.set(true)
            csv.outputLocation.set(File(coverageDir, "coverage.csv"))
            html.required.set(true)
            html.outputLocation.set(coverageDir)
        }

        dependsOn(allprojects.map { it.tasks.named<Test>("test") })
    }

    named<ShadowJar>("shadowJar") {
        archiveFileName.set("tests-with-me-backend.jar")
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "com.github.aivanovski.testswithme.web.WebAppMainKt"))
        }
    }
}

tasks.classes {
    dependsOn("checkSslCertificates")
}

tasks.register("checkSslCertificates") {
    doLast {
        val devKeystoreFile = File(projectDir, "src/main/resources/keys/dev-keystore.jks")
        if (devKeystoreFile.exists()) {
            project.logger.lifecycle("Dev SSL KeyStore: ${devKeystoreFile.name}")
        } else {
            val message = "Failed to find Dev SSL KeyStore at: $devKeystoreFile"
            project.logger.error(message)
            throw GradleException(message)
        }
    }
}

dependencies {
    testImplementation(libs.junit.engine)
    testImplementation(libs.kotest.runner)
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.mockk)

    implementation(libs.logback)
    implementation(libs.koin)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.json)
    implementation(libs.ktor.serialization.json)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.server.negotiation)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.authjwt)
    implementation(libs.ktor.server.keysore)

    // Database
    implementation(libs.db.h2)
    implementation(libs.db.hibernate)

    // Arrow
    implementation(libs.arrow.core)
    implementation(libs.arrow.coroutines)
    implementation(project(":tests-with-me"))

    // TestsWithMe API
    implementation(project(":web-api"))
}