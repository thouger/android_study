//
// Created by thouger on 2023/4/26.
//

#include "hunter.h"
#include <jni.h>
#include "SeccompSVC/library.h"
#include "mylibc.h"
#include "hide.h"
#include "binder_client.h"
#include "dlfcn/local_dlfcn.h"
#include "linker/mylinker.h"
#include <csignal>
#include <cstdio>
#include <cstdlib>
#include "adapter.h"

//extern "C"
//JNIEXPORT jstring JNICALL detect_inlineHook(JNIEnv *env, jobject type) {
//
//#ifdef __LP64__
//    const char *lib_path = "/system/lib64/libc.so";
//#else
//    const char *lib_path = "/system/lib/libc.so";
//#endif
//#define CMP_COUNT 8
//    const char *sym_name = "open";
//
//    struct local_dlfcn_handle *handle = static_cast<local_dlfcn_handle *>(local_dlopen(lib_path));
//
//    off_t offset = local_dlsym(handle,sym_name);
//
//    FILE *fp = fopen(lib_path,"rb");
//    char file_bytes[CMP_COUNT] = {0};
//    if(fp != NULL){
//        fseek(fp,offset,SEEK_SET);
//        fread(file_bytes,1,CMP_COUNT,fp);
//        fclose(fp);
//    }
//
//    void *dl_handle = dlopen(lib_path,RTLD_NOW);
//    void *sym = dlsym(dl_handle,sym_name);
//
//    int is_hook = memcmp(file_bytes,sym,CMP_COUNT) != 0;
//
//    local_dlclose(handle);
//    dlclose(dl_handle);
//
//    char text[128] = {0};
//    snprintf(text,128,"Function \"%s\" is hook: %s",sym_name,is_hook ? "true" : "false");
//
//    return env->NewStringUTF(text);
//}
//
//void NativeAnalysis(JNIEnv *env, jobject type,jobject filterList,jstring filepath) {
//    const auto &clist = parse::jlist2clist(env, filterList);
//    string path;
//
//    //jni hook
//    if (filepath != nullptr) {
//        path = parse::jstring2str(env, filepath);
//        LOGD("nativeAnalyse open file  %s  ",path.c_str());
//        auto *saveOs = new ofstream();
//        saveOs->open(path, ios::app);
//        if (!saveOs->is_open()) {
//            LOG(INFO) << "nativeAnalyse open file error  " << path;
//            LOG(INFO) << "nativeAnalyse open file error  " << path;
//            LOG(INFO) << "nativeAnalyse open file error  " << path;
//            return;
//        }
//        Jnitrace::startjnitrace(env, clist, saveOs);
//        stringHandler::hookStrHandler(clist, saveOs);
//        //invokePrintf::HookJNIInvoke(saveOs);
//    } else {
//        Jnitrace::startjnitrace(env, clist, nullptr);
//        stringHandler::hookStrHandler(clist, nullptr);
//        //invokePrintf::HookJNIInvoke(nullptr);
//    }
//}
//
//extern "C"
//JNIEXPORT jobject JNICALL
//Java_com_android_study_hunter_XposedBridge_hook0(JNIEnv *env,
//                                                 jclass clazz,
//                                                 jobject hooker_object,
//                                                 jobject originalMethod,
//                                                 jobject callbackMethod) {
//    LOGD("hunter hook start");
//    return lsplant::Hook(env, originalMethod, hooker_object, callbackMethod);
//}
//
//extern "C"
//JNIEXPORT jboolean JNICALL
//Java_com_android_study_hunter_XposedBridge_unhook0(JNIEnv *env, jclass clazz, jobject originalMethod) {
//    LOGD("hunter unhook start");
//    return lsplant::UnHook(env, originalMethod);
//}
//
//void *inlineHooker(void *targetFunc, void *replaceFunc) {
//    auto pageSize = sysconf(_SC_PAGE_SIZE);
//    auto funcAddress = ((uintptr_t) targetFunc) & (-pageSize);
//    mprotect((void *) funcAddress, pageSize, PROT_READ | PROT_WRITE | PROT_EXEC);
//
//    void *originalFunc;
//    if (HookUtils::Hooker((void*)targetFunc,(void*)replaceFunc,(void**)&originalFunc)) {
//        return originalFunc;
//    }
//    return nullptr;
//}
//
//bool inlineUnHooker(void *originalFunc) {
//    return HookUtils::unHook(originalFunc);
//}
//
//int (*orig__ZN16SystemProperties3GetEPKcPc)(const char *zhis, char *name,char *value);
//int _ZN16SystemProperties3GetEPKcPc(const char *zhis, char *name,char *value) {
//    int result = orig__ZN16SystemProperties3GetEPKcPc(zhis, name,value);
//
//    if (result < 0) {
//        //failed
//        return result;
//    }
//    LOGD("system_property_get name: %s value: %s",name,value);
////
////  PropertiesMockItem *propertiesMockItem = query_mock_properties(__name);
////  if (propertiesMockItem == nullptr) {
////    return result;
////  }
////
////  if (propertiesMockItem->properties_value == nullptr) {
////    result = -1;
////    __value[0] = '\0';
////    return result;
////  }
////
////  result = propertiesMockItem->value_length;
////  strcpy(__value, propertiesMockItem->properties_value);
//    return result;
//}
//
//int (*orig_system_property_read)(const char *zhis,const prop_info* pi, char* name, char* value);
//int system_property_read(const char *zhis,const prop_info* pi, char* name, char* value) {
//    int result = orig_system_property_read(zhis,pi,name,value);
//    LOGD("success system_property_read name: %s value: %s",name,value);
//    return result;
//}
//
////void *addr = GetSymbolAddress("libc.so", "__system_property_find");
////const prop_info* (*orig_system_property_find)(const char *zhis,const char* name);
////const prop_info* system_property_find(const char *zhis,const char* name) {
////  const prop_info* result = orig_system_property_find(zhis,name);
////
////  auto pageSize = sysconf(_SC_PAGE_SIZE);
////  auto funcAddress = ((uintptr_t) addr) & (-pageSize);
////  mprotect((void *) funcAddress, pageSize, PROT_READ | PROT_WRITE | PROT_EXEC);
////  LOGD("success system_property_find name: %s",name)
////  return result;
////}
//
//std::string (*orig_GetProperty)(const std::string& key, const std::string& default_value);
//std::string GetProperty(const std::string& key, const std::string& default_value) {
//    std::string property_value = orig_GetProperty(key, default_value);
//    LOGD("success GetProperty %s", key.c_str());
//    return property_value;
//}
//
//void (*orig_openat)(const char *path, int oflag, ...);
//void new_openat(const char *path, int oflag, ...) {
//    LOGD("1111success open");
//    orig_openat(path, oflag);
//}
//
//void (*orig_fchownat)(int dirfd, const char *pathname, uid_t owner, gid_t group, int flags);
//void new_fchownat(int dirfd, const char *pathname, uid_t owner, gid_t group, int flags) {
//    LOGD("1111success new_fchownat");
//    orig_fchownat(dirfd,pathname,owner, group, flags);
//}
//
//void (*orig_renameat)(int olddirfd, const char *oldpath, int newdirfd, const char *newpath);
//void new_renameat(int olddirfd, const char *oldpath, int newdirfd, const char *newpath) {
//    LOGD("1111success renameat");
//    orig_renameat( olddirfd, oldpath, newdirfd, newpath);
//}
//
//void (*orig_mkdirat)(int __dir_fd, const char* __path, mode_t __mode);
//void new_mkdirat(int __dir_fd, const char* __path, mode_t __mode) {
//    LOGD("1111success mkdirat");
//    orig_mkdirat( __dir_fd, __path, __mode);
//}
//
//void hookProperties() {
////  xhook_enable_debug(1);
////    int result = xhook_register("libc.so","open",(void*) new_openat, (void **) &orig_openat);
////    if (result != 0) {
////        LOGE("android::base::GetProperty failed %d", result);
////    }
////  xhook_refresh(0);
//
//    //无限重启，不懂
////  hook_function_xhook("libandroid_runtime.so", "_ZN7android4base11GetPropertyERKNSt3__112basic_stringIcNS1_11char_traitsIcEENS1_9allocatorIcEEEES9_",
////                              (void*) GetProperty, (void **) &orig_GetProperty);
//
//    SandHook::ElfImg libc("libc.so");
//    void* addr = libc.getSymbAddress("_ZN16SystemProperties3GetEPKcPc");
//    orig__ZN16SystemProperties3GetEPKcPc = (int (*)(const char *, char *, char *)) inlineHooker(addr, reinterpret_cast<void*>(_ZN16SystemProperties3GetEPKcPc));
//
////  void* my_mkdirat = libc.getSymbAddress("mkdirat");
////    orig_mkdirat = (void (*)(int, const char*, mode_t)) inlineHooker(my_mkdirat, reinterpret_cast<void*>(new_mkdirat));
//
//    // hook SystemProperties::Read
////    void *addr = libc.getSymbAddress("_ZN16SystemProperties4ReadEPK9prop_infoPcS3_");
////    orig_system_property_read = (int (*)(const char *, const prop_info *, char *, char *)) inlineHooker(addr, reinterpret_cast<void*>(system_property_read));
//
////  addr = libc.getSymbAddress("_ZN16SystemProperties4FindEPKc");
////  orig_system_property_find = (const prop_info* (*)(const char *, const char *)) inlineHooker(addr, reinterpret_cast<void*>(system_property_find));
//}

