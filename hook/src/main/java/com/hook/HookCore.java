package com.hook;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * 核心Hook函数 - 通用Hook功能
 */
public class HookCore {
    private static ClassLoader defaultClassLoader = null;

    // ==================== ClassLoader 管理 ====================

    /**
     * 设置默认的 ClassLoader
     */
    public static void setDefaultClassLoader(ClassLoader classLoader) {
        defaultClassLoader = classLoader;
    }

    /**
     * 获取默认的 ClassLoader
     */
    public static ClassLoader getDefaultClassLoader() {
        return defaultClassLoader;
    }

    // ==================== 接口定义 ====================

    /**
     * Application.attach回调接口
     */
    public interface ApplicationAttachCallback {
        void onAttached(Context context, ClassLoader classLoader);
    }

    /**
     * 类加载回调接口
     */
    public interface ClassLoadHandler {
        void onClassLoaded(Class<?> clazz);
    }

    // ==================== 私有工具方法 ====================

    /**
     * 安全地查找类，失败时返回null并记录日志
     */
    private static Class<?> findClassSafe(String className, ClassLoader classLoader) {
        try {
            return XposedHelpers.findClass(className, classLoader);
        } catch (Throwable t) {
            Log.e(HookUtils.TAG, "Failed to find class: " + className, t);
            return null;
        }
    }

