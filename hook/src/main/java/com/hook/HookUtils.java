package com.hook;

import android.util.Log;

import java.util.Arrays;

import de.robv.android.xposed.XC_MethodHook;

/**
 * Hook辅助工具类 - 包含过滤器、格式化输出等通用功能
 */
public class HookUtils {
    public static final String TAG = "thougerhook";

    // ==================== 日志过滤器接口 ====================

    /**
     * 日志过滤器接口
     * @return true表示应该输出日志，false表示过滤掉
     */
    public interface LogFilter {
        boolean shouldLog(String[] formattedArgs, String formattedResult);
    }

    // ==================== 辅助函数 ====================

    /**
     * 打印调用栈
     */
    public static void printStack(String prefix) {
        try {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            StringBuilder sb = new StringBuilder();
            sb.append(prefix).append(" 调用栈:\n");
            for (StackTraceElement element : stackTrace) {
                sb.append("    at ").append(element.toString()).append("\n");
            }
            Log.d(TAG, sb.toString());
        } catch (Exception e) {
            Log.e(TAG, "Error printing stack trace", e);
        }
    }

    /**
     * 分段打印长日志
     */
    public static void logLong(String prefix, String message) {
        final int maxLogLength = 3000;
        String fullMessage = prefix + ": " + message;

        if (fullMessage.length() <= maxLogLength) {
            Log.d(TAG, fullMessage);
            return;
        }

        // 分段打印
        int chunks = (int) Math.ceil((double) fullMessage.length() / maxLogLength);
        for (int i = 0; i < fullMessage.length(); i += maxLogLength) {
            int end = Math.min(i + maxLogLength, fullMessage.length());
            int partNum = (i / maxLogLength) + 1;
            Log.d(TAG, prefix + " [" + partNum + "/" + chunks + "]: " + fullMessage.substring(i, end));
        }
    }

