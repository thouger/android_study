find_library(
        log-lib
        log
)

#添加头文件路径位置
include_directories(
        ./
        ./dlfcn
        ./include
        ./netlink
        ./MapHide
        ./Utils

        #导入nativeLib基础库头文件
        ${HomePath}/nativeLib/src/main/cpp/
        ${HomePath}/nativeLib/src/main/cpp/include/
)

message(STATUS "CMAKE_ANDROID_ARCH_ABI ->   ${CMAKE_ANDROID_ARCH_ABI} ")

if (${CMAKE_ANDROID_ARCH_ABI} STREQUAL "arm64-v8a")
    set(syscall netlink/syscall64.s)
elseif (${CMAKE_ANDROID_ARCH_ABI} STREQUAL "armeabi-v7a")
    set(syscall netlink/syscall32.s)
endif ()

message(STATUS "so name ->   ${SO_NAME} ")
add_library(
        ${SO_NAME}

        SHARED

        #        mynative.cpp
        hunter.cpp
        IORelocator.cpp

        dlfcn/local_dlfcn.c

        netlink/bionic_netlink.cpp
        netlink/ifaddrs.cpp
        #        hook/xhook/xhook.c

        SeccompSVC/library.cpp
        MapHide/hide.cpp
        MapHide/pmparser.c
        MapHide/wrap.cpp

        binder_client.c
        binder.c
        ${syscall}
)

find_package(lsplant REQUIRED CONFIG)
#lsplant
target_link_libraries(
        ${SO_NAME}
        ${log-lib}
        utils
        lsplant::lsplant
        #        adapter
        #        dlfc
        #        runtimehook
        #        libpath
        #        mmkv
        #        mysyscall
)