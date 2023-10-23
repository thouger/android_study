package thouger.study

import android.content.Context
import android.content.pm.ApplicationInfo
import android.graphics.drawable.ClipDrawable
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import org.lsposed.hiddenapibypass.HiddenApiBypass
import timber.log.Timber
import java.lang.reflect.Field


@RequiresApi(Build.VERSION_CODES.P)
class ClassInfo {
    init {
        HiddenApiBypass.addHiddenApiExemptions("android.provider.Settings");
    }
     fun classInfo(clazz : Class<*>){
        val allStaticFields = HiddenApiBypass.getStaticFields(clazz)
        for (field in allStaticFields) {
            Timber.d("NameValueTable field: " + field.toString())
        }

        val allMethods = HiddenApiBypass.getDeclaredMethods(clazz)
        for (method in allMethods) {
            Timber.d("NameValueTable method: " + method.toString())
        }

        val allInstanceFields = HiddenApiBypass.getInstanceFields(
            ApplicationInfo::class.java
        )
        for (field in allInstanceFields) {
            Timber.d("ApplicationInfo field: " + field.toString())
        }

        val ctor = HiddenApiBypass.getDeclaredConstructor(
            ClipDrawable::class.java /*, args */
        )
        Timber.d("ClipDrawable ctor: " + ctor.toString())
    }

    fun getAndroidId(){
        val allStaticFields = HiddenApiBypass.getStaticFields(Settings.Global::class.java)
        for (field in allStaticFields) {
            if(field.toString().contains("sNameValueCache")){
                val nameValueCacheObj = (field as Field).get(null)
                //调用getStringForUser
                val result = HiddenApiBypass.invoke(
                    nameValueCacheObj as Class<*>,
                    "getStringForUser",
                    arrayOf(Context::class.java, String::class.java, Int::class.java).toString(),
                    arrayOf(this, "android_id", 0)
                )
                Timber.d("result: " + result)
            }
        }
    }
}