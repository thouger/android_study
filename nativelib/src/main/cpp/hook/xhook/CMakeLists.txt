cmake_minimum_required(VERSION 3.3)

add_library(
        xhook

        STATIC

        ./
)

target_link_libraries(
        log
        ${log-lib}
)