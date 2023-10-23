cmake_minimum_required(VERSION 3.18.1)
project("thouger")

#添加内联支持
enable_language(C ASM)

SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wno-narrowing -std=c++20")
#开始优化
SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O")
# inline
SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fvisibility-inlines-hidden")


#32位和64位实现在不同文件,方便区分。
if (${CMAKE_ANDROID_ARCH_ABI} STREQUAL "arm64-v8a")
    set(SO_NAME hunter64)
    add_definitions(-DCORE_SO_NAME="libhunter64.so")
elseif (${CMAKE_ANDROID_ARCH_ABI} STREQUAL "armeabi-v7a")
    set(SO_NAME hunter)
    add_definitions(-DCORE_SO_NAME="libhunter64.so")
endif ()

string(REGEX REPLACE "(.*)/app/src.*" "\\1" HomePath ${PROJECT_SOURCE_DIR})
message(STATUS "Building   ${LIB_NAME} for Android(${CMAKE_SYSTEM_PROCESSOR}) ")
message(STATUS "HomePath ->   ${HomePath} PROJECT_SOURCE_DIR -> ${PROJECT_SOURCE_DIR} ")

ADD_SUBDIRECTORY(${PROJECT_SOURCE_DIR}/cpp)
ADD_SUBDIRECTORY(${PROJECT_SOURCE_DIR}/cpp/hello)
ADD_SUBDIRECTORY(${PROJECT_SOURCE_DIR}/cpp/other)
#基础库组件
ADD_SUBDIRECTORY(${HomePath}/nativeLib/src/main/cpp/ nativeLib.out)