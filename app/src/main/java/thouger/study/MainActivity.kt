package thouger.study

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import thouger.study.utils.FileUtils
import thouger.study.utils.GsonUtils.obj2str
import thouger.study.utils.ThreadUtils
import timber.log.Timber
import java.io.File


class MainActivity : AppCompatActivity() {


    //定义请求码和请求的权限数组
    private val REQUEST_CODE_PERMISSIONS = 100

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val PERMISSIONS = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.BLUETOOTH,
    )

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Timber.plant(Timber.DebugTree())

        //输出.so 目录的路径
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

//        Timber.d("检测授权")
        checkAndRequestLocationPermissions()

        //测试native层
//        System.loadLibrary("hello")
//        System.loadLibrary("mynative64");
//
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

        //指纹输出与保存
        val device_info = DeviceInfoUtils()
        val map = device_info.printALL(this,"appsfly_map")
        val fileName = "appsfly_map.json-"+System.currentTimeMillis()
        fileUtils().saveFile(this, fileName, map)



//        val DetectVirtualAppsResult = DetectVirtualApps().isRunningInVirtualApp()
//        if (DetectVirtualAppsResult) {
//            Toast.makeText(this, "检测到虚拟环境", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(this, "未检测到虚拟环境", Toast.LENGTH_SHORT).show()
//        }


        //内存漫游
//        getAllObjectInfo(this, File(this.filesDir, "all_object.txt"))
    }

    external fun listmacaddrs(): String

    external fun callTestttttttttttt(str: String)

    external fun stringFromJNI(): String

    external fun sumArray(array: IntArray?): Int
    external fun Analysis(list: ArrayList<*>?, path: String?)

    external fun copyString(str: String?): String?

    fun testttttttttttt(str: String) {
        Timber.d("testttttttttttt: $str")
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
            //ArrayList<Object> choose = ChooseUtils.choose(String.class, true);
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
        }, 3 * 1000)
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