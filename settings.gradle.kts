pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}


rootProject.name = "thouger"
include(":app", ":nativeLib")
include(":hook")
include(":nativelib")
include(":lsposed")

buildCache { local { removeUnusedEntriesAfterDays = 1 } }