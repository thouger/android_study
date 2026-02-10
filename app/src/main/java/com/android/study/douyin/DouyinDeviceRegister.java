package com.android.study.douyin;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

public class DouyinDeviceRegister {
    private static final String TAG = "DouyinDeviceRegister";

    // 设备注册有些指纹，是从C551290EOt、AFLambdaS419S0000000_48这里设置的
    public static void collect(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            String packageName = context.getPackageName();
            ApplicationInfo appInfo = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);

            // Read APK signature from /data/local/tmp/base.apk
            String apkPath = "/data/local/tmp/base.apk";
            String result = LIZIZ(apkPath);
            Log.d(TAG, "APK Signature result: " + result);

            // Get display metrics (needed for multiple fields)
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

            // Combine into final JSON
            try {
                JSONObject finalJson = new JSONObject();

                // Extract display_name
                String displayName = "";
                int labelRes = appInfo.labelRes;
                if (labelRes > 0) {
                    displayName = context.getString(labelRes);
                }

                // Extract meta_channel from signature result
                String metaChannel = "";
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject signatureJson = new JSONObject(result);
                        if (signatureJson.has("meta_channel")) {
                            metaChannel = signatureJson.getString("meta_channel");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to parse signature result", e);
                    }
                }

                // Calculate density_dpi and display_density
                int densityDpi = displayMetrics.densityDpi;
                String displayDensity = "mdpi";
                if (densityDpi == 120) {
                    displayDensity = "ldpi";
                } else if (densityDpi == 240) {
                    displayDensity = "hdpi";
                } else if (densityDpi == 320) {
                    displayDensity = "xhdpi";
                }

                // Calculate timezone
                float timezone = (TimeZone.getDefault().getRawOffset() * 1.0f) / 3600000.0f;
                if (timezone < -12.0f) timezone = -12.0f;
                if (timezone > 12.0f) timezone = 12.0f;

                // Get signature hash (MD5)
                String sigHash = "";
                if (packageInfo.signatures != null && packageInfo.signatures.length > 0) {
                    Signature signature = packageInfo.signatures[0];
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    md.update(signature.toByteArray());
                    sigHash = new BigInteger(1, md.digest()).toString(16);
                }

                // Get update_version_code from AndroidManifest meta-data
                int updateVersionCode = 0;
                Bundle metaData = appInfo.metaData;
                if (metaData != null && metaData.containsKey("UPDATE_VERSION_CODE")) {
                    updateVersionCode = metaData.getInt("UPDATE_VERSION_CODE", 0);
                }

                // Get manifest_version_code (versionCode from PackageInfo)
                // Source: AppContextManager.getVersionCode() -> versionInfo.LIZ -> PackageInfo.versionCode
                int manifestVersionCode = packageInfo.versionCode;

                // Get app_version_minor (application custom version string)
                // Source: DeviceRegisterManager.getAppVersionMinor() -> sAppVersionMinor (set by app, default "")
                // This is NOT a system value, but managed by the application itself
                // 没有调用，因此为空
                String appVersionMinor = "";  // Default empty, set by DeviceRegisterManager.setAppVersionMinor()

                // 100% confirmed fields
                finalJson.put("display_name", displayName);
                finalJson.put("channel", metaChannel);
                finalJson.put("package", packageName);
                finalJson.put("version_code", packageInfo.versionCode);
                finalJson.put("os", "Android");
                finalJson.put("os_version", Build.VERSION.RELEASE);
                finalJson.put("os_api", Build.VERSION.SDK_INT);
                finalJson.put("device_model", Build.MODEL);
                finalJson.put("device_brand", Build.BRAND);
                finalJson.put("device_manufacturer", Build.MANUFACTURER);
                finalJson.put("cpu_abi", Build.CPU_ABI);
                finalJson.put("density_dpi", densityDpi);
                finalJson.put("display_density", displayDensity);
                finalJson.put("resolution", displayMetrics.heightPixels + "x" + displayMetrics.widthPixels);
                finalJson.put("language", context.getResources().getConfiguration().locale.getLanguage());
                finalJson.put("timezone", timezone);
                finalJson.put("region", Locale.getDefault().getCountry());
                finalJson.put("tz_name", Calendar.getInstance().getTimeZone().getID());
                finalJson.put("tz_offset", Calendar.getInstance().getTimeZone().getOffset(System.currentTimeMillis()) / 1000);
                finalJson.put("sig_hash", sigHash);

