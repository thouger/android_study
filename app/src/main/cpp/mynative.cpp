#include "mynative.h"
#include "xhook/xhook.h"
#include <dobby.h>

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


void NativeAnalysis(JNIEnv *env, jobject type,jobject filterList,jstring filepath) {
    const auto &clist = parse::jlist2clist(env, filterList);
    string path;
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

int (*orig__system_property_get)(const char *__name, char *__value);
//我们之前从native反射到java，有很大问题。华为有一个机型直接卡白屏。目前原因未知，我们将属性替换重心逻辑由java层迁移到native层
//这样会损失一些灵活性，因为不能回调java的handler了。所有替换项需要提前设置
int __system_property_get(const char *__name, char *__value) {
  int result = orig__system_property_get(__name, __value);
  if (result < 0) {
    //failed
    return result;
  }
  LOGD("system_property_get name:%s value:%s result:%d",__name,__value,result);
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

typedef int (*_ZN16SystemProperties3GetEPKcPc)(const char *, char *);

jclass NativiEngineClazz;
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *_vm, void *) {
  void* addr = GetSymbolAddress("libc.so", "__system_property_get");
  DobbyHook(addr, reinterpret_cast<void*>(__system_property_get), reinterpret_cast<void**>(&orig__system_property_get));
//  inlineUnHooker(addr);

  char value[PROP_VALUE_MAX];
  __system_property_get("ro.product.model", value);
  LOGD("JNI_OnLoad111111111111111 %s",value)

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
  //        LOGD("libart.so art_symbol_resolver:%s",symbol)
          return art.getSymbAddress(symbol);
        },
        .art_symbol_prefix_resolver = [&art](auto symbol) {
  //        LOGD("libart.so art_symbol_prefix_resolver:%s",symbol)
          return art.getSymbPrefixFirstOffset(symbol);
        },
    };

//  SandHook::ElfImg libc("libc.so");
//  void* elfAddr = libc.getSymbAddress("_ZN16SystemProperties3GetEPKcPc");
//  LOGD("get libc.so system_properties.Get:%p",elfAddr);

  bool isInitLsp = lsplant::Init(env, initInfo);
  if(!isInitLsp){
    LOG(INFO) << "hunter runtime JNI_OnLoad init lsplant error !!! ";
    return JNI_ERR;
  }

    LOG(INFO) << "mynative JNI_OnLoad init end ,init sucess !   ";


  return JNI_VERSION_1_6;
}

extern "C" {
JNIEXPORT jint JNICALL Java_thouger_study_MainActivity_sumArray(
    JNIEnv *env, jobject thiz, jintArray array) {
  LOG(INFO) << "sumArray called";
  jint *elements = env->GetIntArrayElements(array, NULL);
  jsize length = env->GetArrayLength(array);

  int sum = 0;
  for (int i = 0; i < length; i++) {
    sum += elements[i];
  }

  env->ReleaseIntArrayElements(array, elements, 0);

  return sum;
}

//void test_dlsym() {
//  const char *property_name = "ro.build.version.sdk";
//  char sdk_version[PROP_VALUE_MAX] = {0};
//
//  system_property_get_t system_property_get =
//      reinterpret_cast<system_property_get_t>(
//          GetSymbolAddress("libc.so", "__system_property_get"));
//  if (!system_property_get) {
//    __android_log_print(ANDROID_LOG_ERROR, "MyApp",
//                        "Failed to get the address of __system_property_get");
//    return;
//  }
//
//  system_property_get(property_name, sdk_version);
//  __android_log_print(ANDROID_LOG_INFO, "MyApp", "SDK version is %s",
//                      sdk_version);
//  }
//}

//没成功
void hookProperties() {
    SandHook::ElfImg libc("libc.so");
    void *elfAddr = libc.getSymbAddress("_ZN16SystemProperties3GetEPKcPc");
    LOGD("_ZN16SystemProperties3GetEPKcPc address is:%p", elfAddr);
    auto funcPtr = reinterpret_cast<_ZN16SystemProperties3GetEPKcPc>(elfAddr); // 强制转换为函数指针类型
    char value[PROP_VALUE_MAX]; // 分配内存空间
    memset(value, 0, PROP_VALUE_MAX); // 将内存空间清零
    int ret = funcPtr("ro.product.model", value);
    if (ret == 0) {
      // 获取属性值成功，打印结果
      LOGD("ro.product.model=%s", value);
    } else {
      // 获取属性值失败
      LOGD("Failed to get ro.product.model");
    }
  }
}