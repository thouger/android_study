package com.hook;

import android.util.Log;

/**
 * 加密参数追踪器 - 统一版本
 * 用于Hook系统加密相关方法，追踪加密算法、密钥、IV、明文、密文等参数
 *
 * 使用方法：
 * 1. Hook所有加密相关（无过滤）
 *    CryptoTracker.hookAll(true, null);
 *
 * 2. Hook所有加密相关（带关键字过滤，只输出包含关键字的结果）
 *    CryptoTracker.hookAll(true, "encrypt");
 *    CryptoTracker.hookAll(true, "password");
 *    CryptoTracker.hookAll(true, "token");
 *
 * 3. 只Hook加密算法（不包含网络）
 *    CryptoTracker.hookCryptoOnly(true, "key");
 */
public class CryptoTracker {

    /**
     * Hook OkHttp网络请求响应
     */
    public static class OkHttp {

        /**
         * Hook Response.Body 的读取方法
         * @param showStack 是否显示调用栈
         * @param keyword 关键字过滤（为null则不过滤）
         */
        public static void hookResponseBody(boolean showStack, String keyword) {
            try {
                if (keyword != null) {
                    HookCore.hookMethod("okhttp3.ResponseBody", "string", showStack,
                        new HookUtils.ResultKeywordFilter(keyword));
                    HookCore.hookMethod("okhttp3.ResponseBody", "bytes", showStack,
                        new HookUtils.ResultKeywordFilter(keyword));
                } else {
                    HookCore.hookMethod("okhttp3.ResponseBody", "string", showStack);
                    HookCore.hookMethod("okhttp3.ResponseBody", "bytes", showStack);
                }
                Log.d(HookUtils.TAG, "[✓] Hooked okhttp3.ResponseBody" + (keyword != null ? " with filter: " + keyword : ""));
            } catch (Exception e) {
                Log.e(HookUtils.TAG, "[✗] Failed to hook ResponseBody: " + e.getMessage());
            }
        }

        /**
         * Hook Request 构建方法
         * @param showStack 是否显示调用栈
         * @param keyword 关键字过滤（为null则不过滤）
         */
        public static void hookRequest(boolean showStack, String keyword) {
            try {
                if (keyword != null) {
                    HookCore.hookMethod("okhttp3.Request$Builder", "url", showStack,
                        new HookUtils.AnyArgKeywordFilter(keyword));
                    HookCore.hookMethod("okhttp3.Request$Builder", "build", showStack);
                } else {
                    HookCore.hookMethod("okhttp3.Request$Builder", "url", showStack);
                    HookCore.hookMethod("okhttp3.Request$Builder", "build", showStack);
                }
                Log.d(HookUtils.TAG, "[✓] Hooked okhttp3.Request$Builder" + (keyword != null ? " with filter: " + keyword : ""));
            } catch (Exception e) {
                Log.e(HookUtils.TAG, "[✗] Failed to hook Request$Builder: " + e.getMessage());
            }
        }

        /**
         * Hook OkHttpClient 的网络调用
         * @param showStack 是否显示调用栈
         * @param keyword 关键字过滤（为null则不过滤）
         */
        public static void hookCall(boolean showStack, String keyword) {
            try {
                if (keyword != null) {
                    HookCore.hookMethod("okhttp3.RealCall", "execute", showStack);
                    HookCore.hookMethod("okhttp3.RealCall", "enqueue", showStack);
                } else {
                    HookCore.hookMethod("okhttp3.RealCall", "execute", showStack);
                    HookCore.hookMethod("okhttp3.RealCall", "enqueue", showStack);
                }
                Log.d(HookUtils.TAG, "[✓] Hooked okhttp3.RealCall" + (keyword != null ? " with filter: " + keyword : ""));
            } catch (Exception e) {
                Log.e(HookUtils.TAG, "[✗] Failed to hook RealCall: " + e.getMessage());
            }
        }

