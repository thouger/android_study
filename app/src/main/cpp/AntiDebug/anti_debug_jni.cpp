#include <jni.h>
#include "anti_debug.h"

extern "C" {

// JNI函数：运行所有反调试检测
JNIEXPORT void JNICALL
Java_com_android_study_AntiDebug_runAllChecks(JNIEnv* env, jclass clazz) {
    RunAllAntiDebugChecks();
}

// JNI函数：检测调试端口
JNIEXPORT void JNICALL
Java_com_android_study_AntiDebug_checkPort23946(JNIEnv* env, jclass clazz) {
    CheckPort23946ByTcp();
}

// JNI函数：检测调试进程
JNIEXPORT void JNICALL
Java_com_android_study_AntiDebug_searchDebugProcess(JNIEnv* env, jclass clazz) {
    SearchObjProcess();
}

// JNI函数：检测父进程
JNIEXPORT void JNICALL
Java_com_android_study_AntiDebug_checkParents(JNIEnv* env, jclass clazz) {
    CheckParents();
}

// JNI函数：检测自身进程名
JNIEXPORT void JNICALL
Java_com_android_study_AntiDebug_checkSelfProcessName(JNIEnv* env, jclass clazz) {
    CheckSelfProcessName();
}

// JNI函数：检测线程数
JNIEXPORT void JNICALL
Java_com_android_study_AntiDebug_checkTaskCount(JNIEnv* env, jclass clazz) {
    CheckTaskCount();
}

// JNI函数：检测文件描述符数量
JNIEXPORT void JNICALL
Java_com_android_study_AntiDebug_checkFdCount(JNIEnv* env, jclass clazz) {
    CheckFdCount();
}

// JNI函数：检测Dalvik调试连接
JNIEXPORT void JNICALL
Java_com_android_study_AntiDebug_checkDalvikDebugger(JNIEnv* env, jclass clazz) {
    NativeIsDBGConnected();
}

// JNI函数：ptrace自我保护
JNIEXPORT void JNICALL
Java_com_android_study_AntiDebug_ptraceProtect(JNIEnv* env, jclass clazz) {
    PtraceCheck();
}

// JNI函数：计算函数Hash值
JNIEXPORT jint JNICALL
Java_com_android_study_AntiDebug_calculateFunctionHash(JNIEnv* env, jclass clazz, jlong address, jint size) {
    return (jint)CalculateFunctionHash((uint8_t*)address, (uint32_t)size);
}

// JNI函数：检测断点指令
JNIEXPORT void JNICALL
Java_com_android_study_AntiDebug_checkBreakpoint(JNIEnv* env, jclass clazz, jlong address, jint size) {
    CheckBkpt((uint8_t*)address, (uint32_t)size);
}

// JNI函数：系统源码修改检测(TracerPid)
JNIEXPORT jboolean JNICALL
Java_com_android_study_AntiDebug_checkTracerPid(JNIEnv* env, jclass clazz) {
    return (jboolean)CheckTracerPid();
}

// JNI函数：信号处理检测
JNIEXPORT jboolean JNICALL
Java_com_android_study_AntiDebug_checkSignalHandler(JNIEnv* env, jclass clazz) {
    return (jboolean)CheckSignalHandler();
}

// JNI函数：时间差检测
JNIEXPORT jboolean JNICALL
Java_com_android_study_AntiDebug_checkTimingAttack(JNIEnv* env, jclass clazz) {
    return (jboolean)CheckTimingAttack();
}

// JNI函数：进程状态检测
JNIEXPORT jboolean JNICALL
Java_com_android_study_AntiDebug_checkProcessStatus(JNIEnv* env, jclass clazz) {
    return (jboolean)CheckProcessStatus();
}

// JNI函数：启动Inotify监控
JNIEXPORT void JNICALL
Java_com_android_study_AntiDebug_startInotifyMonitor(JNIEnv* env, jclass clazz) {
    StartInotifyMonitor();
}

} // extern "C"
