cmake_minimum_required(VERSION 3.18.1)

# Frida GUM Example Library
set(LIB_NAME frida_gum_example)

# Set Frida prebuilt directory
set(LIB_FRIDA_DIR ${CMAKE_CURRENT_SOURCE_DIR}/prebuilt/${ANDROID_ABI})

# Add library
add_library(${LIB_NAME} SHARED
    frida-gum-example.cpp
)

# Check if Frida GUM library exists
set(frida-gum-lib ${LIB_FRIDA_DIR}/libs/libfrida-gum.a)
if (NOT EXISTS ${frida-gum-lib})
    message(FATAL_ERROR "frida-gum-lib not found at ${frida-gum-lib}")
endif()

# Include directories
target_include_directories(${LIB_NAME} PRIVATE
    ${LIB_FRIDA_DIR}/include
)

# Link libraries
target_link_libraries(${LIB_NAME}
    ${frida-gum-lib}
    android
    log
)

# Set properties
set_target_properties(${LIB_NAME} PROPERTIES
    CXX_STANDARD 20
    CXX_STANDARD_REQUIRED ON
)