        /**
         * Hook RequestBody 创建方法
         * @param showStack 是否显示调用栈
         * @param keyword 关键字过滤（为null则不过滤）
         */
        public static void hookRequestBody(boolean showStack, String keyword) {
            try {
                if (keyword != null) {
                    HookCore.hookMethod("okhttp3.RequestBody", "create", showStack,
                        new HookUtils.AnyArgKeywordFilter(keyword));
                } else {
                    HookCore.hookMethod("okhttp3.RequestBody", "create", showStack);
                }
                Log.d(HookUtils.TAG, "[✓] Hooked okhttp3.RequestBody" + (keyword != null ? " with filter: " + keyword : ""));
            } catch (Exception e) {
                Log.e(HookUtils.TAG, "[✗] Failed to hook RequestBody: " + e.getMessage());
            }
        }

        /**
         * Hook Interceptor 拦截器
         * @param showStack 是否显示调用栈
         */
        public static void hookInterceptor(boolean showStack) {
            try {
                HookCore.hookMethod("okhttp3.Interceptor", "intercept", showStack);
                Log.d(HookUtils.TAG, "[✓] Hooked okhttp3.Interceptor");
            } catch (Exception e) {
                Log.e(HookUtils.TAG, "[✗] Failed to hook Interceptor: " + e.getMessage());
            }
        }

        /**
         * Hook 全部OkHttp方法
         */
        public static void hookAll(boolean showStack, String keyword) {
            hookResponseBody(showStack, keyword);
            hookRequest(showStack, keyword);
            hookCall(showStack, keyword);
            hookRequestBody(showStack, keyword);
            hookInterceptor(showStack);
        }
    }

    /**
     * Hook Retrofit相关方法
     */
    public static class Retrofit {

        /**
         * Hook Retrofit Call 执行方法
         * @param showStack 是否显示调用栈
         */
        public static void hookCall(boolean showStack) {
            try {
                HookCore.hookMethod("retrofit2.OkHttpCall", "execute", showStack);
                HookCore.hookMethod("retrofit2.OkHttpCall", "enqueue", showStack);
                Log.d(HookUtils.TAG, "[✓] Hooked retrofit2.OkHttpCall");
            } catch (Exception e) {
                Log.e(HookUtils.TAG, "[✗] Failed to hook Retrofit OkHttpCall: " + e.getMessage());
            }
        }

        /**
         * Hook Retrofit ServiceMethod
         * @param showStack 是否显示调用栈
         */
        public static void hookServiceMethod(boolean showStack) {
            try {
                HookCore.hookMethod("retrofit2.ServiceMethod", "invoke", showStack);
                Log.d(HookUtils.TAG, "[✓] Hooked retrofit2.ServiceMethod");
            } catch (Exception e) {
                Log.e(HookUtils.TAG, "[✗] Failed to hook ServiceMethod: " + e.getMessage());
            }
        }

        /**
         * Hook 全部Retrofit方法
         * @param showStack 是否显示调用栈
         * @param keyword 关键字过滤（当前Retrofit不支持过滤）
         */
        public static void hookAll(boolean showStack, String keyword) {
            hookCall(showStack);
            hookServiceMethod(showStack);
        }
    }

    /**
     * Hook 加密算法相关方法 - 核心功能
     */
    public static class Crypto {

        /**
         * Hook Cipher 加解密 - 追踪加密算法、模式、明文、密文
         * @param showStack 是否显示调用栈
         * @param keyword 关键字过滤（为null则不过滤）
         */
        public static void hookCipher(boolean showStack, String keyword) {
            try {
                if (keyword != null) {
                    HookCore.hookMethod("javax.crypto.Cipher", "doFinal", showStack,
                        new HookUtils.ResultKeywordFilter(keyword));
                    HookCore.hookMethod("javax.crypto.Cipher", "init", showStack);
                    HookCore.hookMethod("javax.crypto.Cipher", "getInstance", showStack);
                } else {
                    HookCore.hookMethod("javax.crypto.Cipher", "doFinal", showStack);
                    HookCore.hookMethod("javax.crypto.Cipher", "init", showStack);
                    HookCore.hookMethod("javax.crypto.Cipher", "getInstance", showStack);
                }
                Log.d(HookUtils.TAG, "[✓] Hooked javax.crypto.Cipher" + (keyword != null ? " with filter: " + keyword : ""));
            } catch (Exception e) {
                Log.e(HookUtils.TAG, "[✗] Failed to hook Cipher: " + e.getMessage());
            }
        }

