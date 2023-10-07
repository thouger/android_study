//
// Created by thouger on 2023/8/31.
//
#ifndef THOUGER_MYLOG_H
#define THOUGER_MYLOG_H

#ifdef __cplusplus
#include <cinttypes>
#include <vector>
#include <cstdint>
#include <cstring>
#include <cerrno>
#include <cstdio>
#include <string>
#include <map>
#include <list>
#include <regex>
#include <climits>
#include <fstream>
extern "C" {
#endif

#include <android/log.h>
#include <sys/mman.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <errno.h>
#include <linux/limits.h>
#include <dirent.h>
#include <sys/mount.h>
#include <sys/stat.h>
#include <dirent.h>
#include <unistd.h>
#include <sched.h>
#include <string.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <jni.h>
#include <dlfcn.h>
#include <stddef.h>
#include <bits/getopt.h>
#include <asm/unistd.h>
#include <unistd.h>
#include <asm/fcntl.h>
#include <fcntl.h>
#include <sys/syscall.h>
#include <unistd.h>
#include <stdlib.h>
#include <syscall.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <unistd.h>
#include <jni.h>
#include <dlfcn.h>
#include <android/log.h>
#include <malloc.h>
#include <bits/getopt.h>
#include <asm/unistd.h>
#include <unistd.h>
#include <asm/fcntl.h>
#include <string.h>
#include <unistd.h>
#include <stdlib.h>
#include <syscall.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <unistd.h>
#include <jni.h>
#include <dlfcn.h>
#include <android/log.h>
#include <malloc.h>
#include <bits/getopt.h>
#include <asm/unistd.h>
#include <unistd.h>
#include <asm/fcntl.h>
#include <fcntl.h>
#include <jni.h>
#include <dlfcn.h>
#include <android/log.h>
#include <malloc.h>
#include <stdbool.h>
#include <stdlib.h>
#include <stdio.h>
#include <bits/getopt.h>
#include <asm/unistd.h>
#include <unistd.h>
#include <asm/fcntl.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <sys/syscall.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#ifdef __cplusplus
}
#endif

#define MYTAG "mylog"

#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, MYTAG, __VA_ARGS__);
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, MYTAG ,__VA_ARGS__);
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, MYTAG ,__VA_ARGS__);
#define LOGW_(...) __android_log_print(ANDROID_LOG_WARN, MYTAG ,__VA_ARGS__);

#define ALOGI(...) __android_log_print(ANDROID_LOG_INFO, MYTAG ,__VA_ARGS__);
#define ALOGD(...) __android_log_print(ANDROID_LOG_DEBUG, MYTAG ,__VA_ARGS__);
#define ALOGW(...) __android_log_print(ANDROID_LOG_WARN, MYTAG ,__VA_ARGS__);
#define ALOGE(...) __android_log_print(ANDROID_LOG_ERROR, MYTAG ,__VA_ARGS__);


#define PLOGE(...)  ((void)0);
#define PLOGI(...)  ((void)0);
#define PLOGD(...)  ((void)0);
#define PLOGW(...)  ((void)0);

#endif //THOUGER_MYLOG_H