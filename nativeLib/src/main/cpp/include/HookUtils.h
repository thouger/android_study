//
// Created by zhenxi on 2021/5/16.
//


#include "ZhenxiLogging.h"
#include "dlfcn_compat.h"

#define HOOK_SYMBOL_DOBBY(handle, func)  \
  hook_function_dobby(handle, #func, (void*) new_##func, (void**) &orig_##func) \

#define HOOK_DEF(ret, func, ...) \
  ret (*orig_##func)(__VA_ARGS__)=nullptr; \
  ret new_##func(__VA_ARGS__)


#ifndef VMP_HOOKUTILS_H
#define VMP_HOOKUTILS_H


class HookUtils {
public:

    static bool Hooker(void *dysym, void *repl, void **org);

    static bool Hooker(void *handler, const char *dysym, void *repl, void **org);

    static bool Hooker(void *dysym, void *repl, void **org, const char *dynSymName);

    static bool Hooker(const char *libName, const char *dysym, void *repl, void **org);

    static bool unHook(void *sym);

};

void hook_function_dobby(void *handle, const char *symbol, void *new_func, void **old_func);
void hook_function_xhook(const char *pathname_regex_str, const char *symbol,
                         void *new_func, void **old_func);
#endif //VMP_HOOKUTILS_H