        /**
         * Hook MessageDigest (MD5/SHA等) - 追踪哈希计算
         * @param showStack 是否显示调用栈
         * @param keyword 关键字过滤（为null则不过滤）
         */
        public static void hookMessageDigest(boolean showStack, String keyword) {
            try {
                if (keyword != null) {
                    HookCore.hookMethod("java.security.MessageDigest", "digest", showStack,
                        new HookUtils.ResultKeywordFilter(keyword));
                    HookCore.hookMethod("java.security.MessageDigest", "getInstance", showStack);
                    HookCore.hookMethod("java.security.MessageDigest", "update", showStack);
                } else {
                    HookCore.hookMethod("java.security.MessageDigest", "digest", showStack);
                    HookCore.hookMethod("java.security.MessageDigest", "getInstance", showStack);
                    HookCore.hookMethod("java.security.MessageDigest", "update", showStack);
                }
                Log.d(HookUtils.TAG, "[✓] Hooked MessageDigest" + (keyword != null ? " with filter: " + keyword : ""));
            } catch (Exception e) {
                Log.e(HookUtils.TAG, "[✗] Failed to hook MessageDigest: " + e.getMessage());
            }
        }

        /**
         * Hook Base64 编解码 - 追踪Base64编码的数据
         * @param showStack 是否显示调用栈
         * @param keyword 关键字过滤（为null则不过滤）
         */
        public static void hookBase64(boolean showStack, String keyword) {
            try {
                if (keyword != null) {
                    HookCore.hookMethod("android.util.Base64", "decode", showStack,
                        new HookUtils.ResultKeywordFilter(keyword));
                    HookCore.hookMethod("android.util.Base64", "encode", showStack,
                        new HookUtils.ResultKeywordFilter(keyword));
                    HookCore.hookMethod("android.util.Base64", "encodeToString", showStack,
                        new HookUtils.ResultKeywordFilter(keyword));
                } else {
                    HookCore.hookMethod("android.util.Base64", "decode", showStack);
                    HookCore.hookMethod("android.util.Base64", "encode", showStack);
                    HookCore.hookMethod("android.util.Base64", "encodeToString", showStack);
                }
                Log.d(HookUtils.TAG, "[✓] Hooked Base64" + (keyword != null ? " with filter: " + keyword : ""));
            } catch (Exception e) {
                Log.e(HookUtils.TAG, "[✗] Failed to hook Base64: " + e.getMessage());
            }
        }

        /**
         * Hook SecretKeySpec 密钥构造 - 追踪密钥生成
         * @param showStack 是否显示调用栈
         */
        public static void hookSecretKeySpec(boolean showStack, String keyword) {
            try {
                if (keyword != null) {
                    HookCore.hookMethod("javax.crypto.spec.SecretKeySpec", "<init>", showStack,
                        new HookUtils.AnyArgKeywordFilter(keyword));
                } else {
                    HookCore.hookMethod("javax.crypto.spec.SecretKeySpec", "<init>", showStack);
                }
                Log.d(HookUtils.TAG, "[✓] Hooked SecretKeySpec" + (keyword != null ? " with filter: " + keyword : ""));
            } catch (Exception e) {
                Log.e(HookUtils.TAG, "[✗] Failed to hook SecretKeySpec: " + e.getMessage());
            }
        }

        /**
         * Hook IvParameterSpec IV构造 - 追踪IV生成
         * @param showStack 是否显示调用栈
         * @param keyword 关键字过滤（为null则不过滤）
         */
        public static void hookIvParameterSpec(boolean showStack, String keyword) {
            try {
                if (keyword != null) {
                    HookCore.hookMethod("javax.crypto.spec.IvParameterSpec", "<init>", showStack,
                        new HookUtils.AnyArgKeywordFilter(keyword));
                } else {
                    HookCore.hookMethod("javax.crypto.spec.IvParameterSpec", "<init>", showStack);
                }
                Log.d(HookUtils.TAG, "[✓] Hooked IvParameterSpec" + (keyword != null ? " with filter: " + keyword : ""));
            } catch (Exception e) {
                Log.e(HookUtils.TAG, "[✗] Failed to hook IvParameterSpec: " + e.getMessage());
            }
        }

