package thouger.study

import android.content.Context
import android.util.Log
import org.json.JSONObject
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class fileUtils {
    fun saveFile(context: Context, fileName: String, map: Map<String, String>) {
        Timber.d("保存文件")
        val anyMap: Map<*, *> = map as Map<*, *>
        val jsonString = JSONObject(anyMap).toString()

        try {
            val path = context.getExternalFilesDir(null)

            Timber.d("path: $path")

            if (path != null) {
                val dir = File(path.absolutePath)
                if (!dir.exists()) {
                    dir.mkdirs()
                }
            }
            val file = File(path, fileName)
            if (!file.exists()) {
                file.createNewFile()
            }
            FileOutputStream(file).use { fos ->
                fos.write(jsonString.toByteArray())
            }
            Timber.d("保存成功")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}