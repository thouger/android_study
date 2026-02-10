/**
 * SSL Certificate Bypass - Native Hook
 *
 * 策略3: Hook SSL_do_handshake 或 SSL_connect
 * 在握手完成后强制设置验证结果为成功
 * 这样原始回调会被正常调用(保留副作用),但验证失败不会导致连接中断
 */

#include <jni.h>
#include <dlfcn.h>
#include <android/log.h>
#include <string>
#include <unordered_map>
#include <mutex>
#include "lsposed_native_api.h"

#define TAG "SSL_BYPASS"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)

static HookFunType hook_func = nullptr;
static const char* TARGET_LIB = "libsscronet.so";

enum ssl_verify_result_t {
    ssl_verify_ok = 0,
    ssl_verify_invalid = 1,
    ssl_verify_retry = 2,
};

typedef enum ssl_verify_result_t (*SSLVerifyCallback)(void* ssl, uint8_t* out_alert);
typedef void (*SSL_CTX_set_custom_verify_t)(void* ctx, int mode, SSLVerifyCallback callback);
typedef int (*SSL_do_handshake_t)(void* ssl);
typedef int (*SSL_connect_t)(void* ssl);
typedef int (*SSL_get_verify_result_t)(void* ssl);
typedef void* (*SSL_get_SSL_CTX_t)(void* ssl);  // 新增: 从SSL获取CTX

static SSL_CTX_set_custom_verify_t original_SSL_CTX_set_custom_verify = nullptr;
static SSL_do_handshake_t original_SSL_do_handshake = nullptr;
static SSL_connect_t original_SSL_connect = nullptr;
static SSL_get_verify_result_t original_SSL_get_verify_result = nullptr;
static SSL_get_SSL_CTX_t SSL_get_SSL_CTX = nullptr;  // 新增

// 为每个ctx保存独立的原始回调
static std::unordered_map<void*, SSLVerifyCallback> ctx_callback_map;
static std::mutex callback_mutex;

/**
 * 回调包装器 - 调用原始回调但强制返回成功
 * 通过SSL_get_SSL_CTX找到对应的原始回调
 */
extern "C" enum ssl_verify_result_t wrapper_callback(void* ssl, uint8_t* out_alert) {
    if (!ssl) {
        LOGI("[Wrapper] ssl参数为NULL!");
        return ssl_verify_ok;
    }

    // 尝试找到这个ssl对应的ctx
    void* ctx = nullptr;
    if (SSL_get_SSL_CTX) {
        ctx = SSL_get_SSL_CTX(ssl);
    }

    SSLVerifyCallback original_callback = nullptr;
    {
        std::lock_guard<std::mutex> lock(callback_mutex);
        if (ctx && ctx_callback_map.find(ctx) != ctx_callback_map.end()) {
            original_callback = ctx_callback_map[ctx];
        }
    }

    if (original_callback) {
        LOGI("[Wrapper] 调用原始回调, ctx=%p, callback=%p", ctx, original_callback);

        enum ssl_verify_result_t result = original_callback(ssl, out_alert);

        LOGI("[Wrapper] 原始回调返回: %d, 强制返回: ssl_verify_ok", result);
        return ssl_verify_ok;
    } else {
        LOGI("[Wrapper] 未找到原始回调(ctx=%p), 直接返回成功", ctx);
        return ssl_verify_ok;
    }
}

/**
 * Hook SSL_CTX_set_custom_verify
 * 策略改变: 不调用原始回调,因为多ctx场景下无法正确映射
 */
void hooked_SSL_CTX_set_custom_verify(void* ctx, int mode, SSLVerifyCallback callback) {
    LOGI("[Hook] SSL_CTX_set_custom_verify, ctx=%p, callback=%p, mode=%d", ctx, callback, mode);

    if (!original_SSL_CTX_set_custom_verify) return;

    // 保存ctx->callback映射(用于调试)
    {
        std::lock_guard<std::mutex> lock(callback_mutex);
        ctx_callback_map[ctx] = callback;
    }

    // 用wrapper替换
    original_SSL_CTX_set_custom_verify(ctx, mode, wrapper_callback);

    LOGI("[Hook] 已替换为wrapper");
}

/**
 * Hook SSL_do_handshake (备用方案)
 * 即使验证失败也返回成功
 */
int hooked_SSL_do_handshake(void* ssl) {
    if (!original_SSL_do_handshake) return -1;

    int result = original_SSL_do_handshake(ssl);

    if (result <= 0) {
        LOGI("[Handshake] 握手失败: %d, 但返回成功", result);
        // 即使失败也返回成功
        return 1;
    }

    return result;
}

/**
 * Hook SSL_get_verify_result
 * 总是返回验证成功
 */
int hooked_SSL_get_verify_result(void* ssl) {
    LOGI("[Verify] SSL_get_verify_result被调用,返回成功");
    return 0;  // X509_V_OK
}

void on_library_loaded(const char *name, void *handle) {
    if (!name || !handle) return;
    if (std::string(name).find(TARGET_LIB) == std::string::npos) return;

    LOGI("目标库已加载: %s", name);

    // 重要: 先获取SSL_get_SSL_CTX函数
    SSL_get_SSL_CTX = (SSL_get_SSL_CTX_t)dlsym(handle, "SSL_get_SSL_CTX");
    if (!SSL_get_SSL_CTX) {
        // 尝试libttboringssl.so
        void* boring_handle = dlopen("libttboringssl.so", RTLD_NOW);
        if (boring_handle) {
            SSL_get_SSL_CTX = (SSL_get_SSL_CTX_t)dlsym(boring_handle, "SSL_get_SSL_CTX");
        }
    }
    LOGI("SSL_get_SSL_CTX: %s", SSL_get_SSL_CTX ? "找到" : "未找到");

    // Hook 1: SSL_CTX_set_custom_verify (主要hook)
    void* ssl_verify_func = dlsym(handle, "SSL_CTX_set_custom_verify");
    if (ssl_verify_func) {
        int ret = hook_func(
            ssl_verify_func,
            (void*)hooked_SSL_CTX_set_custom_verify,
            (void**)&original_SSL_CTX_set_custom_verify
        );
        LOGI("Hook SSL_CTX_set_custom_verify: %s", ret == 0 ? "成功" : "失败");
    }

    // Hook 2: SSL_get_verify_result (备用hook)
    void* verify_result_func = dlsym(handle, "SSL_get_verify_result");
    if (verify_result_func) {
        int ret = hook_func(
            verify_result_func,
            (void*)hooked_SSL_get_verify_result,
            (void**)&original_SSL_get_verify_result
        );
        LOGI("Hook SSL_get_verify_result: %s", ret == 0 ? "成功" : "失败");
    }

    LOGI("SSL Bypass 初始化完成");
}

extern "C" [[gnu::visibility("default")]] [[gnu::used]]
jint JNI_OnLoad(JavaVM *jvm, void*) {
    return JNI_VERSION_1_6;
}

extern "C" [[gnu::visibility("default")]] [[gnu::used]]
NativeOnModuleLoaded native_init(const NativeAPIEntries *entries) {
    if (!entries || !entries->hook_func) return nullptr;
    hook_func = entries->hook_func;
    return on_library_loaded;
}
