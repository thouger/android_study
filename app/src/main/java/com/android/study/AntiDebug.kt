package com.android.study

import android.util.Log

object AntiDebug {
    private const val TAG = "AntiDebug"

    init {
        try {
            System.loadLibrary("native-lib")
            Log.i(TAG, "反调试模块加载成功")
        } catch (e: Exception) {
            Log.e(TAG, "反调试模块加载失败: ${e.message}")
        }
    }

    // 运行所有反调试检测
    external fun runAllChecks()

    // 1. 检测调试端口23946
    external fun checkPort23946()

    // 2. 检测调试进程
    external fun searchDebugProcess()

    // 3. 检测父进程
    external fun checkParents()

    // 4. 检测自身进程名
    external fun checkSelfProcessName()

    // 5. 检测线程数
    external fun checkTaskCount()

    // 6. 检测文件描述符数量
    external fun checkFdCount()

    // 7. 检测Dalvik调试连接
    external fun checkDalvikDebugger()

    // 8. ptrace自我保护
    external fun ptraceProtect()

    // 9. 计算函数Hash值
    external fun calculateFunctionHash(address: Long, size: Int): Int

    // 10. 检测断点指令
    external fun checkBreakpoint(address: Long, size: Int)

    // 11. 系统源码修改检测(TracerPid检测)
    external fun checkTracerPid(): Boolean

    // 12. 信号处理检测
    external fun checkSignalHandler(): Boolean

    // 13. 时间差检测
    external fun checkTimingAttack(): Boolean

    // 14. 进程状态检测
    external fun checkProcessStatus(): Boolean

    // 15. Inotify监控检测
    external fun startInotifyMonitor()

    // 辅助函数：在主线程运行所有检测
    fun runAllChecksOnMainThread() {
        Thread {
            try {
                runAllChecks()
            } catch (e: Exception) {
                Log.e(TAG, "反调试检测异常: ${e.message}")
            }
        }.start()
    }
}
