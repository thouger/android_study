package com.hook;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class hook implements IXposedHookLoadPackage {
    private static Context context;
    private static ClassLoader classLoader;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws ClassNotFoundException {
        Log.d(HookUtils.TAG, "handleLoadPackage: " + lpparam.packageName);
        HookCore.setDefaultClassLoader(lpparam.classLoader);

//        System.loadLibrary("native_hook");

        // Hook Application.attach 获取 Context 和 ClassLoader
        HookCore.hookApplicationAttach((ctx, cl) -> {
            context = ctx;
            classLoader = cl;
        });

        hook();
    }

    /**
     * 业务Hook代码 - 在这里写你的Hook逻辑
     */
    private void hook() {
        
//        SSLPinning.enableSSLPinningBypass();
//        CryptoTracker.hookAll(true, "29.9");
        // ==================== Hook AppLog 加密开关 ====================
//        hookAppLogEncryptSwitch();

        // ==================== app_log请求拼接和发送 ====================
//        hookAppLogRequest();

        // ==================== 在这里写Hook代码 ====================
        // ==================== Retrofit2 完整发包流程 Hook ====================
//        hookRetrofit2Flow();

        HookCore.hookMethod("ms.bd.c.p2","b",true);
//        HookCore.hookMethod("ms.bd.c.e4","a",true);
//        HookCore.hookMethod("ms.bd.c.o2","a",true);


        // ==================== Hook动态加载的类 ====================
        // 方式1: 简单hook - 直接指定类名和方法名
//        HookCore.hookClassWhenLoaded("com.bytedance.bdinstall.Api", "LJ", false);

        // 方式2: 使用Handler进行复杂操作
//        HookCore.hookClassWhenLoaded("com.sun.ggactivity.InfoAdapter", new HookCore.ClassLoadHandler() {
//            @Override
//            public void onClassLoaded(Class<?> clazz) {
//                XposedHelpers.findAndHookMethod(clazz, "getView", int.class, View.class,
//                        ViewGroup.class, new XC_MethodHook() {
//                            @Override
//                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                                int num = (Integer) XposedHelpers.callMethod(param.thisObject, "getCount");
//                                XposedBridge.log("item count: " + num);
//                            }
//                        });
//            }
//        });

        // ==================== Hook已加载的类 ====================
        // 示例1: Hook BaseBundle的getString方法,只输出第一个参数包含"channel"的调用
//        try {
//            HookCore.hookMethod("ms.bd.c.p2", "b", true, new HookUtils.ArgKeywordFilter(3, "ri/report"));
//        } catch (Exception e) {
//            Log.e(HookUtils.TAG, "Hook failed", e);
//        }

        // 示例2: Hook HashMap的put方法,只输出任意参数包含"channel"的调用
//        try {
//            Class<?> clazz = XposedHelpers.findClass("java.util.HashMap", classLoader);
//            HookCore.hookMethod(clazz, "put", true, new HookUtils.AnyArgKeywordFilter("application/octet-stream;tt-data=a"));
//        } catch (Exception e) {
//            Log.e(HookUtils.TAG, "Hook failed", e);
//        }

        // ==================== 过滤器使用示例 ====================
        // 示例3: Hook方法并只输出返回值包含"success"的调用
//        try {
//            HookCore.hookMethod("com.bytedance.retrofit2.mime.DigestUtil", "md5Hex", true);
//        } catch (Exception e) {
//            Log.e(HookUtils.TAG, "Hook failed", e);
//        }

        // 示例4: 组合过滤器 - 第一个参数包含"channel" OR 返回值包含"result"
//        try {
//            Class<?> someClazz = XposedHelpers.findClass("com.example.SomeClass", classLoader);
//            HookCore.hookMethod(someClazz, "someMethod", false,
//                new HookUtils.OrFilter(
//                    new HookUtils.ArgKeywordFilter(0, "channel"),
//                    new HookUtils.ResultKeywordFilter("result")
//                )
//            );
//        } catch (Exception e) {
//            Log.e(HookUtils.TAG, "Hook failed", e);
//        }

        // 示例5: 组合过滤器 - 第一个参数包含"user" AND 第二个参数包含"token"
//        try {
//            Class<?> someClazz = XposedHelpers.findClass("com.example.SomeClass", classLoader);
//            HookCore.hookMethod(someClazz, "someMethod", false,
//                new HookUtils.AndFilter(
//                    new HookUtils.ArgKeywordFilter(0, "user"),
//                    new HookUtils.ArgKeywordFilter(1, "token")
//                )
//            );
//        } catch (Exception e) {
//            Log.e(HookUtils.TAG, "Hook failed", e);
//        }

        // 示例6: NOT过滤器 - 不输出参数包含"test"的调用
//        try {
//            Class<?> someClazz = XposedHelpers.findClass("com.example.SomeClass", classLoader);
//            HookCore.hookMethod(someClazz, "someMethod", false,
//                new HookUtils.NotFilter(new HookUtils.AnyArgKeywordFilter("test"))
//            );
//        } catch (Exception e) {
//            Log.e(HookUtils.TAG, "Hook failed", e);
//        }

        // 示例7: 组合过滤器 - 第一个参数全字匹配"Content-Type" AND 第二个参数全字匹配"application/octet-stream;tt-data=a"
//        try {
//            HookCore.hookMethod("java.util.HashMap", "put", true,
//                new HookUtils.AndFilter(
//                    new HookUtils.ArgEqualsFilter(0, "last_deeplink_update_version_code"),
//                    new HookUtils.ArgEqualsFilter(1, "0")
//                )
//            );
//        } catch (Exception e) {
//            Log.e(HookUtils.TAG, "Hook failed", e);
//        }

//        try {
//            HookCore.hookMethod("com.bytedance.retrofit2.Utils", "checkNotNull", true,
//                    new HookUtils.AndFilter(
////                            new HookUtils.ArgEqualsFilter(0, "last_deeplink_update_version_code"),
//                            new HookUtils.ArgEqualsFilter(1, "interceptor == null")
//                    )
//            );
//        } catch (Exception e) {
//            Log.e(HookUtils.TAG, "Hook failed", e);
//        }

        // ==================== Hook抖音加密相关方法 ====================
//        DouyinHooks.hookEncryptionMethods(classLoader);
    }

    public static Context getContext() {
        return context;
    }

    public static ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * Hook Retrofit2 完整发包流程
     * 从发包到响应的全链路追踪
     */
    private void hookRetrofit2Flow() {
        Log.d(HookUtils.TAG, "==================== 开始Hook Retrofit2发包流程 ====================");

        // ==================== 1. Hook 请求参数构建 - RequestFactory ====================
        try {
            Class<?> requestFactoryClass = XposedHelpers.findClass("retrofit2.RequestFactory", classLoader);
            XposedHelpers.findAndHookMethod(requestFactoryClass, "a",
                    Object.class, Object[].class,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            try {
                                Object request = param.getResult();
                                if (request != null) {
                                    Object url = XposedHelpers.callMethod(request, "url");
                                    Log.d(HookUtils.TAG, "\n========================================");
                                    Log.d(HookUtils.TAG, "[0] 构建请求参数 (RequestFactory.a)");
                                    Log.d(HookUtils.TAG, "========================================");
                                    Log.d(HookUtils.TAG, "🔧 参数对象: " + param.args[0]);
                                    if (param.args[1] != null) {
                                        Object[] args = (Object[]) param.args[1];
                                        Log.d(HookUtils.TAG, "🔧 参数数组长度: " + args.length);
                                        for (int i = 0; i < args.length; i++) {
                                            if (args[i] != null) {
                                                Log.d(HookUtils.TAG, "   参数[" + i + "]: " + args[i].toString());
                                            }
                                        }
                                    }
                                    Log.d(HookUtils.TAG, "📍 生成URL: " + url.toString());
                                    Log.d(HookUtils.TAG, "========================================\n");
                                }
                            } catch (Exception e) {
                                Log.e(HookUtils.TAG, "❌ RequestFactory hook error: " + e.getMessage());
                            }
                        }
                    });
            Log.d(HookUtils.TAG, "[✓] Hooked retrofit2.RequestFactory.a()");
        } catch (Exception e) {
            Log.e(HookUtils.TAG, "[✗] Failed to hook RequestFactory: " + e.getMessage());
        }

        // ==================== 2. Hook 发包入口 - OkHttpCall.enqueue ====================
        try {
            Class<?> okHttpCallClass = XposedHelpers.findClass("retrofit2.OkHttpCall", classLoader);
            XposedHelpers.findAndHookMethod(okHttpCallClass, "a",
                    XposedHelpers.findClass("retrofit2.Callback", classLoader),
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            try {
                                Log.d(HookUtils.TAG, "\n========================================");
                                Log.d(HookUtils.TAG, "[1] Retrofit 发包入口 (OkHttpCall.a)");
                                Log.d(HookUtils.TAG, "========================================");

                                // 获取请求信息
                                Object request = XposedHelpers.callMethod(param.thisObject, "request");
                                Object url = XposedHelpers.callMethod(request, "url");
                                String method = (String) XposedHelpers.callMethod(request, "method");

                                Log.d(HookUtils.TAG, "📤 发送请求:");
                                Log.d(HookUtils.TAG, "  URL: " + url.toString());
                                Log.d(HookUtils.TAG, "  Method: " + method);

                                // 打印 Headers
                                Object headers = XposedHelpers.callMethod(request, "headers");
                                int headerSize = (Integer) XposedHelpers.callMethod(headers, "size");
                                Log.d(HookUtils.TAG, "  Headers:");
                                for (int i = 0; i < headerSize; i++) {
                                    String name = (String) XposedHelpers.callMethod(headers, "name", i);
                                    String value = (String) XposedHelpers.callMethod(headers, "value", i);
                                    Log.d(HookUtils.TAG, "    " + name + ": " + value);
                                }

                                // 打印 Request Body
                                Object body = XposedHelpers.callMethod(request, "body");
                                if (body != null) {
                                    try {
                                        Class<?> bufferClass = XposedHelpers.findClass("okio.Buffer", classLoader);
                                        Object buffer = bufferClass.newInstance();
                                        XposedHelpers.callMethod(body, "writeTo", buffer);
                                        String bodyStr = (String) XposedHelpers.callMethod(buffer, "readUtf8");
                                        Log.d(HookUtils.TAG, "  Body: " + bodyStr);
                                    } catch (Exception e) {
                                        Log.d(HookUtils.TAG, "  Body: [无法读取]");
                                    }
                                }

                                // 打印调用堆栈
                                Log.d(HookUtils.TAG, "\n  调用堆栈:");
                                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                                for (int j = 0; j < Math.min(10, stackTrace.length); j++) {
                                    Log.d(HookUtils.TAG, "    " + stackTrace[j].toString());
                                }

                                Log.d(HookUtils.TAG, "========================================\n");

                            } catch (Exception e) {
                                Log.e(HookUtils.TAG, "❌ 获取请求信息失败: " + e.getMessage());
                            }
                        }
                    });
            Log.d(HookUtils.TAG, "[✓] Hooked retrofit2.OkHttpCall.a(Callback)");
        } catch (Exception e) {
            Log.e(HookUtils.TAG, "[✗] Failed to hook OkHttpCall: " + e.getMessage());
        }

        // ==================== 3. Hook 底层发包 - okhttp3.RealCall.enqueue ====================
        try {
            Class<?> realCallClass = XposedHelpers.findClass("okhttp3.internal.call.RealCall", classLoader);
            XposedHelpers.findAndHookMethod(realCallClass, "enqueue",
                    XposedHelpers.findClass("okhttp3.Callback", classLoader),
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            try {
                                Object request = XposedHelpers.callMethod(param.thisObject, "request");
                                Object url = XposedHelpers.callMethod(request, "url");
                                String method = (String) XposedHelpers.callMethod(request, "method");

                                Log.d(HookUtils.TAG, "\n========================================");
                                Log.d(HookUtils.TAG, "[2] OkHttp 底层发包 (RealCall.enqueue)");
                                Log.d(HookUtils.TAG, "========================================");
                                Log.d(HookUtils.TAG, "🚀 真正发包:");
                                Log.d(HookUtils.TAG, "  URL: " + url.toString());
                                Log.d(HookUtils.TAG, "  Method: " + method);
                                Log.d(HookUtils.TAG, "========================================\n");
                            } catch (Exception e) {
                                Log.e(HookUtils.TAG, "❌ RealCall hook error: " + e.getMessage());
                            }
                        }
                    });
            Log.d(HookUtils.TAG, "[✓] Hooked okhttp3.RealCall.enqueue()");
        } catch (Exception e) {
            Log.e(HookUtils.TAG, "[✗] Failed to hook RealCall: " + e.getMessage());
        }

        // ==================== 4. Hook 响应处理 - DHCC_NewSimpleHttpCallback ====================
        try {
            Class<?> callbackClass = XposedHelpers.findClass("com.commonlib.util.net.DHCC_NewSimpleHttpCallback", classLoader);
            XposedHelpers.findAndHookMethod(callbackClass, "e",
                    XposedHelpers.findClass("retrofit2.Call", classLoader),
                    XposedHelpers.findClass("retrofit2.Response", classLoader),
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            try {
                                Object call = param.args[0];
                                Object response = param.args[1];

                                Object request = XposedHelpers.callMethod(call, "request");
                                Object url = XposedHelpers.callMethod(request, "url");

                                Log.d(HookUtils.TAG, "\n========================================");
                                Log.d(HookUtils.TAG, "[3] Retrofit 响应处理 (SimpleHttpCallback.e)");
                                Log.d(HookUtils.TAG, "========================================");
                                Log.d(HookUtils.TAG, "📥 收到响应:");
                                Log.d(HookUtils.TAG, "  URL: " + url.toString());

                                // 获取响应码 (m62527b 方法)
                                int code = (Integer) XposedHelpers.callMethod(response, "b");
                                Log.d(HookUtils.TAG, "  Response Code: " + code);

                                // 获取响应体 (m62526a 方法)
                                String responseBody = (String) XposedHelpers.callMethod(response, "a");
                                if (responseBody != null) {
                                    if (responseBody.length() > 500) {
                                        Log.d(HookUtils.TAG, "  Response Body: " + responseBody.substring(0, 500));
                                        Log.d(HookUtils.TAG, "  ... (已截断，总长度: " + responseBody.length() + ")");
                                    } else {
                                        Log.d(HookUtils.TAG, "  Response Body: " + responseBody);
                                    }
                                }

                                Log.d(HookUtils.TAG, "========================================\n");

                            } catch (Exception e) {
                                Log.e(HookUtils.TAG, "❌ 获取响应信息失败: " + e.getMessage());
                            }
                        }
                    });
            Log.d(HookUtils.TAG, "[✓] Hooked DHCC_NewSimpleHttpCallback.e()");
        } catch (Exception e) {
            Log.e(HookUtils.TAG, "[✗] Failed to hook DHCC_NewSimpleHttpCallback: " + e.getMessage());
        }

        // ==================== 5. Hook OkHttpCall 创建 Call - 捕获请求构建 ====================
        try {
            Class<?> okHttpCallClass = XposedHelpers.findClass("retrofit2.OkHttpCall", classLoader);
            XposedHelpers.findAndHookMethod(okHttpCallClass, "c",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            try {
                                Object call = param.getResult();
                                if (call != null) {
                                    Object request = XposedHelpers.callMethod(call, "request");
                                    Object url = XposedHelpers.callMethod(request, "url");

                                    Log.d(HookUtils.TAG, "\n[*] OkHttpCall.c() - 创建 OkHttp Call");
                                    Log.d(HookUtils.TAG, "    URL: " + url.toString());
                                }
                            } catch (Exception e) {
                                // 忽略错误
                            }
                        }
                    });
            Log.d(HookUtils.TAG, "[✓] Hooked retrofit2.OkHttpCall.c()");
        } catch (Exception e) {
            Log.e(HookUtils.TAG, "[✗] Failed to hook OkHttpCall.c(): " + e.getMessage());
        }

        Log.d(HookUtils.TAG, "==================== Retrofit2发包流程Hook完成 ====================\n");
    }

    /**
     * Hook app_log 请求的拼接和发送
     * 目标: 找到 app_log 完整请求体的拼接和发送位置
     */
    private void hookAppLogRequest() {
        Log.d(HookUtils.TAG, "==================== 开始Hook app_log请求 ====================");

        // ==================== 1. AppLog核心 - 事件收集和上报 ====================

        // 1.1 AppLog - 主入口
        try {
            HookCore.hookMethod("com.bytedance.applog.AppLog", "sendEvent", true);
            HookCore.hookMethod("com.bytedance.applog.AppLog", "onEventV3", true);
            HookCore.hookMethod("com.bytedance.applog.AppLog", "flush", true);
            Log.d(HookUtils.TAG, "[✓] Hooked AppLog event methods");
        } catch (Exception e) {
            Log.e(HookUtils.TAG, "[✗] Failed to hook AppLog: " + e.getMessage());
        }

        // 1.2 EventQueue - 事件队列
        try {
            HookCore.hookMethod("com.bytedance.applog.event.EventQueue", "add", true);
            HookCore.hookMethod("com.bytedance.applog.event.EventQueue", "flush", true);
            HookCore.hookMethod("com.bytedance.applog.event.EventQueue", "poll", true);
            Log.d(HookUtils.TAG, "[✓] Hooked EventQueue");
        } catch (Exception e) {
            Log.e(HookUtils.TAG, "[✗] Failed to hook EventQueue: " + e.getMessage());
        }

        // 1.3 EventBuilder - 事件构造器
        try {
            HookCore.hookMethod("com.bytedance.applog.event.EventBuilder", "build", true);
            HookCore.hookMethod("com.bytedance.applog.event.EventBuilder", "buildEventV3", true);
            Log.d(HookUtils.TAG, "[✓] Hooked EventBuilder");
        } catch (Exception e) {
            Log.e(HookUtils.TAG, "[✗] Failed to hook EventBuilder: " + e.getMessage());
        }

        // ==================== 2. Packer - 数据打包器(重点!) ====================

        // 2.1 EventPacker - 事件打包(可能在这里拼接完整JSON)
        try {
            HookCore.hookMethod("com.bytedance.applog.pack.EventPacker", "pack", true);
            HookCore.hookMethod("com.bytedance.applog.pack.EventPacker", "packEventV3", true);
            HookCore.hookMethod("com.bytedance.applog.pack.EventPacker", "packTerminate", true);
            HookCore.hookMethod("com.bytedance.applog.pack.EventPacker", "packHeader", true);
            Log.d(HookUtils.TAG, "[✓] Hooked EventPacker");
        } catch (Exception e) {
            Log.e(HookUtils.TAG, "[✗] Failed to hook EventPacker: " + e.getMessage());
        }

        // 2.2 PackBuilder - 打包构造器
        try {
            HookCore.hookMethod("com.bytedance.applog.pack.PackBuilder", "build", true);
            HookCore.hookMethod("com.bytedance.applog.pack.PackBuilder", "buildRequest", true);
            HookCore.hookMethod("com.bytedance.applog.pack.PackBuilder", "toJson", true);
            Log.d(HookUtils.TAG, "[✓] Hooked PackBuilder");
        } catch (Exception e) {
            Log.e(HookUtils.TAG, "[✗] Failed to hook PackBuilder: " + e.getMessage());
        }

        // ==================== 3. Upload - 上传模块(重点!) ====================

        // 3.1 UploadManager - 上传管理器
        try {
            HookCore.hookMethod("com.bytedance.applog.upload.UploadManager", "upload", true);
            HookCore.hookMethod("com.bytedance.applog.upload.UploadManager", "doUpload", true);
            HookCore.hookMethod("com.bytedance.applog.upload.UploadManager", "uploadEvents", true);
            Log.d(HookUtils.TAG, "[✓] Hooked UploadManager");
        } catch (Exception e) {
            Log.e(HookUtils.TAG, "[✗] Failed to hook UploadManager: " + e.getMessage());
        }

        // 3.2 UploadTask - 上传任务
        try {
            HookCore.hookMethod("com.bytedance.applog.upload.UploadTask", "run", true);
            HookCore.hookMethod("com.bytedance.applog.upload.UploadTask", "execute", true);
            Log.d(HookUtils.TAG, "[✓] Hooked UploadTask");
        } catch (Exception e) {
            Log.e(HookUtils.TAG, "[✗] Failed to hook UploadTask: " + e.getMessage());
        }

        // 3.3 Uploader - 上传器
        try {
            HookCore.hookMethod("com.bytedance.applog.upload.Uploader", "upload", true);
            HookCore.hookMethod("com.bytedance.applog.upload.Uploader", "post", true);
            Log.d(HookUtils.TAG, "[✓] Hooked Uploader");
        } catch (Exception e) {
            Log.e(HookUtils.TAG, "[✗] Failed to hook Uploader: " + e.getMessage());
        }

        // ==================== 4. Network - 网络层(最接近发送!) ====================

        // 4.1 AppLogNetworkClient - 网络客户端
        try {
            HookCore.hookMethod("com.bytedance.applog.network.AppLogNetworkClient", "post", true);
            HookCore.hookMethod("com.bytedance.applog.network.AppLogNetworkClient", "request", true);
            HookCore.hookMethod("com.bytedance.applog.network.AppLogNetworkClient", "doPost", true);
            Log.d(HookUtils.TAG, "[✓] Hooked AppLogNetworkClient");
        } catch (Exception e) {
            Log.e(HookUtils.TAG, "[✗] Failed to hook AppLogNetworkClient: " + e.getMessage());
        }

        // 4.2 HttpRequest - HTTP请求
        try {
            HookCore.hookMethod("com.bytedance.applog.network.HttpRequest", "execute", true);
            HookCore.hookMethod("com.bytedance.applog.network.HttpRequest", "post", true);
            Log.d(HookUtils.TAG, "[✓] Hooked HttpRequest");
        } catch (Exception e) {
            Log.e(HookUtils.TAG, "[✗] Failed to hook HttpRequest: " + e.getMessage());
        }

        // ==================== 5. IApi/RetrofitApi - API接口(可能在这里!) ====================

        // 5.1 AppLogApi - App日志API接口
        try {
            HookCore.hookMethod("com.bytedance.applog.api.AppLogApi", "uploadLog", true);
            HookCore.hookMethod("com.bytedance.applog.api.AppLogApi", "log", true);
            Log.d(HookUtils.TAG, "[✓] Hooked AppLogApi");
        } catch (Exception e) {
            Log.e(HookUtils.TAG, "[✗] Failed to hook AppLogApi: " + e.getMessage());
        }

        // 5.2 IAppLogApi - App日志API接口(可能是接口)
        try {
            HookCore.hookMethod("com.bytedance.applog.api.IAppLogApi", "uploadLog", true);
            Log.d(HookUtils.TAG, "[✓] Hooked IAppLogApi");
        } catch (Exception e) {
            Log.e(HookUtils.TAG, "[✗] Failed to hook IAppLogApi: " + e.getMessage());
        }

        // ==================== 6. Serializer - 序列化器 ====================

        // 6.1 JsonSerializer - JSON序列化
        try {
            HookCore.hookMethod("com.bytedance.applog.serializer.JsonSerializer", "serialize", true);
            HookCore.hookMethod("com.bytedance.applog.serializer.JsonSerializer", "toJson", true);
            Log.d(HookUtils.TAG, "[✓] Hooked JsonSerializer");
        } catch (Exception e) {
            Log.e(HookUtils.TAG, "[✗] Failed to hook JsonSerializer: " + e.getMessage());
        }

        // 6.2 EventSerializer - 事件序列化
        try {
            HookCore.hookMethod("com.bytedance.applog.serializer.EventSerializer", "serialize", true);
            Log.d(HookUtils.TAG, "[✓] Hooked EventSerializer");
        } catch (Exception e) {
            Log.e(HookUtils.TAG, "[✗] Failed to hook EventSerializer: " + e.getMessage());
        }

        // ==================== 7. OkHttp拦截器 - 请求体拦截 ====================

        // 7.1 RequestBody - 请求体
        try {
            HookCore.hookMethod("okhttp3.RequestBody", "create", true);
            Log.d(HookUtils.TAG, "[✓] Hooked RequestBody.create");
        } catch (Exception e) {
            Log.e(HookUtils.TAG, "[✗] Failed to hook RequestBody: " + e.getMessage());
        }

        // 7.2 RealCall - 真实调用
        try {
            HookCore.hookMethod("okhttp3.internal.connection.RealCall", "execute", true);
            HookCore.hookMethod("okhttp3.RealCall", "execute", true);
            Log.d(HookUtils.TAG, "[✓] Hooked RealCall");
        } catch (Exception e) {
            Log.e(HookUtils.TAG, "[✗] Failed to hook RealCall: " + e.getMessage());
        }

        // ==================== 8. ByteDance Retrofit - 可能用的自己的Retrofit ====================

        // 8.1 自定义Call
        try {
            HookCore.hookMethod("com.bytedance.retrofit2.Call", "execute", true);
            Log.d(HookUtils.TAG, "[✓] Hooked ByteDance Call");
        } catch (Exception e) {
            Log.e(HookUtils.TAG, "[✗] Failed to hook ByteDance Call: " + e.getMessage());
        }

        // 8.2 自定义RequestBody
        try {
            HookCore.hookMethod("com.bytedance.retrofit2.RequestBody", "create", true);
            Log.d(HookUtils.TAG, "[✓] Hooked ByteDance RequestBody");
        } catch (Exception e) {
            Log.e(HookUtils.TAG, "[✗] Failed to hook ByteDance RequestBody: " + e.getMessage());
        }

        // ==================== 9. Native层 - JNI调用 ====================

        // 9.1 native打包方法
        try {
            HookCore.hookMethod("com.bytedance.applog.pack.EventPacker", "nativePack", true);
            HookCore.hookMethod("com.bytedance.applog.AppLog", "nativePackEvent", true);
            Log.d(HookUtils.TAG, "[✓] Hooked native pack methods");
        } catch (Exception e) {
            Log.e(HookUtils.TAG, "[✗] Failed to hook native methods: " + e.getMessage());
        }

        // ==================== 10. URL拼接 - 可能在这里拼接完整URL ====================

        // 10.1 UrlBuilder
        try {
            HookCore.hookMethod("com.bytedance.applog.network.UrlBuilder", "build", true);
            HookCore.hookMethod("com.bytedance.applog.util.UrlUtils", "buildUrl", true);
            Log.d(HookUtils.TAG, "[✓] Hooked UrlBuilder");
        } catch (Exception e) {
            Log.e(HookUtils.TAG, "[✗] Failed to hook UrlBuilder: " + e.getMessage());
        }

        // ==================== 11. 通用JSON序列化 - Gson/FastJson ====================

        // 11.1 Gson
        try {
            HookCore.hookMethod("com.google.gson.Gson", "toJson", true, new HookUtils.AnyArgKeywordFilter("event_v3"));
            Log.d(HookUtils.TAG, "[✓] Hooked Gson.toJson with filter");
        } catch (Exception e) {
            Log.e(HookUtils.TAG, "[✗] Failed to hook Gson: " + e.getMessage());
        }

        // 11.2 JSONObject
        try {
            HookCore.hookMethod("org.json.JSONObject", "toString", true);
            Log.d(HookUtils.TAG, "[✓] Hooked JSONObject.toString");
        } catch (Exception e) {
            Log.e(HookUtils.TAG, "[✗] Failed to hook JSONObject: " + e.getMessage());
        }

        // ==================== 12. 检查URL匹配 - 过滤app_log请求 ====================

        // 12.1 HttpUrl
        try {
            HookCore.hookMethod("okhttp3.HttpUrl", "toString", true, new HookUtils.ResultKeywordFilter("app_log"));
            HookCore.hookMethod("okhttp3.HttpUrl$Builder", "build", true);
            Log.d(HookUtils.TAG, "[✓] Hooked HttpUrl with filter");
        } catch (Exception e) {
            Log.e(HookUtils.TAG, "[✗] Failed to hook HttpUrl: " + e.getMessage());
        }

        // ==================== 13. 常见的数据包装类 ====================

        // 13.1 Pack/Package相关类
        try {
            HookCore.hookMethod("com.bytedance.applog.pack.Pack", "toJson", true);
            HookCore.hookMethod("com.bytedance.applog.pack.Pack", "toString", true);
            HookCore.hookMethod("com.bytedance.applog.pack.Pack", "build", true);
            Log.d(HookUtils.TAG, "[✓] Hooked Pack");
        } catch (Exception e) {
            Log.e(HookUtils.TAG, "[✗] Failed to hook Pack: " + e.getMessage());
        }

        Log.d(HookUtils.TAG, "==================== app_log请求Hook完成 ====================");
    }

    /**
     * Hook AppLog 的 getLogEncryptSwitch 方法，强制返回 false 以禁用日志加密
     * 关键目标: 让 com.ss.android.common.applog.AppLog.getLogEncryptSwitch 返回 false
     */
    private void hookAppLogEncryptSwitch() {
        Log.d(HookUtils.TAG, "==================== 开始Hook AppLog加密开关 ====================");

        final String targetClass = "com.ss.android.common.applog.AppLog";
        final String targetMethod = "getLogEncryptSwitch";

        // 使用 HookCore 的动态类加载机制来 Hook
        // 因为这个类可能是动态加载的，我们需要等待它被加载
        HookCore.hookClassWhenLoaded(targetClass, new HookCore.ClassLoadHandler() {
            @Override
            public void onClassLoaded(Class<?> clazz) {
                try {
                    Log.d(HookUtils.TAG, "[*] AppLog class loaded, hooking getLogEncryptSwitch...");

                    XposedHelpers.findAndHookMethod(clazz, targetMethod, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            // 强制返回 false，禁用加密
                            param.setResult(false);
                            Log.d(HookUtils.TAG, "[✓] AppLog.getLogEncryptSwitch called, forced to return false");
                        }
                    });

                    Log.d(HookUtils.TAG, "[✓] Successfully hooked AppLog.getLogEncryptSwitch");
                } catch (Exception e) {
                    Log.e(HookUtils.TAG, "[✗] Failed to hook getLogEncryptSwitch: " + e.getMessage(), e);
                }
            }
        });

        Log.d(HookUtils.TAG, "==================== AppLog加密开关Hook设置完成 ====================");
    }
}
