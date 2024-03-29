//
// Created by zhenxi on 2021/5/16.
//


#include "HookUtils.h"
/**
 * 全局应该只有这一个地方导入dobby.h
 * dobby hook入口
 */
#include "dobby.h"
/**
 * sandhook里面的inlinehook
 * 异常hook的实现
 */
#include <inline_hook.h>

#include "xhook/include/xhook.h"



/**
 * 保存全部hook的地址,防止某一个方法被多次Hook
 */
static list<void *> *hookedList = nullptr;

/**
 * 取消函数Hook
 * @param sym  被Hook函数地址
 */
bool HookUtils::unHook(void *sym) {
    bool ret = DobbyDestroy(sym) == 0;
    if(hookedList!= nullptr){
        hookedList->remove(sym);
    }
    return ret;
}
/**
 * Hook的整体封装,这个方法可以切换别的Hook框架
 * 先尝试用DobbyHook 如果Hook失败的话用InlineHook二次尝试
 *
 * @param dysym  被Hook函数地址
 * @param repl   替换函数
 * @param org    原始函数指针
 * @return 是否Hook成功
 */
bool HookUtils::Hooker(void *dysym, void *newrep, void **org) {
    if (dysym == nullptr) {
        LOG(ERROR) << "dobby hook org == null ";
        return false;
    }
    if (hookedList == nullptr) {
        hookedList = new list<void *>();
    }

    //如果这个地址已经被Hook了 。也有可能返回失败 。dobby 会提示 already been hooked 。
    for (void *ptr: *hookedList) {
        if (ptr == dysym) {
            LOG(INFO) << "hook utils hook success !:" << dysym;
            //如果保存了这个地址,说明之前hook成功过,我们也认为hook成功
            return true;
        }
    }

    bool ret = DobbyHook(dysym,
                         reinterpret_cast<dobby_dummy_func_t>(newrep),
                         reinterpret_cast<dobby_dummy_func_t *>(org)) == 0;
    if (ret) {
        LOG(INFO) << "dobby hook utils hook success !:" << dysym;
        //将地址添加到已经hook的列表,防止这个地址被多次hook
        hookedList->push_back(dysym);
        return true;
    }

    //如果dobby hook失败了,采用sandhook异常hook进行补救,
    ret = SandHook::Inline::InlineHookImpl(dysym, newrep, org);
    if (ret) {
        LOG(INFO) << "sandhook hook utils hook success !:" << dysym;
        hookedList->push_back(dysym);
        return true;
    }

    //如果sandhook sign hook 也失败了,我们采用dobby附近插装去hook
    dobby_enable_near_branch_trampoline();
    //二次hook
    ret = DobbyHook(dysym,
                    reinterpret_cast<dobby_dummy_func_t>(newrep),
                    reinterpret_cast<dobby_dummy_func_t *>(org)) == 0;
    //关闭附近插装
    dobby_disable_near_branch_trampoline();
    if (!ret) {
        LOG(ERROR) << "!!!!!!!!!!!!!!!  HookUtils hook error   ";
        return false;
    }
    LOG(INFO) << "dobby hook utils hook success !:" << dysym;
    hookedList->push_back(dysym);
    return ret;

}

bool HookUtils::Hooker(void *handler, const char *dysym, void *repl, void **org) {
    void *sym = dlsym_compat(handler, dysym);
    if (sym == nullptr) {
        LOG(ERROR) << "HookUtils hook sym == null    " << dysym;
        return false;
    }
    bool isSuccess = Hooker(sym, repl, org);
    if (!isSuccess) {
        LOG(ERROR) << "HookUtils hook sym error   " << dysym;
    }
    return isSuccess;
}

/**
 *
 * @param dysym  被Hook函数地址
 * @param repl   替换函数
 * @param org    原始函数指针
 * @param dynSymName  函数的符号,主要为了在失败时候方便打印那个函数失败了
 * @return 是否Hook成功
 */
bool HookUtils::Hooker(void *dysym, void *repl, void **org, const char *dynSymName) {
    if (Hooker(dysym, repl, org)) {
        return true;
    }
    return false;
}

/**
 *
 * @param libName  So的路径
 * @param dysym  函数的符号
 * @param repl   替换函数
 * @param org    原始函数指针
 * @return 是否Hook成功
 */
bool HookUtils::Hooker(const char *libName, const char *dysym, void *repl, void **org) {
    void *sym = getSymCompat(libName, dysym);
    if (sym == nullptr) {
        LOG(ERROR) << "Hooker  get sym error " << dysym;
        return false;
    }
    bool isSucess = Hooker(sym, repl, org);
    if (!isSucess) {
        LOG(ERROR) << "Hooker hook error " << libName << " " << dysym;
    }
    return isSucess;
}

void hook_function_dobby(void *handle, const char *symbol, void *new_func, void **old_func) {
    void *addr = dlsym(handle, symbol);
    if (addr == nullptr) {
        _LOGE("hook_function_dobby not found symbol : %s", symbol)
        return;
    }
    if (!HookUtils::Hooker(addr, new_func, old_func)) {
        _LOGE(">>>>>>>>>>> io  hook %s fail !", symbol)
    }
}

void hook_function_xhook(const char *pathname_regex_str, const char *symbol,
                         void *new_func, void **old_func){
    xhook_enable_debug(1);
    int result = xhook_register(pathname_regex_str, symbol,
                                (void*) new_func, (void **) &old_func);
    if (result != 0) {
        _LOGE("android::base::GetProperty failed %d", result);
    }
    xhook_refresh(0);
}

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