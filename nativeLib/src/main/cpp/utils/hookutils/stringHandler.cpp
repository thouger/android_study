//
// Created by zhenxi on 2022/1/21.
//

#include "stringHandler.h"
#include "HookUtils.h"
#include "mylibc.h"

# define DL_INFO \
    Dl_info info; \
    dladdr((void *) __builtin_return_address(0), &info); \

# define IS_MATCH \
    for (const auto &soname: strHandlerFilterNameList) { \
        if (my_strstr(info.dli_fname, soname.c_str())) { \
            match_so_name = soname;     \

# define IS_NULL(value) \
    value == nullptr?"":value\

//typedef int (*system_property_get_t)(const char *, char *);
//void *GetSymbolAddress(const char *library_name, const char *symbol_name) {
//  void *handle = dlopen(library_name, RTLD_LAZY);
//  if (!handle) {
//    __android_log_print(ANDROID_LOG_ERROR, "MyApp", "Failed to load %s: %s",
//                        library_name, dlerror());
//    return nullptr;
//  }
//
//  void *symbol_address = dlsym(handle, symbol_name);
//  if (!symbol_address) {
//    __android_log_print(ANDROID_LOG_ERROR, "MyApp",
//                        "Failed to find symbol %s in %s: %s", symbol_name,
//                        library_name, dlerror());
//    return nullptr;
//  }
//  dlclose(handle);
//
//  return symbol_address;
//}

namespace ZhenxiRunTime::stringHandlerHook {

    static std::ofstream *hookStrHandlerOs;
    static std::list<string> strHandlerFilterNameList;
    static bool isSave = false;
    static string match_so_name = {};
    static std::mutex supernode_ids_mux_;


    static void write(const std::string &msg) {
        //写入方法加锁,防止多进程导致问题
        std::unique_lock<std::mutex> mock(supernode_ids_mux_);
        if (msg.c_str() == nullptr) {
            return;
        }
        if (msg.length() == 0) {
            return;
        }
        if (isSave) {
            if (hookStrHandlerOs != nullptr) {
                (*hookStrHandlerOs) << msg.c_str();
            }
        }
        LOG(INFO) << "[" << match_so_name << "] " << msg;
    }

//    system_property_get_t system_property_get =
//    reinterpret_cast<system_property_get_t>(
//        GetSymbolAddress("libc.so", "__system_property_get"));
//    HOOK_DEF(int, system_property_get, char *name, char *value) {
//        DL_INFO
//        IS_MATCH
//                int ret = orig_system_property_get(name, value);
//                auto strInfo = string("system_properties.Get() arg1 -> ").append(IS_NULL(name)).append(
//                        "  arg2-> ").append(IS_NULL(value));
//                _LOGD("system_properties.Get() arg1 -> %s  arg2-> %s", name, value)
//                if (ret != 0) {
//                    strInfo.append("result -> ").append(IS_NULL(value));
//                }
//                strInfo.append("\n");
//                write(strInfo);
//                return ret;
//            }
//        }
//        return orig_system_property_get(name, value);
//    }

    // char* strstr(char* h, const char* n)
    HOOK_DEF(char*, strstr, char *h, const char *n) {
        DL_INFO
        IS_MATCH
                char *ret = orig_strstr(h, n);
                auto strInfo = string("strstr() arg1 -> ").append(IS_NULL(h)).append(
                        "  arg2-> ").append(IS_NULL(n));
                if (ret != nullptr) {
                    strInfo.append("result -> ").append(IS_NULL(ret));
                }
                strInfo.append("\n");
                write(strInfo);
                return ret;
            }
        }
        return orig_strstr(h, n);
    }

    //size_t strlen(const char* __s)
    HOOK_DEF(size_t, strlen, const char *__s) {
        DL_INFO
        IS_MATCH
                size_t ret = orig_strlen(__s);
                auto strInfo = string("strstr() arg1 -> ").append(IS_NULL(__s));
                strInfo.append("\n");
                write(strInfo);
                return ret;
            }
        }
        return orig_strlen(__s);
    }

    //char* strcat(char* __dst, const char* __src);
    HOOK_DEF(char *, strcat, char *__dst, const char *__src) {
        DL_INFO
        IS_MATCH
                char *ret = orig_strcat(__dst, __src);
                auto strInfo = string("strcat() arg1 -> ").append(IS_NULL(__dst)).append(
                        "  arg2-> ").append(IS_NULL(__src));
                if (ret != nullptr) {
                    strInfo.append("result -> ").append(IS_NULL(ret));
                }
                strInfo.append("\n");
                write(strInfo);
                return ret;
            }
        }
        return orig_strcat(__dst, __src);
    }

    //int strcmp(const char* __lhs, const char* __rhs)
    HOOK_DEF(int, strcmp, const char *__lhs, const char *__rhs) {

        DL_INFO
        IS_MATCH
                int ret = orig_strcmp(__lhs, __rhs);
                auto strInfo = string("strcmp() arg1 -> ").append(IS_NULL(__lhs)).append(
                        "  arg2-> ").append(IS_NULL(__rhs));
                strInfo.append("\n");
                write(strInfo);
                return ret;
            }
        }
        return orig_strcmp(__lhs, __rhs);
    }

    //char* strcpy(char* __dst, const char* __src);
    HOOK_DEF(char *, strcpy, char *__dst, const char *__src) {
        DL_INFO
        IS_MATCH
                char *ret = orig_strcpy(__dst, __src);
                auto strInfo = string("strcpy() arg1 -> ").append(IS_NULL(__dst)).append(
                        "  arg2-> ").append(IS_NULL(__src));
                if (ret != nullptr) {
                    strInfo.append("result -> ").append(IS_NULL(ret));
                }
                strInfo.append("\n");
                write(strInfo);
                return ret;
            }
        }
        return orig_strcpy(__dst, __src);
    }
    //int sprintf(char* __s, const char* __fmt, ...)
    HOOK_DEF(int, sprintf, char *__s, const char *__fmt, char *p...) {
        DL_INFO
        IS_MATCH
                int ret = orig_sprintf(__s, __fmt, p);

                auto strInfo = string("sprintf() arg1 -> ").append(IS_NULL(__fmt)).append(
                        "  ... ").append(IS_NULL(p));

                strInfo.append("\n");
                write(strInfo);
                return ret;
            }
        }

        return orig_sprintf(__s, __fmt, p);
    }
    //int printf(const char* __fmt, ...)
    HOOK_DEF(int, printf, const char *__fmt, char *p...) {
        DL_INFO
        IS_MATCH
                int ret = orig_printf(__fmt, p);
                auto strInfo = string("sprintf() arg1 -> ").append(IS_NULL(__fmt)).append(
                        "  ... ").append(
                        IS_NULL(p));
                strInfo.append("\n");
                write(strInfo);
                return ret;
            }
        }

        return orig_printf(__fmt, p);
    }
    //char *strtok(char *str, const char *delim)
    HOOK_DEF(char *, strtok, char *str, const char *delim) {

        DL_INFO
        IS_MATCH
                char *ret = orig_strtok(str, delim);
                auto strInfo = string("strcpy() arg1 -> ").append(IS_NULL(str)).append(
                        "  arg2-> ").append(IS_NULL(delim));
                if (ret != nullptr) {
                    strInfo.append("result -> ").append(IS_NULL(ret));
                }
                strInfo.append("\n");
                write(strInfo);
                return ret;
            }
        }

        return orig_strtok(str, delim);
    }
    //char* strdup(const char* __s);
    HOOK_DEF(char*, strdup, char *__s) {

        DL_INFO
        IS_MATCH
                auto strInfo = string("strdup() arg1 -> ").append(IS_NULL(__s));
                strInfo.append("\n");
                write(strInfo);
                return orig_strdup(__s);
            }
        }
        return orig_strdup(__s);
    }

    //ssize_t read(int __fd, void* __buf, size_t __count);
    HOOK_DEF(ssize_t, read, int __fd, void *__buf, size_t __count) {

        DL_INFO
        IS_MATCH
                ssize_t ret = orig_read(__fd, __buf, __count);
                auto strInfo = string("read() arg2 -> ").append(IS_NULL((char *) __buf)).append(
                        " read count -> ").append(
                        to_string(__count));
                strInfo.append("\n");
                write(strInfo);
                return ret;

            }
        }
        return orig_read(__fd, __buf, __count);
    }

    //把 str1 和 str2 进行比较，结果取决于 LC_COLLATE 的位置设置。
    //int strcoll(const char* __lhs, const char* __rhs) __attribute_pure__;
    HOOK_DEF(int, strcoll, const char *__lhs, const char *__rhs) {
        DL_INFO
        IS_MATCH
                int ret = orig_strcoll(__lhs, __rhs);

                auto strInfo = string("strcoll() arg1 -> ").append(IS_NULL(__lhs)).append(
                        " arg2 -> ").append(IS_NULL(__rhs));
                strInfo.append("\n");
                write(strInfo);
                return ret;
            }
        }
        return orig_strcoll(__lhs, __rhs);
    }
    //size_t strxfrm(char *dest, const char *src, size_t n)
    HOOK_DEF(size_t, strxfrm, char *dest, const char *src, size_t n) {

        DL_INFO
        IS_MATCH
                size_t ret = orig_strxfrm(dest, src, n);
                auto strInfo = string("strxfrm() arg1 -> ").
                        append(IS_NULL(dest)).append(" arg2 -> ").append(IS_NULL(src));
                strInfo.append("\n");
                write(strInfo);
                return ret;
            }
        }
        return orig_strxfrm(dest, src, n);
    }
    //char* fgets(char* __buf, int __size, FILE* __fp);
    HOOK_DEF(char*, fgets, char *__buf, int __size, FILE *__fp) {
        DL_INFO
        IS_MATCH
                char *ret = orig_fgets(__buf, __size, __fp);
                auto strInfo = string("fgets() arg1 -> ").append(IS_NULL(__buf)).append(
                        "  arg2-> ").append(to_string(__size));
                if (ret != nullptr) {
                    strInfo.append("result -> ").append(IS_NULL(ret));
                }
                strInfo.append("\n");
                write(strInfo);
            }
        }
        return orig_fgets(__buf, __size, __fp);
    }
    //void *memcpy(void *destin, void *source, unsigned n);
    HOOK_DEF(void*, memcpy, void *destin, void *source, size_t __n) {
        DL_INFO
        IS_MATCH
                void *ret = orig_memcpy(destin, source, __n);

                auto strInfo = string("memcpy() arg1 -> ")
                        .append(IS_NULL((char *) destin)).append("  arg2-> ").append(
                        IS_NULL((char *) source));
                strInfo.append("\n");
                write(strInfo);
                return ret;
            }
        }

        return orig_memcpy(destin, source, __n);
    }
    //int snprintf(char* __buf, size_t __size, const char* __fmt, ...) __printflike(3, 4);
    HOOK_DEF(int, snprintf, char *__buf, size_t __size, const char *__fmt, char *p  ...) {
        DL_INFO
        IS_MATCH
                int ret = orig_snprintf(__buf, __size, __fmt, p);
                auto strInfo = string("snprintf() arg1 -> ").append(IS_NULL(__fmt)).append(
                        "  arg2-> ").append(IS_NULL(p));
                strInfo.append("\n");
                write(strInfo);
                return ret;
            }
        }

        return orig_snprintf(__buf, __size, __fmt, p);
    }
    //int vsnprintf(char* __buf, size_t __size, const char* __fmt, va_list __args) __printflike(3, 0);
    HOOK_DEF(int, vsnprintf, char *__buf, size_t __size, const char *__fmt, va_list __args) {
        DL_INFO
        IS_MATCH
                int ret = orig_vsnprintf(__buf, __size, __fmt, __args);

                auto strInfo = string("vsnprintf() arg1 -> ").append(IS_NULL(__fmt));
                strInfo.append("\n");
                write(strInfo);
                return ret;
            }
        }

        return orig_vsnprintf(__buf, __size, __fmt, __args);
    }
}


