package com.example.basemodel

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import com.example.basemodel.utils.AlertToast
import com.example.basemodel.utils.ToastUtils
import com.example.basemodel.widget.StatusBarUtils
import java.lang.ref.WeakReference

/**
 * Created by liub on 2018/7/16 16:24
 *
 */
abstract class AbstractMvpActivity<T : BasePresent<BaseView>> : AbstractActivity(), BaseView {
    protected var presenter: T? = null
    protected var mHandler: Handler = LeakHandler(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = newPresent()
        if (setWhiteStatusBar() && Build.VERSION.SDK_INT >= 23) {
            StatusBarUtils().setWhiteStatusBar(this)
        }
    }


    /**
     * 控制是否需要设置白色标题栏
     *
     * @return 是否需要设置白色标题栏
     */
    protected fun setWhiteStatusBar(): Boolean {
        return true
    }

    override fun initData() {
        if (presenter != null) {
            presenter?.onStart()
        }
    }

    /**
     * 生成presenter的方法，如果页面简单没什么业务逻辑不需要，可以返回null
     */
    protected abstract fun newPresent(): T

    protected fun handleMessage(message: Message) {}

    override fun onDestroy() {
        super.onDestroy()
        if (presenter != null) {
            presenter?.onDestroy()
        }
    }

    /**
     * 防止内存泄露的handler
     */
    inner class LeakHandler : Handler {
        var reference: WeakReference<AbstractMvpActivity<*>>? = null

        constructor(activity: AbstractMvpActivity<*>) {
            reference = WeakReference(activity)

        }

        override fun handleMessage(msg: Message) {
            if (reference == null) return
            val activity = reference?.get()
            activity?.handleMessage(msg)

        }
    }

    override fun showToastMessage(msg: String) {
        showToastMessage(msg, true)
    }

    override fun showToastMessage(msgResId: Int) {
        showToastMessage(resources.getString(msgResId), true)
    }

    override fun showToastMessage(msg: CharSequence, center: Boolean) {
        if (center) {
            AlertToast().make(window.decorView.context, msg).show()
        } else {
            ToastUtils().show(applicationContext, msg)
        }
    }

}