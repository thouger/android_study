package com.android.study.utils

import android.content.Context
import android.widget.Toast

object ToastUtils {
    private var toast: Toast? = null
    @JvmOverloads
    fun showToast(context: Context, msg: String?, isLong: Boolean = false) {
        ThreadUtils.runOnMainThread {
            if (toast == null) {
                //如果toast第一次创建 makeText创建toast对象
                toast = Toast.makeText(
                    context.applicationContext,
                    msg, if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
                )
            } else {
                //如果toast存在 只需要修改文字
                toast!!.setText(msg)
            }
            toast!!.show()
        }
    }
}
