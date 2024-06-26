//
// Created by thouger on 2023/4/19.
//


#include "native_hook.h"

static HookFunType hook_func = nullptr;

int (*backup)();

int fake() {
    return backup() + 1;
}

FILE *(*backup_fopen)(const char *filename, const char *mode);
FILE *fake_fopen(const char *filename, const char *mode) {
    LOGD("fopen filename: %s", filename);
    LOGD("fopen mode: %s", mode);
    if (strstr(filename, "banned")) return nullptr;
    return backup_fopen(filename, mode);
}

jclass (*backup_FindClass)(JNIEnv *env, const char *name);
jclass fake_FindClass(JNIEnv *env, const char *name)
{
    LOGD("FindClass: %s", name);
    if(!strcmp(name, "dalvik/system/BaseDexClassLoader"))
        return nullptr;
    LOGD("find dalvik/system/BaseDexClassLoader class");
    return backup_FindClass(env, name);
}
bool ends_with(const std::string& str, const std::string& suffix) {
  if (suffix.size() > str.size()) {
    return false;
  }
  return str.substr(str.size() - suffix.size()) == suffix;
}

const char *(*backup_system_property_get)(const char *name);
const char *fake_system_property_get(const char *name) {
    LOGD("system_property_get name: %s", name);
    return backup_system_property_get(name);
}


void on_library_loaded(const char *name, void *handle) {
    // 钩子函数加载 `libtarget.so`
    LOGD("on_library_loaded: %s", name);
    std::string lib_name = name;
    if (ends_with(lib_name,("libhello.so"))) {
        LOGD("正在钩取 libtest.so");
        void *target = dlsym(handle, "stringFromJNI");
        LOGD("libtest.so 目标函数: %p", target);
        if (target==nullptr) {
            LOGD("libtest.so 目标函数为 null");
            return;
        }
        hook_func(target, (void *) fake, (void **) &backup);
    }
    //不能钩取libc.so，因为在app的NativeAnalysis已经有dobby的hook了，需要注释native方法Analysis
    else if(ends_with(lib_name,("libc.so"))){
        LOGD("正在钩取 libc.so");
        void *target = dlsym(handle, "__system_property_get");
        LOGD("libc.so 目标函数: %s", "__system_property_get");
        if (target==nullptr) {
            LOGD("libc.so 目标函数为 null");
            return;
        }
        hook_func(target,(void *)fake_system_property_get,(void **)&backup_system_property_get);
    }
}

extern "C" [[gnu::visibility("default")]] [[gnu::used]]
jint JNI_OnLoad(JavaVM *jvm, void*) {
    JNIEnv *env = nullptr;
    jvm->GetEnv((void **)&env, JNI_VERSION_1_6);
    hook_func((void *)env->functions->FindClass, (void *)fake_FindClass, (void **)&backup_FindClass);
    return JNI_VERSION_1_6;
}

const char *(*backup_dlopen)(const char *filename, int flag);
const char *fake_dlopen(const char *filename, int flag) {
    LOGD("dlopen filename: %s,flag: %d", filename,flag);
    return backup_dlopen(filename, flag);
}

extern "C" [[gnu::visibility("default")]] [[gnu::used]]
NativeOnModuleLoaded native_init(const NativeAPIEntries *entries) {
    hook_func = entries->hook_func;
    LOGD("native_init");
    // system hooks
    hook_func((void*) fopen, (void*) fake_fopen, (void**) &backup_fopen);
    // dlopen hooks
    hook_func((void*) dlopen, (void*) fake_dlopen, (void**) &backup_dlopen);
    return on_library_loaded;
}

