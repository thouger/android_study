package com.android.study.douyin;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * 网络访问类型检测器
 *
 * 用于检测设备当前的网络类型（WiFi、2G、3G、4G、5G等）
 *
 * 数据来源：
 * - ConnectivityManager：获取当前网络连接信息
 * - TelephonyManager：获取移动网络的详细类型信息
 *
 * 网络类型返回值:
 * - "wifi": WiFi网络
 * - "2g": 2G移动网络 (GSM, CDMA, EDGE等)
 * - "3g": 3G移动网络 (UMTS, CDMA2000, EVDO等)
 * - "4g": 4G/LTE网络
 * - "5g": 5G网络
 * - "none": 无网络
 *
 * 权限要求:
 * - android.permission.ACCESS_NETWORK_STATE
 * - android.permission.READ_PHONE_STATE (for TelephonyManager on Android 10+)
 */
public class NetworkAccessTypeDetector {
    private static final String TAG = "NetworkAccessTypeDetector";

    /**
     * 获取网络访问类型
     *
     * @param context Android上下文
     * @return 网络类型字符串 (wifi, 2g, 3g, 4g, 5g, none)
     */
    public static String getNetworkAccessType(Context context) {
        try {
            if (context == null) {
                return "none";
            }

            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connectivityManager == null) {
                Log.w(TAG, "ConnectivityManager is null");
                return "none";
            }

            // 获取活跃网络信息
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo == null || !networkInfo.isAvailable()) {
                Log.d(TAG, "No active network");
                return "none";
            }

            int networkType = networkInfo.getType();

            // ConnectivityManager.TYPE_WIFI = 1
            if (networkType == ConnectivityManager.TYPE_WIFI) {
                return "wifi";
            }

            // ConnectivityManager.TYPE_MOBILE = 0
            if (networkType == ConnectivityManager.TYPE_MOBILE) {
                return getMobileNetworkType(context);
            }

            // 其他网络类型 (BLUETOOTH, ETHERNET等)
            Log.d(TAG, "Other network type: " + networkType);
            return "none";

        } catch (Exception e) {
            Log.e(TAG, "Failed to get network access type", e);
            return "";
        }
    }

    /**
     * 获取移动网络的具体类型 (2G/3G/4G/5G)
     *
     * TelephonyManager.getNetworkType() 返回值范围: 0-20
     *
     * @param context Android上下文
     * @return 移动网络类型 (2g, 3g, 4g, 5g, none)
     */
    private static String getMobileNetworkType(Context context) {
        try {
            TelephonyManager telephonyManager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            if (telephonyManager == null) {
                Log.w(TAG, "TelephonyManager is null");
                return "none";
            }

            int networkType = telephonyManager.getNetworkType();

            return networkTypeToString(networkType);

        } catch (Exception e) {
            Log.e(TAG, "Failed to get mobile network type", e);
            return "";
        }
    }

    /**
     * 将TelephonyManager的网络类型码转换为字符串
     *
     * @param networkType TelephonyManager的网络类型
     * @return 网络类型字符串
     */
    private static String networkTypeToString(int networkType) {
        switch (networkType) {
            // 2G Networks
            case 1:  // NETWORK_TYPE_GPRS
            case 2:  // NETWORK_TYPE_EDGE
            case 4:  // NETWORK_TYPE_CDMA
            case 7:  // NETWORK_TYPE_1xRTT
            case 11: // NETWORK_TYPE_IDEN
            case 16: // NETWORK_TYPE_AMPS
                return "2g";

            // 3G Networks
            case 3:  // NETWORK_TYPE_UMTS
            case 5:  // NETWORK_TYPE_EVDO_0
            case 6:  // NETWORK_TYPE_EVDO_A
            case 8:  // NETWORK_TYPE_HSDPA
            case 9:  // NETWORK_TYPE_HSUPA
            case 10: // NETWORK_TYPE_HSPA
            case 12: // NETWORK_TYPE_EVDO_B
            case 14: // NETWORK_TYPE_EHRPD
            case 15: // NETWORK_TYPE_HSPAP
            case 17: // NETWORK_TYPE_TD_SCDMA
                return "3g";

            // 4G Networks
            case 13: // NETWORK_TYPE_LTE
            case 18: // NETWORK_TYPE_IWLAN
            case 19: // NETWORK_TYPE_LTE_CA
                return "4g";

            // 5G Networks
            case 20: // NETWORK_TYPE_NR
                return "5g";

            default:
                Log.w(TAG, "Unknown network type: " + networkType);
                return "none";
        }
    }

    /**
     * 检查是否连接到WiFi网络
     */
    public static boolean isWifiConnected(Context context) {
        try {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connectivityManager == null) {
                return false;
            }

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            return networkInfo != null &&
                   networkInfo.isAvailable() &&
                   networkInfo.getType() == ConnectivityManager.TYPE_WIFI;

        } catch (Exception e) {
            Log.e(TAG, "Failed to check WiFi connection", e);
            return false;
        }
    }

    /**
     * 检查是否连接到移动网络
     */
    public static boolean isMobileConnected(Context context) {
        try {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connectivityManager == null) {
                return false;
            }

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            return networkInfo != null &&
                   networkInfo.isAvailable() &&
                   networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;

        } catch (Exception e) {
            Log.e(TAG, "Failed to check mobile connection", e);
            return false;
        }
    }

    /**
     * 检查是否有网络连接
     */
    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connectivityManager == null) {
                return false;
            }

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            return networkInfo != null && networkInfo.isAvailable();

        } catch (Exception e) {
            Log.e(TAG, "Failed to check network availability", e);
            return false;
        }
    }
}
