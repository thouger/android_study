package com.android.study

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.study.utils.FileUtils
import thouger.study.R
import com.android.study.utils.GsonUtils.obj2str
import com.android.study.utils.ThreadUtils
import timber.log.Timber
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.reflect.Method


class MainActivity : AppCompatActivity() {

    //定义请求码和请求的权限数组
    private val REQUEST_CODE_PERMISSIONS = 100

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val PERMISSIONS = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.POST_NOTIFICATIONS,
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_VIDEO,
        Manifest.permission.READ_MEDIA_AUDIO,
    )

    fun isSuCommandAvailable(): Boolean {
        try {
            // 使用命令行检查/sbin/su是否存在
            val process = Runtime.getRuntime().exec("which su")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val line = reader.readLine()

            // 检查输出是否包含/sbin/su
            if (line != null && line == "/sbin/su") {
                return true // /sbin/su命令存在
            }

            reader.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return false // /sbin/su命令不存在
    }


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        Log.d("testUseRust", "getAppSignature: ${useRust().getAppSignature(this)}")

        // 检查并请求权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkAndRequestLocationPermissions()
        }

//        //测试重定向
//        try {
//            val isSuAvailable = isSuCommandAvailable()
//            println("Is /sbin/su available? $isSuAvailable")
//
//
//            // 打开文件并创建 BufferedReader 对象
//            val reader = BufferedReader(FileReader("/proc/meminfo"))
//
//            // 逐行读取文件内容并打印
//            var line: String?
//            while (reader.readLine().also { line = it } != null) {
//                line?.let { Log.d("111111", it) }
//            }
//
//            // 关闭文件
//            reader.close()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//
//        super.onCreate(savedInstanceState)
////        setContentView(R.layout.activity_main)
//
////        Timber.plant(Timber.DebugTree())
//
////        System.loadLibrary("dobby")
//
//        //输出.so 目录的路径
//        val soPath = applicationInfo.nativeLibraryDir
//        Log.d("NativeLibs", "Native libs path: $soPath")
//        val dir = File(soPath)
//        if (dir.isDirectory) {
//            val files = dir.listFiles()
//            Log.d("NativeLibs", "Files count: " + files!!.size)
//            for (file in files) {
//                if (file.isFile) {
//                    Log.d("NativeLibs", "File name: " + file.name)
//                }
//            }
//        }
//
////        Timber.d("检测授权")
//        checkAndRequestLocationPermissions()
//
//        //测试native层
//        System.loadLibrary("hunter64")

//        Log.d("detect_inlineHook", detect_inlineHook())
//
//        val list = ArrayList<String>()
//        list.add("hello.so")
//
//        Analysis(list, null)
//
//        //调用copyString
//        Timber.d("copyString: "+copyString("hello world"))
//
//        val str = stringFromJNI()
//        Timber.d("stringFromJNI: $str")
//        callTestttttttttttt("hello world")
//        listmacaddrs()
//
//        val sum = sumArray(intArrayOf(1, 2, 3, 4, 5))
//        Timber.d("sum: $sum")

//        val DetectVirtualAppsResult = DetectVirtualApps().isRunningInVirtualApp()
//        if (DetectVirtualAppsResult) {
//            Toast.makeText(this, "检测到虚拟环境", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(this, "未检测到虚拟环境", Toast.LENGTH_SHORT).show()
//        }

//        val classA = Class.forName("com.android.study.ClassA")
//        val classB = Class.forName("com.android.study.ClassB")
//        val srcMethod = classA.getDeclaredMethod("printMessage")
//        val destMethod = classB.getDeclaredMethod("printMessage")
//        srcMethod.isAccessible = true
//        destMethod.isAccessible = true
//        Log.d("hook_native", "before hook")
//        ClassA().printMessage()
//        hook_native(srcMethod, destMethod)
//        ClassA().printMessage()
//        Log.d("hook_native", "after hook")

//        try {
//            // 使用标准库中的类Date和Calendar替换自定义类ClassA和ClassB
//            val classA = Class.forName("java.util.Date")
//            val classB = Class.forName("java.util.Calendar")
//
//            println("ClassA loaded: ${classA.name}")
//            println("ClassB loaded: ${classB.name}")
//
//            // 获取Date类中的toString方法作为srcMethod
//            val srcMethod = classA.getDeclaredMethod("toString")
//            // 获取Calendar类中的getInstance方法作为destMethod
//            val destMethod = classB.getDeclaredMethod("getInstance")
//
//            println("srcMethod: ${srcMethod.name}")
//            println("destMethod: ${destMethod.name}")
//
//            // 设置方法可访问
//            srcMethod.isAccessible = true
//            destMethod.isAccessible = true
//
//            // 调用Date类的toString方法
//            val dateInstance = classA.getDeclaredConstructor().newInstance()
//            val srcResult = srcMethod.invoke(dateInstance)
//            println("srcMethod result: $srcResult")
//
//            // 调用Calendar类的getInstance方法
//            val destResult = destMethod.invoke(null)
//            println("destMethod result: $destResult")
//
//            // 再次调用Date类的toString方法
//            val srcResult2 = srcMethod.invoke(classA.getDeclaredConstructor().newInstance())
//            println("srcMethod result 2: $srcResult2")
//
//            println("Replacement successful")
//        } catch (e: Exception) {
//            e.printStackTrace()
//            println("Replacement failed")
//        }
        
        //内存漫游
