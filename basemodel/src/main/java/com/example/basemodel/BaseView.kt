package com.example.basemodel

/**
 * Created by liub on 2018/7/16 16:25
 *
 */
interface BaseView {

    fun showToastMessage(msgResId: Int)

    fun showToastMessage(msg: String)

    /**
     * 提示框
     * @param msg 提示信息
     * @param center 如果是中心显示，则是类似IOS样式
     */
    fun showToastMessage(msg: CharSequence, center: Boolean)
}