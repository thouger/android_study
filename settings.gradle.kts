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

    repositories {
        maven {
            name = "xposed"
            url = uri("https://api.xposed.info/")
        }
    }
}

rootProject.name = "android_study"
include(":app")
//include(":hook")
include(":nativeLib")
include(":hook")
