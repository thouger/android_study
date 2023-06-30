package thouger.study

import java.io.IOException
import java.lang.reflect.Proxy
import java.net.HttpURLConnection
import java.net.URL

class DetectVirtualApps {
    fun isRunningInVirtualApp(): Boolean {
        // 检测 VirtualApp 库
        try {
            Class.forName("com.lody.virtual.client.core.VirtualCore")
            return true
        } catch (e: ClassNotFoundException) {
            // 没有找到 VirtualApp 库
        }

        // 检测代理类
        var isProxy = false
        val thread = Thread {
            var url: URL? = null
            try {
                url = URL("https://www.google.com/")
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()
                var clazz: Class<*>? = connection.javaClass
                while (clazz != null) {
                    if (Proxy.isProxyClass(clazz)) {
                        // 应用程序正在 VirtualApp 中运行
                        isProxy = true
                        break
                    }
                    clazz = clazz.superclass
                }
            } catch (e: IOException) {
                // 忽略异常
            }
        }
        thread.start()
        thread.join()
        return isProxy
    }
}
