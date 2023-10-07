cmake_minimum_required(VERSION 3.4.1)
project(nativeLib)
ENABLE_LANGUAGE(ASM)

include_directories(
        ${HomePath}/nativeLib/src/main/cpp/
        ${HomePath}/nativeLib/src/main/cpp/includes/
        ./includes/
)
message(STATUS "native printf homepath ->    ${HomePath}  ")

SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wno-narrowing -std=c++20")

add_subdirectory(dlfc)
add_subdirectory(libpath)
add_subdirectory(adapter)
add_subdirectory(mmkv)
add_subdirectory(utils)
add_subdirectory(hook)
#add_subdirectory(raw_syscall)