//        getAllObjectInfo(this, File(this.filesDir, "all_object.txt"))
//        val filter = IntentFilter("android.intent.tec.action.sendotherapp")
//        val receiver = MyBroadcastReceiver()
//        this.registerReceiver(receiver, filter)

//        test_read_file()

//        SeccompSVC()
//        test()
//        loadSo()

        System.loadLibrary("algorithm")

        setupButtons()

        Log.d("MainActivity", "结束")
    }


    private fun setupButtons() {
        val btnFibonacci = findViewById<Button>(R.id.btnFibonacci)
        val btnPrime = findViewById<Button>(R.id.btnPrime)
        val btnBoth = findViewById<Button>(R.id.btnBoth)
        val resultText = findViewById<TextView>(R.id.resultText)

        btnFibonacci.setOnClickListener {
            Log.d("Algorithm", "Fibonacci button clicked")
            val result = calculateFibonacci(10)
            val message = "Fibonacci(10) = $result"
            resultText.text = message
            Log.d("Algorithm", message)
        }

        btnPrime.setOnClickListener {
            Log.d("Algorithm", "Prime button clicked")
            val result = isPrime(17)
            val message = "isPrime(17) = $result"
            resultText.text = message
            Log.d("Algorithm", message)
        }

        btnBoth.setOnClickListener {
            Log.d("Algorithm", "Both algorithms button clicked")
            val fibResult = calculateFibonacci(10)
            val primeResult = isPrime(17)

            // Test Frida GUM example
            Log.d("FridaGUM", "Running Frida GUM example...")
            val fridaResult = runFridaGumExample()
            Log.d("FridaGUM", "Frida GUM example result: $fridaResult")

            val message = "Fibonacci(10) = $fibResult\nisPrime(17) = $primeResult\nFrida GUM result = $fridaResult"
            resultText.text = message
            Log.d("Algorithm", "Fibonacci(10) = $fibResult")
            Log.d("Algorithm", "isPrime(17) = $primeResult")
        }
    }

    fun m1() {}
    fun m2() {}

//    private external fun hook_native(src: Method, dest: Method): Long
//
//    external fun loadSo()
//
//    external fun detect_inlineHook(): String
//
//    external fun SeccompSVC()
//    external fun Analysis(list: ArrayList<*>?, path: String?)
//    external fun native_test()
//
//    external fun listmacaddrs(): String
//
//    external fun callTestttttttttttt(str: String)

    external fun stringFromJNI(): String

    external fun sumArray(array: IntArray?): Int

    external fun copyString(str: String?): String?

    external fun calculateFibonacci(n: Int): Long

    external fun isPrime(number: Int): Boolean

    external fun runFridaGumExample(): Int

    fun testttttttttttt(str: String) {
        Timber.d("testttttttttttt: $str")
    }

    fun test_read_file() {
        //查看/default.prop文件内容并且打印
        try {
            val filePath = "/default.prop" // 修改为您要读取的文件路径
            val file = File(filePath)

            if (file.exists()) {
                val bufferedReader = file.bufferedReader()
                val content = bufferedReader.use { it.readText() }
                Timber.d("File content:\n$content")
            } else {
                Timber.d("File not found: $filePath")
            }
        }catch (e: Exception) {
            Timber.d("result: File not found: $e")
        }

        val process = Runtime.getRuntime().exec("usa")
        val outputStream = DataOutputStream(process.outputStream)
        outputStream.writeBytes("cat /default.prop\n")
        outputStream.flush()
        outputStream.writeBytes("exit\n")
        outputStream.flush()
        process.waitFor()
        val inputStream = process.inputStream
        val buffer = ByteArray(inputStream.available())
        inputStream.read(buffer)
        val result = String(buffer)
        Timber.d("result: $result")
    }

    /**
     * 获取内存全部对象的内存信息,并且保存
     * 主要用于分析内存结构，确定什么对象保存了设备指纹等关键字段
     *
     *
     * 大约需要运行十分钟所以即可全部遍历
     */
    private fun getAllObjectInfo(context: Context, file: File) {
        Timber.d("start get all object !!!")
        //优先保存主进程
        ThreadUtils.runOnMainThread({
            //ArrayList<Object> choose = ChooseUtils.choose(String.class, true)
            val choose = ChooseUtils.choose(Any::class.java, true)
            val size = choose!!.size
            Timber.d("all count size ->  $size")
            for (i in 0 until size) {
                val o = choose[i]
                val s: String? = obj2str(o)
                if (s != null) {
                    val c = o.javaClass.name
                    val s1 = "$c $s\n"
                    Timber.d("thouger.study" + " " + i + " " + size + "  " + s1)
                    FileUtils.saveStringNoClose(context, s1, file)
                }
            }
        }, 10 * 1000)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkAndRequestLocationPermissions() {
        //检查是否已经授权
        for (permission in PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Timber.d("未授权")
                ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_CODE_PERMISSIONS)
            }
        }
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        //检查是否是请求读取手机状态和位置权限的请求码
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            //检查权限是否授予
            var allGranted = true
            for (grantResult in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false
                    break
                }
            }
            if (allGranted) {
                //TODO: 执行需要权限的操作
                Timber.d("已授权")
            } else {
                //TODO: 处理用户未授权的情况
                Timber.d("未授权")
            }
        }
    }
}