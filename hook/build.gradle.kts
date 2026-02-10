plugins {
    alias(libs.plugins.com.android.application)
}

android {
    namespace = "com.hook"
    compileSdk = 34

    // 签名配置
    signingConfigs {
        create("release") {
            storeFile = file("../../keystore")
            storePassword = "As228668*"
            keyAlias = "key0"
            keyPassword = "As228668*"
        }
        getByName("debug") {
            storeFile = file("../../keystore")
            storePassword = "As228668*"
            keyAlias = "key0"
            keyPassword = "As228668*"
        }
    }

    defaultConfig {
        applicationId = "com.hook"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // NDK配置
        ndk {
            // 指定支持的ABI架构
            abiFilters.addAll(listOf("arm64-v8a", "armeabi-v7a", "x86", "x86_64"))
        }

        // CMake配置
        externalNativeBuild {
            cmake {
                // CMake编译参数
                cppFlags.addAll(listOf("-std=c++17", "-Wall", "-Wextra"))
                // 传递给CMake的参数
                arguments.addAll(listOf(
                    "-DANDROID_STL=c++_shared",
                    "-DANDROID_PLATFORM=android-30"
                ))
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }

    // 配置Native库打包
    packaging {
        jniLibs {
            // 确保 native 库被正确打包
            useLegacyPackaging = false
        }
    }

    // 配置CMake路径
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
}

dependencies {
    compileOnly("de.robv.android.xposed:api:82")
    // 不要导入源码，这会导致idea无法索引文件，从而让语法提示失效

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}