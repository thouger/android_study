#include "../Utils/mylog.h"

extern "C" JNIEXPORT jstring JNICALL
Java_thouger_study_MainActivity_copyString(JNIEnv* env, jobject thiz, jstring src) {
  const char* srcStr = env->GetStringUTFChars(src, NULL);
  char destStr[128];
  strcpy(destStr, srcStr);
  env->ReleaseStringUTFChars(src, srcStr);
  return env->NewStringUTF(destStr);
}

extern "C" JNIEXPORT jstring JNICALL
Java_thouger_study_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
//    std::string hello = test();
  LOGD("Received string: %s", hello.c_str());
  return env->NewStringUTF(hello.c_str());
}



#define LOG_TAG "JNI_LOG"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
extern "C" {
//声明一个native方法
JNIEXPORT void JNICALL native_callTestttttttttttt(JNIEnv *env, jobject clazz, jstring str) {
  const char *c_str = env->GetStringUTFChars(str, nullptr);
  LOGD("Received string: %s", c_str);

  jclass mainActivityClass = env->FindClass("thouger/study/MainActivity");
  jmethodID testMethod = env->GetMethodID(mainActivityClass, "testttttttttttt", "(Ljava/lang/String;)V");
  jstring message = env->NewStringUTF("Hello from native code!");
  env->CallVoidMethod(clazz, testMethod, message);

  env->ReleaseStringUTFChars(str, c_str);
}

//动态注册
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *) {
  JNIEnv *env = nullptr;
  if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
    return JNI_ERR;
  }

  jclass clazz = env->FindClass("thouger/study/MainActivity");
  if (clazz == nullptr) {
    return JNI_ERR;
  }

  static const JNINativeMethod methods[] = {
      {"callTestttttttttttt", "(Ljava/lang/String;)V", reinterpret_cast<void *>(native_callTestttttttttttt)}
  };
  jint ret = env->RegisterNatives(clazz, methods, sizeof(methods) / sizeof(methods[0]));
  if (ret != JNI_OK) {
    return JNI_ERR;
  }

  return JNI_VERSION_1_6;
}

}  // extern "C"

//extern "C"
//JNIEXPORT void JNICALL
//Java_thouger_study_MainActivity_callTestttttttttttt(JNIEnv *env, jobject thiz, jstring str) {
//  // TODO: implement callTestttttttttttt()
//}