                // Uncertain fields - fill with empty or default values
                finalJson.put("update_version_code", updateVersionCode);
                finalJson.put("manifest_version_code", manifestVersionCode);
                finalJson.put("app_version_minor", appVersionMinor);

                // AppContextManager.java
                //  public final int getAppId() { return appId; }
                // appId = builder.LJIIIIZZ;
                // kotlin/jvm/internal/AFLambdaS419S0000000_48.java
                // build.LJIIIIZZ = 1128;
                finalJson.put("aid", 1128);
                finalJson.put("app_version", packageInfo.versionName);
//                C551290EOt.java
                finalJson.put("sdk_version", "3.7.3-rc.93-douyin");
                // 固定（？tudo）
                finalJson.put("sdk_target_version", 29);
                finalJson.put("git_hash", "b4ef33f");
                // DeviceCategory - 使用抖音完整判断逻辑
                finalJson.put("device_category", DeviceCategoryDetector.getDeviceCategory(context));
                finalJson.put("release_build", "");
                // Network access type - 网络访问类型（wifi/2g/3g/4g/5g等）
                finalJson.put("access", NetworkAccessTypeDetector.getNetworkAccessType(context));
                // 固定
                finalJson.put("not_request_sender", 0);
                finalJson.put("rom", getRom());
                finalJson.put("rom_version", getRomVersion());
                // cdid: 随机生成的 UUID，用于设备标识
                // 来源: C550180EKm.LIZ() - UUID.randomUUID().toString()
                finalJson.put("cdid", UUID.randomUUID().toString());
                // openudid: 基于 Android ID 或随机生成的设备标识符
                // 来源: AbstractC551110EOb.LJFF() - Settings.Secure.getString(android_id) 或 BigInteger(80位随机数)
                finalJson.put("openudid", getOpenUdid(context));
                // clientudid: 随机生成的 UUID，用于标识客户端
                // 来源: C0EOD.LIZIZ() - UUID.randomUUID().toString()
                finalJson.put("clientudid", UUID.randomUUID().toString());
                finalJson.put("sim_serial_number", new org.json.JSONArray());
                // OAID (Open Advertising ID) - 高通广告ID
                // 来源: com.p738ss.android.deviceregister.base.Oaid.getOaid(long timeout)
                // 通过高通MSA库查询，包含广告ID及相关元数据
                JSONObject oaidResult = getOaidInfo(context);
                if (oaidResult != null) {
                    finalJson.put("oaid", oaidResult);
                    String oaidMaySupport = oaidResult.optString("id", "");
                    finalJson.put("oaid_may_support", !TextUtils.isEmpty(oaidMaySupport));
                } else {
                    finalJson.put("oaid_may_support", false);
                }
                finalJson.put("req_id", "");
                // 固定
                finalJson.put("device_platform", "android");
                // apk_first_install_time: APK 首次安装时间
                // 来源: AbstractC551110EOb.LIZ() line 283-296 -> LIZIZ(packageInfo) line 71-91
                // 通过 PackageInfo.firstInstallTime 获取
                long firstInstallTime = 0;
                try {
                    firstInstallTime = packageInfo.firstInstallTime;
                } catch (Exception e) {
                    Log.e(TAG, "Failed to get firstInstallTime", e);
                }
                finalJson.put("apk_first_install_time", firstInstallTime);

