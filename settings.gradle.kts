pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "test-with-me"

include(
    ":testswithme-core",
    ":testswithme-android",
    ":testswithme-backend-api",
    ":testswithme-backend",
    ":testswithme-cli",
    ":testswithme-gateway-api"
)