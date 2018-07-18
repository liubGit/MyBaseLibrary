package com.example.basemodel

import android.content.Context
import android.os.Handler
import android.os.Message
import android.support.annotation.CallSuper
import java.lang.ref.WeakReference

/**
 * Created by liub on 2018/7/18 10:14
 *
 */
abstract class AbstractMvpFragment<T : BasePresent<BaseView>> : AbstractFragment(), BaseView {
    var presenter: T? = null
    private var baseViewImpl: BaseView?=null

    protected var mHandler: Handler = LeakHandler(this)

    inner class LeakHandler : Handler {
        var reference: WeakReference<AbstractMvpFragment<*>>? = null

        constructor(fragment: AbstractMvpFragment<*>) {
            reference = WeakReference(fragment)
        }

        override fun handleMessage(msg: Message?) {
            if (reference == null) return
            val fragment = reference?.get()
            fragment?.handleMessage(msg)
        }
    }


    /**
     * 当Activity和Fragment关联时，会回调此方法，将Activity的实例传递到此类中。
     */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is BaseView) {
            baseViewImpl = context
        } else {
            throw RuntimeException("宿主activity必须用AbstractMvpActivity")
        }
    }

    override fun onDetach() {
        super.onDetach()
        baseViewImpl = null
        if (presenter != null) {
            presenter?.onDestroy()
        }
    }

    @CallSuper
    override fun initData() {
        presenter = newPresenter()
    }

    fun handleMessage(msg: Message?) {

    }

    abstract fun newPresenter(): T?

    override fun showToastMessage(msg: String) {
        if (baseViewImpl != null) {
            baseViewImpl?.showToastMessage(msg)
        }
    }

    override fun showToastMessage(msgResId: Int) {
        if (baseViewImpl != null) {
            baseViewImpl?.showToastMessage(msgResId)
        }
    }

}
