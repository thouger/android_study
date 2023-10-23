#include <jni.h>
#include <string>
#include "../other/other.cpp"

extern "C" JNIEXPORT jstring JNICALL
Java_thouger_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
//    std::string hello = "Hello from C++";
    std::string hello = test();
            printf("%s",hello.c_str());
    return env->NewStringUTF(hello.c_str());
}