package thouger.hook

import android.util.Log
import thouger.study.hunter.XC_MethodHook
import thouger.study.hunter.XposedHelpers
import thouger.study.hunter.callbacks.XC_LoadPackage
import thouger.study.hunter.XposedBridge
import thouger.study.hunter.IXposedHookLoadPackage
import java.lang.reflect.Method


class hook : IXposedHookLoadPackage {
    @Throws(Throwable::class)
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {


        System.loadLibrary("native_hook")


        Log.d("thouger","look for packagename: " + lpparam.packageName)
        if (lpparam.packageName == "thouger.study") { // 指定目标应用的包名
            System.loadLibrary("native_hook")
            Log.d("thouger","Hooking into target app: " + lpparam.packageName)
            hook_method(lpparam.classLoader)
        }
//        if (lpparam.packageName == "com.thouger.test") { // 指定目标应用的包名
//
//
////            set_language(lpparam.classLoader)
////            hook_gaid(lpparam.classLoader)
////            http_params(lpparam.classLoader)
//        }
    }

    fun hook_method(classLoader: ClassLoader) {
        val clazz = XposedHelpers.findClass("android.os.SystemProperties", classLoader)
        Log.d("hook_method","target class: " + clazz.name)
        val className = clazz.name
        val m: Array<Method> = clazz.getDeclaredMethods()
        for (method in m) {
            val classMethodName = method.name
            Log.d("hook_method","target method: " + method.name)
            //-----------------重要----------------修改函数名
            if ("native_get" != classMethodName) {
                continue
            }
            Log.d("hook_method","find method: " + method.name)
            val output = "类名：$className,方法名：$classMethodName "
            XposedBridge.hookMethod(method, object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: MethodHookParam) {
                    super.beforeHookedMethod(param)
                    Log.d("hook_method",output + "done")

                }

                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam) {
                    for (i in param.args.indices) {
                        Log.d("hook_method",output + "参数$i：${param.args[i]}")
                    }
                    super.afterHookedMethod(param)
                    Log.d("hook_method",output + "afterHookedMethod result: ${param.result}")
                }
            })
        }
    }

    private fun set_language(classLoader: ClassLoader) {
        val clazz = XposedHelpers.findClass("java.util.Locale", classLoader)
        val className = clazz.name
        val m: Array<Method> = clazz.getDeclaredMethods()
        for (method in m) {
            val classMethodName = method.name
            if ("getDisplayLanguage" != classMethodName && "getLanguage" != classMethodName) {
                continue
            }
            Log.d("thouger","Hooking into target app11111111111111: " + method.name)
            val output = "类名：$className,方法名：$classMethodName "
            Log.d("thouger","Hooking into target app2: " + method.name)

            XposedBridge.hookMethod(method, object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam) {
                    super.afterHookedMethod(param)
                    var result = param.result
                    //判断result是否是字符串类型
                    if (result !is String) {
                        return
                    }
                    if (classMethodName == "getDisplayLanguage") {
                        param.result = "한국어"
                        result = param.result
//                        Log.d("thouger","getDisplayLanguage() result: $result")
                    } else if (classMethodName == "getLanguage") {
                        param.result = "ko"
                        result = param.result
//                        Log.d("thouger","getLanguage() result: $result")
                    }
                }
            })
        }
    }

    private fun http_params(classLoader: ClassLoader) {
        var methodName = "AFInAppEventType"
        Log.d("thouger","Hooking into target app1: " + methodName)
        val clazz = XposedHelpers.findClass("com.appsflyer.internal.aj", classLoader)
        val className = clazz.name
        val m: Array<Method> = clazz.getDeclaredMethods()
        for (method in m) {
            val classMethodName = method.name
            if (methodName != classMethodName) {
                continue
            }
            val output = "类名：$className,方法名：$classMethodName "
            Log.d("thouger","Hooking into target app2: " + method.name)

            XposedBridge.hookMethod(method, object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: MethodHookParam) {
                    Log.d("thouger","Hooking into target app3: ")
                    super.beforeHookedMethod(param)
                    val args = param.args
                    val length = args.size
                    Log.d("thouger","参数长度：$output + $length")
                    for (i in 0 until length) {
                        Log.d("thouger","参数：$i = " + args[i])
                    }
                    //输出最后一个数组参数
                    val array = args[length - 1] as Array<*>
                    for (i in array.indices) {
                        Log.d("thouger","数组参数：$i = " + array[i])
                    }
                }
            })
        }
    }

    private fun hook_gaid(classLoader: ClassLoader) {
        try {
            val parcelClass = XposedHelpers.findClass("android.os.Parcel", classLoader)
            Log.d("thouger","Parcel class found: " + parcelClass.name)

            XposedHelpers.findAndHookMethod(parcelClass, "readString", object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam) {
                    super.afterHookedMethod(param)
                    var result = param.result
                    //判断result是否是字符串类型
                    if (result !is String) {
                        return
                    }
                    //用正则判断四个横杠
                    if (result.toString()
                            .matches(Regex("[a-f0-9]{8}-[a-f0-9]{4}-4[a-f0-9]{3}-[8|9|aA|bB][a-fA-F0-9]{3}-[a-fA-F0-9]{12}"))
                    ) {
                        param.result = "00000000-0000-0000-0000-000000000000"
                        result = param.result
                        Log.d("thouger","readString() result: $result")
                    }
                }
            })
        } catch (e: Exception) {
            Log.e("thouger","Hooking failed: " + e.message)
        }
    }

}