        /**
         * Hook Mac (HMAC) - 追踪消息认证码
         * @param showStack 是否显示调用栈
         * @param keyword 关键字过滤（为null则不过滤）
         */
        public static void hookMac(boolean showStack, String keyword) {
            try {
                if (keyword != null) {
                    HookCore.hookMethod("javax.crypto.Mac", "doFinal", showStack,
                        new HookUtils.ResultKeywordFilter(keyword));
                    HookCore.hookMethod("javax.crypto.Mac", "init", showStack);
                    HookCore.hookMethod("javax.crypto.Mac", "getInstance", showStack);
                } else {
                    HookCore.hookMethod("javax.crypto.Mac", "doFinal", showStack);
                    HookCore.hookMethod("javax.crypto.Mac", "init", showStack);
                    HookCore.hookMethod("javax.crypto.Mac", "getInstance", showStack);
                }
                Log.d(HookUtils.TAG, "[✓] Hooked Mac" + (keyword != null ? " with filter: " + keyword : ""));
            } catch (Exception e) {
                Log.e(HookUtils.TAG, "[✗] Failed to hook Mac: " + e.getMessage());
            }
        }

        /**
         * Hook KeyGenerator - 追踪密钥生成
         * @param showStack 是否显示调用栈
         * @param keyword 关键字过滤（为null则不过滤）
         */
        public static void hookKeyGenerator(boolean showStack, String keyword) {
            try {
                if (keyword != null) {
                    HookCore.hookMethod("javax.crypto.KeyGenerator", "generateKey", showStack,
                        new HookUtils.ResultKeywordFilter(keyword));
                    HookCore.hookMethod("javax.crypto.KeyGenerator", "getInstance", showStack);
                } else {
                    HookCore.hookMethod("javax.crypto.KeyGenerator", "generateKey", showStack);
                    HookCore.hookMethod("javax.crypto.KeyGenerator", "getInstance", showStack);
                }
                Log.d(HookUtils.TAG, "[✓] Hooked KeyGenerator" + (keyword != null ? " with filter: " + keyword : ""));
            } catch (Exception e) {
                Log.e(HookUtils.TAG, "[✗] Failed to hook KeyGenerator: " + e.getMessage());
            }
        }

        /**
         * Hook SecureRandom - 追踪随机数生成
         * @param showStack 是否显示调用栈
         * @param keyword 关键字过滤（为null则不过滤）
         */
        public static void hookSecureRandom(boolean showStack, String keyword) {
            try {
                if (keyword != null) {
                    HookCore.hookMethod("java.security.SecureRandom", "nextBytes", showStack,
                        new HookUtils.ResultKeywordFilter(keyword));
                    HookCore.hookMethod("java.security.SecureRandom", "setSeed", showStack,
                        new HookUtils.AnyArgKeywordFilter(keyword));
                } else {
                    HookCore.hookMethod("java.security.SecureRandom", "nextBytes", showStack);
                    HookCore.hookMethod("java.security.SecureRandom", "setSeed", showStack);
                }
                Log.d(HookUtils.TAG, "[✓] Hooked SecureRandom" + (keyword != null ? " with filter: " + keyword : ""));
            } catch (Exception e) {
                Log.e(HookUtils.TAG, "[✗] Failed to hook SecureRandom: " + e.getMessage());
            }
        }

        /**
         * Hook 全部加密方法
         */
        public static void hookAll(boolean showStack, String keyword) {
            hookCipher(showStack, keyword);
            hookMessageDigest(showStack, keyword);
            hookBase64(showStack, keyword);
            hookSecretKeySpec(showStack, keyword);
            hookIvParameterSpec(showStack, keyword);
            hookMac(showStack, keyword);
            hookKeyGenerator(showStack, keyword);
            hookSecureRandom(showStack, keyword);
        }
    }

    /**
     * Hook JSON相关方法 - 追踪JSON序列化
     */
    public static class Json {

