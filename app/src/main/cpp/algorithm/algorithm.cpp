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