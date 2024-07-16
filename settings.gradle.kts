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
        gradlePluginPortal()
        maven("https://jitpack.io")
        jcenter()
    }
}

rootProject.name = "PlayAccess3"
include(":app")
include (":feature:actionsconfigurator")
include (":common")

include(":feature:gamesconfigurator")
include(":feature:actionsrecognizer")
include(":feature:accessibilityservice")
