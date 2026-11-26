pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Pilot"

include(
    ":app",
    ":core:common",
    ":core:ui",
    ":core:voice",
    ":core:ai",
    ":core:navigation",
    ":core:bluetooth",
    ":core:notification",
    ":data",
    ":feature:assistant",
    ":feature:media",
    ":feature:call",
    ":feature:bluetooth",
    ":feature:notification",
    ":feature:fatigue",
    ":feature:dashcam",
    ":feature:adas",
    ":feature:settings"
)
