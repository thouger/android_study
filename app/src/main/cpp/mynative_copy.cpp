#include "mynative.h"
#include <dobby.h>
#include <iostream>
#include <sstream>
#include <execinfo.h>

#include "../../../../nativelib/src/main/cpp/hook/xhook/include/xhook.h"
using namespace std;

void *GetSymbolAddress(const char *library_name, const char *symbol_name) {
  void *handle = dlopen(library_name, RTLD_LAZY);
  if (!handle) {
    __android_log_print(ANDROID_LOG_ERROR, "MyApp", "Failed to load %s: %s",
                        library_name, dlerror());
    return nullptr;
  }

  void *symbol_address = dlsym(handle, symbol_name);
  if (!symbol_address) {
    __android_log_print(ANDROID_LOG_ERROR, "MyApp",
                        "Failed to find symbol %s in %s: %s", symbol_name,
                        library_name, dlerror());
    return nullptr;
  }
  dlclose(handle);

  return symbol_address;
}

void listmacaddrs(JNIEnv *env, jobject thiz) {
  struct ifaddrs *ifap, *ifaptr;

  if (myGetifaddrs(&ifap) == 0) {
    for (ifaptr = ifap; ifaptr != nullptr; ifaptr = (ifaptr)->ifa_next) {
      if(ifaptr->ifa_addr!= nullptr) {
        sa_family_t family = ((ifaptr)->ifa_addr)->sa_family;
        if (family == AF_PACKET) {
          //get mac info
          char macp[INET6_ADDRSTRLEN];
          auto *sockadd = (struct sockaddr_ll *) (ifaptr->ifa_addr);
          int i;
          int len = 0;
          for (i = 0; i < 6; i++) {
            len += sprintf(macp + len, "%02X%s", sockadd->sll_addr[i],( i < 5 ? ":" : ""));
          }
          LOGE("AF_PACKET  %s  %s  ",(ifaptr)->ifa_name,macp)
//                    if(strcmp(ifaptr->ifa_name,"wlan0")== 0){
//                        LOGE("%s  %s  ",(ifaptr)->ifa_name,macp)
//                        freeifaddrs(ifap);
//                        return 1;
//                    }
        } else if(family == AF_INET || family == AF_INET6){
          //get v4 & v6 info
          char host[NI_MAXHOST];
          int ret = getnameinfo(ifaptr->ifa_addr,
                                (family == AF_INET) ? sizeof(struct sockaddr_in) :
                                sizeof(struct sockaddr_in6),
                                host, NI_MAXHOST,
                                nullptr, 0, NI_NUMERICHOST);
          if (ret != 0) {
            LOGE("AF_INET6  getnameinfo() failed   %s  ",gai_strerror(ret))
          }
          LOGE("AF_INET6 %s %s  ",(ifaptr)->ifa_name,host)
        }
      }

    }
    freeifaddrs(ifap);
  } else {
  }
}

void *inlineHooker(void *targetFunc, void *replaceFunc) {
  auto pageSize = sysconf(_SC_PAGE_SIZE);
  auto funcAddress = ((uintptr_t) targetFunc) & (-pageSize);
  mprotect((void *) funcAddress, pageSize, PROT_READ | PROT_WRITE | PROT_EXEC);

  void *originalFunc;
  if (HookUtils::Hooker((void*)targetFunc,(void*)replaceFunc,(void**)&originalFunc)) {
    return originalFunc;
  }
  return nullptr;
}

bool inlineUnHooker(void *originalFunc) {
  return HookUtils::unHook(originalFunc);
}

int (*orig__ZN16SystemProperties3GetEPKcPc)(const char *zhis, char *name,char *value);
int _ZN16SystemProperties3GetEPKcPc(const char *zhis, char *name,char *value) {
  int result = orig__ZN16SystemProperties3GetEPKcPc(zhis, name,value);

  if (result < 0) {
    //failed
    return result;
  }
  LOGD("system_property_get name: %s value: %s",name,value);
//
//  PropertiesMockItem *propertiesMockItem = query_mock_properties(__name);
//  if (propertiesMockItem == nullptr) {
//    return result;
//  }
//
//  if (propertiesMockItem->properties_value == nullptr) {
//    result = -1;
//    __value[0] = '\0';
//    return result;
//  }
//
//  result = propertiesMockItem->value_length;
//  strcpy(__value, propertiesMockItem->properties_value);
  return result;
}

