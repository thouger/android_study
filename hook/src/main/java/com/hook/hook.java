package com.hook;


import android.util.Log;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import java.lang.reflect.Method;

public class hook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        Log.d("thouger", "look for packagename: " + lpparam.packageName);
        hookMethod(lpparam.classLoader);
    }

    public void hookMethod(ClassLoader classLoader) {
        Class<?> clazz = XposedHelpers.findClass("com.adjust.sdk.Logger", classLoader);
        Log.d("hook_method", "target class: " + clazz.getName());
        String className = clazz.getName();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            String classMethodName = method.getName();
            Log.d("hook_method", "target method: " + method.getName());
            // 如果需要过滤特定方法，可以在这里进行判断
            // if (!"native_get".equals(classMethodName)) {
            //     continue;
            // }
            Log.d("hook_method", "find method: " + method.getName());
            String output = "类名：" + className + ", 方法名：" + classMethodName + " ";
            XposedBridge.hookMethod(method, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    Log.d("hook_method", output + "done");
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    for (int i = 0; i < param.args.length; i++) {
                        Log.d("hook_method", output + "参数" + i + "：" + param.args[i]);
                    }
                    super.afterHookedMethod(param);
                    Log.d("hook_method", output + "afterHookedMethod result: " + param.getResult());
                }
            });
        }
    }
}