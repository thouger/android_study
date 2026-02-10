#include <jni.h>
#include <string>
#include <android/log.h>

#define LOG_TAG "Algorithm"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

// Frida GUM example function declaration
extern "C" int frida_gum_example_main(int argc, char *argv[]);

extern "C" JNIEXPORT jlong JNICALL
Java_com_android_study_MainActivity_calculateFibonacci(JNIEnv *env, jobject thiz, jint n) {
    if (n <= 1) return n;

    long long a = 0, b = 1, c;
    for (int i = 2; i <= n; i++) {
        c = a + b;
        a = b;
        b = c;
    }

    LOGI("Fibonacci(%d) = %lld", n, b);
    return (jlong)b;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_android_study_MainActivity_isPrime(JNIEnv *env, jobject thiz, jint number) {
    if (number <= 1) return JNI_FALSE;
    if (number <= 3) return JNI_TRUE;
    if (number % 2 == 0 || number % 3 == 0) return JNI_FALSE;

    for (int i = 5; i * i <= number; i += 6) {
        if (number % i == 0 || number % (i + 2) == 0) {
            return JNI_FALSE;
        }
    }

    LOGI("isPrime(%d) = true", number);
    return JNI_TRUE;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_android_study_MainActivity_runFridaGumExample(JNIEnv *env, jobject thiz) {
    LOGI("Starting Frida GUM example...");

    // Call the Frida GUM example function
    int result = frida_gum_example_main(0, nullptr);

    LOGI("Frida GUM example completed with result: %d", result);
    return (jint)result;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_android_study_MainActivity_nativeCallJavaMethod(JNIEnv *env, jobject thiz, jstring message) {
    LOGI("nativeCallJavaMethod called");

    // Convert Java string to C++ string
    const char *nativeMessage = env->GetStringUTFChars(message, nullptr);
    if (nativeMessage == nullptr) {
        LOGI("Failed to get string from Java");
        return env->NewStringUTF("Error: Failed to get message");
    }

    LOGI("Received message from Java: %s", nativeMessage);

    // Get the class of the calling object (MainActivity)
    jclass mainActivityClass = env->GetObjectClass(thiz);
    if (mainActivityClass == nullptr) {
        LOGI("Failed to get MainActivity class");
        env->ReleaseStringUTFChars(message, nativeMessage);
        return env->NewStringUTF("Error: Failed to get class");
    }

    // Get the method ID of processMessageFromNative
    jmethodID processMethod = env->GetMethodID(mainActivityClass,
                                               "processMessageFromNative",
                                               "(Ljava/lang/String;)Ljava/lang/String;");
    if (processMethod == nullptr) {
        LOGI("Failed to get processMessageFromNative method");
        env->ReleaseStringUTFChars(message, nativeMessage);
        return env->NewStringUTF("Error: Failed to get method");
    }

    // Create a modified message to send to Java
    std::string modifiedMessage = std::string("[Native Modified] ") + nativeMessage;
    jstring jModifiedMessage = env->NewStringUTF(modifiedMessage.c_str());

    // Call the Java method
    LOGI("Calling Java method processMessageFromNative...");
    jstring result = (jstring)env->CallObjectMethod(thiz, processMethod, jModifiedMessage);

    // Check for exceptions
    if (env->ExceptionCheck()) {
        LOGI("Exception occurred while calling Java method");
        env->ExceptionDescribe();
        env->ExceptionClear();
        env->ReleaseStringUTFChars(message, nativeMessage);
        return env->NewStringUTF("Error: Exception in Java method call");
    }

    // Get the result from Java
    const char *resultStr = env->GetStringUTFChars(result, nullptr);
    if (resultStr != nullptr) {
        LOGI("Got result from Java1111111111: %s", resultStr);
        env->ReleaseStringUTFChars(result, resultStr);
    }

    // Clean up
    env->ReleaseStringUTFChars(message, nativeMessage);

    LOGI("nativeCallJavaMethod completed successfully");
    return result;
}