int (*orig_system_property_read)(const char *zhis,const prop_info* pi, char* name, char* value);
int system_property_read(const char *zhis,const prop_info* pi, char* name, char* value) {
  int result = orig_system_property_read(zhis,pi,name,value);
  LOGD("success system_property_read name: %s value: %s",name,value)
  return result;
}

//void *addr = GetSymbolAddress("libc.so", "__system_property_find");
//const prop_info* (*orig_system_property_find)(const char *zhis,const char* name);
//const prop_info* system_property_find(const char *zhis,const char* name) {
//  const prop_info* result = orig_system_property_find(zhis,name);
//
//  auto pageSize = sysconf(_SC_PAGE_SIZE);
//  auto funcAddress = ((uintptr_t) addr) & (-pageSize);
//  mprotect((void *) funcAddress, pageSize, PROT_READ | PROT_WRITE | PROT_EXEC);
//  LOGD("success system_property_find name: %s",name)
//  return result;
//}

  std::string (*orig_GetProperty)(const std::string& key, const std::string& default_value);
  std::string GetProperty(const std::string& key, const std::string& default_value) {
  std::string property_value = orig_GetProperty(key, default_value);
  LOGD("success GetProperty %s", key.c_str());
  return property_value;
}

std::string (*orig__GetProperty)(const char *zhis,const std::string& key, const std::string& default_value);
std::string _GetProperty(const char *zhis,const std::string& key, const std::string& default_value) {
  std::string property_value = orig__GetProperty(zhis,key, default_value);
  LOGD("success GetProperty %s", key.c_str());
  return property_value;
}

void hookProperties() {
//  xhook_enable_debug(1);
//  int result = xhook_register("libandroid_runtime.so", "_ZN7android4base11GetPropertyERKNSt3__112basic_stringIcNS1_11char_traitsIcEENS1_9allocatorIcEEEES9_",
//                 (void*) GetProperty, (void **) &orig_GetProperty);
//  xhook_refresh(0);

  //不成功，原因未知
  void* addr = DobbySymbolResolver("/system/lib64/libandroid_runtime.so", "_ZN7android4base11GetPropertyERKNSt3__112basic_stringIcNS1_11char_traitsIcEENS1_9allocatorIcEEEES9_");
  LOGD("addr: %p",addr);
  auto pageSize = sysconf(_SC_PAGE_SIZE);
  auto funcAddress = ((uintptr_t) addr) & (-pageSize);
  mprotect((void *) funcAddress, pageSize, PROT_READ | PROT_WRITE | PROT_EXEC);
  DobbyHook(addr, (void *) GetProperty, (void **) &orig_GetProperty);
  DobbyHook(addr, (void *) _GetProperty, (void **) &orig__GetProperty);



//  SandHook::ElfImg libc("libc.so");
//  void* addr = libc.getSymbAddress("_ZN16SystemProperties3GetEPKcPc");
//  orig__ZN16SystemProperties3GetEPKcPc = (int (*)(const char *, char *, char *)) inlineHooker(addr, reinterpret_cast<void*>(_ZN16SystemProperties3GetEPKcPc));

  // hook SystemProperties::Read
//    void *addr = libc.getSymbAddress("_ZN16SystemProperties4ReadEPK9prop_infoPcS3_");
//    orig_system_property_read = (int (*)(const char *, const prop_info *, char *, char *)) inlineHooker(addr, reinterpret_cast<void*>(system_property_read));

  //这个函数有点特别
//  addr = libc.getSymbAddress("_ZN16SystemProperties4FindEPKc");
//  orig_system_property_find = (const prop_info* (*)(const char *, const char *)) inlineHooker(addr, reinterpret_cast<void*>(system_property_find));

  }

