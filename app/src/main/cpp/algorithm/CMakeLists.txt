cmake_minimum_required(VERSION 3.18.1)

# 设置Debug模式，生成调试符号
set(CMAKE_BUILD_TYPE "Debug")

# 添加调试标志
set(CMAKE_CXX_FLAGS_DEBUG "-g -O0")
set(CMAKE_C_FLAGS_DEBUG "-g -O0")

# 禁用 PIE (Position Independent Executable) 以固定地址，方便调试
# 对于 Android，使用 -Wl,--no-pic-executable 和 -fno-pic
# 注意：这只在 Debug 构建中使用，Release 构建应该保持 PIE 启用以保证安全性
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -fno-pic -no-pie")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fno-pic -no-pie")
set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} -Wl,-no-pie")

add_library(algorithm SHARED
        algorithm.cpp
        )

find_library(log-lib log)

target_link_libraries(algorithm
        ${log-lib}
        frida_gum_example
        )