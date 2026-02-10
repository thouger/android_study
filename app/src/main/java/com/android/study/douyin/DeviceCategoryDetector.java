package com.android.study.douyin;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * 设备分类检测器
 *
 * 用于检测设备的类型（手机、平板、电视等）
 *
 * 检测逻辑基于：
 * - 屏幕尺寸（DPI和像素密度计算）
 * - 设备型号特征（如"fold"、"flip"等）
 *
 * 设备分类返回值:
 * - "phone": 手机
 * - "pad": 平板电脑
 * - "tv": 电视
 * - "car": 车载设备
 * - "vr": VR设备
 * - "ar": AR设备
 * - "watch": 可穿戴设备/手表
 *
 * 数据来源：
 * DisplayMetrics：屏幕尺寸信息
 * Build.DEVICE：设备标识符
 */
public class DeviceCategoryDetector {
    private static final String TAG = "DeviceCategoryDetector";

    // 平板设备屏幕尺寸阈值（英寸）
    private static final float TABLET_SCREEN_SIZE_THRESHOLD = 6.5f;
    private static final float TV_SCREEN_SIZE_THRESHOLD = 10.0f;

    /**
     * 获取设备分类
     *
     * 基于 com.p738ss.android.deviceregister.DeviceCategory 的实现
     *
     * @param context Android上下文
     * @return 设备分类字符串 (phone, pad, tv, car, vr, ar, watch)
     */
    public static String getDeviceCategory(Context context) {
        try {
            // 首先检查是否为特殊设备（电视、可穿戴、VR等）
            String specialCategory = detectSpecialDevice();
            if (specialCategory != null) {
                return specialCategory;
            }

            // 根据屏幕尺寸判断
            return detectBySCreenSize(context);

        } catch (Exception e) {
            Log.e(TAG, "Failed to detect device category", e);
            return "phone"; // 默认返回手机
        }
    }

    /**
     * 检测特殊设备类型
     *
     * @return 设备类型或null
     */
    private static String detectSpecialDevice() {
        try {
            String device = android.os.Build.DEVICE;
            if (device == null) {
                return null;
            }

            String deviceLower = device.toLowerCase();

            // 检测可折叠屏设备
            if (deviceLower.contains("fold") || deviceLower.contains("flip")) {
                return "pad"; // 将折叠屏视为平板
            }

            // 检测电视
            if (deviceLower.contains("tv") || deviceLower.contains("stb")) {
                return "tv";
            }

            // 检测可穿戴设备
            if (deviceLower.contains("watch") || deviceLower.contains("wear")) {
                return "watch";
            }

            // 检测VR设备
            if (deviceLower.contains("vr") || deviceLower.contains("daydream")) {
                return "vr";
            }

            // 检测AR设备
            if (deviceLower.contains("ar") || deviceLower.contains("glass")) {
                return "ar";
            }

            // 检测车载设备
            if (deviceLower.contains("car") || deviceLower.contains("automotive")) {
                return "car";
            }

            return null;

        } catch (Exception e) {
            Log.e(TAG, "Error detecting special device", e);
            return null;
        }
    }

    /**
     * 根据屏幕尺寸检测设备类型
     *
     * 计算逻辑：
     * 1. 获取屏幕的物理宽度和高度（英寸）
     * 2. 通过勾股定理计算对角线长度
     * 3. 根据对角线长度判断设备类型
     *    - < 6.5英寸: 手机
     * - 6.5-10英寸: 平板
     * - >= 10英寸: 电视/大屏设备
     *
     * @param context Android上下文
     * @return 设备类型 (phone, pad, tv)
     */
    private static String detectBySCreenSize(Context context) {
        try {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();

            // 屏幕物理宽度和高度（英寸）
            float widthInches = metrics.widthPixels / metrics.xdpi;
            float heightInches = metrics.heightPixels / metrics.ydpi;

            // 计算对角线长度（英寸）
            float screenSize = (float) Math.sqrt(
                    Math.pow(widthInches, 2) + Math.pow(heightInches, 2)
            );

            Log.d(TAG, "Screen size: " + screenSize + " inches, " +
                    "Width: " + widthInches + " inches, " +
                    "Height: " + heightInches + " inches");

            // 根据屏幕尺寸判断设备类型
            if (screenSize >= TV_SCREEN_SIZE_THRESHOLD) {
                return "tv";
            } else if (screenSize >= TABLET_SCREEN_SIZE_THRESHOLD) {
                return "pad";
            } else {
                return "phone";
            }

        } catch (Exception e) {
            Log.e(TAG, "Error detecting device by screen size", e);
            return "phone"; // 默认返回手机
        }
    }

    /**
     * 检查是否为平板设备
     *
     * @param context Android上下文
     * @return 如果是平板返回true，否则返回false
     */
    public static boolean isTablet(Context context) {
        String category = getDeviceCategory(context);
        return "pad".equals(category);
    }

    /**
     * 检查是否为手机设备
     *
     * @param context Android上下文
     * @return 如果是手机返回true，否则返回false
     */
    public static boolean isPhone(Context context) {
        String category = getDeviceCategory(context);
        return "phone".equals(category);
    }

    /**
     * 检查是否为电视设备
     *
     * @param context Android上下文
     * @return 如果是电视返回true，否则返回false
     */
    public static boolean isTV(Context context) {
        String category = getDeviceCategory(context);
        return "tv".equals(category);
    }

    /**
     * 检查是否为可穿戴设备
     *
     * @param context Android上下文
     * @return 如果是可穿戴设备返回true，否则返回false
     */
    public static boolean isWearable(Context context) {
        String category = getDeviceCategory(context);
        return "watch".equals(category) || "vr".equals(category) || "ar".equals(category);
    }

    /**
     * 获取屏幕对角线长度（英寸）
     *
     * @param context Android上下文
     * @return 屏幕尺寸（英寸）
     */
    public static float getScreenSizeInches(Context context) {
        try {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();

            float widthInches = metrics.widthPixels / metrics.xdpi;
            float heightInches = metrics.heightPixels / metrics.ydpi;

            return (float) Math.sqrt(
                    Math.pow(widthInches, 2) + Math.pow(heightInches, 2)
            );

        } catch (Exception e) {
            Log.e(TAG, "Error calculating screen size", e);
            return 0;
        }
    }

    /**
     * 获取屏幕DPI
     *
     * @param context Android上下文
     * @return 屏幕DPI值
     */
    public static int getScreenDPI(Context context) {
        try {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            return metrics.densityDpi;
        } catch (Exception e) {
            Log.e(TAG, "Error getting screen DPI", e);
            return 0;
        }
    }

    /**
     * 获取屏幕分辨率
     *
     * @param context Android上下文
     * @return 分辨率字符串 (widthxheight)
     */
    public static String getScreenResolution(Context context) {
        try {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            return metrics.widthPixels + "x" + metrics.heightPixels;
        } catch (Exception e) {
            Log.e(TAG, "Error getting screen resolution", e);
            return "";
        }
    }
}
