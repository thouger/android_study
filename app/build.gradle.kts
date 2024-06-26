import java.util.Locale

plugins {
    id("com.android.application")
    alias(libs.plugins.org.jetbrains.kotlin.android)
}

android {
    namespace = "com.android.study"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.android.study"
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


    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}