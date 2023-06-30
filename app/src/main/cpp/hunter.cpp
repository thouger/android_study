//
// Created by thouger on 2023/4/26.
//

#include "hunter.h"

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

extern "C"
JNIEXPORT jobject JNICALL
Java_thouger_study_hunter_XposedBridge_hook0(JNIEnv *env,
                                             jclass clazz,
                                             jobject hooker_object,
                                             jobject originalMethod,
                                             jobject callbackMethod) {
  LOGD("hunter hook start")
  return lsplant::Hook(env, originalMethod, hooker_object, callbackMethod);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_thouger_study_hunter_XposedBridge_unhook0(JNIEnv *env, jclass clazz, jobject originalMethod) {
  LOGD("hunter unhook start")
  return lsplant::UnHook(env, originalMethod);
}

static JNINativeMethod HunterRuntimeNativeMethods[] = {
    {"Analysis", "(Ljava/util/ArrayList;Ljava/lang/String;)V", (void *) NativeAnalysis},
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

void hookProperties() {
//  xhook_enable_debug(1);
//  int result = xhook_register("libandroid_runtime.so", "_ZN7android4base11GetPropertyERKNSt3__112basic_stringIcNS1_11char_traitsIcEENS1_9allocatorIcEEEES9_",
//                              (void*) GetProperty, (void **) &orig_GetProperty);
//  if (result != 0) {
//    LOGE("android::base::GetProperty failed %d", result);
//  }
//  xhook_refresh(0);

  hook_function_xhook("libandroid_runtime.so", "_ZN7android4base11GetPropertyERKNSt3__112basic_stringIcNS1_11char_traitsIcEENS1_9allocatorIcEEEES9_",
                              (void*) GetProperty, (void **) &orig_GetProperty);

//  SandHook::ElfImg libc("libc.so");
//  void* addr = libc.getSymbAddress("_ZN16SystemProperties3GetEPKcPc");
//  orig__ZN16SystemProperties3GetEPKcPc = (int (*)(const char *, char *, char *)) inlineHooker(addr, reinterpret_cast<void*>(_ZN16SystemProperties3GetEPKcPc));

  // hook SystemProperties::Read
//    void *addr = libc.getSymbAddress("_ZN16SystemProperties4ReadEPK9prop_infoPcS3_");
//    orig_system_property_read = (int (*)(const char *, const prop_info *, char *, char *)) inlineHooker(addr, reinterpret_cast<void*>(system_property_read));

//  addr = libc.getSymbAddress("_ZN16SystemProperties4FindEPKc");
//  orig_system_property_find = (const prop_info* (*)(const char *, const char *)) inlineHooker(addr, reinterpret_cast<void*>(system_property_find));
}

jclass NativiEngineClazz;
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *_vm, void *) {

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

  return JNI_VERSION_1_6;
}