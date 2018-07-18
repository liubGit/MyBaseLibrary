package com.example.basemodel.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.widget.Toast

/**
 * Created by liub on 2018/7/18 10:45
 *
 */
class ToastUtils {

    constructor() {
        throw AssertionError()
    }

    private var handler: Handler? = null


    fun show(context: Context?, resId: Int) {
        if (context != null) {
            show(context, context.resources.getText(resId), 0)
        }
    }

    fun show(context: Context?, resId: Int, duration: Int) {
        if (context != null) {
            show(context, context.resources.getText(resId), duration)
        }
    }

    fun show(context: Context, text: CharSequence) {
        show(context, text, 0)
    }

    fun show(context: Context?, text: CharSequence, duration: Int) {
        if (!TextUtils.isEmpty(text) && context != null) {
            if (handler == null) {
                val var3 = ToastUtils::class.java
                synchronized(ToastUtils::class.java) {
                    if (handler == null) {
                        handler = Handler(Looper.getMainLooper())
                    }
                }
            }

            handler!!.post { Toast.makeText(context, text, duration).show() }
        }
    }

    fun show(context: Context?, resId: Int, vararg args: Any) {
        if (context != null) {
            show(context, String.format(context.resources.getString(resId), *args), 0)
        }
    }

    fun show(context: Context, format: String, vararg args: Any) {
        show(context, String.format(format, *args), 0)
    }

    fun show(context: Context?, resId: Int, duration: Int, vararg args: Any) {
        if (context != null) {
            show(context, String.format(context.resources.getString(resId), *args), duration)
        }
    }

    fun show(context: Context, format: String, duration: Int, vararg args: Any) {
        show(context, String.format(format, *args), duration)
    }
}