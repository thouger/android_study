package thouger//package com.example.android_study
//
//import android.app.Application
//import android.content.Context
//import dalvik.system.DexFile
//
//import com.android.study.xposed.IXposedHookLoadPackage
//import com.android.study.xposed.XC_MethodHook
//import com.android.study.xposed.XposedBridge
//import com.android.study.xposed.XposedHelpers.findAndHookMethod
//import com.android.study.xposed.callbacks.XC_LoadPackage
//import timber.log.Timber
//import java.util.*
//
//class hook_copy : IXposedHookLoadPackage {
//
//    @Throws(ClassNotFoundException::class)
//    override fun handleLoadPackage(_lpparam: XC_LoadPackage.LoadPackageParam?) {
//        lpparam = _lpparam
//        System.out.println("handleLoadPackage: " + (lpparam?.packageName ?: "null"))
//        // 过滤不必要的应用
//        if (!lpparam?.packageName.equals(PACKAGE_NAME)) return
//        // 执行Hook
//        hook_copy(lpparam)
//    }
//
//    private constructor(lpparam: XC_LoadPackage.LoadPackageParam?) {
//        if (lpparam != null) {
//            Timber.d("handleLoadPackage: " + lpparam.packageName)
//        }
//
////        Class<?> clazz = XposedHelpers.findClass("java.lang.System", lpparam.classLoader);
//        val classs: Class<DexFile> = lpparam?.classLoader?.loadClass("dalvik.system.DexFile") as Class<DexFile>
//        //        hook_one_method(classs,"attach", Context.class);
//        hook_all_method(classs, "defineClass")
//    }
//
//    private fun hook_one_method(
//        clazz: Class<*>,
//        methodName: String,
//        vararg parameterTypesAndCallback: Any
//    ) {
//        findAndHookMethod(
//            Application::class.java, "attach",
//            Context::class.java, object : XC_MethodHook() {
//                @Throws(Throwable::class)
//                protected override fun beforeHookedMethod(param: MethodHookParam?) {
//                    super.beforeHookedMethod(param)
//                    if (param == null) {
//                        return
//                    }
//                    //输出所有参数
//                    for (o in param.args) {
//                        Timber.d("beforeHookedMethod参数：$o")
//                    }
//                }
//
//                @Throws(Throwable::class)
//                protected override fun afterHookedMethod(param: MethodHookParam?) {
//                    super.afterHookedMethod(param)
//                    if (param != null) {
//                        Timber.d("afterHookedMethod返回值：" + param.getResult())
//                    }
//                    if (param == null) {
//                        return
//                    }
//                    //输出所有参数
//                    for (o in param.args) {
//                        Timber.d("afterHookedMethod参数：$o")
//                    }
//                    val cl = (param.args.get(0) as Context).classLoader
//                    var hookclass: Class<*>? = null
//                    try {
//                        hookclass =
//                            cl.loadClass("com.appsflyer.internal.AFa1nSDK$30218\$AFa1wSDK$25697")
//                        Timber.d("load success")
//                    } catch (e: Exception) {
//                        Timber.d("load class error")
//                        return
//                    }
//                }
//            })
//    }
//
//    private fun hook_all_method(clazz: Class<*>, methodName: String?) {
//        val className = clazz.name
//        val m = clazz.declaredMethods
//
//        //hook构造函数
//        if (className == "com.appsflyer.internal.AFa1uSDK$24407" || className == "com.appsflyer.internal.AFa1nSDK$30218\$AFa1wSDK$25697") {
//            XposedBridge.hookAllConstructors(clazz, object : XC_MethodHook() {
//                @Throws(Throwable::class)
//                protected override fun beforeHookedMethod(param: MethodHookParam?) {
//                    super.beforeHookedMethod(param)
//                    if (param == null) {
//                        return
//                    }
//                    //输出所有参数
//                    for (o in param.args) {
//                        Timber.d("$className,Constructor beforeHookedMethod参数：$o")
//                    }
//                }
//
//                @Throws(Throwable::class)
//                protected override fun afterHookedMethod(param: MethodHookParam) {
//                    super.afterHookedMethod(param)
//                    val obj: Any = param.thisObject
//                    Timber.d("$className,Constructor afterHookedMethod参数：$obj")
//                    if (className == "com.appsflyer.internal.AFa1nSDK$30218\$AFa1wSDK$25697") {
//                        new_obj = obj
//                        Timber.d("new_obj is1: " + new_obj)
//                    }
//                    try {
//                        val field = obj.javaClass.getDeclaredField("valueOf")
//                        field.isAccessible = true
//                        val value = field[obj]
//                        // 打印成员变量
//                        //values是一个字节数组，下面输出数组的值
//                        Timber.d(
//                            "com.appsflyer.internal.AFa1uSDK$24407  valuesof is1: " + Arrays.toString(
//                                value as ByteArray
//                            )
//                        )
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }
//            })
//        }
//        for (method in m) {
//            val classMethodName = method.name
//            if (methodName != null && methodName != classMethodName) {
//                continue
//            }
//            val output = "类名：$className,方法名：$classMethodName "
//            XposedBridge.hookMethod(method, object : XC_MethodHook() {
//                @Throws(Throwable::class)
//                protected override fun beforeHookedMethod(param: MethodHookParam?) {
//                    super.beforeHookedMethod(param)
//                    if (param == null) {
//                        return
//                    }
//                    //输出所有参数
//                    for (o in param.args) {
//                        Timber.d("$output,beforeHookedMethod参数：$o")
//                    }
//                }
//
//                @Throws(Throwable::class)
//                protected override fun afterHookedMethod(param: MethodHookParam?) {
//                    super.afterHookedMethod(param)
//                    val result: Any = param?.getResult() ?: return
//                    Timber.d("$output,afterHookedMethod返回值：$result")
//                    //输出所有参数
//                    for (i in 0 until param.args.size) {
//                        Timber.d(output + ",afterHookedMethod参数" + i + "： " + param.args.get(i))
//                    }
//                    if (param.args.get(0) is String) {
//                        val ClassName = param.args.get(0) as String
//                        //                            if (ClassName.contains("com.appsflyer.internal.AFa1nSDK$30218")) {
////                                Timber.d("find class1");
////                                Class _clazz = (Class) param.getResult();
////                                hook_all_method(_clazz, null);
////                            }
////                            if (ClassName.contains("com.appsflyer.internal.AFa1uSDK$24407")) {
////                                Timber.d("find class2");
////                                Class _clazz = (Class) param.getResult();
////                                hook_all_method(_clazz, null);
////                            }
//                        if (ClassName.contains("com.appsflyer.internal.AFc1qSDK\$AFa1vSDK")) {
//                            Timber.d("find class")
//                            val _clazz = param.getResult() as Class<*>
//                            hook_all_method(_clazz, null)
//                        }
//                    }
//                    //                        if (classMethodName.equals("AFInAppEventParameterName")) {
////                            //将返回值转化为数组输出
////                            byte[] array = (byte[]) result;
////                            Timber.d("com.appsflyer.internal.AFa1nSDK$30218.AFInAppEventParameterName 返回值：" + Arrays.toString(array));
////                        }
////                        if (classMethodName.equals("AFInAppEventType")) {
////                            //将返回值转化为数组输出
////                            byte[] array = (byte[]) getFieldValue(clazz, "values");
////                            Timber.d("com.appsflyer.internal.AFa1uSDK$24407 values " + Arrays.toString(array));
////                            Timber.d("com.appsflyer.internal.AFa1uSDK$24407 AFInAppEventType bArr " + Arrays.toString((byte[]) param.args[0]));
////                        }
////                        if ( classMethodName.equals("AFKeystoreWrapper") && className.equals("com.appsflyer.internal.AFa1nSDK$30218")){
////                            //返回值是一个字节数组，
////                            byte[] array = (byte[]) result;
////                            //转为字符串输出
////                            String str = new String(array);
////                            Timber.d("com.appsflyer.internal.AFa1nSDK$30218.AFKeystoreWrapper 返回值字符串：" + str);
////                        }
//                }
//            })
//        }
//    }
//
//    //    private Object getFieldValue(Class obj) {
//    //        Field field = null;
//    //        if (obj == null) {
//    //            return null;
//    //        }
//    //        Timber.d("obj is: " + obj);
//    //        for(Field f : obj.getDeclaredFields()){
//    //            f.setAccessible(true);
//    //            try{
//    //                field = obj.getDeclaredField(f.getName());
//    //                Timber.d("field is: " + field.get(obj));
//    //            }catch (Exception e){
//    //                e.printStackTrace();
//    //            }
//    //        }
//    //        return field;
//    //    }
//    private fun getFieldValue(obj: Class<*>?, vararg fieldNames: String): Any? {
//        var field: Any? = null
//        if (obj == null) {
//            return null
//        }
//        for (fieldName in fieldNames) {
//            field = getFieldValue(obj, fieldName)
//        }
//        return field
//    }
//
//    private fun getFieldValue(obj: Class<*>, fieldName: String): Any? {
//        try {
//            Timber.d("obj is: $obj")
//            val field = obj.getDeclaredField(fieldName)
//            field.isAccessible = true
//            return field[obj]
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return null
//    }
//
//    companion object {
//        //定义包名
//        private const val PACKAGE_NAME = "com.example.appa"
//        private var lpparam: XC_LoadPackage.LoadPackageParam? = null
//        private var new_obj: Any? = null
//    }
//}