using namespace ZhenxiRunTime::stringHandlerHook;

void stringHandler::init() {
    _LOGD("stringHandler init");

  //获取system_properties.Get 函数地址
//  void* system_properties_get=getSymByELF("libc.so", __system_property_get);
//  if (system_properties_get == nullptr) {
//    LOG(ERROR) << "strhadler get system_properties_get == null   ";
//    return;
//  }
//  _LOGD("system_properties_get = %p", system_properties_get);

    void *handle = dlopen("libc.so", RTLD_NOW);

    if (handle == nullptr) {
        LOG(ERROR) << "strhadler get handle == null   ";
        return;
    }

//    HOOK_SYMBOL_DOBBY(handle, system_property_get);

    HOOK_SYMBOL_DOBBY(handle, strstr);
    HOOK_SYMBOL_DOBBY(handle, strcmp);
    HOOK_SYMBOL_DOBBY(handle, strcpy);
    HOOK_SYMBOL_DOBBY(handle, strdup);

    HOOK_SYMBOL_DOBBY(handle, strxfrm);
    HOOK_SYMBOL_DOBBY(handle, strtok);

    HOOK_SYMBOL_DOBBY(handle, memcpy);
    HOOK_SYMBOL_DOBBY(handle, read);

    HOOK_SYMBOL_DOBBY(handle, sprintf);
    HOOK_SYMBOL_DOBBY(handle, printf);
    HOOK_SYMBOL_DOBBY(handle, snprintf);
    HOOK_SYMBOL_DOBBY(handle, vsnprintf);



    //strcat底层走的是strcpy
    //https://cs.android.com/android/platform/superproject/+/master:external/musl/src/string/strcat.c;l=5?q=strcat
    HOOK_SYMBOL_DOBBY(handle, strcat);
    HOOK_SYMBOL_DOBBY(handle, strlen);
    HOOK_SYMBOL_DOBBY(handle, fgets);

//

    //strcoll底层是strcmp
    //https://cs.android.com/android/platform/superproject/+/master:external/musl/src/locale/strcoll.c;l=12;drc=master?q=strcoll&ss=android%2Fplatform%2Fsuperproject
    HOOK_SYMBOL_DOBBY(handle, strcoll);



    dlclose(handle);

    LOG(INFO) << ">>>>>>>>>>>>> string handler init sucess !  ";
}

void stringHandler::hookStrHandler(const std::list<string> &filter_list, std::ofstream *os) {
    strHandlerFilterNameList = filter_list;
    if (os != nullptr) {
        isSave = true;
        hookStrHandlerOs = os;
    } else {
        LOG(ERROR) << ">>>>>>>>>>>>> string handler init fail hookStrHandlerOs == null  ";
    }
    init();
}

[[maybe_unused]] void stringHandler::stopjnitrace() {
    strHandlerFilterNameList.clear();
    if (hookStrHandlerOs != nullptr) {
        if (hookStrHandlerOs->is_open()) {
            hookStrHandlerOs->close();
        }
        delete hookStrHandlerOs;
    }
    isSave = false;
}