                // is_system_app: 是否为系统应用（0=否，1=是）
                // 来源: AbstractC551110EOb.LIZ() line 283-296
                // 通过 (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM 判断
                int isSystemApp = 0;
                if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                    isSystemApp = 1;
                }
                finalJson.put("is_system_app", isSystemApp);
                // 固定 C551290EOt.java
                finalJson.put("sdk_flavor", "china");
                // DeviceRegisterManager.java 的public static void setTouristMode(boolean z)
                finalJson.put("guest_mode", 0);

                Log.i(TAG, "Device fingerprint: " + finalJson.toString());
            } catch (Exception e) {
                Log.e(TAG, "Failed to create final JSON", e);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String LIZIZ(String str) {
        RandomAccessFile randomAccessFile;
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            randomAccessFile = new RandomAccessFile(str, "r");
            try {
                Map<Integer, ByteBuffer> mapLIZJ = LIZJ(LIZ(randomAccessFile.getChannel()).LIZ);
                if (mapLIZJ.containsKey(1903654775)) {
                    ByteBuffer byteBuffer = (ByteBuffer) ((LinkedHashMap) mapLIZJ).get(1903654775);
                    byte[] bArrArray = byteBuffer.array();
                    int iArrayOffset = byteBuffer.arrayOffset();
                    String str2 = new String(Arrays.copyOfRange(bArrArray, byteBuffer.position() + iArrayOffset, iArrayOffset + byteBuffer.limit()));
                    try {
                        randomAccessFile.close();
                        return str2;
                    } catch (Throwable th) {
                        return str2;
                    }
                }
            } catch (Throwable th2) {
                Log.d(TAG,"111111111111");
            }
            randomAccessFile = null;
            randomAccessFile.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static C1851880tPy<ByteBuffer, Long> LIZ(FileChannel fileChannel) throws Exception {
        long size = fileChannel.size();
        if (size >= 22) {
            long j = size - 22;
            long jMin = Math.min(j, 65535);
            int i = 0;
            while (true) {
                long j2 = i;
                if (j2 <= jMin) {
                    long j3 = j - j2;
                    ByteBuffer byteBufferAllocate = ByteBuffer.allocate(4);
                    fileChannel.position(j3);
                    fileChannel.read(byteBufferAllocate);
                    ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
                    byteBufferAllocate.order(byteOrder);
                    if (byteBufferAllocate.getInt(0) == 101010256) {
                        ByteBuffer byteBufferAllocate2 = ByteBuffer.allocate(2);
                        fileChannel.position(j3 + 20);
                        fileChannel.read(byteBufferAllocate2);
                        byteBufferAllocate2.order(byteOrder);
                        short s = byteBufferAllocate2.getShort(0);
                        if (s == i) {
                            ByteBuffer byteBufferAllocate3 = ByteBuffer.allocate(4);
                            byteBufferAllocate3.order(byteOrder);
                            fileChannel.position((fileChannel.size() - s) - 6);
                            fileChannel.read(byteBufferAllocate3);
                            long j4 = byteBufferAllocate3.getInt(0);
                            if (j4 >= 32) {
                                fileChannel.position(j4 - 24);
                                ByteBuffer byteBufferAllocate4 = ByteBuffer.allocate(24);
                                fileChannel.read(byteBufferAllocate4);
                                byteBufferAllocate4.order(byteOrder);
                                if (byteBufferAllocate4.getLong(8) == 2334950737559900225L && byteBufferAllocate4.getLong(16) == 3617552046287187010L) {
                                    long j5 = byteBufferAllocate4.getLong(0);
                                    if (j5 >= byteBufferAllocate4.capacity() && j5 <= 2147483639) {
                                        int i2 = (int) (8 + j5);
                                        long j6 = j4 - i2;
                                        if (j6 >= 0) {
                                            fileChannel.position(j6);
                                            ByteBuffer byteBufferAllocate5 = ByteBuffer.allocate(i2);
                                            fileChannel.read(byteBufferAllocate5);
                                            byteBufferAllocate5.order(byteOrder);
                                            long j7 = byteBufferAllocate5.getLong(0);
                                            if (j7 == j5) {
                                                return new C1851880tPy<>(byteBufferAllocate5, Long.valueOf(j6));
                                            }
                                            StringBuilder sb = new StringBuilder();
                                            sb.append("APK Signing Block sizes in header and footer do not match: ");
                                            sb.append(j7);
                                            sb.append(" vs ");
                                            sb.append(j5);
                                            throw new Exception(sb.toString());
                                        }
                                        StringBuilder sb2 = new StringBuilder();
                                        sb2.append("APK Signing Block offset out of range: ");
                                        sb2.append(j6);
                                        throw new Exception(sb2.toString());
                                    }
                                    StringBuilder sb3 = new StringBuilder();
                                    sb3.append("APK Signing Block size out of range: ");
                                    sb3.append(j5);
                                    throw new Exception(sb3.toString());
                                }
                                throw new Exception("No APK Signing Block before ZIP Central Directory");
                            }
                            StringBuilder sb4 = new StringBuilder();
                            sb4.append("APK too small for APK Signing Block. ZIP Central Directory offset: ");
                            sb4.append(j4);
                            throw new Exception(sb4.toString());
                        }
                    }
                    i++;
                } else {
                    throw new IOException("ZIP End of Central Directory (EOCD) record not found");
                }
            }
        } else {
            throw new IOException("APK too small for ZIP End of Central Directory (EOCD) record");
        }
    }

    public static Map<Integer, ByteBuffer> LIZJ(ByteBuffer byteBuffer) throws Exception {
        if (byteBuffer.order() == ByteOrder.LITTLE_ENDIAN) {
            int iCapacity = byteBuffer.capacity() - 24;
            if (iCapacity >= 8) {
                int iCapacity2 = byteBuffer.capacity();
                if (iCapacity <= byteBuffer.capacity()) {
                    int iLimit = byteBuffer.limit();
                    int iPosition = byteBuffer.position();
                    int i = 0;
                    try {
                        byteBuffer.position(0);
                        byteBuffer.limit(iCapacity);
                        byteBuffer.position(8);
                        ByteBuffer byteBufferSlice = byteBuffer.slice();
                        byteBufferSlice.order(byteBuffer.order());
                        byteBuffer.position(0);
                        byteBuffer.limit(iLimit);
                        byteBuffer.position(iPosition);
                        LinkedHashMap linkedHashMap = new LinkedHashMap();
                        while (byteBufferSlice.hasRemaining()) {
                            i++;
                            if (byteBufferSlice.remaining() >= 8) {
                                long j = byteBufferSlice.getLong();
                                if (j >= 4 && j <= 2147483647L) {
                                    int i2 = (int) j;
                                    int iPosition2 = byteBufferSlice.position() + i2;
                                    if (i2 <= byteBufferSlice.remaining()) {
                                        Integer numValueOf = Integer.valueOf(byteBufferSlice.getInt());
                                        int i3 = i2 - 4;
                                        if (i3 >= 0) {
                                            int iLimit2 = byteBufferSlice.limit();
                                            int iPosition3 = byteBufferSlice.position();
                                            int i4 = i3 + iPosition3;
                                            if (i4 >= iPosition3 && i4 <= iLimit2) {
                                                byteBufferSlice.limit(i4);
                                                try {
                                                    ByteBuffer byteBufferSlice2 = byteBufferSlice.slice();
                                                    byteBufferSlice2.order(byteBufferSlice.order());
                                                    byteBufferSlice.position(i4);
                                                    byteBufferSlice.limit(iLimit2);
                                                    linkedHashMap.put(numValueOf, byteBufferSlice2);
                                                    byteBufferSlice.position(iPosition2);
                                                } catch (Throwable th) {
                                                    byteBufferSlice.limit(iLimit2);
                                                    throw th;
                                                }
                                            } else {
                                                throw new BufferUnderflowException();
                                            }
                                        } else {
                                            StringBuilder sb = new StringBuilder();
                                            sb.append("size: ");
                                            sb.append(i3);
                                            throw new IllegalArgumentException(sb.toString());
                                        }
                                    } else {
                                        StringBuilder sb2 = new StringBuilder();
                                        sb2.append("APK Signing Block entry #");
                                        sb2.append(i);
                                        sb2.append(" size out of range: ");
                                        sb2.append(i2);
                                        sb2.append(", available: ");
                                        sb2.append(byteBufferSlice.remaining());
                                        throw new Exception(sb2.toString());
                                    }
                                } else {
                                    StringBuilder sb3 = new StringBuilder();
                                    sb3.append("APK Signing Block entry #");
                                    sb3.append(i);
                                    sb3.append(" size out of range: ");
                                    sb3.append(j);
                                    throw new Exception(sb3.toString());
                                }
                            } else {
                                StringBuilder sb4 = new StringBuilder();
                                sb4.append("Insufficient data to read size of APK Signing Block entry #");
                                sb4.append(i);
                                throw new Exception(sb4.toString());
                            }
                        }
                        return linkedHashMap;
                    } catch (Throwable th2) {
                        byteBuffer.position(0);
                        byteBuffer.limit(iLimit);
                        byteBuffer.position(iPosition);
                        throw th2;
                    }
                }
                StringBuilder sb5 = new StringBuilder();
                sb5.append("end > capacity: ");
                sb5.append(iCapacity);
                sb5.append(" > ");
                sb5.append(iCapacity2);
                throw new IllegalArgumentException(sb5.toString());
            }
            StringBuilder sb6 = new StringBuilder();
            sb6.append("end < start: ");
            sb6.append(iCapacity);
            sb6.append(" < ");
            sb6.append(8);
            throw new IllegalArgumentException(sb6.toString());
        }
        throw new IllegalArgumentException("ByteBuffer byte order must be little endian");
    }

    /**
     * Get system property using getprop command
     * Based on C0E7O.LIZLLL() implementation
     */
    private static String getSystemProperty(String key) {
        String line = "";
        java.io.BufferedReader reader = null;
        try {
            Process process = Runtime.getRuntime().exec("getprop " + key);
            reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(process.getInputStream()), 1024);
            line = reader.readLine();
            process.destroy();
        } catch (Exception e) {
            Log.e(TAG, "Failed to get system property: " + key, e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    Log.e(TAG, "Failed to close reader", e);
                }
            }
        }
        return line != null ? line : "";
    }

    /**
     * Get ROM info string
     * Based on C551250EOp line 386-410 logic
     */
    private static String getRom() {
        try {
            StringBuilder sb = new StringBuilder();

            // Check if it's MIUI
            try {
                Class.forName("miui.os.Build");
                sb.append("MIUI-");
            } catch (ClassNotFoundException e) {
                // Not MIUI, check Flyme
                String display = Build.DISPLAY;
                if (display != null && display.toLowerCase().contains("flyme")) {
                    sb.append("FLYME-");
                } else {
                    // Check EMUI
                    String emuiVersion = getSystemProperty("ro.build.version.emui");
                    if (!TextUtils.isEmpty(emuiVersion) &&
                        (emuiVersion.toLowerCase().contains("emotionui") ||
                         emuiVersion.toLowerCase().contains("magicui"))) {
                        sb.append("EMUI-");
                    }
                    if (!TextUtils.isEmpty(emuiVersion)) {
                        sb.append(emuiVersion);
                        sb.append("_");
                    }
                }
            }

            // Append Build.VERSION.INCREMENTAL
            sb.append(Build.VERSION.INCREMENTAL);

            return sb.length() > 0 ? sb.toString() : "";
        } catch (Exception e) {
            Log.e(TAG, "Failed to get ROM info", e);
            return Build.VERSION.INCREMENTAL != null ? Build.VERSION.INCREMENTAL : "";
        }
    }

    /**
     * Get ROM version based on device manufacturer
     * Based on C0E7O.LIZ() implementation
     */
    private static String getRomVersion() {
        try {
            String manufacturer = Build.MANUFACTURER.toLowerCase();
            String brand = Build.BRAND.toLowerCase();
            String display = Build.DISPLAY;

            // Check if it's MIUI (Xiaomi)
            try {
                Class.forName("miui.os.Build");
                String miuiVersion = getSystemProperty("ro.miui.ui.version.name");
                return "miui_" + miuiVersion + "_" + Build.VERSION.INCREMENTAL;
            } catch (ClassNotFoundException e) {
                // Not MIUI
            }

            // Check if it's Flyme (Meizu)
            if (display != null && display.toLowerCase().contains("flyme")) {
                return display;
            }

            // Check if it's ColorOS (OPPO/Realme)
            if ("oppo".equals(manufacturer) || "oppo".equals(brand) || "realme".equals(brand)) {
                String colorOsVersion = getSystemProperty("ro.build.version.opporom");
                return "coloros_" + colorOsVersion + "_" + display;
            }

            // Check if it's EMUI/HarmonyOS (Huawei/Honor)
            String emuiVersion = getSystemProperty("ro.build.version.emui");
            if (!TextUtils.isEmpty(emuiVersion)) {
                if (emuiVersion.toLowerCase().contains("emotionui") ||
                    emuiVersion.toLowerCase().contains("magicui")) {
                    return emuiVersion + "_" + display;
                }
            }

            // Check if it's Vivo
            String vivoDisplayId = getSystemProperty("ro.vivo.os.build.display.id");
            if (!TextUtils.isEmpty(vivoDisplayId)) {
                String vivoVersion = getSystemProperty("ro.vivo.product.version");
                return vivoDisplayId + "_" + vivoVersion;
            }

            // Check if it's Amigo (Gionee)
            if (display != null && display.toLowerCase().contains("amigo")) {
                String gnVersion = getSystemProperty("ro.gn.sv.version");
                return display + "_" + gnVersion;
            }

            // Check if it's 360/Qiku
            String brandManufacturer = (manufacturer + brand).toLowerCase();
            if (brandManufacturer.contains("360") || brandManufacturer.contains("qiku")) {
                String uiVersion = getSystemProperty("ro.build.uiversion");
                return uiVersion + "_" + display;
            }

            // Check if it's LeEco
            String letvVersion = getSystemProperty("ro.letv.release.version");
            if (!TextUtils.isEmpty(letvVersion)) {
                return "eui_" + letvVersion + "_" + display;
            }

            // Default: return Build.DISPLAY
            return display != null ? display : "";

        } catch (Exception e) {
            Log.e(TAG, "Failed to get ROM version", e);
            return Build.DISPLAY != null ? Build.DISPLAY : "";
        }
    }

    /**
     * Get Open UDID - based on Android ID or random generated value
     * Based on AbstractC551110EOb.LJFF() line 217-272
     */
    private static String getOpenUdid(Context context) {
        try {
            // 1. Try to get Android ID
            String androidId = Settings.Secure.getString(
                context.getContentResolver(), "android_id");

            if (androidId != null) {
                // 2. Check if Android ID is valid
                // Invalid if it's the default value or length < 13
                if (androidId.equals("9774d56d682e549c") || androidId.length() < 13) {
                    // Generate random 80-bit hex number
                    String openudid = new BigInteger(80, new SecureRandom()).toString(16);

                    // Remove negative sign if present
                    if (openudid.charAt(0) == '-') {
                        openudid = openudid.substring(1);
                    }

                    // Pad with 'F' to ensure at least 13 characters
                    int paddingLength = 13 - openudid.length();
                    if (paddingLength > 0) {
                        StringBuilder sb = new StringBuilder();
                        while (paddingLength > 0) {
                            sb.append('F');
                            paddingLength--;
                        }
                        sb.append(openudid);
                        openudid = sb.toString();
                    }

                    return openudid;
                }
                // Valid Android ID, return as-is
                return androidId;
            }

            return "";
        } catch (Exception e) {
            Log.e(TAG, "Failed to get openudid", e);
            return "";
        }
    }

    /**
     * Get OAID (Open Advertising ID) information
     *
     * OAID components (from C551940ERg and Oaid.java):
     * - id: 高通OAID广告ID标识符
     * - req_id: OAID查询请求的唯一ID标识
     * - is_track_limited: 设备是否限制跟踪的布尔值
     * - take_ms: 获取OAID花费的毫秒时间
     * - time: OAID查询时刻的系统时间戳
     * - query_times: 累计OAID查询的次数计数
     * - hw_id_version_code: 高通硬件ID的版本代码
     *
     * Based on: Oaid.java getOaid(long timeout)
     *           -> C551970ERj LIZIZ()
     *           -> C0ETS.LIZ(context) (高通MSA API)
     */
    private static JSONObject getOaidInfo(Context context) {
        try {
            JSONObject oaidJson = new JSONObject();

            // 模拟OAID查询 - 实际应用中需要引入高通MSA库
            // 通过 Oaid.instance(context).getOaid(timeout_ms) 获取

            // 示例值 (实际应用中从高通MSA库动态获取):
            oaidJson.put("id", "");                          // 广告ID
            oaidJson.put("req_id", UUID.randomUUID().toString()); // 请求ID
            oaidJson.put("hw_id_version_code", "0");         // 硬件版本
            oaidJson.put("take_ms", "0");                    // 查询耗时(毫秒)
            oaidJson.put("is_track_limited", "false");       // 限制跟踪
            oaidJson.put("query_times", "1");                // 查询次数
            oaidJson.put("time", String.valueOf(System.currentTimeMillis())); // 时间戳

            return oaidJson;
        } catch (Exception e) {
            Log.e(TAG, "Failed to get OAID info", e);
            return null;
        }
    }

    /**
     * 获取设备注册API参数 (用于 device_register 接口)
     *
     * API地址: https://klink.amemv.com/service/2/device_register/
     * 这些参数通过URL Query Parameters方式发送，而非在Body中加密
     *
     * 参数来源分类:
     * 1. 系统API获取: device_type, device_brand, os_version, os_api, host_abi, resolution, dpi, language等
     * 2. 硬编码值: aid, app_name, app_type, device_platform, os, tt_data, ssmix, md等
     * 3. 元数据: channel(APK签名块), update_version_code(AndroidManifest meta-data)
     * 4. 动态生成: cdid(UUID), _rticket(timestamp), ts(timestamp), ac(网络检测)等
     * 5. 库版本: cronet_version, ttnet_version
     */
    public static Map<String, String> getApiParams(Context context) {
        try {
            Map<String, String> params = new LinkedHashMap<>();

            PackageManager pm = context.getPackageManager();
            String packageName = context.getPackageName();
            ApplicationInfo appInfo = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

            // 基础应用信息 (系统API + 硬编码)
            params.put("aid", "1128");                                    // 硬编码: 应用ID
            params.put("app_name", "aweme");                              // 硬编码: 应用名称
            params.put("version_code", String.valueOf(packageInfo.versionCode));  // 系统API: 版本代码
            params.put("version_name", packageInfo.versionName);          // 系统API: 版本名称
            params.put("package", packageName);                           // 系统API: 包名
            params.put("app_type", "normal");                             // 硬编码: 应用类型
            params.put("channel", "xiaomi_1128_64");                      // 元数据: 从APK签名块提取

            // 设备信息 (系统API)
            params.put("device_platform", "android");                     // 硬编码: 设备平台
            params.put("device_type", Build.MODEL);                       // 系统API: 设备型号 (Build.MODEL)
            params.put("device_brand", Build.BRAND);                      // 系统API: 设备品牌 (Build.BRAND)
            params.put("device_model", Build.MODEL);                      // 系统API: 设备型号 (Build.MODEL)

            // 操作系统信息 (系统API + 硬编码)
            params.put("os", "android");                                  // 硬编码: 操作系统
            params.put("os_version", Build.VERSION.RELEASE);              // 系统API: 系统版本
            params.put("os_api", String.valueOf(Build.VERSION.SDK_INT));  // 系统API: API Level
            params.put("manifest_version_code", String.valueOf(packageInfo.versionCode));  // 系统API: Manifest版本
            params.put("update_version_code", "36009900");                // 元数据: 从AndroidManifest meta-data获取

            // 显示和分辨率 (系统API)
            params.put("resolution", displayMetrics.heightPixels + "*" + displayMetrics.widthPixels);  // 系统API: 屏幕分辨率
            params.put("dpi", String.valueOf(displayMetrics.densityDpi)); // 系统API: 屏幕DPI

            // 语言和地区 (系统API)
            params.put("language", context.getResources().getConfiguration().locale.getLanguage());  // 系统API: 系统语言

            // CPU和架构 (系统API)
            params.put("host_abi", Build.CPU_ABI);                        // 系统API: CPU ABI架构
            params.put("cpu_support64", isCpu64Bit() ? "true" : "false"); // 系统API: 检查SUPPORTED_ABIS

            // 网络信息 (动态检测)
            params.put("ac", NetworkAccessTypeDetector.getNetworkAccessType(context));  // 动态检测: 网络访问类型 (wifi/2g/3g/4g/5g)

            // 设备标识符 (动态生成)
            params.put("cdid", UUID.randomUUID().toString());             // UUID随机生成: 设备ID

            // 应用状态 (系统API + 硬编码)
            params.put("is_guest_mode", "0");                             // 硬编码: 游客模式
            params.put("minor_status", "0");                              // 硬编码: 次要状态
            params.put("is_preinstall", isPreinstalledApp(appInfo) ? "1" : "0");  // 系统API: 预装应用
            params.put("is_system_app", ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) ? "1" : "0");  // 系统API: 系统应用

            // 时间戳和请求相关 (系统API + 动态生成)
            params.put("first_launch_timestamp", String.valueOf(packageInfo.firstInstallTime / 1000));  // 系统API: 首次安装时间(秒)
            params.put("last_deeplink_update_version_code", "0");         // 硬编码: 最后深链接更新版本
            params.put("_rticket", String.valueOf(System.currentTimeMillis()));  // 动态: 请求时间戳(毫秒)
            params.put("ts", String.valueOf(System.currentTimeMillis() / 1000)); // 动态: 请求时间戳(秒)

            // 设备特征 (系统API + 检测)
            params.put("is_android_pad", DeviceCategoryDetector.isTablet(context) ? "1" : "0");  // 屏幕尺寸计算: 是否平板
            params.put("is_android_fold", isFoldedDevice(context) ? "1" : "0");  // 设备型号检测: 是否折叠屏
            params.put("appTheme", getAppTheme(context));                 // 系统API: 应用主题 (light/dark)

            // 网络库版本 (内置库版本)
            params.put("cronet_version", "5e1fe14e_2025-08-26");          // 库版本: Cronet库版本
            params.put("ttnet_version", "4.2.243.14-douyin");             // 库版本: TTNet库版本

            // 其他标志 (硬编码)
            params.put("tt_data", "a");                                   // 硬编码: 固定标志
            params.put("ssmix", "a");                                     // 硬编码: 固定标志
            params.put("md", "0");                                        // 硬编码: 固定标志
            params.put("need_personal_recommend", "1");                   // 用户设置: 个性化推荐
            params.put("use_store_region_cookie", "1");                   // 硬编码: 地区Cookie

            return params;
        } catch (Exception e) {
            Log.e(TAG, "Failed to get API params", e);
            return new LinkedHashMap<>();
        }
    }

    /**
     * 检查是否支持64位CPU
     */
    private static boolean isCpu64Bit() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return Arrays.asList(Build.SUPPORTED_ABIS).stream()
                    .anyMatch(abi -> abi.contains("64"));
        }
        return Build.CPU_ABI.contains("64") || Build.CPU_ABI2.contains("64");
    }

    /**
     * 检查应用是否为预装应用
     */
    private static boolean isPreinstalledApp(ApplicationInfo appInfo) {
        return (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM;
    }

    /**
     * 检查是否为平板设备
     */
    private static boolean isTablet(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float screenSize = (float) Math.sqrt(
                Math.pow(metrics.widthPixels / metrics.xdpi, 2) +
                Math.pow(metrics.heightPixels / metrics.ydpi, 2)
        );
        return screenSize > 6.5f;
    }

    /**
     * 检查是否为折叠屏设备
     */
    private static boolean isFoldedDevice(Context context) {
        try {
            // 尝试检查是否存在折叠屏相关的系统属性
            Class<?> buildClass = Class.forName("android.os.Build");
            java.lang.reflect.Field field = buildClass.getDeclaredField("DEVICE");
            String device = (String) field.get(null);
            return device != null && (device.contains("fold") || device.contains("flip"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取应用主题
     */
    private static String getAppTheme(Context context) {
        try {
            // 检查夜间模式设置
            int nightMode = context.getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
            if (nightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
                return "dark";
            }
            return "light";
        } catch (Exception e) {
            return "light";
        }
    }

    /**
     * 获取网络访问类型
     *
     * 委托给 NetworkAccessTypeDetector 处理
     * 返回值: wifi, 2g, 3g, 4g, 5g, none
     *
     * @param context Android上下文
     * @return 网络类型字符串
     */
    private static String getNetworkAccessType(Context context) {
        return NetworkAccessTypeDetector.getNetworkAccessType(context);
    }
}
