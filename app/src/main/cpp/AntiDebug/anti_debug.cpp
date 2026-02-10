#include "anti_debug.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <dirent.h>
#include <sys/ptrace.h>
#include <dlfcn.h>
#include <android/log.h>

#define TAG "AntiDebug"
#define LOGA(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
#define LOGB(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)

// 1. 调试端口检测 - 读取/proc/net/tcp，查找IDA远程调试所用的23946端口
void CheckPort23946ByTcp() {
    FILE* pfile = NULL;
    char buf[0x1000] = {0};

    // 执行命令 - 23946端口的十六进制是0x5D8A
    char* strCatTcp = "cat /proc/net/tcp | grep :5D8A";

    pfile = popen(strCatTcp, "r");
    if (NULL == pfile) {
        LOGA("CheckPort23946ByTcp: popen打开命令失败!\n");
        return;
    }

    // 获取结果
    while (fgets(buf, sizeof(buf), pfile)) {
        // 执行到这里，判定为调试状态
        LOGA("检测到调试端口23946!");
        LOGB("执行cat /proc/net/tcp | grep :5D8A的结果:\n%s", buf);
    }

    pclose(pfile);
}

// 2. 调试进程名检测 - 遍历进程，查找固定的调试器进程名
void SearchObjProcess() {
    FILE* pfile = NULL;
    char buf[0x1000] = {0};

    // 执行命令
    pfile = popen("ps", "r");
    if (NULL == pfile) {
        LOGA("SearchObjProcess: popen打开命令失败!\n");
        return;
    }

    // 获取结果
    LOGA("开始遍历进程:\n");
    while (fgets(buf, sizeof(buf), pfile)) {
        // 查找已知的调试器进程名
        char* strA = strstr(buf, "android_server");
        char* strB = strstr(buf, "gdbserver");
        char* strC = strstr(buf, "gdb");
        char* strD = strstr(buf, "fuwu");

        if (strA || strB || strC || strD) {
            // 执行到这里，判定为调试状态
            LOGA("发现可疑调试进程: %s\n", buf);
        }
    }

    pclose(pfile);
}

// 3. 父进程名检测 - 读取/proc/ppid/cmdline，查看内容是否为zygote
void CheckParents() {
    char strPpidCmdline[0x100] = {0};
    snprintf(strPpidCmdline, sizeof(strPpidCmdline), "/proc/%d/cmdline", getppid());

    // 打开文件
    int file = open(strPpidCmdline, O_RDONLY);
    if (file < 0) {
        LOGA("CheckParents: open错误!\n");
        return;
    }

    // 文件内容读入内存
    memset(strPpidCmdline, 0, sizeof(strPpidCmdline));
    ssize_t ret = read(file, strPpidCmdline, sizeof(strPpidCmdline));
    close(file);

    if (-1 == ret) {
        LOGA("CheckParents: read错误!\n");
        return;
    }

    // 没找到返回NULL
    char* sRet = strstr(strPpidCmdline, "zygote");
    if (NULL == sRet) {
        // 执行到这里，判定为调试状态
        LOGA("父进程cmdline没有zygote子串! 父进程: %s\n", strPpidCmdline);
    } else {
        LOGB("父进程检测正常: %s\n", strPpidCmdline);
    }
}

// 4. 自身进程名检测 - 读取/proc/self/cmdline，检查进程名是否正常
void CheckSelfProcessName() {
    char strSelfCmdline[0x100] = {0};
    const char* cmdlinePath = "/proc/self/cmdline";

    // 打开文件
    int file = open(cmdlinePath, O_RDONLY);
    if (file < 0) {
        LOGA("CheckSelfProcessName: open错误!\n");
        return;
    }

    // 文件内容读入内存
    ssize_t ret = read(file, strSelfCmdline, sizeof(strSelfCmdline));
    close(file);

    if (-1 == ret) {
        LOGA("CheckSelfProcessName: read错误!\n");
        return;
    }

    LOGB("当前进程名: %s\n", strSelfCmdline);

    // 可以在这里添加自定义的进程名检测逻辑
    // 例如检查进程名是否包含特定字符串
}

// 5. APK线程数检测 - 正常APK有多个线程，自己写的加载器可能只有1个线程
void CheckTaskCount() {
    char buf[0x100] = {0};
    snprintf(buf, sizeof(buf), "/proc/%d/task", getpid());

    // 打开目录
    DIR* pdir = opendir(buf);
    if (!pdir) {
        LOGA("CheckTaskCount: opendir失败.\n");
        return;
    }

    // 查看目录下文件个数
    struct dirent* pde = NULL;
    int count = 0;
    while ((pde = readdir(pdir))) {
        // 字符过滤 - 只统计线程ID（数字开头）
        if ((pde->d_name[0] <= '9') && (pde->d_name[0] >= '0')) {
            ++count;
            LOGB("线程 %d: %s\n", count, pde->d_name);
        }
    }
    closedir(pdir);

    LOGB("线程个数为: %d\n", count);

    if (1 >= count) {
        // 此处判定为调试状态
        LOGA("线程数异常! 可能处于调试状态!\n");
    }
}

