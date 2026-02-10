#ifndef ANDROID_STUDY_ANTI_DEBUG_H
#define ANDROID_STUDY_ANTI_DEBUG_H

#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

// 1. 调试端口检测 - 检测IDA远程调试端口23946
void CheckPort23946ByTcp();

// 2. 调试进程名检测 - 查找已知的调试器进程
void SearchObjProcess();

// 3. 父进程名检测 - 检查父进程是否为zygote
void CheckParents();

// 4. 自身进程名检测 - 检查自身进程名是否正常
void CheckSelfProcessName();

// 5. APK线程数检测 - 检查线程数是否异常
void CheckTaskCount();

// 6. APK进程FD文件检测 - 检查文件描述符数量
void CheckFdCount();

// 7. Android系统自带调试检测函数 - dalvik模式下检测
void NativeIsDBGConnected();

// 8. ptrace检测 - 主动调用ptrace防止被调试
void PtraceCheck();

// 9. 函数hash值检测 - 检测函数是否被修改
uint32_t CalculateFunctionHash(uint8_t* addr, uint32_t size);

// 10. 断点指令检测 - 检测函数中是否存在断点指令
void CheckBkpt(uint8_t* addr, uint32_t size);

// 11. 系统源码修改检测 - 通过TracerPid检测
bool CheckTracerPid();

// 12. 信号处理检测 - IDA会先截获信号
bool CheckSignalHandler();

// 13. 时间差检测 - 5种方法综合检测
bool CheckTimingAttack();

// 14. 进程状态检测 - 检测status、stat、wchan
bool CheckProcessStatus();

// 15. Inotify监控 - 监控/proc/pid/mem和pagemap
void StartInotifyMonitor();

// 运行所有高级反调试检测
void RunAllAdvancedAntiDebugChecks();

// 统一调用所有反调试检测(基础+高级)
void RunAllAntiDebugChecks();

#ifdef __cplusplus
}
#endif

#endif // ANDROID_STUDY_ANTI_DEBUG_H
