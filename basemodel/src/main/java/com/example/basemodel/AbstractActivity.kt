package com.example.basemodel

import android.content.Intent
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.example.basemodel.utils.ScreenUtils
import com.example.basemodel.utils.SoftInputUtils

/**
 * Created by liub on 2018/7/16 10:49
 * 抽象父类
 *
 */
abstract class AbstractActivity : AppCompatActivity() {

    protected lateinit var mContext: AbstractActivity
    private var swipeBackEnable = true
    private var swipeGoback: Boolean = false
    private var touchX: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseSetContentView()
        ActivityStack().getInstance().addActivity(this)
        ScreenUtils().init(this)
        mContext = this
        bindViews(savedInstanceState)
        setSwipeBackEnable(setSwipeBack())
        if (savedInstanceState != null) {
            restore(savedInstanceState)
        }
    }

    /**
     * 页面回复,
     * savedInstanceState保存的数据
     */
    private fun restore(savedInstanceState: Bundle) {
        //回复用户信息
        //只要导致了变化,回到主页
    }

    private fun baseSetContentView() {
        setContentView(setContentView())
    }

    private fun setSwipeBackEnable(swipeBack: Boolean) {
        this.swipeBackEnable = swipeBack
    }

    private fun getSwipeBackEnable(): Boolean {
        return swipeBackEnable
    }


    /**
     * 启用滑动返回，8.0以下默认为开启
     */
    private fun setSwipeBack(): Boolean {
        return true
    }

    abstract fun setContentView(): Int

    abstract fun bindViews(savedInstanceState: Bundle?)

    abstract fun initData()

    private var isFirstEntry: Boolean = true
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && isFirstEntry) {
            initData()
            isFirstEntry = false
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    /**
     * 展示Fragment，调用此方法，请确保Fragment的容器id为fl_container
     */
    fun showFragment(fragment: Fragment) {
        showFragment(fragment, false)
    }

    /**
     * 展示fragment
     */
    fun showFragment(fragment: Fragment, addToBackStack: Boolean) {
        showFragment(fragment, addToBackStack, true, R.id.fl_container)
    }

    /**
     * 展示fragment，允许其他fragment同时存在。用于一个Activity多个Fragment模块的情况
     */

    fun showFragmentAllowOtherExist(fragment: Fragment, containerId: Int) {
        showFragment(fragment, false, false, containerId)
    }

    /**
     * 展示Fragment，调用此方法，请确保Fragment的容器id为fl_container
     *
     * @param fragment       fragment
     * @param addToBackStack 是否需要加入到回退栈
     * @param hide           是否会隐藏其他Fragment
     * @param containerId    容器ID
     */
    private lateinit var mCurrFragment: Fragment
    protected lateinit var mFragmentMgr: FragmentManager
    protected lateinit var mFragmentTransaction: FragmentTransaction

    private fun showFragment(fragment: Fragment, addToBackStack: Boolean, hide: Boolean, fl_containerId: Int) {

        if (fl_containerId != R.id.fl_container) {
            var containerView = findViewById<View>(fl_containerId)
            if (containerView == null || containerView !is ViewGroup) {
                throw IllegalArgumentException("fl_containerId 必须是一个有效的容器id")
            }
        }
        if (fragment == null) {
            return
        }
        if (fragment.isAdded && mCurrFragment == fragment) {
            Log.e("showFragment", "fragment has show")
            return
        }
        if (mFragmentMgr == null) {
            mFragmentMgr = supportFragmentManager
        }
        //如果activity已经销毁往下走了
        if (mFragmentMgr.isDestroyed) {
            return
        }
        mFragmentTransaction = mFragmentMgr.beginTransaction()

        //如果当前页面需要隐藏
        if (null != mCurrFragment && hide) {
            mFragmentTransaction.hide(mCurrFragment)
        }
        //// 如果页面未添加，则添加；如果已经添加则显示
        if (fragment.isAdded) {
            mFragmentTransaction.show(fragment)
        } else {
            mFragmentTransaction.add(R.id.fl_container, fragment)
        }
        //是否需要加入任务栈
        if (addToBackStack) {
            mFragmentTransaction.addToBackStack(null)
        }
        mFragmentTransaction.commitAllowingStateLoss()////commitAllowingStateLoss代替commit
        mCurrFragment = fragment
    }

    /**
     * 管理fragment栈,后退动作
     */
    fun popBackStack() {
        if (mFragmentMgr == null) {
            finish()
            return
        }
        var fragments = mFragmentMgr.fragments
        if (fragments == null || fragments.size == 0) {
            finish()
            return
        }
        var position = fragments.indexOf(mCurrFragment)
        if (position == 0) {
            finish()
        } else {
            mCurrFragment = fragments[position - 1]
            mFragmentMgr.popBackStack()
        }
    }

    /**
     * 获取当前展示的fragment
     */
    fun getCurrenShowFragment(): Fragment {
        return mCurrFragment
    }

    /**
     * 设置返回键默认动作
     */
    fun setToolbarDefaultBackAction() {
        onBack()
    }

    private fun onBack() {
        if (this.currentFocus != null) {
            SoftInputUtils().closeSoftInput(applicationContext, this.currentFocus)
        }
        ActivityCompat.finishAfterTransition(this)
    }

    /**
     * 是否触摸自动关闭软件盘（默认开启，可重写关闭）
     */
    private fun isCloseSoftKeyBoardOnTouch(): Boolean {
        return true
    }

    /**
     * 手势关闭页面
     */
    @CallSuper
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN && isCloseSoftKeyBoardOnTouch()) {
            var v = currentFocus
            if (SoftInputUtils().isShouldHideInput(v, ev)) {
                SoftInputUtils().closeSoftInput(this, v)
            }
        }
        if (getSwipeBackEnable()) {
            if (swipeGoback) {
                if (ev.actionMasked == MotionEvent.ACTION_UP) {
                    swipeGoback = false
                }
                return true
            }
            var swipStart = resources.displayMetrics.widthPixels / 15
            var swipDistance = resources.displayMetrics.widthPixels / 4
            when (ev.actionMasked) {
                MotionEvent.ACTION_UP -> {
                    touchX = 0f
                }
                MotionEvent.ACTION_DOWN -> {
                    if (ev.x < swipStart) {
                        touchX = ev.x
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (touchX < 0) {
                        var dis = Math.abs(ev.x - touchX)
                        if (dis > swipDistance) {
                            swipeGoback = true
                            finish()
                            touchX = 0f
                            return true
                        }
                    }
                }
                MotionEvent.ACTION_CANCEL -> {
                    touchX = 0f

                }
            }
        }
        return super.dispatchTouchEvent(ev)

    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        overridePendingTransition(R.anim.slide_right_to_left, R.anim.slide_old)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_old, R.anim.slide_right_to_left)
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityStack().getInstance().removeActivity(this)
    }
}