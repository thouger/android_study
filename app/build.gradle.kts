import java.util.Locale

plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
}

android {
    namespace = "thouger.study"
    compileSdk = 33

    defaultConfig {
        applicationId = "thouger.study"
        minSdk = 29
        //noinspection ExpiredTargetSdkVersion
        targetSdk = 29
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        externalNativeBuild {
            cmake {
                cppFlags += "-std=c++20"
            }
        }
        ndk {
//            abiFilters.add("armeabi-v7a")
            abiFilters.add("arm64-v8a")
        }
    }

    sourceSets {
        getByName("main").apply {
            jniLibs.srcDirs("src/libs")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    externalNativeBuild {
        cmake {
            // 设置 CMakeLists.txt 的路径和版本号
            path = file("src/main/CMakeLists.txt")
            version = "3.22.1"
        }
    }
    buildFeatures {
        prefab = true
        viewBinding = true
    }
    ndkVersion = "25.2.9519653"
}

task("buildRust") {
    val rustAbi = "arm64-v8a"

    doLast {
        if (System.getProperty("os.name").lowercase(Locale.getDefault()).contains("win")) {
            project.exec {
                workingDir = File(project.projectDir, "rs")
                commandLine("cmd", "/c", "build_rs.bat", rustAbi, "--release")
            }
        } else {
            project.exec {
                workingDir = File(project.projectDir, "rs")
                commandLine("sh", "build_rs.sh", rustAbi, "--release")
            }
        }
    }
}

dependencies {
    implementation("org.lsposed.lsplant:lsplant-standalone:5.2")
    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.lsposed.hiddenapibypass:hiddenapibypass:4.3")
    implementation(project(":nativeLib"))


    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}