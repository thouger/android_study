//
// Created by zhenxi on 2021/12/12.
//

#pragma once

#include <sys/syscall.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <jni.h>
#include <dlfcn.h>
#include <android/log.h>
#include <malloc.h>
#include <regex>
#include <bits/getopt.h>
#include <asm/unistd.h>
#include <unistd.h>
#include <asm/fcntl.h>
#include <string.h>
#include <cerrno>
#include <cstring>
#include <climits>
#include <sys/mman.h>

#include "limits.h"
#include "syscall.h"
#include "stringUtils.h"
#include "mapItemInfo.h"

#include "ZhenxiLogging.h"
#include "allInclude.h"
#include "ZhenxiLog.h"
#include "mapItemInfo.h"

#define MEMORY_R    PROT_READ
#define MEMORY_RW   PROT_READ |PROT_WRITE
#define MEMORY_RWX  PROT_READ |PROT_WRITE| PROT_EXEC
#define MEMORY_RX   PROT_READ | PROT_EXEC


//HookUtils::Hooker
//MSHookFunction
#define HOOK_JNI(env, func) \
     HookUtils::Hooker(    \
      (void*)env->functions->func,\
      (void*)new_##func,\
      (void**)&orig_##func );     \


#define JNI_HOOK_DEF(ret, func, ...)    \
  ret (*orig_##func)(JNIEnv *env, jobject obj, jmethodID jmethodId,...);  \
  ret new_##func(__VA_ARGS__) {         \
//LOG(INFO) << #func<<"("#__VA_ARGS__")" ;\



struct mapAddresInfo;

void  getNameByPid(pid_t pid, char *task_name);


/**
 * 打印当前So在Maps里面的开始地址
 * @param name so的name
 * @return 返回开始地址
 */
ZHENXI_RUNTIME_MAPINFO::mapItemInfo getSoBaseAddress(const char *name);

bool MPROTECT(size_t addr,size_t size,int __prot);
/**
 * hook art method里面的 regiest 打印被注册的native函数相关信息
 */
void HookRegiest();

/**
 * 根据一个地址获取符号信息
 *
 * @param address 内存的任意地址
 * @return  函数的符号
 */
ZHENXI_RUNTIME_MAPINFO::mapAddresInfo * getSymbolforAddress(size_t address, pid_t pid);

void findSymBySo(size_t address, mapAddresInfo* info);

/**
 * Hook artmethod里面的invoke 方法
 * 打印全部的方法信。
 * //todo 在打印Object参数对象时候有问题待实现
 */
void HookJNIInvoke();


void hookStringAppend();

bool getpData(char temp[],const void *p, size_t size);

bool mProtect(size_t addr, size_t len);

