pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "test-with-me"

include(
    ":tests-with-me",
    ":android-app",
    ":web-api",
    ":web-backend",
    ":cli-app",
    ":driver-server-api"
)