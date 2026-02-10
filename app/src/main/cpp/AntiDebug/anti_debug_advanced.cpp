#include "anti_debug.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/ptrace.h>
#include <sys/wait.h>
#include <sys/time.h>
#include <sys/resource.h>
#include <sys/inotify.h>
#include <time.h>
#include <signal.h>
#include <pthread.h>
#include <android/log.h>

#define TAG "AntiDebug-Advanced"
#define LOGA(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
#define LOGB(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)

// 全局变量用于信号检测
static volatile int g_signal_received = 0;

// 11. 系统源码修改检测 - 通过TracerPid检测
bool CheckTracerPid() {
    // 建立管道
    int pipefd[2];
    if (-1 == pipe(pipefd)) {
        LOGA("CheckTracerPid: pipe() 错误\n");
        return false;
    }

    // 创建子进程
    pid_t pid = fork();
    if (0 > pid) {
        LOGA("CheckTracerPid: fork() 错误\n");
        close(pipefd[0]);
        close(pipefd[1]);
        return false;
    }

    // 子进程程序
    if (0 == pid) {
        int iRet = ptrace(PTRACE_TRACEME, 0, 0, 0);
        if (-1 == iRet) {
            LOGA("子进程: ptrace 失败\n");
            close(pipefd[0]);
            close(pipefd[1]);
            exit(0);
        }

        // 获取TracerPid
        char pathbuf[0x100] = {0};
        char readbuf[1024] = {0};
        snprintf(pathbuf, sizeof(pathbuf), "/proc/%d/status", getpid());

        int fd = open(pathbuf, O_RDONLY);
        if (-1 == fd) {
            LOGA("子进程: open失败\n");
            close(pipefd[0]);
            close(pipefd[1]);
            exit(0);
        }

        read(fd, readbuf, sizeof(readbuf) - 1);
        close(fd);

        // 查找TracerPid
        char* tracerPidStr = strstr(readbuf, "TracerPid:");
        int childTracePid = 0;
        if (tracerPidStr != NULL) {
            sscanf(tracerPidStr, "TracerPid:\t%d", &childTracePid);
        }

        LOGB("子进程TracerPid: %d\n", childTracePid);

        // 向管道写入数据
        close(pipefd[0]);
        write(pipefd[1], (void*)&childTracePid, sizeof(int));
        close(pipefd[1]);
        exit(0);
    } else {
        // 父进程程序
        waitpid(pid, NULL, 0);

        int tracerPid = 0;
        close(pipefd[1]);
        read(pipefd[0], (void*)&tracerPid, sizeof(int));
        close(pipefd[0]);

        LOGB("父进程收到的TracerPid: %d\n", tracerPid);

        if (0 == tracerPid) {
            LOGA("检测到系统源码被修改!\n");
            return false;
        } else {
            LOGB("系统源码检测正常\n");
            return true;
        }
    }
}

// 12. 信号处理检测 - IDA会先截获信号
static void signal_handler(int sig) {
    g_signal_received = 1;
    LOGB("信号处理函数被调用, 信号: %d\n", sig);
}

bool CheckSignalHandler() {
    g_signal_received = 0;

    // 设置SIGTRAP信号处理函数
    sighandler_t ret = signal(SIGTRAP, signal_handler);
    if (ret == SIG_ERR) {
        LOGA("CheckSignalHandler: signal设置失败\n");
        return false;
    }

    LOGB("signal返回值: %p\n", (void*)ret);

    // 发送信号给自己
    raise(SIGTRAP);
    usleep(100000); // 等待100ms

    if (g_signal_received == 0) {
        LOGA("信号未被接收,可能正在被IDA调试!\n");
        return false;
    } else {
        LOGB("信号处理正常\n");
        return true;
    }
}

// 13. 时间差检测 - 5种方法

// 13.1 使用time()
bool CheckTiming_Time() {
    time_t t1, t2;
    time(&t1);

    // 在这里放置要保护的代码
    volatile int sum = 0;
    for (int i = 0; i < 1000; i++) {
        sum += i;
    }

    time(&t2);

    if (t2 - t1 > 1) {
        LOGA("time()检测: 时间差异常,可能正在调试! (%ld秒)\n", (long)(t2 - t1));
        return false;
    }
    return true;
}

// 13.2 使用clock()
bool CheckTiming_Clock() {
    clock_t t1, t2;
    t1 = clock();

    volatile int sum = 0;
    for (int i = 0; i < 1000; i++) {
        sum += i;
    }

    t2 = clock();
    double used = (double)(t2 - t1) / CLOCKS_PER_SEC;

    if (used > 0.1) { // 0.1秒
        LOGA("clock()检测: 时间差异常,可能正在调试! (%.3f秒)\n", used);
        return false;
    }
    return true;
}

