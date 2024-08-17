package thouger.hook

import android.util.Log
import com.android.study.hunter.XC_MethodHook
import com.android.study.hunter.XposedHelpers
import com.android.study.hunter.callbacks.XC_LoadPackage
import com.android.study.hunter.XposedBridge
import com.android.study.hunter.IXposedHookLoadPackage
import java.lang.reflect.Method


class hook : IXposedHookLoadPackage {
    @Throws(Throwable::class)
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        Log.d("thouger","look for packagename: " + lpparam.packageName)
        if (lpparam.packageName == "com.miravia.android") { // 指定目标应用的包名
//            System.loadLibrary("native_hook")
            Log.d("thouger","Hooking into target app: " + lpparam.packageName)
            hook_method(lpparam.classLoader)
        }
    }

    fun hook_method(classLoader: ClassLoader) {
        val clazz = XposedHelpers.findClass("com.alibaba.minilibc.android.DataReportJniBridge", classLoader)
        Log.d("hook_method","target class: " + clazz.name)
        val className = clazz.name
        val m: Array<Method> = clazz.getDeclaredMethods()
        for (method in m) {
            val classMethodName = method.name
            Log.d("hook_method","target method: " + method.name)
            //-----------------重要----------------修改函数名
//            if ("native_get" != classMethodName) {
//                continue
//            }
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
}