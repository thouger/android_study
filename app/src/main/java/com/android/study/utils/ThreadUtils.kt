package com.android.study.utils

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

object ThreadUtils {
    //使用主线程的looper创建handler 这个handler一定执行在主线程的
    var handler = Handler(Looper.getMainLooper())

    //单线程的线程池
    private val executor: Executor = Executors.newSingleThreadExecutor()

    /**
     * 在子线程中执行代码
     */
    fun runOnNonUIThread(r: Runnable?) {
        executor.execute(r)
    }

    /**
     * 在主线程中执行代码
     */
    fun runOnMainThread(r: Runnable?) {
        handler.post(r!!)
    }

    fun runOnMainThread(r: Runnable?, delayed: Long) {
        handler.postDelayed(r!!, delayed)
    }
}