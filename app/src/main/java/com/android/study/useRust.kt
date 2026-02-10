package com.android.study

import android.content.Context
class useRust {
    companion object {
        init {
            System.loadLibrary("rustdemo")
        }
    }

    external fun getAppSignature(content: Context): Int
}