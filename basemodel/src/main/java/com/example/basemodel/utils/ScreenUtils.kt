package com.example.basemodel.utils

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue

/**
 * Created by liub on 2018/7/16 14:37
 *
 */
class ScreenUtils constructor() {
    private var dpiUtil: ScreenUtils? = null
    private var dm: DisplayMetrics? = null

    constructor(context: Context) : this() {
        if (context == null) {
            throw NullPointerException()
        } else {
            dm = context.resources.displayMetrics
        }
    }


    fun init(context: Context) {
        if (dpiUtil == null) {
            dpiUtil = ScreenUtils(context)
        }

    }

    fun dpi2Px(dpi: Float): Float {
        return dpi * dm!!.density + 0.5f
    }

    fun px2Dpi(context: Context, px: Int): Int {
        if (dm == null) {
            dm = context.resources.displayMetrics
        }

        return (px.toFloat() / dm!!.density + 0.5f).toInt()
    }

    fun pt2Px(context: Context, pt: Int): Int {
        if (dm == null) {
            dm = context.resources.displayMetrics
        }

        return TypedValue.complexToDimensionPixelSize(pt, dm)
    }

    fun pt2Dpi(context: Context, pt: Int): Int {
        if (dm == null) {
            dm = context.resources.displayMetrics
        }

        return px2Dpi(context, TypedValue.complexToDimensionPixelSize(pt, dm))
    }

    fun pt2Sp(context: Context, pt: Int): Int {
        if (dm == null) {
            dm = context.resources.displayMetrics
        }

        return px2Sp(context, TypedValue.complexToDimensionPixelSize(pt, dm).toFloat())
    }

    fun px2Sp(context: Context, px: Float): Int {
        if (dm == null) {
            dm = context.resources.displayMetrics
        }

        return (px / dm!!.scaledDensity + 0.5f).toInt()
    }

    fun getScreenWidth(): Int {
        return if (dm == null) 0 else dm!!.widthPixels
    }

    fun getScreenHeight(): Int {
        return if (dm == null) 0 else dm!!.heightPixels
    }
}