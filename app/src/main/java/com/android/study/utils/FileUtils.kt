package com.android.study.utils

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

object FileUtils {
    fun makeSureDirExist(file: File): File {
        if (file.path == "") {
            return file
        }
        if (file.exists() && file.isFile) {
            return file
        }
        val mkdirs = file.mkdirs()
        if (mkdirs) {
            try {
                val a = file.setExecutable(true, false)
                val b = file.setWritable(true, false)
                val c = file.setReadable(true, false)
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
        return file
    }

    private var outStream: FileOutputStream? = null
    private var writer: OutputStreamWriter? = null

    /*
     * 用于一个文件保存大量字符串
     * 不进行关闭,防止反复创建影响效率
     */
    fun saveStringNoClose(mContext: Context?, str: String?, file: File) {
        try {
            if (str == null) {
                return
            }
            //CLog.e("save str info -> "+str);
            if (!file.exists()) {
                val newFile = file.createNewFile()
            }
            if (outStream == null) {
                outStream = FileOutputStream(file, true)
                writer = OutputStreamWriter(outStream, StandardCharsets.UTF_8)
            }
            writer!!.write(str)
            writer!!.flush()
        } catch (e: Throwable) {
            if (mContext != null) {
                ToastUtils.showToast(
                    mContext,
                    "Please check the sdcard read and write permissions : " + e.message
                )
            }
        }
    }

    fun saveStringNoCloseDestroy() {
        try {
            if (outStream != null) {
                outStream!!.close()
            }
            if (writer != null) {
                writer!!.close()
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            try {
                if (outStream != null) {
                    outStream!!.close()
                }
                if (writer != null) {
                    writer!!.close()
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }
}