        /**
         * Hook JSONObject
         * @param showStack 是否显示调用栈
         * @param keyword 关键字过滤（为null则不过滤）
         */
        public static void hookJSONObject(boolean showStack, String keyword) {
            try {
                if (keyword != null) {
                    HookCore.hookMethod("org.json.JSONObject", "<init>", showStack,
                        new HookUtils.AnyArgKeywordFilter(keyword));
                    HookCore.hookMethod("org.json.JSONObject", "toString", showStack,
                        new HookUtils.ResultKeywordFilter(keyword));
                    HookCore.hookMethod("org.json.JSONObject", "put", showStack);
                    HookCore.hookMethod("org.json.JSONObject", "opt", showStack);
                } else {
                    HookCore.hookMethod("org.json.JSONObject", "<init>", showStack);
                    HookCore.hookMethod("org.json.JSONObject", "toString", showStack);
                    HookCore.hookMethod("org.json.JSONObject", "put", showStack);
                    HookCore.hookMethod("org.json.JSONObject", "opt", showStack);
                }
                Log.d(HookUtils.TAG, "[✓] Hooked JSONObject" + (keyword != null ? " with filter: " + keyword : ""));
            } catch (Exception e) {
                Log.e(HookUtils.TAG, "[✗] Failed to hook JSONObject: " + e.getMessage());
            }
        }

        /**
         * Hook JSONArray
         * @param showStack 是否显示调用栈
         * @param keyword 关键字过滤（为null则不过滤）
         */
        public static void hookJSONArray(boolean showStack, String keyword) {
            try {
                if (keyword != null) {
                    HookCore.hookMethod("org.json.JSONArray", "<init>", showStack,
                        new HookUtils.AnyArgKeywordFilter(keyword));
                    HookCore.hookMethod("org.json.JSONArray", "toString", showStack,
                        new HookUtils.ResultKeywordFilter(keyword));
                } else {
                    HookCore.hookMethod("org.json.JSONArray", "<init>", showStack);
                    HookCore.hookMethod("org.json.JSONArray", "toString", showStack);
                }
                Log.d(HookUtils.TAG, "[✓] Hooked JSONArray" + (keyword != null ? " with filter: " + keyword : ""));
            } catch (Exception e) {
                Log.e(HookUtils.TAG, "[✗] Failed to hook JSONArray: " + e.getMessage());
            }
        }

        /**
         * Hook Gson
         * @param showStack 是否显示调用栈
         * @param keyword 关键字过滤（为null则不过滤）
         */
        public static void hookGson(boolean showStack, String keyword) {
            try {
                if (keyword != null) {
                    HookCore.hookMethod("com.google.gson.Gson", "toJson", showStack,
                        new HookUtils.ResultKeywordFilter(keyword));
                    HookCore.hookMethod("com.google.gson.Gson", "fromJson", showStack,
                        new HookUtils.AnyArgKeywordFilter(keyword));
                } else {
                    HookCore.hookMethod("com.google.gson.Gson", "toJson", showStack);
                    HookCore.hookMethod("com.google.gson.Gson", "fromJson", showStack);
                }
                Log.d(HookUtils.TAG, "[✓] Hooked Gson" + (keyword != null ? " with filter: " + keyword : ""));
            } catch (Exception e) {
                Log.e(HookUtils.TAG, "[✗] Failed to hook Gson: " + e.getMessage());
            }
        }

        /**
         * Hook FastJson
         * @param showStack 是否显示调用栈
         * @param keyword 关键字过滤（为null则不过滤）
         */
        public static void hookFastJson(boolean showStack, String keyword) {
            try {
                if (keyword != null) {
                    HookCore.hookMethod("com.alibaba.fastjson.JSON", "toJSONString", showStack,
                        new HookUtils.ResultKeywordFilter(keyword));
                    HookCore.hookMethod("com.alibaba.fastjson.JSON", "parseObject", showStack,
                        new HookUtils.AnyArgKeywordFilter(keyword));
                } else {
                    HookCore.hookMethod("com.alibaba.fastjson.JSON", "toJSONString", showStack);
                    HookCore.hookMethod("com.alibaba.fastjson.JSON", "parseObject", showStack);
                }
                Log.d(HookUtils.TAG, "[✓] Hooked FastJson" + (keyword != null ? " with filter: " + keyword : ""));
            } catch (Exception e) {
                Log.e(HookUtils.TAG, "[✗] Failed to hook FastJson: " + e.getMessage());
            }
        }