void NativeAnalysis(JNIEnv *env, jobject type,jobject filterList,jstring filepath) {
    const auto &clist = parse::jlist2clist(env, filterList);
    string path;

    //jni hook
    if (filepath != nullptr) {
        path = parse::jstring2str(env, filepath);
        LOGD("nativeAnalyse open file  %s  ",path.c_str())
        auto *saveOs = new ofstream();
        saveOs->open(path, ios::app);
        if (!saveOs->is_open()) {
          LOG(INFO) << "nativeAnalyse open file error  " << path;
          LOG(INFO) << "nativeAnalyse open file error  " << path;
          LOG(INFO) << "nativeAnalyse open file error  " << path;
            return;
        }
        Jnitrace::startjnitrace(env, clist, saveOs);
        stringHandler::hookStrHandler(clist, saveOs);
        //invokePrintf::HookJNIInvoke(saveOs);
    } else {
        Jnitrace::startjnitrace(env, clist, nullptr);
        stringHandler::hookStrHandler(clist, nullptr);
        //invokePrintf::HookJNIInvoke(nullptr);
    }
}

static JNINativeMethod HunterRuntimeNativeMethods[] = {
    {"Analysis", "(Ljava/util/ArrayList;Ljava/lang/String;)V",(void *) NativeAnalysis},
    {"listmacaddrs", "()Ljava/lang/String;",(void *) listmacaddrs},
};

jclass NativiEngineClazz;
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *_vm, void *) {
  hookProperties();

  char value[PROP_VALUE_MAX];
  __system_property_get("ro.product.model", value);
  LOGD("get ro.product.model value %s",value)

    JNIEnv *env;
    vm = _vm;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }
    mEnv = env;
    NativiEngineClazz = mEnv->FindClass("thouger/study/MainActivity");
    if (env->RegisterNatives(NativiEngineClazz, HunterRuntimeNativeMethods,
                             sizeof(HunterRuntimeNativeMethods) / sizeof(HunterRuntimeNativeMethods[0])) <0) {
      LOG(INFO) << "RegisterNatives failed";
        return JNI_ERR;
    }

    SandHook::ElfImg art("libart.so");
    lsplant::InitInfo initInfo {
        .inline_hooker = inlineHooker,
        .inline_unhooker = inlineUnHooker,
        .art_symbol_resolver = [&art](std::string_view symbol) -> void *{
//          LOGD("libart.so art_symbol_resolver:%s",symbol)
          return art.getSymbAddress(symbol);
        },
        .art_symbol_prefix_resolver = [&art](auto symbol) {
//          LOGD("libart.so art_symbol_prefix_resolver:%s",symbol)
          return art.getSymbPrefixFirstOffset(symbol);
        },
    };

    bool isInitLsp = lsplant::Init(env, initInfo);
    if(!isInitLsp){
      LOG(INFO) << "hunter runtime JNI_OnLoad init lsplant error !!! ";
      return JNI_ERR;
    }

    LOG(INFO) << "mynative JNI_OnLoad init end ,init sucess !   ";


//  SandHook::ElfImg art1("libandroid_runtime.so");
//  lsplant::InitInfo initInfo1 {
//      .inline_hooker = inlineHooker,
//      .inline_unhooker = inlineUnHooker,
//      .art_symbol_resolver = [&art1](std::string_view symbol) -> void *{
//          LOGD("libart.so art_symbol_resolver:%s",symbol)
//        return art1.getSymbAddress(symbol);
//      },
//      .art_symbol_prefix_resolver = [&art1](auto symbol) {
//          LOGD("libart.so art_symbol_prefix_resolver:%s",symbol)
//        return art1.getSymbPrefixFirstOffset(symbol);
//      },
//  };
//  isInitLsp = lsplant::Init(env, initInfo1);
//  if(!isInitLsp){
//    LOG(INFO) << "hunter runtime JNI_OnLoad init lsplant error !!! ";
//    return JNI_ERR;
//  }
//
//  LOG(INFO) << "mynative JNI_OnLoad init end ,init sucess !   ";


  return JNI_VERSION_1_6;
}

extern "C" {
JNIEXPORT void JNICALL Java_thouger_study_MainActivity_test(
    JNIEnv *env, jobject thiz) {
}
}