void NativeSeccompSVC(JNIEnv *env, jobject type) {
    LOGD("SeccompSVC hook start");
//    InitCvmSeccomp();
}

void test(JNIEnv *env, jobject type) {
//    void* handle = dlopen("/system/lib64/libdo64.so", RTLD_NOW);
//    if (!handle) {
//        LOGD("Failed to load libdo64.so: %s", dlerror());
//    }
//    LOGI("find libdo64.so %p", handle);
////
//    typedef void (*DobbyDestroyFunc)(void*);
//    DobbyDestroyFunc DobbyDestroy = reinterpret_cast<DobbyDestroyFunc>(dlsym(handle, "DobbyDestroy"));
//    if (!DobbyDestroy) {
//        LOGD("Failed to find DobbyDestroy symbol: %s", dlerror());
//        dlclose(handle);
//    }
//    LOGI("find DobbyDestroy success： %p", DobbyDestroy)
//
//    DobbyDestroy(nullptr);
//
//    dlclose(handle);
}


void signalHandler(int signal) {
    if (signal == SIGSEGV) {
        LOGD("Error: Segmentation fault (signal %d)\n", signal);
        // 执行任何需要的清理操作
        exit(signal); // 退出程序
    }
}
static struct {
    jmethodID m1;
    jmethodID m2;
    size_t methodSize;
} methodHookClassInfo;
static long methodHook(JNIEnv* env, jclass type, jobject srcMethodObj, jobject destMethodObj) {
    // 设置信号处理函数
    std::signal(SIGSEGV, signalHandler);

    void* srcMethod = reinterpret_cast<void*>(env->FromReflectedMethod(srcMethodObj));
    void* destMethod = reinterpret_cast<void*>(env->FromReflectedMethod(destMethodObj));

    int* backupMethod = new int[methodHookClassInfo.methodSize];

    // 复制内存并检查结果
    memcpy(backupMethod, srcMethod, methodHookClassInfo.methodSize);
    memcpy(srcMethod, destMethod, methodHookClassInfo.methodSize);

    LOGD("Method hook successfully: srcMethod=%p, destMethod=%p\n", srcMethod, destMethod);
    return reinterpret_cast<long>(backupMethod);
}

