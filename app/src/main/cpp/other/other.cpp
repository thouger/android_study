#include <jni.h>
#include <string>

extern "C" JNIEXPORT std::basic_string<char, std::char_traits<char>, std::allocator<char>> JNICALL
Java_thouger_MainActivity_other(
        JNIEnv* env,
        jobject /* this */) {
    std::string other = "other";
    return other;
}

std::string test(){
    std::string other = "other";
    return other;
}