    /**
     * 格式化参数输出，支持多种类型
     */
    public static String formatValue(Object value) {
        if (value == null) {
            return "null";
        }

        // 处理 Map 类型
        if (value instanceof java.util.Map) {
            java.util.Map<?, ?> map = (java.util.Map<?, ?>) value;
            if (map.isEmpty()) {
                return "Map{}";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Map{");
            int count = 0;
            for (java.util.Map.Entry<?, ?> entry : map.entrySet()) {
                if (count > 0) sb.append(", ");
                sb.append(entry.getKey()).append("=").append(entry.getValue());
                count++;
            }
            sb.append("}");
            return sb.toString();
        }

        // 处理 Context 类型
        if (value instanceof android.content.Context) {
            return "Context{" + value.getClass().getSimpleName() + "@" +
                    Integer.toHexString(value.hashCode()) + "}";
        }

        // 处理byte数组
        if (value instanceof byte[]) {
            byte[] bytes = (byte[]) value;
            if (bytes.length == 0) {
                return "byte[0]";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("byte[").append(bytes.length).append("]: ");

            // 尝试转换为UTF-8字符串
            try {
                String strContent = new String(bytes, "UTF-8");
                int printableCount = 0;
                for (byte b : bytes) {
                    if ((b >= 32 && b <= 126) || b == 9 || b == 10 || b == 13 || b < 0) {
                        printableCount++;
                    }
                }

                // 如果超过80%是可打印字符，认为是文本
                if (printableCount > bytes.length * 0.8) {
                    sb.append("text=\"").append(strContent).append("\"");
                    return sb.toString();
                }
            } catch (Exception e) {
                // ignore
            }

            // 输出完整的十六进制数据
            sb.append("hex[");
            for (int i = 0; i < bytes.length; i++) {
                if (i > 0) sb.append(" ");
                sb.append(String.format("%02X", bytes[i]));
            }
            sb.append("]");
            return sb.toString();
        }

        // 处理其他类型的数组
        if (value.getClass().isArray()) {
            if (value instanceof int[]) {
                return "int" + Arrays.toString((int[]) value);
            } else if (value instanceof long[]) {
                return "long" + Arrays.toString((long[]) value);
            } else if (value instanceof short[]) {
                return "short" + Arrays.toString((short[]) value);
            } else if (value instanceof boolean[]) {
                return "boolean" + Arrays.toString((boolean[]) value);
            } else if (value instanceof char[]) {
                return "char" + Arrays.toString((char[]) value);
            } else if (value instanceof float[]) {
                return "float" + Arrays.toString((float[]) value);
            } else if (value instanceof double[]) {
                return "double" + Arrays.toString((double[]) value);
            } else if (value instanceof Object[]) {
                Object[] arr = (Object[]) value;
                StringBuilder sb = new StringBuilder();
                sb.append(value.getClass().getComponentType().getSimpleName())
                  .append("[").append(arr.length).append("] {");

                for (int i = 0; i < arr.length; i++) {
                    if (i > 0) sb.append(", ");
                    sb.append("\n    [").append(i).append("]: ");

                    // 递归格式化每个元素
                    if (arr[i] == null) {
                        sb.append("null");
                    } else {
                        String elementStr = formatValue(arr[i]);
                        // 对多行内容进行缩进
                        elementStr = elementStr.replace("\n", "\n    ");
                        sb.append(elementStr);
                    }
                }

                if (arr.length > 0) {
                    sb.append("\n");
                }
                sb.append("}");
                return sb.toString();
            }
        }

        // 其他类型直接toString
        String result = value.toString();
        return result + " [" + value.getClass().getSimpleName() + "]";
    }

    // ==================== 过滤器实现 ====================

    /**
     * 参数关键字过滤器
     */
    public static class ArgKeywordFilter implements LogFilter {
        private final int argIndex;
        private final String keyword;
        private final boolean ignoreCase;

        public ArgKeywordFilter(int argIndex, String keyword) {
            this(argIndex, keyword, true);
        }

        public ArgKeywordFilter(int argIndex, String keyword, boolean ignoreCase) {
            this.argIndex = argIndex;
            this.keyword = keyword;
            this.ignoreCase = ignoreCase;
        }

        @Override
        public boolean shouldLog(String[] formattedArgs, String formattedResult) {
            if (formattedArgs == null || argIndex >= formattedArgs.length) return false;
            String argStr = formattedArgs[argIndex];
            if (argStr == null) return false;

            if (ignoreCase) {
                return argStr.toLowerCase().contains(keyword.toLowerCase());
            }
            return argStr.contains(keyword);
        }
    }

    /**
     * 参数全字匹配过滤器
     */
    public static class ArgEqualsFilter implements LogFilter {
        private final int argIndex;
        private final String expectedValue;
        private final boolean ignoreCase;

        public ArgEqualsFilter(int argIndex, String expectedValue) {
            this(argIndex, expectedValue, false);
        }

        public ArgEqualsFilter(int argIndex, String expectedValue, boolean ignoreCase) {
            this.argIndex = argIndex;
            this.expectedValue = expectedValue;
            this.ignoreCase = ignoreCase;
        }

        @Override
        public boolean shouldLog(String[] formattedArgs, String formattedResult) {
            if (formattedArgs == null || argIndex >= formattedArgs.length) return false;
            String argStr = formattedArgs[argIndex];
            if (argStr == null) return expectedValue == null;

            if (ignoreCase) {
                return argStr.equalsIgnoreCase(expectedValue);
            }
            return argStr.equals(expectedValue);
        }
    }

    /**
     * 任意参数关键字过滤器
     */
    public static class AnyArgKeywordFilter implements LogFilter {
        private final String keyword;
        private final boolean ignoreCase;

        public AnyArgKeywordFilter(String keyword) {
            this(keyword, true);
        }

        public AnyArgKeywordFilter(String keyword, boolean ignoreCase) {
            this.keyword = keyword;
            this.ignoreCase = ignoreCase;
        }

        @Override
        public boolean shouldLog(String[] formattedArgs, String formattedResult) {
            if (formattedArgs == null) return false;
            for (String argStr : formattedArgs) {
                if (argStr != null) {
                    if (ignoreCase) {
                        if (argStr.toLowerCase().contains(keyword.toLowerCase())) return true;
                    } else {
                        if (argStr.contains(keyword)) return true;
                    }
                }
            }
            return false;
        }
    }

    /**
     * 返回值关键字过滤器
     */
    public static class ResultKeywordFilter implements LogFilter {
        private final String keyword;
        private final boolean ignoreCase;

        public ResultKeywordFilter(String keyword) {
            this(keyword, true);
        }

        public ResultKeywordFilter(String keyword, boolean ignoreCase) {
            this.keyword = keyword;
            this.ignoreCase = ignoreCase;
        }

        @Override
        public boolean shouldLog(String[] formattedArgs, String formattedResult) {
            if (formattedResult == null) return false;
            if (ignoreCase) {
                return formattedResult.toLowerCase().contains(keyword.toLowerCase());
            }
            return formattedResult.contains(keyword);
        }
    }

    /**
     * AND组合过滤器
     */
    public static class AndFilter implements LogFilter {
        private final LogFilter[] filters;

        public AndFilter(LogFilter... filters) {
            this.filters = filters;
        }

        @Override
        public boolean shouldLog(String[] formattedArgs, String formattedResult) {
            for (LogFilter filter : filters) {
                if (!filter.shouldLog(formattedArgs, formattedResult)) return false;
            }
            return true;
        }
    }

    /**
     * OR组合过滤器
     */
    public static class OrFilter implements LogFilter {
        private final LogFilter[] filters;

        public OrFilter(LogFilter... filters) {
            this.filters = filters;
        }

        @Override
        public boolean shouldLog(String[] formattedArgs, String formattedResult) {
            for (LogFilter filter : filters) {
                if (filter.shouldLog(formattedArgs, formattedResult)) return true;
            }
            return false;
        }
    }

    /**
     * NOT过滤器
     */
    public static class NotFilter implements LogFilter {
        private final LogFilter filter;

        public NotFilter(LogFilter filter) {
            this.filter = filter;
        }

        @Override
        public boolean shouldLog(String[] formattedArgs, String formattedResult) {
            return !filter.shouldLog(formattedArgs, formattedResult);
        }
    }

    /**
     * 总是输出的过滤器
     */
    public static class AlwaysLogFilter implements LogFilter {
        @Override
        public boolean shouldLog(String[] formattedArgs, String formattedResult) {
            return true;
        }
    }
}
