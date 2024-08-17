cmake_minimum_required(VERSION 3.18.1)
enable_language(C ASM)
# Declares and names the project.

project("hook")

set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI})

ADD_SUBDIRECTORY(${PROJECT_SOURCE_DIR}/cpp/)
ADD_SUBDIRECTORY(${PROJECT_SOURCE_DIR}/cpp/hello)
ADD_SUBDIRECTORY(${PROJECT_SOURCE_DIR}/cpp/other)
ADD_SUBDIRECTORY(${PROJECT_SOURCE_DIR}/cpp/native_hook)
#ADD_SUBDIRECTORY(${HomePath}/nativeLib/src/main/cpp/ nativeLib.out)