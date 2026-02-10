# Android 反调试模块

本模块实现了15种常见的Android反调试检测技术，用于检测应用是否处于被调试状态。

## 功能列表

### 基础检测 (1-10)

1. **调试端口检测** - 检测IDA远程调试端口23946
2. **调试进程名检测** - 查找已知的调试器进程(android_server, gdbserver等)
3. **父进程名检测** - 检查父进程是否为zygote
4. **自身进程名检测** - 检查进程名是否正常
5. **APK线程数检测** - 检测线程数是否异常
6. **APK进程FD文件检测** - 检查文件描述符数量
7. **Android系统调试检测** - dalvik模式下的调试连接检测
8. **ptrace检测** - 主动ptrace防止被调试
9. **函数hash值检测** - 检测函数是否被修改或下断点
10. **断点指令检测** - 检测ARM/Thumb断点指令

### 高级检测 (11-15)

11. **系统源码修改检测** - 通过fork+ptrace检测TracerPid
12. **信号处理检测** - 利用IDA先截获信号的特性
13. **时间差检测** - 5种时间API综合检测单步调试
    - time()
    - clock()
    - gettimeofday()
    - clock_gettime()
    - getrusage()
14. **进程状态检测** - 检测/proc/pid/下的status、stat、wchan文件
15. **Inotify监控** - 监控/proc/pid/mem和pagemap的访问

## 使用方法

### Kotlin/Java层调用

```kotlin
import com.android.study.AntiDebug

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 方式1: 运行所有检测(基础+高级)
        AntiDebug.runAllChecksOnMainThread()

        // 方式2: 单独调用特定检测
        AntiDebug.checkPort23946()
        AntiDebug.ptraceProtect()

        // 方式3: 调用高级检测
        val isTracerPidNormal = AntiDebug.checkTracerPid()
        val isSignalNormal = AntiDebug.checkSignalHandler()

        // 方式4: 启动后台监控
        AntiDebug.startInotifyMonitor()
    }
}
```

### Native层调用

```c
#include "anti_debug.h"

void protect_function() {
    // 运行所有检测
    RunAllAntiDebugChecks();

    // 或单独调用
    PtraceCheck();
    CheckTracerPid();
    CheckTimingAttack();
}
```

## 编译配置

已在CMakeLists.txt中配置：

```cmake
# 反调试模块
AntiDebug/anti_debug.cpp
AntiDebug/anti_debug_jni.cpp
AntiDebug/anti_debug_advanced.cpp
```

## 日志标签

- 基础检测: `AntiDebug`
- 高级检测: `AntiDebug-Advanced`

## 注意事项

1. ptrace检测只能调用一次，之后会失效
2. Inotify监控会创建后台线程，持续运行
3. 信号检测可能与应用的信号处理冲突
4. 时间差检测在模拟器上可能误报
5. TracerPid检测会fork子进程，有性能开销

## 防护建议

1. 在Application.onCreate()中调用基础检测
2. 在关键函数入口调用快速检测(ptrace, 时间差)
3. 启动后台Inotify监控防止内存dump
4. 定期检查进程状态和TracerPid
5. 对关键函数使用hash检测和断点检测

## 文件结构

```
AntiDebug/
├── anti_debug.h              # 头文件
├── anti_debug.cpp            # 基础实现(1-10)
├── anti_debug_advanced.cpp   # 高级实现(11-15)
├── anti_debug_jni.cpp        # JNI接口
└── README.md                 # 本文档
```
