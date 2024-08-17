LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

# 添加源文件
LOCAL_SRC_FILES := native.c

# 可选：指定依赖库（如果有）
LOCAL_LDLIBS := -llog

# 可选：添加其他需要的配置
# LOCAL_CFLAGS += -DDEBUG

LOCAL_MODULE := mylib

include $(BUILD_SHARED_LIBRARY)
