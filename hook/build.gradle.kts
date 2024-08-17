plugins {
    id("com.android.application")
}

android {
    namespace = "thouger.hook"
    compileSdk = 33

    defaultConfig {
        applicationId = "thouger.hook"
        minSdk = 29
        targetSdk = 29
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        externalNativeBuild {
            cmake {

            }
        }
        ndk {
            abiFilters.add("armeabi-v7a")
            abiFilters.add("arm64-v8a")
//            abiFilters.add("x86")
//            abiFilters.add("x86_64")
        }
    }

    sourceSets {
        getByName("main").apply {
            jniLibs.srcDirs("src/main/libs")
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

    externalNativeBuild {
        cmake {
            path = file("src/main/CMakeLists.txt")
            version = "3.22.1"
        }
    }
    buildFeatures {
        viewBinding = true
    }
    ndkVersion = "25.2.9519653"
}

dependencies {
    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.lsposed.hiddenapibypass:hiddenapibypass:4.3")
    implementation(project(mapOf("path" to ":app")))

    compileOnly("de.robv.android.xposed:api:82")
    // 不要导入源码，这会导致idea无法索引文件，从而让语法提示失效

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}