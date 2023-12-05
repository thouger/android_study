package thouger.study

import android.content.Context
import android.util.Log
class useRust {
    companion object {
        init {
            System.loadLibrary("rustdemo")
        }
    }

    external fun getAppSignature(content: Context): Int
}