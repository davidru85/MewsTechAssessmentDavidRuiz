@file:Suppress("UnstableApiUsage")

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

rootProject.name = "SmartGuestRoom"

include(":app")

include(":core:common")
include(":core:ui")

include(":feature:controls:domain")
include(":feature:controls:data")
include(":feature:controls:presentation")
