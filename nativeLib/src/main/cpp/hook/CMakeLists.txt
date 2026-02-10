cmake_minimum_required(VERSION 3.4.1)
#sandhook inlinehook新版本 采用C++ 17编译,优先采用17编译。
SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wno-narrowing -std=c++20")
#添加内联支持
enable_language(C ASM)

#sandhook sign hook
add_subdirectory(signhook)

#add dobby lib
string(REGEX REPLACE "(.*)/nativeLib/src.*" "\\1" HOMEPATH ${PROJECT_SOURCE_DIR})
#设置路径,引入Dobby
include_directories(
        ${DobbyHome}/include
        ${DobbyHome}/source
        ${DobbyHome}/builtin-plugin
        ${DobbyHome}/builtin-plugin/AndroidRestriction
        ${DobbyHome}/builtin-plugin/SymbolResolver
        ${DobbyHome}/external/logging
)
macro(SET_OPTION option value)
    set(${option} ${value} CACHE INTERNAL "" FORCE)
endmacro()
# 关闭DobbyDebug日志
SET_OPTION(DOBBY_DEBUG NO)
# 关闭shared库,和我们的so 集成到一起。
SET_OPTION(DOBBY_GENERATE_SHARED OFF)
#是否加载linker加载so的回调
SET_OPTION(Plugin.LinkerLoadCallback OFF)
add_subdirectory(Dobby)




include_directories(
        ${HomePath}/nativeLib/src/main/cpp/
        ${HomePath}/nativeLib/src/main/cpp/includes/

#        ${HomePath}/Dobby/include/
        ${HomePath}/nativeLib/src/main/cpp/hook/signhook/include/
        ${HomePath}/nativeLib/src/main/cpp/hook/xhook/include/
)

add_library(
        runtimehook

        STATIC

        HookUtils.cpp
        xhook/xhook.c
        xhook/xh_core.c
        xhook/xh_elf.c
        xhook/xh_log.c
        xhook/xh_util.c
        xhook/xh_version.c
        xhook/xh_jni.c

)

add_library(runtimehook_imported SHARED IMPORTED)
set_target_properties(
        runtimehook_imported
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/../libs/${ANDROID_ABI}/libdobby.so
)


target_link_libraries(
        runtimehook
        runtimehook_imported
        dobby #dobby
        signhook #sandhook sign hook
        dlfc
        ${log-lib}
)


find_library(
        log-lib
        log
)