// 13.3 使用gettimeofday()
bool CheckTiming_Gettimeofday() {
    struct timeval t1, t2;
    struct timezone tz;

    gettimeofday(&t1, &tz);

    volatile int sum = 0;
    for (int i = 0; i < 1000; i++) {
        sum += i;
    }

    gettimeofday(&t2, &tz);

    long diff = (t2.tv_sec - t1.tv_sec) * 1000000 + (t2.tv_usec - t1.tv_usec);

    if (diff > 100000) { // 100ms
        LOGA("gettimeofday()检测: 时间差异常,可能正在调试! (%ld微秒)\n", diff);
        return false;
    }
    return true;
}

// 13.4 使用clock_gettime()
bool CheckTiming_ClockGettime() {
    struct timespec t1, t2;

    clock_gettime(CLOCK_REALTIME, &t1);

    volatile int sum = 0;
    for (int i = 0; i < 1000; i++) {
        sum += i;
    }

    clock_gettime(CLOCK_REALTIME, &t2);

    long diff = (t2.tv_sec - t1.tv_sec) * 1000000000L + (t2.tv_nsec - t1.tv_nsec);

    if (diff > 100000000L) { // 100ms
        LOGA("clock_gettime()检测: 时间差异常,可能正在调试! (%ld纳秒)\n", diff);
        return false;
    }
    return true;
}

// 13.5 使用getrusage()
bool CheckTiming_Getrusage() {
    struct rusage t1;

    getrusage(RUSAGE_SELF, &t1);

    volatile int sum = 0;
    for (int i = 0; i < 100000; i++) {
        sum += i;
    }

    long used = t1.ru_utime.tv_sec + t1.ru_stime.tv_sec;

    if (used > 2) {
        LOGA("getrusage()检测: CPU时间异常,可能正在调试! (%ld秒)\n", used);
        return false;
    }
    return true;
}

// 综合时间检测
bool CheckTimingAttack() {
    LOGA("\n[时间差检测] 开始...\n");

    bool result = true;

    if (!CheckTiming_Time()) result = false;
    if (!CheckTiming_Clock()) result = false;
    if (!CheckTiming_Gettimeofday()) result = false;
    if (!CheckTiming_ClockGettime()) result = false;
    if (!CheckTiming_Getrusage()) result = false;

    if (result) {
        LOGB("时间差检测: 通过\n");
    }

    return result;
}

// 14. 进程状态检测 - 检测/proc/pid/status、stat和wchan

// 14.1 检测status中的TracerPid
bool CheckProcessStatus_TracerPid() {
    char pathbuf[0x100] = {0};
    char readbuf[2048] = {0};

    snprintf(pathbuf, sizeof(pathbuf), "/proc/%d/status", getpid());

    int fd = open(pathbuf, O_RDONLY);
    if (-1 == fd) {
        LOGA("CheckProcessStatus: open失败\n");
        return false;
    }

    read(fd, readbuf, sizeof(readbuf) - 1);
    close(fd);

    // 查找TracerPid
    char* tracerPidStr = strstr(readbuf, "TracerPid:");
    if (tracerPidStr != NULL) {
        int tracerPid = 0;
        sscanf(tracerPidStr, "TracerPid:\t%d", &tracerPid);

        if (tracerPid != 0) {
            LOGA("检测到TracerPid非0: %d, 正在被调试!\n", tracerPid);
            return false;
        }
    }

    // 检查State字段
    char* stateStr = strstr(readbuf, "State:");
    if (stateStr != NULL) {
        char state[16] = {0};
        sscanf(stateStr, "State:\t%s", state);

        if (strchr(state, 't') != NULL || strchr(state, 'T') != NULL) {
            LOGA("检测到进程状态为tracing stop: %s\n", state);
            return false;
        }
    }

    return true;
}

// 14.2 检测stat文件
bool CheckProcessStatus_Stat() {
    char pathbuf[0x100] = {0};
    char readbuf[1024] = {0};

    snprintf(pathbuf, sizeof(pathbuf), "/proc/%d/stat", getpid());

    int fd = open(pathbuf, O_RDONLY);
    if (-1 == fd) {
        LOGA("CheckProcessStatus_Stat: open失败\n");
        return false;
    }

    read(fd, readbuf, sizeof(readbuf) - 1);
    close(fd);

    // stat文件格式: pid (comm) state ...
    char* statePos = strchr(readbuf, ')');
    if (statePos != NULL && statePos[1] == ' ') {
        char state = statePos[2];
        if (state == 't' || state == 'T') {
            LOGA("检测到stat中状态为T(tracing stop): %c\n", state);
            return false;
        }
    }

    return true;
}

// 14.3 检测wchan文件
bool CheckProcessStatus_Wchan() {
    char pathbuf[0x100] = {0};
    char readbuf[256] = {0};

    snprintf(pathbuf, sizeof(pathbuf), "/proc/%d/wchan", getpid());

    int fd = open(pathbuf, O_RDONLY);
    if (-1 == fd) {
        LOGA("CheckProcessStatus_Wchan: open失败\n");
        return false;
    }

    ssize_t len = read(fd, readbuf, sizeof(readbuf) - 1);
    close(fd);

    if (len > 0) {
        readbuf[len] = '\0';
        if (strstr(readbuf, "ptrace_stop") != NULL) {
            LOGA("检测到wchan为ptrace_stop: %s\n", readbuf);
            return false;
        }
    }

    return true;
}

