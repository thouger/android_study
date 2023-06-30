package thouger.study.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder

object GsonUtils {
    val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    fun <T> str2obj(jsonString: String?, c: Class<T>?): T? {
        return try {
            gson.fromJson(jsonString, c)
        } catch (e: Throwable) {
            null
        }
    }

    fun obj2str(`object`: Any?): String? {
        return try {
            if (`object` == null) {
                null
            } else gson.toJson(`object`)
        } catch (e: Throwable) {
            null
        }
    }
}