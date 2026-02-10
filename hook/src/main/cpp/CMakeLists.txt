# CMake最低版本要求
cmake_minimum_required(VERSION 3.18.1)

# 项目名称
project("native_hook")

# 设置C++标准
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# 添加编译选项
add_compile_options(-Wall -Wextra)

# 如果是Release模式，添加优化选项
if(CMAKE_BUILD_TYPE STREQUAL "Release")
    add_compile_options(-O2 -fvisibility=hidden)
endif()

# 创建共享库
add_library(
        # 库名称（会生成 libnative_hook.so）
        native_hook

        # 共享库类型
        SHARED

        # 源文件列表
        ssl_bypass.cpp  # 基于官方文档的标准实现
)

# 查找并链接Android Log库
find_library(
        # 设置变量名
        log-lib

        # 要链接的库名称
        log
)

# 链接库
target_link_libraries(
        native_hook

        # Android Log库
        ${log-lib}

        # dl库（用于dlopen, dlsym等动态链接函数）
        dl
)

# 设置输出目录（可选）
# set_target_properties(native_hook PROPERTIES
#         LIBRARY_OUTPUT_DIRECTORY ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}
# )

# 打印一些调试信息
message(STATUS "========================================")
message(STATUS "Building native_hook library")
message(STATUS "CMake Version: ${CMAKE_VERSION}")
message(STATUS "Build Type: ${CMAKE_BUILD_TYPE}")
message(STATUS "C++ Standard: ${CMAKE_CXX_STANDARD}")
message(STATUS "Android ABI: ${ANDROID_ABI}")
message(STATUS "Android Platform: ${ANDROID_PLATFORM}")
message(STATUS "========================================")