void loadSo(JNIEnv *env, jobject type) {
    my_dlopen("/data/data/thouger.study/libnativeLib.so", RTLD_NOW);
}

static JNINativeMethod HunterRuntimeNativeMethods[] = {
//        {"Analysis", "(Ljava/util/ArrayList;Ljava/lang/String;)V", (void *) NativeAnalysis},
//        {"SeccompSVC", "()V", (void *) NativeSeccompSVC},
//        {"native_test", "()V", (void *) test},
//        {"detect_inlineHook", "()Ljava/lang/String;", (void *) detect_inlineHook},
//        {
//                "hook_native",
//                "(Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;)J",
//                (void*)methodHook
//        },
        {"loadSo", "()V", (void *) loadSo},
};

bool fileExists(const std::string& filepath) {
    std::ifstream file(filepath);
    return file.good();  // 使用 ifstream 的good()函数来判断文件是否成功打开
}

JavaVM *vm;
JNIEnv *mEnv;
jclass NativiEngineClazz;
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *_vm, void *) {

    JNIEnv *env;
    vm = _vm;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    mEnv = env;
    NativiEngineClazz = mEnv->FindClass("com/android/study/MainActivity");
    if (env->RegisterNatives(NativiEngineClazz, HunterRuntimeNativeMethods,
                             sizeof(HunterRuntimeNativeMethods) / sizeof(HunterRuntimeNativeMethods[0])) <0) {
        LOG(INFO) << "RegisterNatives failed";
        return JNI_ERR;
    }

    methodHookClassInfo.m1 = mEnv->GetMethodID(NativiEngineClazz, "m1", "()V");
    methodHookClassInfo.m2 = mEnv->GetMethodID(NativiEngineClazz, "m2", "()V");
    methodHookClassInfo.methodSize = reinterpret_cast<size_t>(methodHookClassInfo.m2) - reinterpret_cast<size_t>(methodHookClassInfo.m1);

    const char* data = "Hello, /dev/null!";
    int fd = open("/dev/null", O_WRONLY, 0); // 打开 /dev/null

    if (fd != -1) {
        ssize_t bytes_written = write(fd, data, strlen(data)); // 写入数据
        close(fd); // 关闭文件描述符

        if (bytes_written == -1) {
            perror("write to /dev/null failed");
        } else {
            printf("Data written to /dev/null successfully.\n");
        }
    } else {
        perror("open /dev/null failed");
    }
//    hookProperties();
//    std::vector<std::string> so_list;
//    so_list.emplace_back("libdo64.so");
////    doMapsHide(false,so_list);
////    xhook_enable_debug(1);
////    int result = xhook_register("libc.so","mkdirat",(void*) new_mkdirat, (void **) &orig_mkdirat);
////    if (result != 0) {
////        LOGE("android::base::GetProperty failed %d", result);
////    }
////    xhook_refresh(1);
//
////    int rt = execl("/system/xbin/usa", "usa", "-c", "ls /", (char *) 0);
////    LOGD("execl rt %d",rt)
//    int result = execl("pwd", "pwd", (char *)0);
//
//    main();
//
//    syscall(__NR_openat, AT_FDCWD, "/data/local/tmp/test.txt", O_RDONLY, 0);
//    my_openat(AT_FDCWD, "/data/local/tmp/svc", O_RDONLY, 0);
//    openat(0,"/data/local/tmp/test.txt",O_RDONLY,0);
//
////    const char *pathname = "/proc/meminfo";
////    char buf[1024];
////    int fd = openat(0,pathname,O_RDONLY,0);
////    int len = read(fd,buf,1024);
////    buf[len] = '\0';
////    LOGD("openat read default.prop %s",buf)
//
//    mkdirat(0,"/data/local/tmp/test",0);
//
//    char value[PROP_VALUE_MAX];
//    __system_property_get("ro.product.model", value);
//    LOGD("get ro.product.model value %s",value)

//
//    SandHook::ElfImg art("libart.so");
////
//    lsplant::InitInfo initInfo {
//            .inline_hooker = inlineHooker,
//            .inline_unhooker = inlineUnHooker,
//            .art_symbol_resolver = [&art](std::string_view symbol) -> void *{
////          LOGD("libart.so art_symbol_resolver:%s",symbol)
//                return art.getSymbAddress(symbol);
//            },
//            .art_symbol_prefix_resolver = [&art](auto symbol) {
////          LOGD("libart.so art_symbol_prefix_resolver:%s",symbol)
//                return art.getSymbPrefixFirstOffset(symbol);
//            },
//    };
//
//    bool isInitLsp = lsplant::Init(env, initInfo);
//    if(!isInitLsp){
//        LOG(INFO) << "hunter runtime JNI_OnLoad init lsplant error !!! ";
//        return JNI_ERR;
//    }

//    LOG(INFO) << "mynative JNI_OnLoad init end ,init sucess !   ";

    return JNI_VERSION_1_6;
}
