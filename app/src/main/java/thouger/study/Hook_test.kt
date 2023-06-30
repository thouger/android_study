package thouger.study

import android.util.Log
import thouger.study.hunter.XC_MethodHook
import thouger.study.hunter.XposedBridge
import java.lang.reflect.Method

class Hook_test {

    fun test(){
        // hook thouger.study.Test.testttttttttttt
        var methodName = "testttttttttttt"
        Log.d("thouger","Hooking into target app1: " + methodName)
        val clazz = Test::class.java
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

        Test().testttttttttttt("hello world")
    }
}