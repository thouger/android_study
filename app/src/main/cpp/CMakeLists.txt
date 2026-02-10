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
        ./AntiDebug
        ./linker_copy
        ./linker_copy/android-base
        ./linker_copy/dobby

        #导入nativeLib基础库头文件
        ${HomePath}/nativeLib/src/main/cpp/
        ${HomePath}/nativeLib/src/main/cpp/include/
)

#开始优化
SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O")
# inline
SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fvisibility-inlines-hidden")

macro(SET_OPTION option value)
    set(${option} ${value} CACHE INTERNAL "" FORCE)
endmacro()
SET_OPTION(DOBBY_DEBUG ON)
SET_OPTION(DOBBY_GENERATE_SHARED OFF)
SET_OPTION(Plugin.LinkerLoadCallback OFF)
add_subdirectory(${DobbyHome} Dobby)
set(DobbyHome Dobby)

message(STATUS "CMAKE_ANDROID_ARCH_ABI ->   ${CMAKE_ANDROID_ARCH_ABI} ")

if (${CMAKE_ANDROID_ARCH_ABI} STREQUAL "arm64-v8a")
    set(syscall netlink/syscall64.s)
elseif (${CMAKE_ANDROID_ARCH_ABI} STREQUAL "armeabi-v7a")
    set(syscall netlink/syscall32.s)
endif ()

#set_source_files_properties(
#        linker_copy/mylinker.cpp
#        linker_copy/linker.cpp
#
#        Utils/ProcessRuntime.cpp
#        linker_copy/private/bionic_elf_tls.cpp
#        hunter.cpp
#        IORelocator.cpp
#        dlfcn/local_dlfcn.c
#        netlink/bionic_netlink.cpp
#        netlink/ifaddrs.cpp
#        SeccompSVC/library.cpp
#        MapHide/hide.cpp
#        MapHide/pmparser.c
#        MapHide/wrap.cpp
#        binder_client.c
#        binder.
#        linker_soinfo.cpp
#        PROPERTIES COMPILE_FLAGS "-Wno-macro-redefined"
#)


message(STATUS "so name ->   ${SO_NAME} ")
add_library(
        ${SO_NAME}

        SHARED

        # 自定义linker
        linker/linker.cpp
        linker/linker_soinfo.cpp
        linker/linker_common_types.h
        linker/linker_mapped_file_fragment.cpp
        linker/linker_phdr.cpp
        linker/linker_note_gnu_property.cpp
        linker/linker_utils.cpp
        linker/linker_block_allocator.cpp
        linker/linker_main.cpp
        linker/linker_namespaces.cpp
        linker/linker_tls.cpp
        linker/linker_config.cpp
        linker/linker_transparent_hugepage_support.cpp
        linker/linker_relocate.cpp
        linker/linker_gun_hash.cpp
        linker/fake_mmap.cpp
        linker/private/bionic_elf_tls.cpp
        Utils/FileUtils.cpp
        Utils/ProcessRuntime.cpp
#        Utils/ProcessRuntime.cpp
#        linker_copy/mylinker.cpp
#        linker_copy/linker.cpp
#        linker_copy/private/bionic_elf_tls.cpp
#        linker_copy/linker_soinfo.cpp
#        linker_copy/linker_config.cpp
#        linker_copy/linker_gnu_hash.cpp
#        linker_copy/linker_relocate.cpp
#        linker_copy/linker_utils.cpp
#        linker_copy/linker_block_allocator.cpp
#        linker_copy/linker_mapped_file_fragment.cpp
#        linker_copy/linker_phdr.cpp
#        linker_copy/linker_namespaces.cpp
#        linker_copy/linker_main.cpp
#        linker_copy/dobby/bionic_linker_restriction.cc
#        linker_copy/dobby/dobby_symbol_resolver.cc
#        linker_copy/dobby/ProcesssRuntimeUtility.cc

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

        # 反调试模块
        AntiDebug/anti_debug.cpp
        AntiDebug/anti_debug_jni.cpp
        AntiDebug/anti_debug_advanced.cpp

        binder_client.c
        binder.c
        ${syscall}


)

find_package(lsplant REQUIRED CONFIG)
#lsplant
target_link_libraries(
        ${SO_NAME}
        ${log-lib}
        lsplant::lsplant
        aaa
#        utils
        #        adapter
        #        dlfc
        #        runtimehook
        #        libpath
        #        mmkv
        #        mysyscall
)