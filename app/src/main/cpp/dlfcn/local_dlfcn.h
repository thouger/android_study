//
// Created by luoyesiqiu
//

#ifndef LOCAL_DLFCN_H
#define LOCAL_DLFCN_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/mman.h>
#include <elf.h>

#include <android/log.h>

#define TAG "local_dlfcn"

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)

#ifdef __cplusplus
extern "C" {
#endif

struct local_dlfcn_handle{
    char *elf;
    size_t size;
    char *dynsym;
    int nsyms;
    char *dynstr;
};

void *local_dlopen(const char *lib_path);
off_t local_dlsym(void *handle, const char *sym_name);
void local_dlclose(void *handle);

#ifdef __cplusplus
}
#endif

#endif
