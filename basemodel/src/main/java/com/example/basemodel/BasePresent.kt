package com.example.basemodel

import android.content.Intent
import android.util.Log
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * Created by liub on 2018/7/16 16:25
 *
 */
abstract class BasePresent<V : BaseView> {
    private var targetView: V? = null
    protected lateinit var view: V  // view的代理

    constructor(view: V) {
        this.targetView = view
        this.view = Proxy.newProxyInstance(view.javaClass.classLoader, view.javaClass.interfaces, NotNullHandler()) as V
    }

    abstract fun onStart()
    /**
     * 用来处理onActivityResult,onFragmentResult
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    fun handViewResult(requestCode: Int, resultCode: Int, data: Intent) {

    }

    /**
     * 释放view。方法在AbstractMvpActivity的onDestroy方法里面被调用。用弱引用也可以得到同样的效果。
     */
    fun onDestroy() {
        targetView = null
    }

    inner class NotNullHandler : InvocationHandler {
        override fun invoke(p0: Any?, p1: Method?, args: Array<out Any>?): Any? {
            if (targetView == null) {  // 防止view为空，用动态代理在每个方法执行前增加判断
                Log.e("", "什么都没执行~~~~~~~~~~~~~")
                return null
            }
            try {
                return p1?.invoke(targetView, args)
            } catch (e: Exception) {
                // todo 发布的时候不让错误影响程序稳定性
//                if (Lmsg.isDebug) {
//                    throw Exception(e)
//                } else {
//                    e.printStackTrace()
//                }
                return null
            }

        }
    }
}