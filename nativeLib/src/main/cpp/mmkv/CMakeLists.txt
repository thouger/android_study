cmake_minimum_required(VERSION 3.4.1)

set(can_use_assembler TRUE)
enable_language(ASM)
IF("${ANDROID_ABI}" STREQUAL "arm64-v8a")
    SET(ASM_OPTIONS "-x assembler-with-cpp")
    SET(CMAKE_ASM_FLAGS "${CFLAGS} ${ASM_OPTIONS} -march=armv8+crypto -D__ANDROID__")
ELSEIF("${ANDROID_ABI}" STREQUAL "armeabi-v7a")
    SET(ASM_OPTIONS "-x assembler-with-cpp")
    SET(CMAKE_ASM_FLAGS "${CFLAGS} ${ASM_OPTIONS} -march=armv7a -D__ANDROID__")
ELSEIF("${ANDROID_ABI}" STREQUAL "armeabi")
    SET(ASM_OPTIONS "-x assembler-with-cpp")
    SET(CMAKE_ASM_FLAGS "${CFLAGS} ${ASM_OPTIONS} -march=armv5 -D__ANDROID__")
ENDIF()

#include(CMakePrintHelpers)
#cmake_print_variables(CMAKE_SYSTEM_PROCESSOR)
IF(UNIX AND (NOT APPLE))
    IF("${CMAKE_SYSTEM_PROCESSOR}" STREQUAL "aarch64")
        SET(ASM_OPTIONS "-x assembler-with-cpp")
        SET(CMAKE_ASM_FLAGS "${CFLAGS} ${ASM_OPTIONS} -march=armv8-a+crypto")
    ENDIF()
ENDIF()




# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library(
        mmkv

        # Sets the library as a shared library.
        STATIC

        # Provides a relative path to your source file(s).
        ../includes/MMKV.h
        MMKV.cpp
        MMKV_Android.cpp
        MMKV_IO.h
        MMKV_IO.cpp
        MMKV_OSX.cpp
        MMKVLog.h
        MMKVLog.cpp
        MMKVLog_Android.cpp
        CodedInputData.h
        CodedInputData.cpp
        CodedInputData_OSX.cpp
        CodedInputDataCrypt.h
        CodedInputDataCrypt.cpp
        CodedInputDataCrypt_OSX.cpp
        CodedOutputData.h
        CodedOutputData.cpp
        KeyValueHolder.h
        KeyValueHolder.cpp
        PBUtility.h
        PBUtility.cpp
        MiniPBCoder.h
        MiniPBCoder.cpp
        MiniPBCoder_OSX.cpp
        MMBuffer.h
        MMBuffer.cpp
        InterProcessLock.h
        InterProcessLock.cpp
        InterProcessLock_Win32.cpp
        InterProcessLock_Android.cpp
        MemoryFile.h
        MemoryFile.cpp
        MemoryFile_Android.cpp
        MemoryFile_Linux.cpp
        MemoryFile_Win32.cpp
        MemoryFile_OSX.cpp
        ThreadLock.h
        ThreadLock.cpp
        ThreadLock_Win32.cpp
        MMKVMetaInfo.hpp
        aes/AESCrypt.h
        aes/AESCrypt.cpp
        aes/openssl/openssl_aes.h
        aes/openssl/openssl_aes_core.cpp
        aes/openssl/openssl_aes_locl.h
        aes/openssl/openssl_cfb128.cpp
        aes/openssl/openssl_opensslconf.h
        aes/openssl/openssl_md5_dgst.cpp
        aes/openssl/openssl_md5_locl.h
        aes/openssl/openssl_md5_one.cpp
        aes/openssl/openssl_md5.h
        aes/openssl/openssl_md32_common.h
        aes/openssl/openssl_aesv8-armx.S
        aes/openssl/openssl_aes-armv4.S
        aes/openssl/openssl_arm_arch.h
        crc32/Checksum.h
        crc32/crc32_armv8.cpp
        crc32/zlib/zconf.h
        crc32/zlib/zutil.h
        crc32/zlib/crc32.h
        crc32/zlib/crc32.cpp
        MMKVPredef.h
        )

target_include_directories(mmkv PUBLIC ${CMAKE_CURRENT_SOURCE_DIR})

##SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wno-narrowing -std=c++20")

set_target_properties(
        mmkv PROPERTIES
        CXX_EXTENSIONS OFF
        POSITION_INDEPENDENT_CODE ON
        )



target_compile_definitions(mmkv PUBLIC MMKV_EMBED_ZLIB=1)



