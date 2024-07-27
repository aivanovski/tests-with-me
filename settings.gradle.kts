pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "test-with-me"

include(
    ":test-with-me",
    ":android-app",
    ":web-api",
    ":web-backend"
)