    /**
     * 创建通用的方法Hook回调
     */
    private static XC_MethodHook createMethodHook(String className, String methodName, boolean printStackTrace, HookUtils.LogFilter filter) {
        return new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                // 格式化参数，用于过滤判断和日志输出
                String[] formattedArgs = new String[param.args.length];
                StringBuilder argsInfo = new StringBuilder();
                for (int i = 0; i < param.args.length; i++) {
                    formattedArgs[i] = HookUtils.formatValue(param.args[i]);
                    argsInfo.append("参数[").append(i).append("]: ").append(formattedArgs[i]).append("\n");
                }

                // 保存格式化的参数和参数信息
                param.setObjectExtra("formattedArgs", formattedArgs);
                param.setObjectExtra("argsInfo", argsInfo.toString());
                param.setObjectExtra("printStackTrace", printStackTrace);

                // 如果有过滤器，在before阶段就过滤（基于参数）
                if (filter != null && !filter.shouldLog(formattedArgs, null)) {
                    param.setObjectExtra("filtered", true);
                }
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                // 格式化返回值
                Object result = param.getResult();
                String formattedResult = HookUtils.formatValue(result);

                // 获取之前格式化的参数
                String[] formattedArgs = (String[]) param.getObjectExtra("formattedArgs");

                // 再次检查过滤器（基于参数和返回值）
                if (filter != null && !filter.shouldLog(formattedArgs, formattedResult)) return;

                // 检查before阶段是否已经过滤
                if (Boolean.TRUE.equals(param.getObjectExtra("filtered"))) return;

                String prefix = className + "." + methodName;
                StringBuilder output = new StringBuilder();
                output.append("\n========== ").append(prefix).append(" ==========\n");

                // 输出堆栈（如果需要）
                if (Boolean.TRUE.equals(param.getObjectExtra("printStackTrace"))) {
                    output.append("【调用堆栈】\n");
                    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                    for (StackTraceElement element : stackTrace) {
                        output.append("  at ").append(element.toString()).append("\n");
                    }
                    output.append("\n");
                }

                // 输出参数信息
                String argsInfo = (String) param.getObjectExtra("argsInfo");
                if (argsInfo != null && !argsInfo.isEmpty()) {
                    output.append("【参数信息】\n").append(argsInfo);
                }

                // 输出返回值
                output.append("【返回值】\n").append(formattedResult).append("\n");
                output.append("=====================================");

                HookUtils.logLong(prefix, output.toString());
            }
        };
    }

    /**
     * 创建通用的方法Hook回调（用于hookAllMethods）
     */
    private static XC_MethodHook createAllMethodsHook(String className, String methodName, boolean printStackTrace) {
        return new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                // 收集参数信息和堆栈信息，保存到Extra中，在after中一起输出
                StringBuilder argsInfo = new StringBuilder();
                for (int i = 0; i < param.args.length; i++) {
                    argsInfo.append("参数[").append(i).append("]: ").append(HookUtils.formatValue(param.args[i])).append("\n");
                }
                param.setObjectExtra("argsInfo", argsInfo.toString());
                param.setObjectExtra("printStackTrace", printStackTrace);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                String prefix = className + "." + methodName;
                StringBuilder output = new StringBuilder();
                output.append("\n========== ").append(prefix).append(" ==========\n");

                // 输出堆栈（如果需要）
                if (Boolean.TRUE.equals(param.getObjectExtra("printStackTrace"))) {
                    output.append("【调用堆栈】\n");
                    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                    for (StackTraceElement element : stackTrace) {
                        output.append("  at ").append(element.toString()).append("\n");
                    }
                    output.append("\n");
                }

                // 输出参数信息
                String argsInfo = (String) param.getObjectExtra("argsInfo");
                if (argsInfo != null && !argsInfo.isEmpty()) {
                    output.append("【参数信息】\n").append(argsInfo);
                }

                // 输出返回值
                output.append("【返回值】\n").append(HookUtils.formatValue(param.getResult())).append("\n");
                output.append("=====================================");

                HookUtils.logLong(prefix, output.toString());
            }
        };
    }

    // ==================== 核心Hook函数 ====================

    /**
     * Hook Application.attach
     */
    public static void hookApplicationAttach(ApplicationAttachCallback callback) {
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Context context = (Context) param.args[0];
                ClassLoader classLoader = context.getClassLoader();
//                Log.d(HookUtils.TAG, "Application.attach() called");
                if (callback != null) {
                    callback.onAttached(context, classLoader);
                }
            }
        });
    }

    /**
     * Hook动态加载的类 - 简单版本（直接指定方法名）
     */
    public static void hookClassWhenLoaded(String className, String methodName, boolean printStackTrace) {
        hookClassWhenLoaded(className, clazz -> hookMethodImpl(clazz, methodName, printStackTrace, null));
    }

    /**
     * Hook动态加载的类 - 完整版本（使用Handler）
     */
    public static void hookClassWhenLoaded(String className, ClassLoadHandler handler) {
        XposedHelpers.findAndHookMethod(ClassLoader.class, "loadClass", String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Class<?> clazz = (Class<?>) param.getResult();
                if (clazz == null) return;

                String loadedClassName = clazz.getName();
                if (loadedClassName != null && loadedClassName.equals(className)) {
                    HookUtils.logLong("ClassLoad", "Found target class: " + className);
                    try {
                        handler.onClassLoaded(clazz);
                    } catch (Throwable t) {
                        Log.e(HookUtils.TAG, "Error in ClassLoadHandler for " + className, t);
                    }
                }
            }
        });
    }

    /**
     * Hook多个动态加载的类
     */
    public static void hookClassesWhenLoaded(Map<String, ClassLoadHandler> handlers) {
        XposedHelpers.findAndHookMethod(ClassLoader.class, "loadClass", String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Class<?> clazz = (Class<?>) param.getResult();
                if (clazz == null) return;

                String className = clazz.getName();
                if (className != null && handlers.containsKey(className)) {
                    HookUtils.logLong("ClassLoad", "Found target class: " + className);
                    ClassLoadHandler handler = handlers.get(className);
                    if (handler != null) {
                        try {
                            handler.onClassLoaded(clazz);
                        } catch (Throwable t) {
                            Log.e(HookUtils.TAG, "Error in ClassLoadHandler for " + className, t);
                        }
                    }
                }
            }
        });
    }

    // ==================== hookMethod 系列 ====================

    /**
     * Hook指定方法（使用默认 ClassLoader）
     */
    public static void hookMethod(String className, String methodName, boolean printStackTrace) {
        hookMethod(className, defaultClassLoader, methodName, printStackTrace, null);
    }

    /**
     * Hook指定方法（使用默认 ClassLoader + 过滤器）
     */
    public static void hookMethod(String className, String methodName, boolean printStackTrace, HookUtils.LogFilter filter) {
        hookMethod(className, defaultClassLoader, methodName, printStackTrace, filter);
    }

    /**
     * Hook指定方法（指定 ClassLoader）
     */
    public static void hookMethod(String className, ClassLoader classLoader, String methodName, boolean printStackTrace) {
        hookMethod(className, classLoader, methodName, printStackTrace, null);
    }

    /**
     * Hook指定方法（指定 ClassLoader + 过滤器）- 主实现方法
     */
    public static void hookMethod(String className, ClassLoader classLoader, String methodName, boolean printStackTrace, HookUtils.LogFilter filter) {
        Class<?> clazz = findClassSafe(className, classLoader);
        if (clazz != null) {
            HookUtils.logLong("HookMethod", "[✓✓✓] 成功找到类: " + className);
            HookUtils.logLong("HookMethod", "[✓✓✓] 准备Hook方法: " + methodName);
            hookMethodImpl(clazz, methodName, printStackTrace, filter);
        } else {
            HookUtils.logLong("HookMethod",
                "\n╔════════════════════════════════════════════════════════════╗\n" +
                "║  ✗✗✗ 找不到类: " + className + "\n" +
                "║  ✗✗✗ 无法Hook方法: " + methodName + "\n" +
                "╚════════════════════════════════════════════════════════════╝");
        }
    }

    /**
     * Hook指定方法的实际实现
     */
    private static void hookMethodImpl(Class<?> clazz, String methodName, boolean printStackTrace, HookUtils.LogFilter filter) {
        String className = clazz.getName();
        Method[] methods = clazz.getDeclaredMethods();

        int hookedCount = 0;
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                XposedBridge.hookMethod(method, createMethodHook(className, methodName, printStackTrace, filter));
                hookedCount++;
                HookUtils.logLong("HookMethod", "[✓✓✓] 成功Hook方法: " + className + "." + methodName);
                HookUtils.logLong("HookMethod", "[✓✓✓] 方法签名: " + method.toString());
            }
        }

        if (hookedCount == 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("\n╔════════════════════════════════════════════════════════════╗\n");
            sb.append("║  ✗✗✗ 类中找不到方法: ").append(className).append(".").append(methodName).append("\n");
            sb.append("║  ✗✗✗ 类中可用的方法列表:\n");
            for (Method method : methods) {
                sb.append("║      - ").append(method.getName()).append("\n");
            }
            sb.append("╚════════════════════════════════════════════════════════════╝");
            HookUtils.logLong("HookMethod", sb.toString());
        }
    }

    // ==================== hookAllMethods 系列 ====================

    /**
     * Hook指定类的所有方法（使用默认 ClassLoader）
     */
    public static void hookAllMethods(String className, boolean printStackTrace) {
        hookAllMethods(className, defaultClassLoader, printStackTrace);
    }

    /**
     * Hook指定类的所有方法（指定 ClassLoader）
     */
    public static void hookAllMethods(String className, ClassLoader classLoader, boolean printStackTrace) {
        Class<?> clazz = findClassSafe(className, classLoader);
        if (clazz != null) {
            HookUtils.logLong("HookAllMethods", "[✓✓✓] 成功找到类: " + className);
            hookAllMethodsImpl(clazz, printStackTrace);
        } else {
            HookUtils.logLong("HookAllMethods",
                "\n╔════════════════════════════════════════════════════════════╗\n" +
                "║  ✗✗✗ 找不到类: " + className + "\n" +
                "╚════════════════════════════════════════════════════════════╝");
        }
    }

    /**
     * Hook指定类的所有方法的实际实现
     */
    private static void hookAllMethodsImpl(Class<?> clazz, boolean printStackTrace) {
        String className = clazz.getName();
        Method[] methods = clazz.getDeclaredMethods();

        // Hook构造函数
        XposedBridge.hookAllConstructors(clazz, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                // 收集参数信息和堆栈信息，保存到Extra中，在after中一起输出
                StringBuilder argsInfo = new StringBuilder();
                for (int i = 0; i < param.args.length; i++) {
                    argsInfo.append("参数[").append(i).append("]: ").append(HookUtils.formatValue(param.args[i])).append("\n");
                }
                param.setObjectExtra("argsInfo", argsInfo.toString());
                param.setObjectExtra("printStackTrace", printStackTrace);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                String prefix = className + ".Constructor";
                StringBuilder output = new StringBuilder();
                output.append("\n========== ").append(prefix).append(" ==========\n");

                // 输出堆栈（如果需要）
                if (Boolean.TRUE.equals(param.getObjectExtra("printStackTrace"))) {
                    output.append("【调用堆栈】\n");
                    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                    for (StackTraceElement element : stackTrace) {
                        output.append("  at ").append(element.toString()).append("\n");
                    }
                    output.append("\n");
                }

                // 输出参数信息
                String argsInfo = (String) param.getObjectExtra("argsInfo");
                if (argsInfo != null && !argsInfo.isEmpty()) {
                    output.append("【参数信息】\n").append(argsInfo);
                }

                // 输出返回值（构造函数返回this对象）
                output.append("【返回对象】\n").append(HookUtils.formatValue(param.thisObject)).append("\n");
                output.append("=====================================");

                HookUtils.logLong(prefix, output.toString());
            }
        });

        // Hook所有方法
        for (Method method : methods) {
            String methodName = method.getName();
            XposedBridge.hookMethod(method, createAllMethodsHook(className, methodName, printStackTrace));
        }
    }
}
