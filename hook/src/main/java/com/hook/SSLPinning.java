package com.hook;

/**
 * SSL Pinning Bypass Script
 *
 * Hook list:
 * 1. SSLContext
 * 2. OkHttp
 * 3. WebView
 * 4. XUtils
 * 5. httpclientandroidlib
 * 6. JSSE
 * 7. network_security_config (Android 7.0+)
 * 8. Apache Http client (support partly)
 * 9. OpenSSLSocketImpl
 * 10. TrustKit
 * 11. Cronet
 */
public class SSLPinning {

    /**
     * 获取 SSL Pinning 绕过脚本
     * @return Frida JavaScript 脚本内容
     */
    public static String getSSLPinningBypassScript() {
        return "Java.perform(function() {\n" +
                "\n" +
                "    // Attempts to bypass SSL pinning implementations\n" +
                "    var X509TrustManager = Java.use('javax.net.ssl.X509TrustManager');\n" +
                "    var HostnameVerifier = Java.use('javax.net.ssl.HostnameVerifier');\n" +
                "    var SSLContext = Java.use('javax.net.ssl.SSLContext');\n" +
                "\n" +
                "    // Implement a new TrustManager\n" +
                "    var X509Certificate = Java.use('java.security.cert.X509Certificate');\n" +
                "    var TrustManager;\n" +
                "    try {\n" +
                "        TrustManager = Java.registerClass({\n" +
                "            name: 'org.wooyun.TrustManager',\n" +
                "            implements: [X509TrustManager],\n" +
                "            methods: {\n" +
                "                checkClientTrusted: function(chain, authType) {},\n" +
                "                checkServerTrusted: function(chain, authType) {},\n" +
                "                getAcceptedIssuers: function() {\n" +
                "                    return [];\n" +
                "                }\n" +
                "            }\n" +
                "        });\n" +
                "    } catch (e) {\n" +
                "        console.log('registerClass from X509TrustManager >>>>>>>> ' + e.message);\n" +
                "    }\n" +
                "\n" +
                "    // Prepare the TrustManagers array\n" +
                "    var TrustManagers = [TrustManager.$new()];\n" +
                "\n" +
                "    var EmptySSLFactory;\n" +
                "    try {\n" +
                "        var TLS_SSLContext = SSLContext.getInstance('TLS');\n" +
                "        TLS_SSLContext.init(null, TrustManagers, null);\n" +
                "        EmptySSLFactory = TLS_SSLContext.getSocketFactory();\n" +
                "    } catch (e) {\n" +
                "        console.log(e.message);\n" +
                "    }\n" +
                "\n" +
                "    console.log('[+] Custom, Empty TrustManager ready');\n" +
                "\n" +
                "    // Override SSLContext.init()\n" +
                "    var SSLContext_init = SSLContext.init.overload(\n" +
                "        '[Ljavax.net.ssl.KeyManager;', '[Ljavax.net.ssl.TrustManager;', 'java.security.SecureRandom');\n" +
                "\n" +
                "    SSLContext_init.implementation = function(keyManager, trustManager, secureRandom) {\n" +
                "        console.log('[+] Overriding SSLContext.init() with custom TrustManager');\n" +
                "        SSLContext_init.call(this, null, TrustManagers, null);\n" +
                "    };\n" +
                "\n" +
                "    /*** OkHttp 3.x unpinning ***/\n" +
                "    try {\n" +
                "        var CertificatePinner = Java.use('okhttp3.CertificatePinner');\n" +
                "        console.log('[+] OkHTTP 3.x Found');\n" +
                "\n" +
                "        CertificatePinner.check.overload('java.lang.String', 'java.util.List').implementation = function() {\n" +
                "            console.log('[+] OkHTTP 3.x check() called. Not throwing exception.');\n" +
                "        }\n" +
                "    } catch (err) {\n" +
                "        if (err.message.indexOf('ClassNotFoundException') === 0) {\n" +
                "            throw new Error(err);\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    // Appcelerator Titanium\n" +
                "    try {\n" +
                "        var PinningTrustManager = Java.use('appcelerator.https.PinningTrustManager');\n" +
                "        console.log('[+] Appcelerator Titanium Found');\n" +
                "\n" +
                "        PinningTrustManager.checkServerTrusted.implementation = function() {\n" +
                "            console.log('[+] Appcelerator checkServerTrusted() called.');\n" +
                "        }\n" +
                "    } catch (err) {\n" +
                "        if (err.message.indexOf('ClassNotFoundException') === 0) {\n" +
                "            throw new Error(err);\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    /*** OkHttp unpinning ***/\n" +
                "    try {\n" +
                "        var OkHttpClient = Java.use('com.squareup.okhttp.OkHttpClient');\n" +
                "        OkHttpClient.setCertificatePinner.implementation = function(certificatePinner) {\n" +
                "            console.log('[+] OkHttpClient.setCertificatePinner Called!');\n" +
                "            return this;\n" +
                "        };\n" +
                "\n" +
                "        var CertificatePinner = Java.use('com.squareup.okhttp.CertificatePinner');\n" +
                "        CertificatePinner.check.overload('java.lang.String', '[Ljava.security.cert.Certificate;').implementation = function(p0, p1) {\n" +
                "            console.log('[+] okhttp Called! [Certificate]');\n" +
                "            return;\n" +
                "        };\n" +
                "        CertificatePinner.check.overload('java.lang.String', 'java.util.List').implementation = function(p0, p1) {\n" +
                "            console.log('[+] okhttp Called! [List]');\n" +
                "            return;\n" +
                "        };\n" +
                "    } catch (e) {\n" +
                "        console.log('[-] com.squareup.okhttp not found');\n" +
                "    }\n" +
                "\n" +
                "    /*** WebView Hooks ***/\n" +
                "    var WebViewClient = Java.use('android.webkit.WebViewClient');\n" +
                "\n" +
                "    WebViewClient.onReceivedSslError.implementation = function(webView, sslErrorHandler, sslError) {\n" +
                "        console.log('[+] WebViewClient onReceivedSslError invoke');\n" +
                "        sslErrorHandler.proceed();\n" +
                "        return;\n" +
                "    };\n" +
                "\n" +
                "    WebViewClient.onReceivedError.overload('android.webkit.WebView', 'int', 'java.lang.String', 'java.lang.String').implementation = function(a, b, c, d) {\n" +
                "        console.log('[+] WebViewClient onReceivedError invoked');\n" +
                "        return;\n" +
                "    };\n" +
                "\n" +
                "    WebViewClient.onReceivedError.overload('android.webkit.WebView', 'android.webkit.WebResourceRequest', 'android.webkit.WebResourceError').implementation = function() {\n" +
                "        console.log('[+] WebViewClient onReceivedError invoked');\n" +
                "        return;\n" +
                "    };\n" +
                "\n" +
                "    /*** JSSE Hooks ***/\n" +
                "    var HttpsURLConnection = Java.use('javax.net.ssl.HttpsURLConnection');\n" +
                "\n" +
                "    HttpsURLConnection.setDefaultHostnameVerifier.implementation = function(hostnameVerifier) {\n" +
                "        console.log('[+] HttpsURLConnection.setDefaultHostnameVerifier invoked');\n" +
                "        return null;\n" +
                "    };\n" +
                "\n" +
                "    HttpsURLConnection.setSSLSocketFactory.implementation = function(SSLSocketFactory) {\n" +
                "        console.log('[+] HttpsURLConnection.setSSLSocketFactory invoked');\n" +
                "        return null;\n" +
                "    };\n" +
                "\n" +
                "    HttpsURLConnection.setHostnameVerifier.implementation = function(hostnameVerifier) {\n" +
                "        console.log('[+] HttpsURLConnection.setHostnameVerifier invoked');\n" +
                "        return null;\n" +
                "    };\n" +
                "\n" +
                "    /*** Xutils3.x hooks ***/\n" +
                "    var TrustHostnameVerifier;\n" +
                "    try {\n" +
                "        TrustHostnameVerifier = Java.registerClass({\n" +
                "            name: 'org.wooyun.TrustHostnameVerifier',\n" +
                "            implements: [HostnameVerifier],\n" +
                "            method: {\n" +
                "                verify: function(hostname, session) {\n" +
                "                    return true;\n" +
                "                }\n" +
                "            }\n" +
                "        });\n" +
                "    } catch (e) {\n" +
                "        console.log('registerClass from hostnameVerifier >>>>>>>> ' + e.message);\n" +
                "    }\n" +
                "\n" +
                "    try {\n" +
                "        var RequestParams = Java.use('org.xutils.http.RequestParams');\n" +
                "        RequestParams.setSslSocketFactory.implementation = function(sslSocketFactory) {\n" +
                "            sslSocketFactory = EmptySSLFactory;\n" +
                "            return null;\n" +
                "        }\n" +
                "\n" +
                "        RequestParams.setHostnameVerifier.implementation = function(hostnameVerifier) {\n" +
                "            hostnameVerifier = TrustHostnameVerifier.$new();\n" +
                "            return null;\n" +
                "        }\n" +
                "    } catch (e) {\n" +
                "        console.log('[-] Xutils hooks not Found');\n" +
                "    }\n" +
                "\n" +
                "    /*** httpclientandroidlib Hooks ***/\n" +
                "    try {\n" +
                "        var AbstractVerifier = Java.use('ch.boye.httpclientandroidlib.conn.ssl.AbstractVerifier');\n" +
                "        AbstractVerifier.verify.overload('java.lang.String', '[Ljava.lang.String', '[Ljava.lang.String', 'boolean').implementation = function() {\n" +
                "            console.log('[+] httpclientandroidlib Hooks');\n" +
                "            return null;\n" +
                "        }\n" +
                "    } catch (e) {\n" +
                "        console.log('[-] httpclientandroidlib Hooks not found');\n" +
                "    }\n" +
                "\n" +
                "    /*** Android 7.0+ network_security_config ***/\n" +
                "    var TrustManagerImpl = Java.use('com.android.org.conscrypt.TrustManagerImpl');\n" +
                "\n" +
                "    try {\n" +
                "        TrustManagerImpl.verifyChain.implementation = function(untrustedChain, trustAnchorChain, host, clientAuth, ocspData, tlsSctData) {\n" +
                "            console.log('[+] TrustManagerImpl verifyChain called');\n" +
                "            return untrustedChain;\n" +
                "        }\n" +
                "    } catch (e) {\n" +
                "        console.log('[-] TrustManagerImpl verifyChain not found below 7.0');\n" +
                "    }\n" +
                "\n" +
                "    // OpenSSLSocketImpl\n" +
                "    try {\n" +
                "        var OpenSSLSocketImpl = Java.use('com.android.org.conscrypt.OpenSSLSocketImpl');\n" +
                "        OpenSSLSocketImpl.verifyCertificateChain.implementation = function(certRefs, authMethod) {\n" +
                "            console.log('[+] OpenSSLSocketImpl.verifyCertificateChain');\n" +
                "        }\n" +
                "        console.log('[+] OpenSSLSocketImpl pinning')\n" +
                "    } catch (err) {\n" +
                "        console.log('[-] OpenSSLSocketImpl pinner not found');\n" +
                "    }\n" +
                "\n" +
                "    // Trustkit\n" +
                "    try {\n" +
                "        var Activity = Java.use('com.datatheorem.android.trustkit.pinning.OkHostnameVerifier');\n" +
                "        Activity.verify.overload('java.lang.String', 'javax.net.ssl.SSLSession').implementation = function(str) {\n" +
                "            console.log('[+] Trustkit.verify1: ' + str);\n" +
                "            return true;\n" +
                "        };\n" +
                "        Activity.verify.overload('java.lang.String', 'java.security.cert.X509Certificate').implementation = function(str) {\n" +
                "            console.log('[+] Trustkit.verify2: ' + str);\n" +
                "            return true;\n" +
                "        };\n" +
                "        console.log('[+] Trustkit pinning')\n" +
                "    } catch (err) {\n" +
                "        console.log('[-] Trustkit pinner not found')\n" +
                "    }\n" +
                "\n" +
                "    // Cronet\n" +
                "    try {\n" +
                "        var netBuilder = Java.use('org.chromium.net.CronetEngine$Builder');\n" +
                "\n" +
                "        netBuilder.enablePublicKeyPinningBypassForLocalTrustAnchors.implementation = function(arg) {\n" +
                "            console.log('[+] Enables public key pinning bypass = ' + arg);\n" +
                "            var ret = netBuilder.enablePublicKeyPinningBypassForLocalTrustAnchors.call(this, true);\n" +
                "            return ret;\n" +
                "        };\n" +
                "\n" +
                "        netBuilder.addPublicKeyPins.implementation = function(hostName, pinsSha256, includeSubdomains, expirationDate) {\n" +
                "            console.log('[+] cronet addPublicKeyPins hostName = ' + hostName);\n" +
                "            return this;\n" +
                "        };\n" +
                "    } catch (err) {\n" +
                "        console.log('[-] Cronet pinner not found')\n" +
                "    }\n" +
                "\n" +
                "    console.log('[+] SSL Pinning Bypass enabled successfully!');\n" +
                "});";
    }

    /**
     * 执行 SSL Pinning 绕过
     * 使用示例:
     * String script = SSLPinning.getSSLPinningBypassScript();
     * // 然后使用 Frida 加载并执行这个脚本
     */
    public static void enableSSLPinningBypass() {
        // 这个方法可以用来集成到你的 Hook 框架中
        String script = getSSLPinningBypassScript();
        // 在这里调用你的 Frida 脚本执行逻辑
        // 例如: FridaLoader.load(script);
    }
}
