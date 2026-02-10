cmake_minimum_required(VERSION 3.4.1)
#添加内联支持
enable_language(C ASM)

#native基础类库路径
include_directories(
        ./
        ${HomePath}/nativeLib/src/main/cpp/
        ${HomePath}/nativeLib/src/main/cpp/includes/
        ${HomePath}/Dobby/include/
        ${HomePath}/container-runtime/src/main/cpp/include/
)

#这个库主要是包含一些常用的工具类
add_library(
        utils

        STATIC

        parse.cpp
        fileUtils.cpp
        stringUtils.cpp
        appUtils.cpp

        hookutils/stringHandler.cpp
        hookutils/JnitraceForC.cpp
        hookutils/invokePrintf.cpp
        hookutils/HookClassUtils.cpp
)

target_link_libraries(
        utils

        dlfc
        libpath
        runtimehook
        ${log-lib}
)

find_library(
        log-lib
        log
)






