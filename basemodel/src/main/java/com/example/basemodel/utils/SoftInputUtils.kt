package com.example.basemodel.utils

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 * Created by liub on 2018/7/16 16:05
 *
 */
class SoftInputUtils {

    constructor()

    fun openSoftInput(context: Context, focusView: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        focusView.post {
            focusView.requestFocus()
            imm.showSoftInput(focusView, 2)
        }
    }

    fun closeSoftInput(context: Context, focusView: View?) {
        if (focusView != null) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(focusView.windowToken, 0)
            focusView.clearFocus()
        }
    }

    fun isActive(context: Context, view: View): Boolean {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return imm.isActive(view)
    }

    fun isShouldHideInput(v: View?, event: MotionEvent): Boolean {
        if (v != null && v is EditText) {
            val leftTop = intArrayOf(0, 0)
            v.getLocationInWindow(leftTop)
            val left = leftTop[0]
            val top = leftTop[1]
            val bottom = top + v.height
            val right = left + v.width
            return event.x <= left.toFloat() || event.x >= right.toFloat() || event.y <= top.toFloat() || event.y >= bottom.toFloat()
        } else {
            return false
        }
    }
}