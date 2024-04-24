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
        maven("https://jitpack.io")
        jcenter()
    }
}

rootProject.name = "SandBoxTest"
include(":app")
include(":Bcore")
include(":android-mirror")
include(":Bcore:black-fake")
include(":Bcore:black-hook")
include(":Bcore:android-mirror")
include(":Bcore:pine-core")
include(":Bcore:pine-xposed")
include(":Bcore:pine-xposed-res")