        /**
         * Hook 全部JSON方法
         */
        public static void hookAll(boolean showStack, String keyword) {
            hookJSONObject(showStack, keyword);
            hookJSONArray(showStack, keyword);
            hookGson(showStack, keyword);
            hookFastJson(showStack, keyword);
        }
    }

    /**
     * Hook String相关方法 - 追踪字符串编码转换
     */
    public static class StringEncoding {

        /**
         * Hook String 编码转换
         * @param showStack 是否显示调用栈
         * @param keyword 关键字过滤（为null则不过滤）
         */
        public static void hookStringEncoding(boolean showStack, String keyword) {
            try {
                if (keyword != null) {
                    HookCore.hookMethod("java.lang.String", "<init>", showStack,
                        new HookUtils.AnyArgKeywordFilter(keyword));
                    HookCore.hookMethod("java.lang.String", "getBytes", showStack,
                        new HookUtils.ResultKeywordFilter(keyword));
                } else {
                    HookCore.hookMethod("java.lang.String", "<init>", showStack);
                    HookCore.hookMethod("java.lang.String", "getBytes", showStack);
                }
                Log.d(HookUtils.TAG, "[✓] Hooked String encoding" + (keyword != null ? " with filter: " + keyword : ""));
            } catch (Exception e) {
                Log.e(HookUtils.TAG, "[✗] Failed to hook String encoding: " + e.getMessage());
            }
        }

        /**
         * Hook URLEncoder/Decoder
         * @param showStack 是否显示调用栈
         * @param keyword 关键字过滤（为null则不过滤）
         */
        public static void hookURLEncoding(boolean showStack, String keyword) {
            try {
                if (keyword != null) {
                    HookCore.hookMethod("java.net.URLEncoder", "encode", showStack,
                        new HookUtils.ResultKeywordFilter(keyword));
                    HookCore.hookMethod("java.net.URLDecoder", "decode", showStack,
                        new HookUtils.ResultKeywordFilter(keyword));
                } else {
                    HookCore.hookMethod("java.net.URLEncoder", "encode", showStack);
                    HookCore.hookMethod("java.net.URLDecoder", "decode", showStack);
                }
                Log.d(HookUtils.TAG, "[✓] Hooked URLEncoder/Decoder" + (keyword != null ? " with filter: " + keyword : ""));
            } catch (Exception e) {
                Log.e(HookUtils.TAG, "[✗] Failed to hook URL encoding: " + e.getMessage());
            }
        }

        /**
         * Hook 全部String编码方法
         */
        public static void hookAll(boolean showStack, String keyword) {
            hookStringEncoding(showStack, keyword);
            hookURLEncoding(showStack, keyword);
        }
    }

    /**
     * 一键Hook所有加密相关方法（推荐使用）
     * @param showStack 是否显示调用栈
     * @param keyword 关键字过滤（为null则不过滤，只输出包含关键字的结果）
     */
    public static void hookAll(boolean showStack, String keyword) {
        Log.d(HookUtils.TAG, "==================== 开始追踪所有加密参数 ====================");
        if (keyword != null) {
            Log.d(HookUtils.TAG, "关键字过滤：" + keyword);
        }

        // Hook网络相关
        OkHttp.hookAll(showStack, keyword);
        Retrofit.hookAll(showStack, keyword);

        // Hook加密算法
        Crypto.hookAll(showStack, keyword);

        // Hook JSON处理
        Json.hookAll(showStack, keyword);

        // Hook字符串编码
        StringEncoding.hookAll(showStack, keyword);

        Log.d(HookUtils.TAG, "==================== 加密参数追踪Hook完成 ====================");
    }

    /**
     * 一键Hook所有加密算法（不包含网络）
     * @param showStack 是否显示调用栈
     * @param keyword 关键字过滤（为null则不过滤，只输出包含关键字的结果）
     */
    public static void hookCryptoOnly(boolean showStack, String keyword) {
        Log.d(HookUtils.TAG, "==================== 开始追踪加密算法参数 ====================");
        if (keyword != null) {
            Log.d(HookUtils.TAG, "关键字过滤：" + keyword);
        }

        Crypto.hookAll(showStack, keyword);
        StringEncoding.hookAll(showStack, keyword);

        Log.d(HookUtils.TAG, "==================== 加密算法参数追踪Hook完成 ====================");
    }
}
