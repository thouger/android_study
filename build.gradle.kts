// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.com.android.application) apply false
    alias(libs.plugins.org.jetbrains.kotlin.android) apply false
    alias(libs.plugins.com.android.library) apply false
}

buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.0")
    }
}

allprojects {
    repositories {
        repositories {
            maven {
                name = "aliyunmaven"
                url = uri("https://maven.aliyun.com/repository/public")
            }
            maven {
                name = "aliyunGoogle"
                url = uri("https://maven.aliyun.com/repository/google")
            }
            gradlePluginPortal()
            google()
            mavenCentral()
        }
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}