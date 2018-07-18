package com.example.basemodel.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.basemodel.R

/**
 * Created by liub on 2018/7/18 10:39
 *3秒警告消息
 */
class AlertToast  {
    private var mDuration = 3000
    private var mShowText = true
    private var mView: View? = null
    private lateinit var mContext: Context
    private var mToastTextView: TextView? = null
    private var mToastImageView: ImageView? = null
    private val mHandler = Handler(Looper.getMainLooper())

    constructor()
    constructor(context: Context) {
        mContext = context
    }


    fun make(context: Context, message: CharSequence): AlertToast {
        val alertToast = AlertToast(context)
        alertToast.setText(message)
        return alertToast
    }


    fun make(context: Context, contentView: View): AlertToast {
        val alertToast = AlertToast(context)
        alertToast.setShowText(false)
        alertToast.setContentView(contentView)
        return alertToast
    }

    fun setText(message: CharSequence): AlertToast {
        if (mShowText) {
            if (mView == null) {
                mView = LayoutInflater.from(mContext).inflate(R.layout.lib_ui_toast_view, null)
                mToastTextView = mView!!.findViewById<View>(R.id.tv_toast) as TextView
                mToastImageView = mView!!.findViewById<View>(R.id.iv_toast) as ImageView
            }
            mToastTextView!!.text = message
        }
        return this
    }

    fun setContentView(view: View): AlertToast {
        mView = view
        return this
    }

    fun setDrawable(drawable: Drawable?): AlertToast {
        if (!mShowText) {
            return this
        } else if (drawable == null) {
            return this
        } else {
            mToastImageView!!.visibility = View.VISIBLE
            mToastImageView!!.setImageDrawable(drawable)
        }
        return this
    }


    fun setDrawable(drawable: Drawable?, width: Int, height: Int): AlertToast {
        if (!mShowText) {
            return this
        } else if (drawable == null) {
            return this
        } else {
            mToastImageView!!.visibility = View.VISIBLE
            mToastImageView!!.setImageDrawable(drawable)
            mToastImageView!!.layoutParams.width = width
            mToastImageView!!.layoutParams.height = height
        }
        return this
    }

    fun setDuration(duration: Int): AlertToast {
        if (duration > 0) {
            mDuration = duration
        }
        return this
    }

    fun show() {
        mHandler.post(ShowRunnable())
    }

    private fun setShowText(showText: Boolean) {
        mShowText = showText
    }

    internal inner class ShowRunnable : Runnable {
        override fun run() {
            val toast = Toast(mContext)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.view = mView
            toast.duration = Toast.LENGTH_LONG
            toast.show()
            //            showToast(toast, mDuration);
        }
    }

    private fun showToast(toast: Toast, cnt: Int) {
        mHandler.postDelayed({ toast.show() }, 3000)

        mHandler.postDelayed({ toast.cancel() }, cnt.toLong())
    }
}