// 综合进程状态检测
bool CheckProcessStatus() {
    LOGA("\n[进程状态检测] 开始...\n");

    bool result = true;

    if (!CheckProcessStatus_TracerPid()) result = false;
    if (!CheckProcessStatus_Stat()) result = false;
    if (!CheckProcessStatus_Wchan()) result = false;

    if (result) {
        LOGB("进程状态检测: 通过\n");
    }

    return result;
}

// 15. Inotify监控 - 监控/proc/pid/mem和pagemap的访问
static void* thread_watchDumpMem(void* arg) {
    LOGA("开始监控 /proc/pid/mem...\n");

    char dirName[256] = {0};
    snprintf(dirName, sizeof(dirName), "/proc/%d/mem", getpid());

    int fd = inotify_init();
    if (fd < 0) {
        LOGA("inotify_init失败\n");
        return NULL;
    }

    int wd = inotify_add_watch(fd, dirName, IN_ALL_EVENTS);
    if (wd < 0) {
        LOGA("inotify_add_watch失败\n");
        close(fd);
        return NULL;
    }

    char buf[sizeof(struct inotify_event) * 256] = {0};
    fd_set readfds;

    while (1) {
        FD_ZERO(&readfds);
        FD_SET(fd, &readfds);

        struct timeval timeout = {5, 0}; // 5秒超时
        int iRet = select(fd + 1, &readfds, NULL, NULL, &timeout);

        if (iRet == -1) {
            break;
        }

        if (iRet > 0) {
            memset(buf, 0, sizeof(buf));
            int len = read(fd, buf, sizeof(buf));
            int i = 0;

            while (i < len) {
                struct inotify_event* event = (struct inotify_event*)&buf[i];

                if (event->mask & IN_OPEN) {
                    LOGA("警告: 检测到有进程打开/proc/pid/mem!\n");
                    // 可以在这里终止进程或采取其他保护措施
                }

                i += sizeof(struct inotify_event) + event->len;
            }
        }
    }

    inotify_rm_watch(fd, wd);
    close(fd);
    return NULL;
}

static void* thread_watchDumpPagemap(void* arg) {
    LOGA("开始监控 /proc/pid/pagemap...\n");

    char dirName[256] = {0};
    snprintf(dirName, sizeof(dirName), "/proc/%d/pagemap", getpid());

    int fd = inotify_init();
    if (fd < 0) {
        LOGA("inotify_init失败\n");
        return NULL;
    }

    int wd = inotify_add_watch(fd, dirName, IN_ALL_EVENTS);
    if (wd < 0) {
        LOGA("inotify_add_watch失败\n");
        close(fd);
        return NULL;
    }

    char buf[sizeof(struct inotify_event) * 256] = {0};
    fd_set readfds;

    while (1) {
        FD_ZERO(&readfds);
        FD_SET(fd, &readfds);

        struct timeval timeout = {5, 0};
        int iRet = select(fd + 1, &readfds, NULL, NULL, &timeout);

        if (iRet == -1) {
            break;
        }

        if (iRet > 0) {
            memset(buf, 0, sizeof(buf));
            int len = read(fd, buf, sizeof(buf));
            int i = 0;

            while (i < len) {
                struct inotify_event* event = (struct inotify_event*)&buf[i];

                if (event->mask & IN_OPEN) {
                    LOGA("警告: 检测到有进程打开/proc/pid/pagemap!\n");
                }

                i += sizeof(struct inotify_event) + event->len;
            }
        }
    }

    inotify_rm_watch(fd, wd);
    close(fd);
    return NULL;
}

void StartInotifyMonitor() {
    pthread_t ptMem, ptPageMap;

    // 启动mem监控线程
    int ret = pthread_create(&ptMem, NULL, thread_watchDumpMem, NULL);
    if (ret != 0) {
        LOGA("创建mem监控线程失败\n");
    } else {
        pthread_detach(ptMem);
    }

    // 启动pagemap监控线程
    ret = pthread_create(&ptPageMap, NULL, thread_watchDumpPagemap, NULL);
    if (ret != 0) {
        LOGA("创建pagemap监控线程失败\n");
    } else {
        pthread_detach(ptPageMap);
    }

    LOGB("Inotify监控已启动\n");
}

// 运行所有高级反调试检测
void RunAllAdvancedAntiDebugChecks() {
    LOGA("\n========== 开始高级反调试检测 ==========\n");

    LOGA("\n[11] 系统源码修改检测(TracerPid)...\n");
    CheckTracerPid();

    LOGA("\n[12] 信号处理检测...\n");
    CheckSignalHandler();

    LOGA("\n[13] 时间差检测...\n");
    CheckTimingAttack();

    LOGA("\n[14] 进程状态检测...\n");
    CheckProcessStatus();

    LOGA("\n[15] 启动Inotify监控...\n");
    StartInotifyMonitor();

    LOGA("\n========== 高级反调试检测完成 ==========\n");
}
