#继承上一层的CMakeLists.txt的变量，也可以在这里重新赋值
#C 的编译选项是 CMAKE_C_FLAGS
# 指定编译参数，可选
#SET(CMAKE_CXX_FLAGS "-Wno-error=format-security -Wno-error=pointer-sign")

#生成动态库名称、类型、资源文件
add_library(hello SHARED hello.cpp)
#依赖库
find_library(log-lib log)
#
target_link_libraries(hello ${log-lib})