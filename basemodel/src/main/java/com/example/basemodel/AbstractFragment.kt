package com.example.basemodel

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by liub on 2018/7/18 10:09
 *
 */
abstract class AbstractFragment : Fragment() {
    private var mContext: AbstractActivity? = null

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        mContext = activity as AbstractActivity?
    }

    override fun onDetach() {
        super.onDetach()
        mContext = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(setContentView(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bindViews(view, savedInstanceState)
        initData()

    }

    abstract fun initData()

    abstract fun bindViews(view: View, savedInstanceState: Bundle?)

    abstract fun setContentView(): Int
}