// 6. APK进程FD文件检测 - 根据/proc/pid/fd/路径下文件的个数差异判断进程状态
void CheckFdCount() {
    char buf[0x100] = {0};
    snprintf(buf, sizeof(buf), "/proc/%d/fd", getpid());

    // 打开目录
    DIR* pdir = opendir(buf);
    if (!pdir) {
        LOGA("CheckFdCount: opendir失败.\n");
        return;
    }

    // 统计文件描述符个数
    struct dirent* pde = NULL;
    int count = 0;
    while ((pde = readdir(pdir))) {
        // 排除 . 和 ..
        if (strcmp(pde->d_name, ".") != 0 && strcmp(pde->d_name, "..") != 0) {
            ++count;
        }
    }
    closedir(pdir);

    LOGB("文件描述符个数为: %d\n", count);

    // 可以根据实际情况设定阈值
    if (count < 5) {
        LOGA("文件描述符数量异常! 可能处于调试状态!\n");
    }
}

// 7. Android系统自带调试检测函数 - dalvik模式下的实现
typedef unsigned char wbool;
typedef wbool (*PPP)();

void NativeIsDBGConnected() {
    void* handle = NULL;
    handle = dlopen("/system/lib/libdvm.so", RTLD_LAZY);

    if (NULL == handle) {
        // 可能是art模式，libdvm.so不存在
        LOGB("NativeIsDBGConnected: 无法打开libdvm.so (可能是ART模式)\n");
        return;
    }

    PPP fun = (PPP)dlsym(handle, "_Z25dvmDbgIsDebuggerConnectedv");
    if (NULL == fun) {
        LOGA("NativeIsDBGConnected: dlsym获取函数失败!\n");
        dlclose(handle);
        return;
    }

    wbool ret = fun();
    if (1 == ret) {
        // 此处判定为调试模式
        LOGA("dalvik模式下检测到调试状态!\n");
    } else {
        LOGB("dalvik模式下未检测到调试\n");
    }

    dlclose(handle);
}

// 8. ptrace检测 - 每个进程同时只能被1个调试进程ptrace
void PtraceCheck() {
    // ptrace如果被调试返回值为-1，如果正常运行，返回值为0
    int iRet = ptrace(PTRACE_TRACEME, 0, 0, 0);

    if (-1 == iRet) {
        LOGA("ptrace失败，进程正在被调试!\n");
    } else {
        LOGB("ptrace成功，返回值: %d\n", iRet);
    }
}

// 9. 函数hash值检测 - 计算函数指令的hash值，检测是否被修改或下断点
uint32_t CalculateFunctionHash(uint8_t* addr, uint32_t size) {
    if (addr == NULL || size == 0) {
        LOGA("CalculateFunctionHash: 参数无效!\n");
        return 0;
    }

    uint32_t hash = 0;

    // 简单的hash算法
    for (uint32_t i = 0; i < size; i++) {
        hash = hash * 31 + addr[i];
    }

    LOGB("函数Hash值: 0x%08X (地址: %p, 大小: %d)\n", hash, addr, size);

    return hash;
}

// 10. 断点指令检测 - 检测函数中是否存在bkpt断点指令
void CheckBkpt(uint8_t* addr, uint32_t size) {
    if (addr == NULL || size == 0) {
        LOGA("CheckBkpt: 参数无效!\n");
        return;
    }

    // ARM断点指令: 0xE7F001F0
    uint8_t armBkpt[4] = {0xF0, 0x01, 0xF0, 0xE7};

    // Thumb断点指令: 0xDE10
    uint8_t thumbBkpt[2] = {0x10, 0xDE};

    // 判断是ARM模式还是Thumb模式
    int mode = (uintptr_t)addr % 2;

    if (1 == mode) {
        // Thumb模式
        LOGB("CheckBkpt: 检测Thumb模式函数\n");
        uint8_t* start = (uint8_t*)((uintptr_t)addr - 1);
        uint8_t* end = (uint8_t*)((uintptr_t)start + size);

        // 遍历对比
        while (start < end) {
            if (0 == memcmp(start, thumbBkpt, 2)) {
                LOGA("CheckBkpt: 发现Thumb断点指令! 地址: %p\n", start);
                return;
            }
            start = start + 2;
        }

        LOGB("CheckBkpt: 未发现Thumb断点\n");

    } else {
        // ARM模式
        LOGB("CheckBkpt: 检测ARM模式函数\n");
        uint8_t* start = (uint8_t*)addr;
        uint8_t* end = (uint8_t*)((uintptr_t)start + size);

        // 遍历对比
        while (start < end) {
            if (0 == memcmp(start, armBkpt, 4)) {
                LOGA("CheckBkpt: 发现ARM断点指令! 地址: %p\n", start);
                return;
            }
            start = start + 4;
        }

        LOGB("CheckBkpt: 未发现ARM断点\n");
    }
}

// 统一调用所有反调试检测(基础+高级)
void RunAllAntiDebugChecks() {
    LOGA("========== 开始完整反调试检测 ==========\n");

    // 基础检测 (1-10)
    LOGA("\n[1] 检测调试端口23946...\n");
    CheckPort23946ByTcp();

    LOGA("\n[2] 检测调试进程名...\n");
    SearchObjProcess();

    LOGA("\n[3] 检测父进程名...\n");
    CheckParents();

    LOGA("\n[4] 检测自身进程名...\n");
    CheckSelfProcessName();

    LOGA("\n[5] 检测线程数...\n");
    CheckTaskCount();

    LOGA("\n[6] 检测文件描述符数量...\n");
    CheckFdCount();

    LOGA("\n[7] 检测Dalvik调试连接...\n");
    NativeIsDBGConnected();

    LOGA("\n[8] ptrace自我保护...\n");
    PtraceCheck();

    // 调用高级检测 (11-15)
    RunAllAdvancedAntiDebugChecks();

    LOGA("\n========== 完整反调试检测完成 ==========\n");
}
