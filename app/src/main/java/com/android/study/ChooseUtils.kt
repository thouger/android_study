package com.android.study

import android.annotation.SuppressLint
import android.annotation.TargetApi
import org.lsposed.hiddenapibypass.HiddenApiBypass
import timber.log.Timber
import java.lang.reflect.Method
import java.util.Arrays

@SuppressLint("SoonBlockedPrivateApi")
object ChooseUtils {
    private var startMethodTracingMethod: Method? = null
    private var stopMethodTracingMethod: Method? = null
    private var getMethodTracingModeMethod: Method? = null
    private var getRuntimeStatMethod: Method? = null
    private var getRuntimeStatsMethod: Method? = null
    private var countInstancesOfClassMethod: Method? = null
    private var countInstancesOfClassesMethod: Method? = null
    private var getInstancesOfClassesMethod: Method? = null

    init {
        HiddenApiBypass.addHiddenApiExemptions()

        try {
            val c = Class.forName("dalvik.system.VMDebug")

//            for(method in c.declaredMethods){
//                Timber.d("method: ${method.name}")
//            }

//            startMethodTracingMethod = c.getDeclaredMethod(
//                "startMethodTracing", String::class.java,
//                Integer.TYPE, Integer.TYPE, java.lang.Boolean.TYPE, Integer.TYPE
//            )
            for (method in HiddenApiBypass.getDeclaredMethods(c)) {
                if(method.toString().contains("startMethodTracing(java.lang.String,int,int,boolean,int)"))
                {
                    val _method = method as Method
                    _method.isAccessible = true
                    startMethodTracingMethod = _method
                }
                if(method.toString().contains("stopMethodTracing()"))
                {
                    val _method = method as Method
                    _method.isAccessible = true
                    stopMethodTracingMethod = _method
                }
                if(method.toString().contains("getMethodTracingMode()"))
                {
                    val _method = method as Method
                    _method.isAccessible = true
                    getMethodTracingModeMethod = _method
                }
                if(method.toString().contains("getRuntimeStat(java.lang.String)"))
                {
                    val _method = method as Method
                    _method.isAccessible = true
                    getRuntimeStatMethod = _method
                }
                if(method.toString().contains("getRuntimeStats()"))
                {
                    val _method = method as Method
                    _method.isAccessible = true
                    getRuntimeStatsMethod = _method
                }
                if(method.toString().contains("countInstancesOfClasses(java.lang.Class[],boolean)"))
                {
                    val _method = method as Method
                    _method.isAccessible = true
                    countInstancesOfClassMethod = _method
                }
                if(method.toString().contains("countInstancesOfClasses(java.lang.Class[],boolean)"))
                {
                    val _method = method as Method
                    _method.isAccessible = true
                    countInstancesOfClassesMethod = _method
                }
                if(method.toString().contains("getInstancesOfClasses(java.lang.Class[],boolean)"))
                {
                    val _method = method as Method
                    _method.isAccessible = true
                    getInstancesOfClassesMethod = _method
                }
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    /**
     * 根据Class获取当前进程全部的实例
     *
     * @param clazz 需要查找的Class
     * @return 当前进程的全部实例。
     */
    fun choose(clazz: Class<*>?): ArrayList<Any>? {
        return choose(clazz, false)
    }

    /**
     * 根据Class获取当前进程全部的实例
     *
     * @param clazz      需要查找的Class
     * @param assignable 是否包含子类的实例
     * @return 当前进程的全部实例。
     */
    @TargetApi(28)
    @Synchronized
    fun choose(clazz: Class<*>?, assignable: Boolean): ArrayList<Any>? {
        var resut: ArrayList<Any>? = null
        try {
            val instancesOfClasses = getInstancesOfClasses(arrayOf(clazz), assignable)
            if (instancesOfClasses != null) {
                resut = ArrayList()
                for (instancesOfClass in instancesOfClasses) {
                    resut.addAll(Arrays.asList(*instancesOfClass))
                }
            }
        } catch (e: Throwable) {
            Timber.d( "ChooseUtils choose error ", e)
            e.printStackTrace()
        }
        return resut
    }

    @TargetApi(28)
    @Throws(Exception::class)
    private fun getInstancesOfClasses(
        classes: Array<Class<*>?>,
        assignable: Boolean
    ): Array<Array<Any>> {
        return getInstancesOfClassesMethod!!.invoke(
            null, *arrayOf<Any>(classes, assignable)
        ) as Array<Array<Any>>
    }

    @Throws(Exception::class)
    fun startMethodTracing(
        filename: String?, bufferSize: Int, flags: Int,
        samplingEnabled: Boolean, intervalUs: Int
    ) {
        startMethodTracingMethod!!.invoke(
            null, filename, bufferSize, flags, samplingEnabled,
            intervalUs
        )
    }

    @Throws(Exception::class)
    fun stopMethodTracing() {
        stopMethodTracingMethod!!.invoke(null)
    }

    @get:Throws(Exception::class)
    val methodTracingMode: Int
        get() = getMethodTracingModeMethod!!.invoke(null) as Int

    /**
     * String gc_count = VMDebug.getRuntimeStat("art.gc.gc-count");
     * String gc_time = VMDebug.getRuntimeStat("art.gc.gc-time");
     * String bytes_allocated = VMDebug.getRuntimeStat("art.gc.bytes-allocated");
     * String bytes_freed = VMDebug.getRuntimeStat("art.gc.bytes-freed");
     * String blocking_gc_count = VMDebug.getRuntimeStat("art.gc.blocking-gc-count");
     * String blocking_gc_time = VMDebug.getRuntimeStat("art.gc.blocking-gc-time");
     * String gc_count_rate_histogram = VMDebug.getRuntimeStat("art.gc.gc-count-rate-histogram");
     * String blocking_gc_count_rate_histogram =VMDebug.getRuntimeStat("art.gc.gc-count-rate-histogram");
     */
    @Throws(Exception::class)
    fun getRuntimeStat(statName: String?): String {
        return getRuntimeStatMethod!!.invoke(null, statName) as String
    }

    @get:Throws(Exception::class)
    val runtimeStats: Map<String, String>
        /**
         * 获取当前进程的状态信息
         */
        get() = getRuntimeStatsMethod!!.invoke(null) as Map<String, String>

    @Throws(Exception::class)
    fun countInstancesofClass(c: Class<*>, assignable: Boolean): Long {
        return countInstancesOfClassMethod!!.invoke(null, *arrayOf<Any>(c, assignable)) as Long
    }

    @Throws(Exception::class)
    fun countInstancesofClasses(classes: Array<Class<*>?>, assignable: Boolean): LongArray {
        return countInstancesOfClassesMethod!!.invoke(
            null, *arrayOf<Any>(classes, assignable)
        ) as LongArray
    }
}