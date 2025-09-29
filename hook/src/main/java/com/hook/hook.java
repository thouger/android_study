package com.hook;


import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import android.app.Application;
import android.content.Context;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class hook implements IXposedHookLoadPackage {
    //定义包名
    private static final String TAG = "XposedModule";
    private static XC_LoadPackage.LoadPackageParam lpparam = null;

    private static Context context;
    private static ClassLoader classLoader;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam _lpparam) throws ClassNotFoundException {
        lpparam = _lpparam;
        hook(lpparam);
    }

    private void hook(XC_LoadPackage.LoadPackageParam lpparam) throws ClassNotFoundException {
        Log.d(TAG, "handleLoadPackage: " + lpparam.packageName);

        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Log.d(TAG, param.thisObject.getClass().getName());

                context = (Context) param.args[0];
                classLoader = context.getClassLoader();

                setClipboardText(context,"https://detail.tmall.com/item.htm?spm=a21bo.jianhua/a.201876.d9.5af92a89UVNQXw&id=935498097143&xxc=ad_ct&skuId=5826999421165&mi_id=ShfN7fmMUiVlJdyfPXQX-59LnVIIuwsLB7A-X7GJd6fgQrQuNEJzMXG0PrDIPXD48sVR40B2HClzqypF7bOVwBWuqMQ8KHbGyqlk09a6enQ&utparam=%7B%22item_ctr%22%3A0.00338%2C%22x_object_type%22%3A%22p4p_item%22%2C%22item_price%22%3A%2241.9%22%2C%22item_cvr%22%3A0.064683%2C%22pc_scene%22%3A%2220001%22%2C%22aplus_abtest%22%3A%227ad864ee5baa8f23bc7ce002d97de132%22%2C%22tpp_buckets%22%3A%2230986%23449124%230_30986%23520364%2357062_30986%23521582%2357267_30986%23526068%2358189_30986%23524388%2357910%22%2C%22ab_info%22%3A%2230986%23449124%230_30986%23520364%2357062_30986%23521582%2357267_30986%23526068%2358189_30986%23524388%2357910%22%2C%22abid%22%3A%22520364_521582_526068_524388%22%2C%22pc_pvid%22%3A%22015081b6-4997-4440-8a65-983ab08bdec6%22%2C%22mix_group%22%3A%22%22%2C%22item_ecpm%22%3A0.011187%2C%22x_object_id%22%3A935498097143%7D");
            }
        });

        Class<?> clazz = XposedHelpers.findClass("android.net.Activity", lpparam.classLoader);
//        Class<?> classs = lpparam.classLoader.loadClass("dalvik.system.DexFile");;
//        hook_one_method(classs,"attach", Context.class);
//        hook_all_method(classs,"defineClass");

//        hook_all_method(clazz);
    }

    private void hook_one_method(Class<?> clazz, String methodName, Object... parameterTypesAndCallback) {
        findAndHookMethod(Application.class, "attach",
                Context.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        if (param == null) {
                            return;
                        }
                        //输出所有参数
                        for (Object o : param.args) {
                            Log.d(TAG, "beforeHookedMethod参数：" + o);
                        }
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Log.d(TAG, "afterHookedMethod返回值：" + param.getResult());
                        if (param == null) {
                            return;
                        }
                        //输出所有参数
                        for (Object o : param.args) {
                            Log.d(TAG, "afterHookedMethod参数：" + o);
                        }

                        ClassLoader cl = ((Context) param.args[0]).getClassLoader();
                        Class<?> hookclass = null;
                        try {
                            hookclass = cl.loadClass("com.appsflyer.internal.AFa1nSDK$30218$AFa1wSDK$25697");
                            Log.d(TAG, "load success");
                        } catch (Exception e) {
                            Log.d(TAG, "load class error");
                            return;
                        }
                    }
                });
    }

    private void hook_all_method(Class<?> clazz) {
        String className = clazz.getName();
        Method[] m = clazz.getDeclaredMethods();

        //hook构造函数
        XposedBridge.hookAllConstructors(clazz, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                if (param == null) {
                    return;
                }
                //输出所有参数
                for (Object o : param.args) {
                    Log.d(TAG, className + ",Constructor beforeHookedMethod参数：" + o);
                }
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Object obj = param.thisObject;
                Log.d(TAG, className + ",Constructor afterHookedMethod参数：" + obj);
            }
        });

        for (Method method : m) {
            String classMethodName = method.getName();
            String output = "类名：" + className + ",方法名：" + classMethodName + " ";

            XposedBridge.hookMethod(method, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (param == null) {
                        return;
                    }
                    //输出所有参数
                    for (Object o : param.args) {
                        Log.d(TAG, output + ",beforeHookedMethod参数：" + o);
                    }
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Object result = param.getResult();
                    Log.d(TAG, output + ",afterHookedMethod返回值：" + result);
                    if (param == null) {
                        return;
                    }
                    //输出所有参数
                    for (int i = 0; i < param.args.length; i++) {
                        Log.d(TAG, output + ",afterHookedMethod参数" + i + "： " + param.args[i]);
                    }
                }
            });
        }
    }

//    private Object getFieldValue(Class obj) {
//        Field field = null;
//        if (obj == null) {
//            return null;
//        }
//        Log.d(TAG,"obj is: " + obj);
//        for(Field f : obj.getDeclaredFields()){
//            f.setAccessible(true);
//            try{
//                field = obj.getDeclaredField(f.getName());
//                Log.d(TAG,"field is: " + field.get(obj));
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//        return field;
//    }


    private Object getFieldValue(Class<?> obj, String... fieldNames) {
        Object field = null;
        if (obj == null) {
            return null;
        }
        for (String fieldName : fieldNames) {
            field = getFieldValue(obj, fieldName);
        }
        return field;
    }
    private Object getFieldValue(Class<?> obj, String fieldName) {
        try {
            Log.d(TAG, "obj is: " + obj);
            Field field = obj.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setClipboardText(Context context, String text) {
        Log.d(TAG, "setClipboardText: " + text);
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", text);
        clipboard.setPrimaryClip(clip);
    }
    // 第一步：Hook方法ClassLoader#loadClass(String)
//        findAndHookMethod(ClassLoader.class, "loadClass", String.class, new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) {
//                if (param.hasThrowable()) return;
//                Class<?> cls = (Class<?>) param.getResult();
//                String name = cls.getName();
//                Log.d(TAG,"loadClass: " + name);
////                if (classes.contains(name)) {
////                    // 所有的类都是通过loadClass方法加载的
////                    // 所以这里通过判断全限定类名，查找到目标类
////                    // 第二步：Hook目标方法
////                    findAndHookMethod(cls, "methodName", paramTypes, new XC_MethodHook() {
////                        @Override
////                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
////                            // TODO
////                        }
////
////                        @Override
////                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
////                            // TODO
////                        }
////                    });
////                }
//            }
//        });
}